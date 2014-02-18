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

import org.dbunit.dataset.DataSetException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
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
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
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

    private long now;

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
    public void initialize(){
        jobService.deleteAll();
        now = System.currentTimeMillis();
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
        int lastCreatedJobId = initializeTestData(new Timestamp(now), new Timestamp(now + TIME_ONE_HOUR), false, 1, DatabaseUtils.SQL_SCALE_OUT);

        // When
        JobExecution jobExecution = jobLauncherTestUtils.launchJob();

        // Then
        assertThatNothingHappened(jobExecution, lastCreatedJobId);

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
        int lastCreatedJobId = initializeTestData(new Timestamp(now + 9 * TIME_ONE_HOUR), new Timestamp(now + 10 * TIME_ONE_HOUR), true, 1, DatabaseUtils.SQL_SCALE_OUT);

        // When
        JobExecution jobExecution = jobLauncherTestUtils.launchJob();

        // Then
        assertThatNothingHappened(jobExecution, lastCreatedJobId);
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
        int lastCreatedJobId = initializeTestData(new Timestamp(now - 9 * TIME_ONE_HOUR), new Timestamp(now - 8 * TIME_ONE_HOUR), true, 1, DatabaseUtils.SQL_SCALE_OUT);

        // When
        JobExecution jobExecution = jobLauncherTestUtils.launchJob();

        // Then
        assertThatNothingHappened(jobExecution, lastCreatedJobId);
    }

    /**
     * Test case when sampling period is invalid
     *
     * Given automatic (scheduled) scaling is turned on,
     * and sampling period is invalid so that period start equals period end,
     * and scaling is needed according to scaling rule,
     * When batch job executes,
     * Then don't scale the cluster.
     *
     * @throws Exception
     */
    @Test
    public void SamplingPeriodInvalidFromEqualsToPeriodTest() throws Exception {

        // Given
        int lastCreatedJobId = initializeTestData(new Timestamp(now), new Timestamp(now), true, 1, DatabaseUtils.SQL_SCALE_OUT);

        // When
        JobExecution jobExecution = jobLauncherTestUtils.launchJob();

        // Then
        assertThatNothingHappened(jobExecution, lastCreatedJobId);
    }

    /**
     * Test case when sampling period is invalid
     *
     * Given automatic (scheduled) scaling is turned on,
     * and sampling period is invalid so that period start is after period end,
     * and scaling is needed according to scaling rule,
     * When scaling rule is applied
     * Then don't scale the cluster.
     *
     * @throws Exception
     */
    @Test
    public void SamplingPeriodInvalidFromAfterToPeriodTest() throws Exception {

        // Given
        int lastCreatedJobId = initializeTestData(new Timestamp(now), new Timestamp(now-1), true, 1, DatabaseUtils.SQL_SCALE_OUT);

        // When
        JobExecution jobExecution = jobLauncherTestUtils.launchJob();

        // Then
        assertThatNothingHappened(jobExecution, lastCreatedJobId);
    }

    /**
     * Test case when sampling period "caught" scheduled scaling period start,
     * but scaling has already been done.
     *
     * Given automatic (scheduled) scaling is turned on,
     * and sampling period "caught" scheduled scaling period start,
     * and scaling state is READY_FOR_SCALE_IN(0),
     * When batch job executes,
     * Then don't scale the cluster.
     *
     * @throws Exception
     */
    @Test
    @Ignore
    public void PeriodFromInWindowAndNotRequiredScalingOutTest() throws Exception {

        // Given
        int lastCreatedJobId = initializeTestData(new Timestamp(now), new Timestamp(now + TIME_ONE_HOUR), true, 0, DatabaseUtils.SQL_SCALE_OUT);

        // When
        JobExecution jobExecution = jobLauncherTestUtils.launchJob();

        // Then
        assertThatNothingHappened(jobExecution, lastCreatedJobId);
    }

    /**
     * Test case when sampling period "caught" scheduled scaling period end,
     * but scaling out has not yet been done.
     *
     * Given automatic (scheduled) scaling is turned on,
     * and sampling period "caught" scheduled scaling period start,
     * and scaling state is READY_FOR_SCALE_IN(0),
     * When batch job executes,
     * Then don't scale the cluster.
     *
     * @throws Exception
     */
    @Test
    @Ignore
    public void PeriodToInWindowAndNotRequiredScalingInTest() throws Exception {

        // Given
        int lastCreatedJobId = initializeTestData(new Timestamp(now - TIME_ONE_HOUR), new Timestamp(now), true, 1, DatabaseUtils.SQL_SCALE_IN);

        // When
        JobExecution jobExecution = jobLauncherTestUtils.launchJob();

        // Then
        assertThatNothingHappened(jobExecution, lastCreatedJobId);
    }

    /**
     * Test case when scaling state is invalid
     *
     * Given automatic (scheduled) scaling is turned on,
     * and sampling period "caught" scheduled scaling period start,
     * and scaling state is READY_FOR_SCALE_IN(0),
     * When batch job executes,
     * Then don't scale the cluster.
     *
     * @throws Exception
     */
    @Test
    public void InvalidStateTest() throws Exception {

        // Given
        int lastCreatedJobId = initializeTestData(new Timestamp(now - TIME_ONE_HOUR), new Timestamp(now), true, 99, DatabaseUtils.SQL_SCALE_IN);

        // When
        JobExecution jobExecution = jobLauncherTestUtils.launchJob();

        // Then
        Assert.assertEquals(jobExecution.getStatus(), BatchStatus.COMPLETED);
        ScalingRule scalingRule = scalingRuleService.getRule(CLUSTER_ID);
        Assert.assertEquals(1, scalingRule.getClusterSizeOriginal());
        Assert.assertEquals(5, scalingRule.getClusterSizeNew());
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

    private void assertThatNothingHappened(JobExecution jobExecution, int lastCreatedJobId){
        Assert.assertEquals(jobExecution.getStatus(), BatchStatus.COMPLETED);
        ScalingRule scalingRule = scalingRuleService.getRule(CLUSTER_ID);
        Assert.assertEquals(6, scalingRule.getClusterSizeOriginal());
        Assert.assertEquals(5, scalingRule.getClusterSizeNew());
        Assert.assertEquals(1, scalingRule.getScheduledScalingState());
        Assert.assertEquals(-1, scalingRule.getJobId());
        Assert.assertEquals(lastCreatedJobId, jobService.getNewest().getJobId());
    }

    /**
     *
     * @param from Timestamp of sampling periond start
     * @param to Timestamp of sampling periond end
     * @param scheduledScaling Flag insicating id scheduled scaling is turned on
     * @param scheduledScalingState Indicates current state as defined in enum ScheduledScalingState
     * @param dataSetFile Relative path to xml file containing data set for the test case
     * @return Returns a job id of freshly created dummy job. That in formation is later
     * used to make sure that no new jobs after this one were created.
     * @throws DataSetException
     * @throws URISyntaxException
     * @throws FileNotFoundException
     */
    private int initializeTestData(Timestamp from, Timestamp to, boolean scheduledScaling, int scheduledScalingState, String dataSetFile) throws DataSetException, URISyntaxException, FileNotFoundException{
        DatabaseUtils.updateTestDatabase(DatabaseUtils.initDataSet(this, dataSetFile, from, to), dataSource);
        ScalingRule rule = scalingRuleService.getRule(CLUSTER_ID);
        rule.setScheduledScalingOn(scheduledScaling);
        rule.setScheduledScalingState(scheduledScalingState);
        scalingRuleService.store(rule);
        Assert.assertEquals(scheduledScaling, scalingRuleService.getRule(CLUSTER_ID).isScheduledScalingOn());
        return createJob();
    }

}

