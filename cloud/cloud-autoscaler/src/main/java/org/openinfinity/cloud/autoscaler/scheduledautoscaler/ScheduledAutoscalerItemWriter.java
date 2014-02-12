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

package org.openinfinity.cloud.autoscaler.scheduledautoscaler;

import org.openinfinity.cloud.domain.Job;
import org.openinfinity.cloud.service.administrator.JobService;
import org.openinfinity.cloud.service.scaling.ScalingRuleService;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Batch writer.
 *
 * @author Vedran Bartonicek
 * @version 1.3.0
 * @since 1.2.0
 */
@Component("scheduledAutoscalerItemWriter")
public class ScheduledAutoscalerItemWriter implements ItemWriter<Job> {

    @Autowired
    JobService jobService;

    @Autowired
    ScalingRuleService scalingRuleService;

    @Override
    public void write(List<? extends Job> items) throws Exception {
        for (Job item : items) {
            int jobId = jobService.addJob(item);
            String[] services = item.getServices().split(",");
            int clusterId = Integer.parseInt(services[0]);
            scalingRuleService.storeJobId(clusterId, jobId);
        }
    }
}

