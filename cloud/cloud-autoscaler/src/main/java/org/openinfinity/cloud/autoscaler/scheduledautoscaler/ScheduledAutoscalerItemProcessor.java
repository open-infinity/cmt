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

package org.openinfinity.cloud.autoscaler.scheduledautoscaler;

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

import java.sql.Timestamp;

/**
 * Scheduled Autoscaler batch processor
 * 
 * @author Vedran Bartonicek
 * @version 1.3.0
 * @since 1.2.0
 */
@Component("scheduledAutoscalerItemProcessor")
public class ScheduledAutoscalerItemProcessor implements ItemProcessor<ScalingRule, Job> {
	private static final Logger LOG = Logger.getLogger(ScheduledAutoscalerItemProcessor.class.getName());
	
	@Autowired
	ScalingRuleService scalingRuleService;
	
	@Autowired
	ClusterService clusterService;
	
	@Autowired
	InstanceService instanceService;
	
	@Value("${sampling.offset.end}")
    int offsetEnd;
    
    @Value("${sampling.offset.start}")
    int offsetStart;
    
	@Override
	public Job process(ScalingRule scalingRule) throws Exception {
	    Timestamp periodFrom = scalingRule.getPeriodFrom();
		Timestamp periodTo = scalingRule.getPeriodTo();
		long now = System.currentTimeMillis();	
		Timestamp samplingPeriodStart = new Timestamp(now - offsetStart );
		Timestamp samplingPeriodEnd = new Timestamp(now + offsetEnd); 
	
	    LOG.debug(Integer.toString(scalingRule.getClusterId()));
   	    LOG.debug("state="+scalingRule.getScheduledScalingState());
   	    LOG.debug("now=" + (new Timestamp(now)).toString());
	    LOG.debug("offsetStart=" + Integer.toString(offsetStart));
	    LOG.debug("offsetEnd=" + Integer.toString(offsetEnd));

	    LOG.debug("samplingPeriodStart=" + samplingPeriodStart.toString());
  	    LOG.debug("samplingPeriodEnd=" + samplingPeriodEnd.toString());
   	    LOG.debug("periodFrom=" + periodFrom.toString());
  	    LOG.debug("periodTo=" + periodTo.toString());

		Cluster cluster = clusterService.getCluster(scalingRule.getClusterId());
		Job job;
		
		if (periodTo.before(periodFrom) || periodTo.equals(periodFrom)){
			job = null;

			
		} else if (samplingPeriodStart.before(periodFrom) && samplingPeriodEnd.after(periodFrom) && scalingRule.getScheduledScalingState() == 1){
			job = createJob(scalingRule, cluster, scalingRule.getClusterSizeNew());
			scalingRuleService.storeScalingOutParameters(cluster.getNumberOfMachines(), scalingRule.getClusterId());
		} else if ((samplingPeriodStart.before(periodTo) && samplingPeriodEnd.after(periodTo)
				&& samplingPeriodStart.after(periodFrom) && scalingRule.getScheduledScalingState() == 0)
				|| (samplingPeriodStart.before(periodFrom) && samplingPeriodEnd.after(periodTo))) {
			job = createJob(scalingRule, cluster, scalingRule.getClusterSizeOriginal());
			scalingRuleService.storeScalingInParameters(scalingRule.getClusterId());			
		} else {
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
