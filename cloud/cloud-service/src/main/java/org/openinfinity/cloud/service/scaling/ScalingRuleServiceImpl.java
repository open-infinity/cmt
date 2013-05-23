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
import java.util.Date;
import java.sql.Timestamp;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.openinfinity.cloud.domain.Job;
import org.openinfinity.cloud.domain.ScalingRule;
import org.openinfinity.cloud.service.scaling.Enumerations.ScalingBalance;
import org.openinfinity.cloud.domain.repository.scaling.ScalingRuleRepository;
import org.openinfinity.cloud.service.administrator.JobService;
import org.openinfinity.cloud.service.administrator.MachineService;
import org.openinfinity.core.exception.BusinessViolationException;
import org.openinfinity.core.util.ExceptionUtil;
import org.openinfinity.core.util.IOUtil;

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
	JobService jobService;
	
	@Override
	public ScalingBalance calculateScalingBalance(float cpuLoad, int clusterId) {
		validateInput(cpuLoad, clusterId);
		try{
			ScalingRule scalingRule = scalingRuleRepository.loadByClusterId(clusterId);
			return applyScalingRule(cpuLoad, clusterId, scalingRule);
		}
		catch(EmptyResultDataAccessException dae){
			LOG.warn("Scaling rule not defined for the cluster");
			return ScalingBalance.RULE_NOT_DEFINED;
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
	
	public ScalingBalance applyScalingRule(float load, int clusterId, ScalingRule rule) {
		int clusterSize = machineService.getMachinesInCluster(clusterId).size();
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
		
		ScalingBalance scalingBalance = ScalingBalance.RULE_NOT_DEFINED;
		
		boolean isScalingOngoing = false;
		int jobId = rule.getJobId();
		Job job = null;
		// Initially blank rule has jobId == -1
		if (jobId != -1) { 
			job = jobService.getJob(jobId);
			if (job != null){
				int jobStatus = job.getJobStatus();
				if (jobStatus == JobService.CLOUD_JOB_CREATED || jobStatus == JobService.CLOUD_JOB_STARTED)
					isScalingOngoing = true;
			} 	
		}
		// TODO: it wound be better to check if the machine is running, but
		// that too woul dnot promise that the services are actualy up.
		// So again it would be needed to include some delay after the 
		// new machine comes into "running" state
		
		if (!rule.isPeriodicScalingOn() || isScalingOngoing) 
			LOG.debug("Cluster" +  clusterId + "can't be scaled");
		
		// Range OK
		else if (clusterSize < maxMachines && clusterSize > minMachines ) {
			
		    // Try scaling out if possible
		    if (maxLoad <= load ) {
		        // There was no scaling ever done for this cluster, scale out.
		        if (job == null) {
		            scalingBalance =  ScalingBalance.SCALE_OUT;
		        }
		        // There was a scaling job. Check when it ended.
		        // If it ended long enough time ago, scale out.
		        else{
		            
		            // FIXME: repeating code -> function
		            Timestamp jobEndTimestamp = job.getEndTime();
                    Date now = new Date();
                    Timestamp ts = new Timestamp(now.getTime() - 500);
                    LOG.debug ("ts.getTime()=" + ts.getTime() + 
                               " jobEndTimestamp.getTime= " + jobEndTimestamp.getTime() );
                    
                    if (ts.after(jobEndTimestamp))
                        scalingBalance =  ScalingBalance.SCALE_OUT; 
                    else
                        scalingBalance =  ScalingBalance.HARMONIZED; 
		        }         
		    }
		    
            // Try scaling in if possible
			else if (minLoad > load){
			    if (job == null) 
			        scalingBalance =  ScalingBalance.SCALE_IN;
			    else{
			       
			        // get time - done
			        Timestamp jobEndTimestamp = job.getEndTime();
			        Date now = new Date();
			        Timestamp ts = new Timestamp(now.getTime() - 30);
			        LOG.debug ("ts.getTime()=" + ts.getTime() +  
			                   " jobEndTimestamp.getTime= " + jobEndTimestamp.getTime() );
			        
			        if (ts.after(jobEndTimestamp))
			            scalingBalance =  ScalingBalance.SCALE_IN;
			        else 
			            scalingBalance = ScalingBalance.HARMONIZED;
			    }		    
			    
			}
		    
		    // Load in range, do nothing
			else 
			    scalingBalance = ScalingBalance.HARMONIZED;
		} 
		else if (clusterSize == maxMachines && load > maxLoad) 
			scalingBalance = ScalingBalance.SYSTEM_DISASTER_PANIC;
		else scalingBalance = ScalingBalance.HARMONIZED;
		
		LOG.debug ("applyScalingRule() return = " + scalingBalance.name());
		return scalingBalance;
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