/*
 * Copyright (c) 2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.openinfinity.cloud.service.scaling;

import org.apache.log4j.Logger;
import org.openinfinity.cloud.domain.Cluster;
import org.openinfinity.cloud.domain.ScalingRule;
import org.openinfinity.cloud.domain.repository.scaling.ScalingRuleRepository;
import org.openinfinity.cloud.service.administrator.ClusterService;
import org.openinfinity.cloud.service.administrator.JobService;
import org.openinfinity.cloud.service.administrator.MachineService;
import org.openinfinity.cloud.service.scaling.Enumerations.ScalingStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Collection;

/**
 * Service interface implementation of the automated provisioning business
 * rules.
 *
 * @author Ilkka Leinonen
 * @author Vedran Bartonicek
 * @version 1.0.0
 * @since 1.0.0
 */
@Service("scalingRuleService")
public class ScalingRuleServiceImpl implements ScalingRuleService {

    private static final Logger LOG = Logger.getLogger(ScalingRuleServiceImpl.class.getName());

    @Autowired
	ScalingRuleRepository scalingRuleRepository;

	@Autowired
	MachineService machineService;

	@Autowired
	ClusterService clusterService;

	@Autowired
	JobService jobService;

    @Override
	public ScalingStatus applyScalingRule(float load, int clusterId,	ScalingRule rule) {
		int clusterSize = clusterService.getCluster(clusterId).getNumberOfMachines();
		int maxMachines = rule.getMaxNumberOfMachinesPerCluster();
		int minMachines = rule.getMinNumberOfMachinesPerCluster();
		float maxLoad = rule.getMaxLoad();
		float minLoad = rule.getMinLoad();

        LOG.debug("ENTER applyScalingRule");
        LOG.debug("load:" + load + ", clusterId:" + clusterId);

        ClusterStatus clusterStatus = getClusterScalingStatus(rule);
        ScalingStatus status;

        if(clusterStatus == ClusterStatus.SCALING_JOB_ERROR){
            status = ScalingStatus.SCALING_IMPOSSIBLE_CLUSTER_ERROR;

        } else if(clusterStatus == ClusterStatus.MACHINE_UNDER_CONSTRUCTION){
            status = ScalingStatus.SCALING_IMPOSSIBLE_SCALING_ALREADY_ONGOING;

        } else if ((load >= maxLoad && clusterSize < maxMachines) || (clusterSize < minMachines)){
            status = ScalingStatus.SCALING_OUT_REQUIRED;

        } else if ((load <= minLoad && clusterSize > minMachines) || (clusterSize > maxMachines)){
            status = ScalingStatus.SCALING_IN_REQUIRED;

        } else if (clusterSize >= maxMachines && load > maxLoad){
            status = ScalingStatus.SCALING_IMPOSSIBLE_SCALING_RULE_LIMIT;

        } else {
            status = ScalingStatus.SCALING_NOT_REQUIRED;
        }
        LOG.debug("applyScalingRule result:" + status);
		return status;
	}

    @Override
    public ScalingStatus applyScalingRule(Timestamp samplingPeriodFrom, Timestamp samplingPeriodTo, Cluster cluster, ScalingRule rule) {
        Timestamp periodFrom = rule.getPeriodFrom();
        Timestamp periodTo = rule.getPeriodTo();
        ScalingStatus state;
        ClusterStatus clusterStatus = getClusterScalingStatus(rule);

        if(clusterStatus == ClusterStatus.SCALING_JOB_ERROR){
            state = ScalingStatus.SCALING_IMPOSSIBLE_CLUSTER_ERROR;

        } else if(clusterStatus == ClusterStatus.MACHINE_UNDER_CONSTRUCTION){
            state = ScalingStatus.SCALING_IMPOSSIBLE_SCALING_ALREADY_ONGOING;

        } else if(clusterStatus == ClusterStatus.MACHINE_CONFIGURATION_FAILURE){
            state = ScalingStatus.SCALING_IMPOSSIBLE_MACHINE_CONFIGURATION_ERROR;

        } else if (rule.getMaxNumberOfMachinesPerCluster() <= rule.getClusterSizeNew()){
                state = ScalingStatus.SCALING_IMPOSSIBLE_SCALING_RULE_LIMIT;

        } else if (periodTo.before(periodFrom) || periodTo.equals(periodFrom)){
            state = ScalingStatus.SCALING_IMPOSSIBLE_INVALID_RULE;

        } else if (samplingPeriodFrom.before(periodFrom) && samplingPeriodTo.after(periodFrom)
                && rule.getScheduledScalingState() == ScalingRule.ScheduledScalingState.READY_FOR_SCALE_OUT.getValue()){

            storeScalingOutParameters(cluster.getNumberOfMachines(), rule.getClusterId());
            state = ScalingStatus.SCALING_OUT_REQUIRED;

        } else if ((samplingPeriodFrom.before(periodTo) && samplingPeriodTo.after(periodTo)
                && samplingPeriodFrom.after(periodFrom) && rule.getScheduledScalingState() == ScalingRule.ScheduledScalingState.READY_FOR_SCALE_IN.getValue())
                || (samplingPeriodFrom.before(periodFrom) && samplingPeriodTo.after(periodTo))) {

            storeScalingInParameters(rule.getClusterId());
            state = ScalingStatus.SCALING_IN_REQUIRED;
        }
        else {
            state = ScalingStatus.SCALING_NOT_REQUIRED;
        }
        LOG.debug("applyScalingRule result:" + state);
        return state;
    }

    ClusterStatus getClusterScalingStatus(ScalingRule rule){
        ClusterStatus scalingJobStatus = ClusterStatus.IDLE;
        int jobId = rule.getJobId();
        try{
            if (jobId == -1) {
                scalingJobStatus = ClusterStatus.IDLE;
            } else {
                int jobStatus = jobService.getJob(jobId).getJobStatus();
                if (jobStatus == JobService.CLOUD_JOB_READY) {
                    if (machineService.allMachinesConfigured(rule.getClusterId())){
                        scalingJobStatus = ClusterStatus.IDLE;
                    } else if (machineService.machinesWithConfigureErrorExist(rule.getClusterId())){
                        scalingJobStatus = ClusterStatus.MACHINE_CONFIGURATION_FAILURE;
                    } else{
                        scalingJobStatus = ClusterStatus.MACHINE_UNDER_CONSTRUCTION;
                    }
                } else if (jobStatus == JobService.CLOUD_JOB_ERROR) {
                    scalingJobStatus = ClusterStatus.SCALING_JOB_ERROR;
                } else if (jobStatus == JobService.CLOUD_JOB_CREATED) {
                    scalingJobStatus = ClusterStatus.MACHINE_UNDER_CONSTRUCTION;
                } else if (jobStatus == JobService.CLOUD_JOB_STARTED) {
                    scalingJobStatus = ClusterStatus.MACHINE_UNDER_CONSTRUCTION;
                }
            }
        }
        catch (Exception e){
            scalingJobStatus = ClusterStatus.SCALING_JOB_ERROR;
            LOG.error("Fetching job for scaling rule for cluster " + rule.getClusterId() +  "failed.", e);
        }
        LOG.debug("getClusterScalingStatus for clusterId:" +  rule.getClusterId() + " and job id:" +jobId + " is:" + scalingJobStatus);
        return scalingJobStatus;
    }

	/*
	 * Using two sql requests instead of using single with command
	 * "on duplicate key update". That is because H2 db, which is used for
	 * testing, would not support it.
	 */
	@Transactional
	public void store(ScalingRule newScalingRule) {
        try{
            scalingRuleRepository.getRule(newScalingRule.getClusterId());
            scalingRuleRepository.updateExisting(newScalingRule);
        }
        catch (EmptyResultDataAccessException e){
            scalingRuleRepository.addNew(newScalingRule);
        }
	}

    public void delete(int clusterId){
    	scalingRuleRepository.delete(clusterId);
    }

	public ScalingRule getRule(int clusterId) {
		return scalingRuleRepository.getRule(clusterId);
	}

	public void deleteByClusterId(int id) {
		scalingRuleRepository.deleteByClusterId(id);
	}

	public Collection<ScalingRule> loadAll() {
		return scalingRuleRepository.loadAll();
	}

	public void storeScalingOutParameters(int numberOfMachines, int clusterId) {
		scalingRuleRepository.storeStateScheduledScaling(numberOfMachines, clusterId);
	}

	public void storeScalingInParameters(int clusterId) {
		scalingRuleRepository.storeStateScheduledUnScaling(clusterId);
	}

	public void storeJobId(int clusterId, int jobId) {
		scalingRuleRepository.storeJobId(clusterId, jobId);
	}
}