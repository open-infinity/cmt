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
        ScalingState state = ScalingState.SCALING_NOT_REQUIRED;

        if (isScalingOngoing(rule)){
			state = ScalingState.SCALING_ONGOING;
		} else if ((load >= maxLoad && clusterSize < maxMachines) || (clusterSize < minMachines)){
			state = ScalingState.SCALE_OUT;
		} else if ((load <= minLoad && clusterSize > minMachines) || (clusterSize > maxMachines)){
			state = ScalingState.SCALE_IN;
		} else if (clusterSize >= maxMachines && load > maxLoad){
			state = ScalingState.SCALING_OUT_IMPOSSIBLE;
		}
		return state;
	}

    @Override
    public ScalingState applyScalingRule(Timestamp samplingPeriodFrom, Timestamp samplingPeriodTo, Cluster cluster, ScalingRule scalingRule) {
        Timestamp periodFrom = scalingRule.getPeriodFrom();
        Timestamp periodTo = scalingRule.getPeriodTo();

        ScalingState state = ScalingState.SCALING_NOT_REQUIRED;

        if (isScalingOngoing(scalingRule)){
            state = ScalingState.SCALING_ONGOING;
        } else if (scalingRule.getMaxNumberOfMachinesPerCluster() <= scalingRule.getClusterSizeNew()){
            state = ScalingState.SCALING_OUT_IMPOSSIBLE;
        }
        else if (periodTo.before(periodFrom) || periodTo.equals(periodFrom)){
            state = ScalingState.SCALING_RULE_INVALID;
        }
        else if (samplingPeriodFrom.before(periodFrom) && samplingPeriodTo.after(periodFrom)
                && scalingRule.getScheduledScalingState() == ScalingRule.ScheduledScalingState.READY_FOR_SCALE_OUT.getValue()){

            storeScalingOutParameters(cluster.getNumberOfMachines(), scalingRule.getClusterId());
            state = ScalingState.SCALE_OUT;

        } else if ((samplingPeriodFrom.before(periodTo) && samplingPeriodTo.after(periodTo)
                && samplingPeriodFrom.after(periodFrom) && scalingRule.getScheduledScalingState() == ScalingRule.ScheduledScalingState.READY_FOR_SCALE_IN.getValue())
                || (samplingPeriodFrom.before(periodFrom) && samplingPeriodTo.after(periodTo))) {

            storeScalingInParameters(scalingRule.getClusterId());
            state = ScalingState.SCALE_IN;
        }
        return state;
    }

    boolean isScalingOngoing(ScalingRule rule){
        boolean scalingJobActive = true;
        int jobId = rule.getJobId();

        // If there was a scaling job, check if it is ready
        if (jobId != -1) {
            Job job = jobService.getJob(jobId);
            if (job != null) {
                int jobStatus = job.getJobStatus();
                if (jobStatus == JobService.CLOUD_JOB_READY)
                    scalingJobActive = false;
            }
        } else
            scalingJobActive = false;

        boolean allMachinesConfigured = false;
        if (!scalingJobActive) {
            allMachinesConfigured = machineService.allMachinesConfigured(rule.getClusterId());
        }
        return scalingJobActive || !allMachinesConfigured;
    }

	/*
	 * Using two sql requests instead of using single with command
	 * "on duplicate key update". That is beacuse H2 db, which is used for
	 * testing, would not support it. -Vedran Bartonicek
	 */
	@Transactional
	public void store(ScalingRule newScalingRule) {
        try{
            // If rule exists update it, otherwise this throws EmptyResultDataAccessException
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