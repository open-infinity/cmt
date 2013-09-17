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

import java.util.Collection;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.openinfinity.cloud.domain.Job;
import org.openinfinity.cloud.domain.ScalingRule;
import org.openinfinity.cloud.service.scaling.Enumerations.ScalingState;
import org.openinfinity.cloud.domain.repository.scaling.ScalingRuleRepository;
import org.openinfinity.cloud.service.administrator.ClusterService;
import org.openinfinity.cloud.service.administrator.JobService;
import org.openinfinity.cloud.service.administrator.MachineService;
import org.openinfinity.core.exception.BusinessViolationException;

/**
 * Service interface implementation of the automated provisioning business rules.
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
	public ScalingState calculateScalingState(ScalingRule rule, float load, int clusterId) {
		validateInput(rule, load, clusterId);
		return applyScalingRule(load, clusterId, rule);
	}

	public void validateInput(ScalingRule rule, float load, int clusterId) {
	    if (rule == null ) throw new BusinessViolationException("Invalid rule");
		if (load < 0.0 ) throw new BusinessViolationException("Invalid load: [" + load + "]");
		if (clusterId < 0) throw new BusinessViolationException("Invalid cluster: [" + clusterId + "]");
	}
	
	public ScalingState applyScalingRule(float load, int clusterId, ScalingRule rule) {
	    int clusterSize = clusterService.getCluster(clusterId).getNumberOfMachines();
		int maxMachines = rule.getMaxNumberOfMachinesPerCluster();
		int minMachines = rule.getMinNumberOfMachinesPerCluster();
		float maxLoad = rule.getMaxLoad();
		float minLoad = rule.getMinLoad();
		LOG.debug ("executeBusinessLogic() Parameters: clusterId = " + clusterId
                + " load = " + load 
                + " maxMachines = "  + maxMachines
                + " minMachines = "  + minMachines
                + " maxLoad = "  + maxLoad
                + " minLoad = "  + minLoad
                + " clusterSize = "  + clusterSize);
		
		ScalingState state = ScalingState.SCALING_DISABLED;
		
		boolean scalingJobActive = true;
		int jobId = rule.getJobId();
		Job job = null;
		
		// If there was a scaling job, check if it is ready
		if (jobId != -1) { 
			job = jobService.getJob(jobId);
			if (job != null){
				int jobStatus = job.getJobStatus();
				if (jobStatus == JobService.CLOUD_JOB_READY)
				    scalingJobActive = false;
			} 	
		}
		else scalingJobActive = false;
		
        boolean allMachinesConfigured = false;
        
        if (!scalingJobActive)
            allMachinesConfigured = machineService.allMachinesConfigured(clusterId);

        LOG.debug("scalingJobReady=" +  scalingJobActive + " allMachinesConfigured=" + allMachinesConfigured);

		if (!rule.isPeriodicScalingOn() || scalingJobActive || !allMachinesConfigured)
			state = ScalingState.SCALING_SKIPPED;
		else if (load >= maxLoad && clusterSize < maxMachines) 
	        state =  ScalingState.SCALING_OUT;
	    else if (load <= minLoad && clusterSize > minMachines)
	        state =  ScalingState.SCALING_IN; 
		else if (clusterSize >= maxMachines && load > maxLoad) 
		    state = ScalingState.SCALING_NEEDED_BUT_IMPOSSIBLE;
		else 
		    state = ScalingState.SCALING_NOT_NEEDED;
		
		LOG.debug ("applyScalingRule() return = " + state.name());
		return state;
	}
	
	public void store (ScalingRule scalingRule){
		scalingRuleRepository.store(scalingRule);
	}

	public ScalingRule getRule(int clusterId){
		ScalingRule r = scalingRuleRepository.getRule(clusterId);
		LOG.debug("-------------------------------------------");
		LOG.debug(Integer.toString(r.getClusterSizeOriginal()));

        return scalingRuleRepository.getRule(clusterId);
	}
    
	public void deleteByClusterId(int id){
		scalingRuleRepository.deleteByClusterId(id);
	}

	public Collection<ScalingRule> loadAll(){
		return scalingRuleRepository.loadAll();
	}
	
	public void storeScalingOutParameters(int numberOfMachines, int clusterId){
		scalingRuleRepository.storeStateScheduledScaling(numberOfMachines, clusterId);
	}

	public void storeScalingInParameters(int clusterId){
		scalingRuleRepository.storeStateScheduledUnScaling(clusterId);
	}
	
	public void storeJobId(int clusterId, int jobId){
        scalingRuleRepository.storeJobId(clusterId, jobId);
	}
}