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

package org.openinfinity.cloud.autoscaler.test.periodicscaler;

import static org.junit.Assert.assertNotNull;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openinfinity.cloud.autoscaler.test.scheduledscaler.SmokeTests;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Batch configuration integration tests.
 * 
 * @author Ilkka Leinonen
 * @author Vedran Bartonicek
 * @version 1.0.0
 * @since 1.0.0
 */


//@ContextConfiguration(locations={"classpath*:META-INF/spring/test-periodic-scaler-context.xml"})
//@RunWith(SpringJUnit4ClassRunner.class)
public class CapacityUsageJobConfigurationTests {
	/*
	private static final Logger LOG = Logger.getLogger(CapacityUsageJobConfigurationTests.class.getName());

	
	@Autowired
	private JobLauncher jobLauncher;

	@Autowired
	@Qualifier("periodicJob")
	private Job job;
	
	@Test
	public void testSimpleProperties() throws Exception {
		assertNotNull(jobLauncher);
	}
	
	@Test
	public void testLaunchJob() throws Exception {
		//jobLauncher.run(job, new JobParameters());
	}
	
	@Test
	public void have_a_napp() throws Exception {
		// Sleep so that task scheduler can run in the test context
		//Thread.sleep(1000);
		assertNotNull(job);
	}
	*/
}
