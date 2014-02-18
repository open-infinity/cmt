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
import org.openinfinity.cloud.autoscaler.periodicautoscaler.ClusterProcessingState;
import org.openinfinity.cloud.domain.Cluster;
import org.openinfinity.cloud.domain.Job;
import org.openinfinity.cloud.domain.ScalingRule;
import org.openinfinity.cloud.service.scaling.Enumerations.ScalingStatus;
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

    public int getOffsetEnd() {
        return offsetEnd;
    }

    public int getOffsetStart() {
        return offsetStart;
    }

	@Override
	public Job process(ScalingRule rule) throws Exception {

		long now = System.currentTimeMillis();
		Timestamp samplingPeriodStart = new Timestamp(now - offsetStart );
		Timestamp samplingPeriodEnd = new Timestamp(now + offsetEnd);

        clusterId = rule.getClusterId();
        Cluster cluster = clusterService.getCluster(clusterId);
        Job job = null;
        ClusterProcessingState clusterProcessingState = initializeFailures();

        ScalingStatus status = scalingRuleService.applyScalingRule(samplingPeriodStart, samplingPeriodEnd, cluster, rule);
        switch (status) {
            case SCALING_OUT_REQUIRED:
                return createJob(cluster, rule.getClusterSizeNew());
            case SCALING_IN_REQUIRED:
                return createJob(cluster, rule.getClusterSizeOriginal());
            case SCALING_IMPOSSIBLE_SCALING_RULE_LIMIT:
                break;
            case SCALING_IMPOSSIBLE_SCALING_ALREADY_ONGOING:
                break;
            case SCALING_NOT_REQUIRED:
                break;
            case SCALING_IMPOSSIBLE_INVALID_RULE:
                break;
            case SCALING_IMPOSSIBLE_CLUSTER_ERROR:
                notifyPreviousScalingFailed(clusterProcessingState, cluster, rule);
                break;
            case SCALING_IMPOSSIBLE_MACHINE_CONFIGURATION_ERROR:
                notifyMachineConfigurationError(clusterProcessingState, cluster, rule);
                break;
            default:
                break;
        }
		return job;
	}

}
