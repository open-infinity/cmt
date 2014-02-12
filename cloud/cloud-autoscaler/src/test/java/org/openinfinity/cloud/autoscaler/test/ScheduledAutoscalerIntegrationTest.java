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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openinfinity.cloud.autoscaler.test.util.DatabaseUtils;
import org.openinfinity.cloud.domain.Cluster;
import org.openinfinity.cloud.domain.Instance;
import org.openinfinity.cloud.domain.Job;
import org.openinfinity.cloud.domain.ScalingRule;
import org.openinfinity.cloud.service.administrator.ClusterService;
import org.openinfinity.cloud.service.administrator.InstanceService;
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

/**
 * Integration tests for Scheduled Autoscaler.
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

    @Autowired
    ClusterService clusterService;

    @Autowired
    InstanceService instanceService;

    @Before
    public void clearDatabases(){
        jobService.deleteAll();
    }

    /**
     * Test case for scaling out
     *
     * Given that instance, cluster and machines exists,
     * and scaling rule for cluster is defined,
     * and automatic (scheduled) scaling is turned on,
     * and scaling is scheduled for ${now},
     * When batch job executes,
     * Then batch job status is BatchStatus.COMPLETED,
     * and new (worker)job was created so that cluster size would be increased by one machine.
     *
     * @throws Exception
     */
	@Test
	public void scaleOut() throws Exception {

        // Given
        long now = System.currentTimeMillis();
	    Timestamp from = new Timestamp(now);
	    Timestamp to = new Timestamp(now + TIME_ONE_HOUR);
		DatabaseUtils.updateTestDatabase(DatabaseUtils.initDataSet(this, DatabaseUtils.SQL_SCALE_OUT, from, to), dataSource);

        // When
		JobExecution jobExecution = jobLauncherTestUtils.launchJob();

        // Then
        Assert.assertEquals(jobExecution.getStatus(), BatchStatus.COMPLETED);
		ScalingRule scalingRule = scalingRuleService.getRule(CLUSTER_ID);
		Assert.assertEquals(2, scalingRule.getClusterSizeOriginal());
		Assert.assertEquals(2, scalingRule.getScheduledScalingState());
        Assert.assertEquals("1,5", jobService.getJob(jobService.getNewest().getJobId()).getServices());

    }
    /**
     * Test case for scaling in
     *
     * Given that instance, cluster and machines exists,
     * and scaling rule for cluster is defined,
     * and automatic (scheduled) scaling is turned on,
     * and scaling period expires ${now},
     * When batch job executes,
     * Then batch job status is BatchStatus.COMPLETED,
     * and new (worker)job was created so that cluster size would be decreased by one machine.
     *
     * @throws Exception
     */
	@Test
	public void scaleIn() throws Exception {

        // Given
        long now = System.currentTimeMillis();
	    Timestamp from = new Timestamp(now - TIME_ONE_HOUR);
	    Timestamp to = new Timestamp(now);
	    DatabaseUtils.updateTestDatabase(DatabaseUtils.initDataSet(this, DatabaseUtils.SQL_SCALE_IN, from, to), dataSource);

        // When
	    JobExecution jobExecution = jobLauncherTestUtils.launchJob();

        // Then
        Assert.assertEquals(jobExecution.getStatus(), BatchStatus.COMPLETED);
		Assert.assertEquals(2, scalingRuleService.getRule(CLUSTER_ID).getScheduledScalingState());
        Assert.assertEquals("1,1", jobService.getJob(jobService.getNewest().getJobId()).getServices());
    }

    /**
     * Test case for case when scaling is not required
     *
     * Given that instance, cluster and machines exists,
     * and scaling rule for cluster is defined,
     * and automatic (scheduled) scaling is turned off,
     * and scaling is scheduled for ${now},
     * When batch job executes,
     * Then batch job status is BatchStatus.COMPLETED,
     * and new (worker)job was not created and
     * scaling rule parameters were not changed.
     *
     * @throws Exception
     */
    @Test
    public void scalingTurnedOff() throws Exception {

        // Given
        long now = System.currentTimeMillis();
        Timestamp from = new Timestamp(now);
        Timestamp to = new Timestamp(now + TIME_ONE_HOUR);
        DatabaseUtils.updateTestDatabase(DatabaseUtils.initDataSet(this, DatabaseUtils.SQL_SCALE_OUT, from, to), dataSource);
        ScalingRule sr = scalingRuleService.getRule(CLUSTER_ID);
        sr.setScheduledScalingOn(false);
        scalingRuleService.store(sr);
        Assert.assertEquals(false, scalingRuleService.getRule(CLUSTER_ID).isScheduledScalingOn());
        int lastCreatedJobId  = createJob(); // Create one job in db, and later check that no new jobs were created

        // When
        JobExecution jobExecution = jobLauncherTestUtils.launchJob();

        // Then
        Assert.assertEquals(jobExecution.getStatus(), BatchStatus.COMPLETED);
        ScalingRule scalingRule = scalingRuleService.getRule(CLUSTER_ID);
        Assert.assertEquals(6, scalingRule.getClusterSizeOriginal());
        Assert.assertEquals(5, scalingRule.getClusterSizeNew());
        Assert.assertEquals(1, scalingRule.getScheduledScalingState());
        Assert.assertEquals(-1, scalingRule.getJobId());
        Assert.assertEquals(lastCreatedJobId, jobService.getNewest().getJobId());
    }

    /**
     * Test case for case when scaling is scheduled for the future
     *
     * Given that instance, cluster and machines exists,
     * and scaling rule for cluster is defined,
     * and automatic (scheduled) scaling is turned on,
     * and scaling is scheduled for the future,
     * When batch job executes,
     * Then batch job status is BatchStatus.COMPLETED,
     * and new (worker)job was not created and
     * scaling rule parameters were not changed.
     *
     * @throws Exception
     */
    @Test
    public void scalingScheduledForFuture() throws Exception {

        // Given
        long now = System.currentTimeMillis();
        Timestamp from = new Timestamp(now + 9 * TIME_ONE_HOUR);
        Timestamp to = new Timestamp(now + 10 * TIME_ONE_HOUR);
        DatabaseUtils.updateTestDatabase(DatabaseUtils.initDataSet(this, DatabaseUtils.SQL_SCALE_OUT, from, to), dataSource);
        ScalingRule sr = scalingRuleService.getRule(CLUSTER_ID);
        sr.setScheduledScalingOn(false);
        scalingRuleService.store(sr);
        Assert.assertEquals(false, scalingRuleService.getRule(CLUSTER_ID).isScheduledScalingOn());
        int lastCreatedJobId  = createJob(); // Create one job in db, and later check that no new jobs were created

        // When
        JobExecution jobExecution = jobLauncherTestUtils.launchJob();

        // Then
        Assert.assertEquals(jobExecution.getStatus(), BatchStatus.COMPLETED);
        ScalingRule scalingRule = scalingRuleService.getRule(CLUSTER_ID);
        Assert.assertEquals(6, scalingRule.getClusterSizeOriginal());
        Assert.assertEquals(5, scalingRule.getClusterSizeNew());
        Assert.assertEquals(1, scalingRule.getScheduledScalingState());
        Assert.assertEquals(-1, scalingRule.getJobId());
        Assert.assertEquals(lastCreatedJobId, jobService.getNewest().getJobId());
    }

    /**
     * Test case for case when scaling was scheduled in the past
     *
     * Given that instance, cluster and machines exists,
     * and scaling rule for cluster is defined,
     * and automatic (scheduled) scaling is turned on,
     * and scaling was scheduled in the past,
     * When batch job executes,
     * Then batch job status is BatchStatus.COMPLETED,
     * and new (worker)job was not created and
     * scaling rule parameters were not changed.
     *
     * @throws Exception
     */
    @Test
    public void scalingScheduledInThePast() throws Exception {

        // Given
        long now = System.currentTimeMillis();
        Timestamp from = new Timestamp(now - 9 * TIME_ONE_HOUR);
        Timestamp to = new Timestamp(now - 8 * TIME_ONE_HOUR);
        DatabaseUtils.updateTestDatabase(DatabaseUtils.initDataSet(this, DatabaseUtils.SQL_SCALE_OUT, from, to), dataSource);
        ScalingRule sr = scalingRuleService.getRule(CLUSTER_ID);
        sr.setScheduledScalingOn(false);
        scalingRuleService.store(sr);
        Assert.assertEquals(false, scalingRuleService.getRule(CLUSTER_ID).isScheduledScalingOn());
        int lastCreatedJobId  = createJob(); // Create one job in db, and later check that no new jobs were created

        // When
        JobExecution jobExecution = jobLauncherTestUtils.launchJob();

        // Then
        Assert.assertEquals(jobExecution.getStatus(), BatchStatus.COMPLETED);
        ScalingRule scalingRule = scalingRuleService.getRule(CLUSTER_ID);
        Assert.assertEquals(6, scalingRule.getClusterSizeOriginal());
        Assert.assertEquals(5, scalingRule.getClusterSizeNew());
        Assert.assertEquals(1, scalingRule.getScheduledScalingState());
        Assert.assertEquals(-1, scalingRule.getJobId());
        Assert.assertEquals(lastCreatedJobId, jobService.getNewest().getJobId());
    }

    private int createJob(){
        Cluster cluster = clusterService.getCluster(CLUSTER_ID);
        Instance instance = instanceService.getInstance(cluster.getInstanceId());
        jobService.addJob(new Job("scale_cluster", 1, instance.getCloudType(), JobService.CLOUD_JOB_CREATED, instance.getZone(),
                Integer.toString(55), 55));
        return jobService.getNewest().getJobId();
    }

}

