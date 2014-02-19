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
import org.openinfinity.cloud.domain.Job;
import org.openinfinity.cloud.domain.ScalingRule;
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
	public Job process(ScalingRule scalingRule) throws Exception {

        rule = scalingRule;
        cluster = clusterService.getCluster(rule.getClusterId());

        long now = System.currentTimeMillis();
		Timestamp samplingPeriodStart = new Timestamp(now - offsetStart);
		Timestamp samplingPeriodEnd = new Timestamp(now + offsetEnd);

        ClusterProcessingState clusterState = getClusterState();
        Job job = handleScalingStatus(clusterState, rule, scalingRuleService.applyScalingRule(samplingPeriodStart, samplingPeriodEnd, clusterService.getCluster(rule.getClusterId()), rule));
        processingStatusMap.put(cluster.getId(), clusterState);

        return job;
	}

    protected int getScaleOutSize(){
        return rule.getClusterSizeNew();
    }

    protected int getScaleInSize(){
        return rule.getClusterSizeOriginal();
    }

}
