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

import org.openinfinity.cloud.autoscaler.common.AutoscalerItemProcessor;
import org.openinfinity.cloud.domain.Cluster;
import org.openinfinity.cloud.domain.Job;
import org.openinfinity.cloud.domain.ScalingRule;
import org.openinfinity.cloud.service.scaling.Enumerations;
import org.springframework.batch.item.ItemProcessor;
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
public class ScheduledAutoscalerItemProcessor extends AutoscalerItemProcessor implements ItemProcessor<ScalingRule, Job> {

	@Value("${sampling.offset.end}")
    int offsetEnd;
    
    @Value("${sampling.offset.start}")
    int offsetStart;

	@Override
	public Job process(ScalingRule scalingRule) throws Exception {

		long now = System.currentTimeMillis();
		Timestamp samplingPeriodStart = new Timestamp(now - offsetStart );
		Timestamp samplingPeriodEnd = new Timestamp(now + offsetEnd); 
		Cluster cluster = clusterService.getCluster(scalingRule.getClusterId());
        Job job = null;
        Enumerations.ScalingState state = scalingRuleService.applyScalingRule(samplingPeriodStart, samplingPeriodEnd, cluster, scalingRule);

        switch (state) {
            case SCALE_OUT:
                return createJob(cluster, scalingRule.getClusterSizeNew());
            case SCALE_IN:
                return createJob(cluster, scalingRule.getClusterSizeOriginal());
            case SCALING_OUT_IMPOSSIBLE:
            case SCALING_ONGOING:
            case SCALING_NOT_REQUIRED:
            default:
                break;
        }
		return job;
	}

}
