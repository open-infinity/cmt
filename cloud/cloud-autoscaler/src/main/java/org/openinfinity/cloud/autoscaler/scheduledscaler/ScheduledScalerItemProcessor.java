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

package org.openinfinity.cloud.autoscaler.scheduledscaler;

import java.sql.Timestamp;

import org.apache.log4j.Logger;
import org.openinfinity.cloud.domain.Cluster;
import org.openinfinity.cloud.domain.Instance;
import org.openinfinity.cloud.domain.Job;
import org.openinfinity.cloud.domain.ScalingRule;
import org.openinfinity.cloud.service.administrator.ClusterService;
import org.openinfinity.cloud.service.administrator.InstanceService;
import org.openinfinity.cloud.service.administrator.JobService;
import org.openinfinity.cloud.service.scaling.ScalingRuleService;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Scheduled scaler batch processor
 * 
 * @author Vedran Bartonicek
 * @version 1.3.0
 * @since 1.2.0
 */
@Component("scheduledScalerItemProcessor")
public class ScheduledScalerItemProcessor implements ItemProcessor<ScalingRule, Job> {
	private static final Logger LOG = Logger.getLogger(ScheduledScalerItemProcessor.class.getName());
	
	@Autowired
	ScalingRuleService scalingRuleService;
	
	@Autowired
	ClusterService clusterService;
	
	@Autowired
	InstanceService instanceService;
	
	@Value("${deltaPlus}")
    int deltaPlus;
    
    @Value("${deltaMinus}")
    int deltaMinus;
    
	@Override
	public Job process(ScalingRule scalingRule) throws Exception {
	    Timestamp periodFrom = scalingRule.getPeriodFrom();
		Timestamp periodTo = scalingRule.getPeriodTo();
		long now = System.currentTimeMillis();	
		Timestamp windowStart = new Timestamp(now - deltaMinus );
		Timestamp windowEnd = new Timestamp(now + deltaPlus); 
	
		Cluster cluster = clusterService.getCluster(scalingRule.getClusterId());
		Job job;
		
		if (periodTo.before(periodFrom) || periodTo.equals(periodFrom)){
			job = null;
		} else if (windowStart.before(periodFrom) && windowEnd.after(periodFrom) && scalingRule.getScheduledScalingState() == 1){
			job = createJob(scalingRule, cluster, scalingRule.getClusterSizeNew());
			scalingRuleService.storeScalingOutParameters(cluster.getNumberOfMachines(), scalingRule.getClusterId());
		
		// Scheduled scaling period is over, create a job to return to original size
		// Update scaling rule, state -> idle
		} else if ((windowStart.before(periodTo) && windowEnd.after(periodTo)
				&& windowStart.after(periodFrom) && scalingRule.getScheduledScalingState() == 2)
				|| (windowStart.before(periodFrom) && windowEnd.after(periodTo))) {
			job = createJob(scalingRule, cluster, scalingRule.getClusterSizeOriginal());
			scalingRuleService.storeScalingInParameters(scalingRule
					.getClusterId());
		}
		else {
			job = null;
		}
		return job;
	}
	
	private Job createJob(ScalingRule scalingRule, Cluster cluster, int scaledClusterSize){
		int instanceId = cluster.getInstanceId();
		Instance instance = instanceService.getInstance(instanceId);
		return new Job("scale_cluster",
			instanceId,
			instance.getCloudType(),
			JobService.CLOUD_JOB_CREATED,
			instance.getZone(),
			Integer.toString(scalingRule.getClusterId()),
			scaledClusterSize);
	}
}