/*
 * Copyright (c) 2012 the original author or authors.
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
import org.springframework.dao.EmptyResultDataAccessException;
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
	private ScalingRuleRepository scalingRuleRepository;
	
	@Autowired
	MachineService machineService;
	
	@Autowired
    ClusterService clusterService;
	
	@Autowired
	JobService jobService;
	
	@Override
	public ScalingState calculateScalingState(float load, int clusterId) {
		validateInput(load, clusterId);
		try{
			ScalingRule scalingRule = scalingRuleRepository.loadByClusterId(clusterId);
			return applyScalingRule(load, clusterId, scalingRule);
		}
		catch(EmptyResultDataAccessException dae){
			LOG.warn("Scaling rule not defined for the cluster");
			return ScalingState.RULE_NOT_DEFINED;
		}
	}

	public void validateInput(float load, int clusterId) {
		if (load < 0.0 ) {
			throw new BusinessViolationException("Load is not valid: [" + load + "]");
		}
		if (clusterId < 0) {
			throw new BusinessViolationException("Cluster id is not valid: [" + clusterId + "]");
		}
	}
	
	public ScalingState applyScalingRule(float load, int clusterId, ScalingRule rule) {
		//int clusterSize = machineService.getMachinesInClusterExceptType(clusterId, "loadbalancer").size();
	    int clusterSize = clusterService.getCluster(clusterId).getNumberOfMachines();
		int maxMachines = rule.getMaxNumberOfMachinesPerCluster();
		int minMachines = rule.getMinNumberOfMachinesPerCluster();
		float maxLoad = rule.getMaxLoad();
		float minLoad = rule.getMinLoad();
        float a = 0.01f;
		LOG.debug ("executeBusinessLogic() Parameters: clusterId = " + clusterId
                + " load = " + load 
                + " maxMachines = "  + maxMachines
                + " minMachines = "  + minMachines
                + " maxLoad = "  + maxLoad
                + " minLoad = "  + minLoad
                + " clusterSize = "  + clusterSize
                + " a = "  + a);
		
		ScalingState scalingState = ScalingState.RULE_NOT_DEFINED;
		
		boolean scalingJobReady = false;
		int jobId = rule.getJobId();
		Job job = null;
		// Initially blank rule has jobId == -1
		if (jobId != -1) { 
			job = jobService.getJob(jobId);
			if (job != null){
				int jobStatus = job.getJobStatus();
				if (jobStatus == JobService.CLOUD_JOB_READY)
				    scalingJobReady = true;
			} 	
		}
		
        boolean allMachinesConfigured = false;
        
        // In case that job is active scaling will be aborted, so no need to check if machines are configured 
		if (scalingJobReady){
		      //allMachinesConfigured = machineService.getMachinesInClusterNotConfigured(clusterId).size() > 0 ? false : true;
              allMachinesConfigured = machineService.allMachinesInClusterConfigured(clusterId);

		}
        LOG.debug("scalingJobReady=" +  scalingJobReady + " allMachinesConfigured=" + allMachinesConfigured);

		if (!rule.isPeriodicScalingOn() || !scalingJobReady || !allMachinesConfigured){ 
			LOG.debug("Cluster " +  clusterId + "can't be scaled");
		    scalingState = ScalingState.SCALING_ONGOING;
	    }
	    // Scaling out check
	    else if (load >= maxLoad && clusterSize < maxMachines) {
/*
	        // There was no scaling ever done for this cluster, scale out.
	        if (job == null) {
	            LOG.debug("--------2");
	            scalingState =  ScalingState.SCALE_OUT;
	        }
	        // There was a scaling job. Check when it ended.
	        // If it ended long enough time ago, scale out.
	        else{
	            LOG.debug("--------3");
	            // FIXME: repeating code -> function
	            Timestamp jobEndTimestamp = job.getEndTime();
                Date now = new Date();
                Timestamp ts = new Timestamp(now.getTime() - 500);
                LOG.debug ("ts.getTime()=" + ts.getTime() + 
                           " jobEndTimestamp.getTime= " + jobEndTimestamp.getTime() );
                
                if (ts.after(jobEndTimestamp)){
                    LOG.debug("--------4");
                    scalingState =  ScalingState.SCALE_OUT; 

                }
                else{
                    LOG.debug("--------5");
                    scalingState =  ScalingState.HARMONIZED; 

                }
	        }         
*/	    
	        LOG.debug("--------SCALE_OUT"); 
	        scalingState =  ScalingState.SCALE_OUT;
	    }
	    
	    
        // Scaling in check
		else if (load <= minLoad && clusterSize > minMachines){
/*
		    if (job == null) {
                LOG.debug("--------6");
                scalingState =  ScalingState.SCALE_IN;


		    }
		    else{
                LOG.debug("--------7");


                // get time - done
		        Timestamp jobEndTimestamp = job.getEndTime();
		        Date now = new Date();
		        Timestamp ts = new Timestamp(now.getTime() - 30);
		        LOG.debug ("ts.getTime()=" + ts.getTime() +  
		                   " jobEndTimestamp.getTime= " + jobEndTimestamp.getTime() );
		        
		        if (ts.after(jobEndTimestamp)){
		            scalingState =  ScalingState.SCALE_IN;
                    LOG.debug("--------8");

		        }
		        else{
		            
                    LOG.debug("--------9");

		            scalingState = ScalingState.HARMONIZED;
		        }

		    }
*/
	        LOG.debug("--------SCALE_IN"); 
		    scalingState =  ScalingState.SCALE_IN;
		    
		}

		//} 
		else if (clusterSize >= maxMachines && load > maxLoad) 
		{
		    // TODO: fire notification of some sort?
			scalingState = ScalingState.SYSTEM_DISASTER_PANIC;
			LOG.debug("--------11"); 
			}
		else {
		    scalingState = ScalingState.HARMONIZED;
		    LOG.debug("--------12"); 
		}
		
		LOG.debug ("applyScalingRule() return = " + scalingState.name());
		return scalingState;
	}
	
	public void store (ScalingRule scalingRule){
		scalingRuleRepository.store(scalingRule);
	}

	public ScalingRule loadByClusterId (int clusterId){
		return scalingRuleRepository.loadByClusterId(clusterId);
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