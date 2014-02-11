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

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openinfinity.cloud.autoscaler.test.util.DatabaseUtils;
import org.openinfinity.cloud.domain.Job;
import org.openinfinity.cloud.domain.ScalingRule;
import org.openinfinity.cloud.service.administrator.JobService;
import org.openinfinity.cloud.service.scaling.ScalingRuleService;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.util.Collection;

/**
 * Functional tests for Periodic Autoscaler.
 * 
 * @author Vedran Bartonicek
 * @version 1.2.2
 * @since 1.2.0
 */
@ContextConfiguration(locations={"classpath*:META-INF/spring/cloud-autoscaler-test-integration-context.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class ScheduledAutoscalerIntegrationTest {

	private final int CLUSTER_ID = 1;

    private static final int TIME_ONE_HOUR = 3600000;

    @Autowired
    @Qualifier("scheduledJobLauncherTestUtils")
    private JobLauncherTestUtils jobLauncherTestUtils;
    
	@Autowired
	@Qualifier("cloudDataSource")
	DataSource dataSource;

	@Autowired
    @Qualifier("scalingRuleService")
	ScalingRuleService scalingRuleService;
	
    @Autowired
    @Qualifier("jobService")
    JobService jobService;

    @Before
    public void clearDatabases(){
        jobService.deleteAll();
    }

	@Test
	public void scaleOut() throws Exception {
        long now = System.currentTimeMillis();
	    Timestamp from = new Timestamp(now);
	    Timestamp to = new Timestamp(now + TIME_ONE_HOUR);
		DatabaseUtils.updateTestDatabase(DatabaseUtils.initDataSet(this, DatabaseUtils.SQL_SCALE_OUT, from, to), dataSource);

		JobExecution jobExecution = jobLauncherTestUtils.launchJob();
        Assert.assertEquals(jobExecution.getStatus(), BatchStatus.COMPLETED);
		ScalingRule scalingRule = scalingRuleService.getRule(CLUSTER_ID);
		Assert.assertEquals(2, scalingRule.getClusterSizeOriginal());
		Assert.assertEquals(2, scalingRule.getScheduledScalingState());
		Assert.assertEquals("1,5", jobService.getJob(getJobId()).getServices());
	}
	
	@Test
	public void scaleIn() throws Exception {
        long now = System.currentTimeMillis();
	    Timestamp from = new Timestamp(now - TIME_ONE_HOUR);
	    Timestamp to = new Timestamp(now);
	    DatabaseUtils.updateTestDatabase(DatabaseUtils.initDataSet(this, DatabaseUtils.SQL_SCALE_IN, from, to), dataSource);
	    
	    JobExecution jobExecution = jobLauncherTestUtils.launchJob();
        Assert.assertEquals(jobExecution.getStatus(), BatchStatus.COMPLETED);
		Assert.assertEquals(2, scalingRuleService.getRule(CLUSTER_ID).getScheduledScalingState());
        Assert.assertEquals("1,1", jobService.getJob(getJobId()).getServices());
    }

    private int getJobId(){
        int jobId = -1;
        Collection<Job> jobs = jobService.getJobs(1,1);
        for (Job j : jobs){
            jobId =  j.getJobId();
        }
        return jobId;
    }
	
}

