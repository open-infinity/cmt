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
import org.openinfinity.cloud.domain.Job;
import org.openinfinity.cloud.domain.ScalingRule;
import org.openinfinity.cloud.domain.repository.scaling.ScalingRuleRepository;
import org.openinfinity.cloud.service.administrator.ClusterService;
import org.openinfinity.cloud.service.administrator.JobService;
import org.openinfinity.cloud.service.administrator.MachineService;
import org.openinfinity.cloud.service.scaling.Enumerations.ScalingState;
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
	public ScalingState applyScalingRule(float load, int clusterId,	ScalingRule rule) {
		int clusterSize = clusterService.getCluster(clusterId).getNumberOfMachines();
		int maxMachines = rule.getMaxNumberOfMachinesPerCluster();
		int minMachines = rule.getMinNumberOfMachinesPerCluster();
		float maxLoad = rule.getMaxLoad();
		float minLoad = rule.getMinLoad();

        LOG.debug("ENTER applyScalingRule");
        LOG.debug("load:" + load + ", clusterId:" + clusterId);

        ClusterScalingStatus clusterScalingStatus = getClusterScalingStatus(rule);
        ScalingState state;

        if(clusterScalingStatus == ClusterScalingStatus.ERROR){
            state = ScalingState.SCALING_ERROR;

        } else if(clusterScalingStatus == ClusterScalingStatus.UNDER_CONSTRUCTION){
            state = ScalingState.SCALING_ONGOING;

        } else if ((load >= maxLoad && clusterSize < maxMachines) || (clusterSize < minMachines)){
            state = ScalingState.SCALE_OUT;

        } else if ((load <= minLoad && clusterSize > minMachines) || (clusterSize > maxMachines)){
            state = ScalingState.SCALE_IN;

        } else if (clusterSize >= maxMachines && load > maxLoad){
            state = ScalingState.SCALING_OUT_IMPOSSIBLE;

        } else {
            state = ScalingState.SCALING_NOT_REQUIRED;
        }
        LOG.debug("applyScalingRule result:" + state);
		return state;
	}

    @Override
    public ScalingState applyScalingRule(Timestamp samplingPeriodFrom, Timestamp samplingPeriodTo, Cluster cluster, ScalingRule rule) {
        Timestamp periodFrom = rule.getPeriodFrom();
        Timestamp periodTo = rule.getPeriodTo();
        ScalingState state;
        ClusterScalingStatus clusterScalingStatus = getClusterScalingStatus(rule);

        if(clusterScalingStatus == ClusterScalingStatus.ERROR){
            state = ScalingState.SCALING_ERROR;

        } else if(clusterScalingStatus == ClusterScalingStatus.UNDER_CONSTRUCTION){
            state = ScalingState.SCALING_ONGOING;

        } else if (rule.getMaxNumberOfMachinesPerCluster() <= rule.getClusterSizeNew()){
                state = ScalingState.SCALING_OUT_IMPOSSIBLE;

        } else if (periodTo.before(periodFrom) || periodTo.equals(periodFrom)){
            state = ScalingState.SCALING_RULE_INVALID;

        } else if (samplingPeriodFrom.before(periodFrom) && samplingPeriodTo.after(periodFrom)
                && rule.getScheduledScalingState() == ScalingRule.ScheduledScalingState.READY_FOR_SCALE_OUT.getValue()){

            storeScalingOutParameters(cluster.getNumberOfMachines(), rule.getClusterId());
            state = ScalingState.SCALE_OUT;

        } else if ((samplingPeriodFrom.before(periodTo) && samplingPeriodTo.after(periodTo)
                && samplingPeriodFrom.after(periodFrom) && rule.getScheduledScalingState() == ScalingRule.ScheduledScalingState.READY_FOR_SCALE_IN.getValue())
                || (samplingPeriodFrom.before(periodFrom) && samplingPeriodTo.after(periodTo))) {

            storeScalingInParameters(rule.getClusterId());
            state = ScalingState.SCALE_IN;

        } else {
            state = ScalingState.SCALING_NOT_REQUIRED;
        }
        LOG.debug("applyScalingRule result:" + state);
        return state;
    }

    ClusterScalingStatus getClusterScalingStatus(ScalingRule rule){
        ClusterScalingStatus scalingJobStatus = ClusterScalingStatus.IDLE;
        int jobId = rule.getJobId();
        try{
            if (jobId == -1) {
                scalingJobStatus = ClusterScalingStatus.IDLE;
            } else {
                Job job = jobService.getJob(jobId);
                int jobStatus = job.getJobStatus();
                boolean allMachinesConfigured = machineService.allMachinesConfigured(rule.getClusterId());
                if (jobStatus == JobService.CLOUD_JOB_READY) {
                    if (allMachinesConfigured){
                        scalingJobStatus = ClusterScalingStatus.IDLE;
                    } else {
                        scalingJobStatus = ClusterScalingStatus.UNDER_CONSTRUCTION;
                    }
                } else if (jobStatus == JobService.CLOUD_JOB_ERROR) {
                    scalingJobStatus = ClusterScalingStatus.ERROR;
                } else if (jobStatus == JobService.CLOUD_JOB_CREATED) {
                    scalingJobStatus = ClusterScalingStatus.UNDER_CONSTRUCTION;
                } else if (jobStatus == JobService.CLOUD_JOB_STARTED) {
                    scalingJobStatus = ClusterScalingStatus.UNDER_CONSTRUCTION;
                }
            }
        }
        catch (Exception e){
            scalingJobStatus = ClusterScalingStatus.ERROR;
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