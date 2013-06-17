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

import org.apache.log4j.Logger;
import org.openinfinity.cloud.service.healthmonitoring.HealthMonitoringService;
import org.openinfinity.core.annotation.Log;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * Periodic batch job launcher.
 * 
 * @author Vedran Bartonicek
 *
 */

@Component("periodicScalerLauncher")
public class PeriodicScalerLauncher {
	private static final Logger LOG = Logger.getLogger(PeriodicScalerLauncher.class.getName());

	@Autowired
	@Qualifier("periodicJob")
	private Job job;
	
	@Autowired
	private JobLauncher jobLauncher;
	
	@Autowired
	private HealthMonitoringService healthMonitoringService;
		
	@Log
	public void launch() throws Exception {
		JobExecution exec = jobLauncher.run(job, new JobParametersBuilder().addLong("p_time", System.currentTimeMillis()).toJobParameters());
	}
}