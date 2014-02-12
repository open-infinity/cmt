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

        // Get scheduled scaling period
	    Timestamp periodFrom = scalingRule.getPeriodFrom();
		Timestamp periodTo = scalingRule.getPeriodTo();

        // Get sampling period
		long now = System.currentTimeMillis();	
		Timestamp samplingPeriodStart = new Timestamp(now - offsetStart );
		Timestamp samplingPeriodEnd = new Timestamp(now + offsetEnd); 
	
		Cluster cluster = clusterService.getCluster(scalingRule.getClusterId());

        Job job;
        // TODO : move this into Scaling service
        // Invalid period defined in scaling_rule_tbl;
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
		return job;
	}
	 /*
	 private Job createJob(Cluster cluster, int machinesGrowth) {
        Instance instance = instanceService.getInstance(cluster.getInstanceId());
		return new Job("scale_cluster",
			cluster.getInstanceId(),
			instance.getCloudType(),
			JobService.CLOUD_JOB_CREATED,
			instance.getZone(),
			Integer.toString(cluster.getId()),
			cluster.getNumberOfMachines() + machinesGrowth);
	}
	  */
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
