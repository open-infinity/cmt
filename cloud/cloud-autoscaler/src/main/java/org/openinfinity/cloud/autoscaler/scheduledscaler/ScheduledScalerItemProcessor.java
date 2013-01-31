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
import org.springframework.stereotype.Component;

/**
 * Batch processor for verifying the scaling rules.
 * 
 * @author Vedran Bartonicek
 * @version 1.0.0
 * @since 1.0.0
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

	@Override
	public Job process(ScalingRule scalingRule) throws Exception {
		Job ret = null;
		Timestamp periodFrom = scalingRule.getPeriodFrom();
		Timestamp periodTo = scalingRule.getPeriodTo();
		long now = System.currentTimeMillis();	
		
		// TODO make a property for test / normal case
		Timestamp windowStart = new Timestamp(now - 90000 );
		Timestamp windowEnd = new Timestamp(now + 60000); 
		
		//Timestamp windowStart = new Timestamp(now - 1300 );
		//Timestamp windowEnd = new Timestamp(now + 1000); 
	
		LOG.debug("periodFrom = " + periodFrom.toString());
		LOG.debug("periodTo = " + periodTo.toString());
		LOG.debug("windowStart = " + windowStart.toString());
		LOG.debug("windowEnd = " + windowEnd.toString());
		LOG.debug("state = " + scalingRule.getScheduledScalingState());
		LOG.debug("clusterId = " + scalingRule.getClusterId());
		
		//TODO: DB reading should happen in ItemReader, so this below should be redesigned to gain optimal performance.
		Cluster cluster = clusterService.getCluster(scalingRule.getClusterId());
		if (windowStart.before(periodFrom) && windowEnd.after(periodFrom) && scalingRule.getScheduledScalingState() == 1){
			ret = createJob(scalingRule, cluster, scalingRule.getClusterSizeNew());
			scalingRuleService.storeScalingOutParameters(cluster.getNumberOfMachines(), scalingRule.getClusterId());
		}	
		// Two cases are covered here: periodEnd in window, and both periodStart and periodEnd in window. 
		// In both cases state goes to idle, and flag for scheduling gets cleared
		// so batch job doesn't select it any more.
		else if ((windowStart.before(periodTo) && windowEnd.after(periodTo) && windowStart.after(periodFrom) && scalingRule.getScheduledScalingState() == 2)
				  || (windowStart.before(periodFrom) && windowEnd.after(periodTo))){
			ret = createJob(scalingRule, cluster, scalingRule.getClusterSizeOriginal());
			scalingRuleService.storeScalingInParameters(scalingRule.getClusterId());
			}
		else{
			// border cases fit here e.g. TODO handle cases when windowsStart==periodTo etc.
		}
		return ret;
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