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

		LOG.debug("executeBusinessLogic() Parameters: clusterId = " + clusterId
				+ " load = " + load + " maxMachines = " + maxMachines
				+ " minMachines = " + minMachines + " maxLoad = " + maxLoad
				+ " minLoad = " + minLoad + " clusterSize = " + clusterSize);

		boolean scalingJobActive = true;
		int jobId = rule.getJobId();
		Job job = null;

		// If there was a scaling job, check if it is ready
		if (jobId != -1) {
			job = jobService.getJob(jobId);
			if (job != null) {
				int jobStatus = job.getJobStatus();
				if (jobStatus == JobService.CLOUD_JOB_READY)
					scalingJobActive = false;
			}
		} else
			scalingJobActive = false;

		boolean allMachinesConfigured = false;

		if (!scalingJobActive) allMachinesConfigured = machineService.allMachinesConfigured(clusterId);

		LOG.debug("scalingJobReady=" + scalingJobActive	+ " allMachinesConfigured=" + allMachinesConfigured);

        ScalingState state;
        if (!rule.isPeriodicScalingOn() || scalingJobActive || !allMachinesConfigured){
			state = ScalingState.SCALING_SKIPPED;			
		} else if ((load >= maxLoad && clusterSize < maxMachines) || (clusterSize < minMachines)){
			state = ScalingState.REQUIRES_SCALING_OUT;
		} else if ((load <= minLoad && clusterSize > minMachines) || (clusterSize > maxMachines)){
			state = ScalingState.REQUIRES_SCALING_IN;
		} else if (clusterSize >= maxMachines && load > maxLoad){
			state = ScalingState.REQUIRED_SCALING_IS_NOT_POSSIBLE;
		} else {
			state = ScalingState.REQUIRES_NO_SCALING;
		}

		LOG.debug("applyScalingRule() return = " + state.name());
		return state;
	}
    //TODO check if there is "ANY" scaling job for the cluster
    @Override
    public ScalingState applyScalingRule(Timestamp samplingPeriodFrom, Timestamp samplingPeriodTo, int clusterId, ScalingRule scalingRule) {
        Timestamp periodFrom = scalingRule.getPeriodFrom();
        Timestamp periodTo = scalingRule.getPeriodTo();
        return null;
        /*
        if (periodTo.before(periodFrom) || periodTo.equals(periodFrom)){
            job = null;

            // Sampling period has "caught" the beginning of scheduled scaling period, and scheduled state is valid (state 1 = "OK for scaling out")
        }
        else if (samplingPeriodStart.before(periodFrom) && samplingPeriodEnd.after(periodFrom) && scalingRule.getScheduledScalingState() == ScalingRule.ScheduledScalingState.READY_FOR_SCALE_OUT.getValue()){
            job = createJob(scalingRule, cluster, scalingRule.getClusterSizeNew());
            scalingRuleService.storeScalingOutParameters(cluster.getNumberOfMachines(), scalingRule.getClusterId());

            // Sampling period has "caught" the end of scheduled scaling period, and scheduled state is valid (state 0 = "OK for scaling in")
        } else if ((samplingPeriodStart.before(periodTo) && samplingPeriodEnd.after(periodTo)
                && samplingPeriodStart.after(periodFrom) && scalingRule.getScheduledScalingState() == ScalingRule.ScheduledScalingState.READY_FOR_SCALE_IN.getValue())
                || (samplingPeriodStart.before(periodFrom) && samplingPeriodEnd.after(periodTo))) {
            job = createJob(scalingRule, cluster, scalingRule.getClusterSizeOriginal());
            scalingRuleService.storeScalingInParameters(scalingRule.getClusterId());

            // Sampling period is before or after scheduled scaling period
        } else {
            job = null;
        } */
    }
    /*
    if (periodTo.before(periodFrom) || periodTo.equals(periodFrom)){
			job = null;

        // Sampling period has "caught" the beginning of scheduled scaling period, and scheduled state is valid (state 1 = "OK for scaling out")
		}
		else if (samplingPeriodStart.before(periodFrom) && samplingPeriodEnd.after(periodFrom) && scalingRule.getScheduledScalingState() == ScalingRule.ScheduledScalingState.READY_FOR_SCALE_OUT.getValue()){
			job = createJob(scalingRule, cluster, scalingRule.getClusterSizeNew());
			scalingRuleService.storeScalingOutParameters(cluster.getNumberOfMachines(), scalingRule.getClusterId());

        // Sampling period has "caught" the end of scheduled scaling period, and scheduled state is valid (state 0 = "OK for scaling in")
		} else if ((samplingPeriodStart.before(periodTo) && samplingPeriodEnd.after(periodTo)
				&& samplingPeriodStart.after(periodFrom) && scalingRule.getScheduledScalingState() == ScalingRule.ScheduledScalingState.READY_FOR_SCALE_IN.getValue())
				|| (samplingPeriodStart.before(periodFrom) && samplingPeriodEnd.after(periodTo))) {
			job = createJob(scalingRule, cluster, scalingRule.getClusterSizeOriginal());
			scalingRuleService.storeScalingInParameters(scalingRule.getClusterId());

        // Sampling period is before or after scheduled scaling period
		} else {
			job = null;
		}

     */
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
		scalingRuleRepository.storeStateScheduledScaling(numberOfMachines,
				clusterId);
	}

	public void storeScalingInParameters(int clusterId) {
		scalingRuleRepository.storeStateScheduledUnScaling(clusterId);
	}

	public void storeJobId(int clusterId, int jobId) {
		scalingRuleRepository.storeJobId(clusterId, jobId);
	}
}