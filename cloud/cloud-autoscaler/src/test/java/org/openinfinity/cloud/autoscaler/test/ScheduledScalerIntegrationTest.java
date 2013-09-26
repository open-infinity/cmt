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

package org.openinfinity.cloud.autoscaler.test;

import java.sql.Timestamp;

import javax.sql.DataSource;

import junit.framework.Assert;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.openinfinity.cloud.autoscaler.test.util.DatabaseUtils;
import org.openinfinity.cloud.autoscaler.test.util.HttpGateway;
import org.openinfinity.cloud.domain.ScalingRule;
import org.openinfinity.cloud.service.administrator.ClusterService;
import org.openinfinity.cloud.service.administrator.JobService;
import org.openinfinity.cloud.service.scaling.ScalingRuleService;

/**
 * Functional tests for Periodic scaler.
 * 
 * @author Vedran Bartonicek
 * @version 1.3.0
 * @since 1.2.0
 */

/*
 * E2E Spring batch testing for Autoscaler.
 * 
 */
@ContextConfiguration(locations={"classpath*:META-INF/spring/t1-context.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class ScheduledScalerIntegrationTest {
	private static final int CLUSTER_ID = 1;    
	private static final String MOCK_SERVER_PATH = "src/test/python/mock-rrd-server.py";
	private static final int AUTOSCALER_PERIOD_MS = 10000;
	private static final String URL_LOAD_LOW = "http://127.0.0.1:8181/test/load/low";
	private static final String URL_LOAD_HIGH = "http://127.0.0.1:8181/test/load/high";
    private static final String URL_LOAD_MEDIUM = "http://127.0.0.1:8181/test/load/medium";
    private static final int JOB_UNDEFINED = -1;
    
   	private static int jobId = -1;

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;
    
	@Autowired
	@Qualifier("cloudDataSource")
	DataSource dataSource;
	
	@Autowired
	@Qualifier("clusterService")
	ClusterService clusterService;
	
	@Autowired
    @Qualifier("scalingRuleService")
	ScalingRuleService scalingRuleService;
	
    @Autowired
    @Qualifier("jobService")
    JobService jobService;

	@Test
	public void scaleOut() throws Exception {
        long now = System.currentTimeMillis();
	    Timestamp from = new Timestamp(now);
	    Timestamp to = new Timestamp(now + 3600000);
		DatabaseUtils.updateTestDatabase(DatabaseUtils.initDataSet(this,
				DatabaseUtils.SQL_SCALE_OUT, from, to), dataSource);

		JobExecution jobExecution = jobLauncherTestUtils.launchJob();
        Assert.assertEquals(jobExecution.getStatus(), BatchStatus.COMPLETED);
		ScalingRule scalingRule = scalingRuleService.getRule(CLUSTER_ID);
		Assert.assertEquals(2, scalingRule.getClusterSizeOriginal());
		Assert.assertEquals(2, scalingRule.getScheduledScalingState());
		Assert.assertEquals("1,5", jobService.getJob(++jobId).getServices());
	}
	
	@Test
	public void scaleIn() throws Exception {
        long now = System.currentTimeMillis();
	    Timestamp from = new Timestamp(now - 3600000);
	    Timestamp to = new Timestamp(now);
	    DatabaseUtils.updateTestDatabase(DatabaseUtils.initDataSet(this,
				DatabaseUtils.SQL_SCALE_IN, from, to), dataSource);
	    
	    JobExecution jobExecution = jobLauncherTestUtils.launchJob();
        Assert.assertEquals(jobExecution.getStatus(), BatchStatus.COMPLETED);
	
        ScalingRule scalingRule = scalingRuleService.getRule(CLUSTER_ID);
		Assert.assertEquals(0, scalingRuleService.getRule(CLUSTER_ID).getScheduledScalingState());
        Assert.assertEquals("1,1", jobService.getJob(++jobId).getServices()); 
	}
	
}

