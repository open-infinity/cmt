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

import org.openinfinity.core.annotation.Log;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * Scheduled Autoscaler batch job launcher.
 * 
 * @author Vedran Bartonicek
 * @version 1.3.0
 * @since 1.2.0
 */
@Component("scheduledAutoscalerLauncher")
public class ScheduledAutoscalerLauncher {
	@Autowired
	@Qualifier("scheduledJob")
	private Job job;
	
	@Autowired
	private JobLauncher jobLauncher;
		
	@Log
	public void launch() throws Exception {
		jobLauncher.run(job, new JobParametersBuilder().addLong("s_time", System.currentTimeMillis()).toJobParameters());
	}
}
