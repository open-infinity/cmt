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

package org.openinfinity.cloud.autoscaler.periodicscaler;


import java.util.List;
import org.apache.log4j.Logger;
import org.openinfinity.cloud.domain.Job;
import org.openinfinity.cloud.service.administrator.JobService;
import org.openinfinity.cloud.service.scaling.ScalingRuleService;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Batch writer.
 * 
 * @author Vedran Bartonicek
 * @version 1.0.0
 * @since 1.0.0
 */
@Component("periodicScalerItemWriter")
public class PeriodicScalerItemWriter implements ItemWriter<Job> {
	private static final Logger LOG = Logger.getLogger(PeriodicScalerItemWriter.class.getName());

	@Autowired
    JobService jobService;		

	@Autowired
	ScalingRuleService scalingRuleService;
	
	@Override
	public void write(List<? extends Job> items) throws Exception {
	    for (Job item : items) {
	        LOG.debug("Enter write");
	        // FIXME: parsing of cluster id 
	        int jobId = jobService.addJob(item);        
	        String[] services = item.getServices().split(",");
	        LOG.debug("services = " + services);
	        int clusterId = Integer.parseInt(services[0]);
	        scalingRuleService.storeJobId(clusterId, jobId); 
        }
    }
}
	  
