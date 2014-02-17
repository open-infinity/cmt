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
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openinfinity.cloud.autoscaler.periodicautoscaler.Failures;
import org.openinfinity.cloud.autoscaler.scheduledautoscaler.ScheduledAutoscalerItemProcessor;
import org.openinfinity.cloud.domain.*;
import org.openinfinity.cloud.service.administrator.ClusterService;
import org.openinfinity.cloud.service.administrator.InstanceService;
import org.openinfinity.cloud.service.administrator.JobService;
import org.openinfinity.cloud.service.scaling.Enumerations.ScalingState;
import org.openinfinity.cloud.service.scaling.ScalingRuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.sql.Timestamp;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.when;

/**
 * Unit tests for Scheduled Autoscaler.
 * 
 * @author Vedran Bartonicek
 * @version 1.2.2
 * @since 1.2.0
 */

@ContextConfiguration(locations={"classpath*:META-INF/spring/cloud-autoscaler-test-unit-context.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class ScheduledAutoscalerUnitTest {

    @InjectMocks
	@Autowired
    ScheduledAutoscalerItemProcessor itemProcessor;

	@Mock
	ClusterService mockClusterService;

	@Mock
	InstanceService mockInstanceService;

	@Mock
	ScalingRule mockScalingRule;

    @Mock
    JobService mockJobService;

    @Mock
    ScalingRuleService mockScalingRuleService;

    @Before
	public void initMocks() {
		MockitoAnnotations.initMocks(this);
	}

    /**
     * Test case when sampling period is before scheduled scaling period
     *
     * Given automatic (scheduled) scaling is turned on,
     * and sampling period is before scheduled period,
     * and scaling is needed according to scaling rule,
     * When scaling rule is applied
     * Then don't scale the cluster.
     *
     * @throws Exception
     */
	@Test
    @Ignore
	public void SamplingPeriodBeforeScheduledScalingPeriodTest() throws Exception {

        // Given
		//long now = System.currentTimeMillis();

		long now = System.currentTimeMillis();
        Timestamp samplingPeriodStart = new Timestamp(now - itemProcessor.getOffsetStart() );
        Timestamp samplingPeriodEnd = new Timestamp(now + itemProcessor.getOffsetEnd());
        when(mockScalingRule.getPeriodFrom()).thenReturn(new Timestamp(now + 300000));
        when(mockScalingRule.getPeriodTo()).thenReturn(new Timestamp(now + 600000));

        Machine machine = new Machine();
        machine.setClusterId(1);
        machine.setId(1);
        ScalingRule rule = new ScalingRule();
        Cluster cluster = new Cluster();
        cluster.setInstanceId(1);
        cluster.setId(1);
        Failures failures = new Failures(1, false);
        itemProcessor.getFailuresMap().put(1, failures);
        when(mockScalingRuleService.getRule(1)).thenReturn(rule);
        when(mockClusterService.getCluster(1)).thenReturn(cluster);
		when(mockScalingRule.getClusterId()).thenReturn(1);
		when(mockScalingRule.getScheduledScalingState()).thenReturn(1);	
		when(mockScalingRule.getClusterSizeNew()).thenReturn(100);

        //Enumerations.ScalingState state = scalingRuleService.applyScalingRule(samplingPeriodStart, samplingPeriodEnd, cluster, rule);
        when(mockScalingRuleService.applyScalingRule(samplingPeriodStart, samplingPeriodEnd, cluster, rule)).thenReturn(ScalingState.SCALING_RULE_INVALID);

        // When, Then
		Assert.assertNull(itemProcessor.process(mockScalingRule));
	}

    /**
     * Test case when sampling period is after scheduled scaling period
     *
     * Given automatic (scheduled) scaling is turned on,
     * and sampling period is after scheduled period,
     * and scaling is needed according to scaling rule,
     * When scaling rule is applied
     * Then don't scale the cluster.
     *
     * @throws Exception
     */
	@Test
    @Ignore
	public void SamplingPeriodAfterScheduledPeriodTest() throws Exception {

        // Given
        long now = System.currentTimeMillis();
		when(mockScalingRule.getPeriodFrom()).thenReturn(new Timestamp(now - 600000));
		when(mockScalingRule.getPeriodTo()).thenReturn(new Timestamp(now - 300000));	
		when(mockScalingRule.getClusterId()).thenReturn(1);	
		when(mockScalingRule.getScheduledScalingState()).thenReturn(1);	
		when(mockScalingRule.getClusterSizeNew()).thenReturn(100);	

        // When, Then
		Assert.assertNull(itemProcessor.process(mockScalingRule));	
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
    @Ignore
	public void SamplingPeriodInvalidFromAfterToPeriodTest() throws Exception {

        // Given
        long now = System.currentTimeMillis();
		when(mockScalingRule.getPeriodFrom()).thenReturn(new Timestamp(now+1));
		when(mockScalingRule.getPeriodTo()).thenReturn(new Timestamp(now-1));
		when(mockScalingRule.getClusterId()).thenReturn(1);	
		when(mockScalingRule.getScheduledScalingState()).thenReturn(ScalingRule.ScheduledScalingState.READY_FOR_SCALE_OUT.getValue());
		when(mockScalingRule.getClusterSizeNew()).thenReturn(100);

        // When, Then
        Assert.assertNull(itemProcessor.process(mockScalingRule));
	}

    /**
     * Test case when sampling period is invalid
     *
     * Given automatic (scheduled) scaling is turned on,
     * and sampling period is invalid so that period start equals period end,
     * and scaling is needed according to scaling rule,
     * When scaling rule is applied
     * Then don't scale the cluster.
     *
     * @throws Exception
     */
	@Test
    @Ignore
	public void SamplingPeriodInvalidFromEqualsToPeriodTest() throws Exception {

        // Given
        long now = System.currentTimeMillis();
		when(mockScalingRule.getPeriodFrom()).thenReturn(new Timestamp(now));
		when(mockScalingRule.getPeriodTo()).thenReturn(new Timestamp(now));
		when(mockScalingRule.getClusterId()).thenReturn(1);	
		when(mockScalingRule.getScheduledScalingState()).thenReturn(1);	
		when(mockScalingRule.getClusterSizeNew()).thenReturn(100);

        // When, Then
        Assert.assertNull(itemProcessor.process(mockScalingRule));
	}

    /**
     * Test case when sampling period "caught" scheduled scaling period start,
     * but scaling has already been done.
     *
     * Given automatic (scheduled) scaling is turned on,
     * and sampling period "caught" scheduled scaling period start,
     * and scaling state is READY_FOR_SCALE_IN(0),
     * When scaling rule is applied
     * Then don't scale the cluster.
     *
     * @throws Exception
     */
	@Test
    @Ignore
	public void PeriodFromInWindowAndNotRequiredScalingOutTest() throws Exception {

        // Given
        long now = System.currentTimeMillis();
		when(mockScalingRule.getPeriodFrom()).thenReturn(new Timestamp(now));
		when(mockScalingRule.getPeriodTo()).thenReturn(new Timestamp(now + 300000));
		when(mockScalingRule.getClusterId()).thenReturn(1);	
		when(mockScalingRule.getScheduledScalingState()).thenReturn(ScalingRule.ScheduledScalingState.READY_FOR_SCALE_IN.getValue());
		when(mockScalingRule.getClusterSizeNew()).thenReturn(100);

        // When, Then
        Assert.assertNull(itemProcessor.process(mockScalingRule));
	}

    /**
     * Test scale out.
     *
     * Given automatic (scheduled) scaling is turned on,
     * and sampling period "caught" scheduled scaling period start,
     * and scaling is needed according to scaling rule,
     * When scaling rule is applied
     * Then scale the cluster to size defined by scaling rule,
     * and make sure that old size was stored in the scaling rule.
     *
     * @throws Exception
     */
    // Note: Ignored, difficult to mock - arguments for scalingRuleService.applyScalingRule are based on time.
    // Other approach would be to make custom argument matcher for mockito.
    // Testing of functionality covered with ScheduledAutoscalerIntegrationTest.scaleIn()
    // In fact, all the tests below should go to unit test module for ScalingRuleService
	@Test
    @Ignore
	public void ScaleOutTest() throws Exception {

        // Given
        long now = System.currentTimeMillis();
		when(mockScalingRule.getPeriodFrom()).thenReturn(new Timestamp(now));
		when(mockScalingRule.getPeriodTo()).thenReturn(new Timestamp(now + 300000));
		when(mockScalingRule.getClusterId()).thenReturn(1);
        when(mockScalingRule.getJobId()).thenReturn(1);
        when(mockScalingRule.getScheduledScalingState()).thenReturn(ScalingRule.ScheduledScalingState.READY_FOR_SCALE_OUT.getValue());
		when(mockScalingRule.getClusterSizeNew()).thenReturn(100);

        Cluster cluster = new Cluster();
		cluster.setInstanceId(1);
		cluster.setNumberOfMachines(10);
		when(mockClusterService.getCluster(1)).thenReturn(cluster);	

		Instance instance = new Instance();
		instance.setCloudType(1);
		instance.setZone("whatever");
		when(mockInstanceService.getInstance(1)).thenReturn(instance);

        // When
        Job newJob = itemProcessor.process(mockScalingRule);

        // Then
        Assert.assertThat("1,100", is(newJob.getServices()));

    }

    /**
     * Test scale in
     *
     * Given automatic (scheduled) scaling is turned on,
     * and sampling period "caught" scheduled scaling period end,
     * and scaling out has not yet been done,
     * When scaling rule is applied
     * Then scale the cluster to size defined by scaling rule,
     * and make sure that scaling rule is updated (scaling state set back to READY_FOR_SCALE_OUT),
     *
     * @throws Exception
     */

    // Note: Ignored, difficult to mock. Testing of functionality covered with
    // ScheduledAutoscalerIntegrationTest.scaleIn()
    @Test
    @Ignore
    public void ScaleInTest() throws Exception {

        // Given
        long now = System.currentTimeMillis();
        when(mockScalingRule.getPeriodFrom()).thenReturn(new Timestamp(now - 300000));
        when(mockScalingRule.getPeriodTo()).thenReturn(new Timestamp(now));
        when(mockScalingRule.getClusterId()).thenReturn(1);
        when(mockScalingRule.getScheduledScalingState()).thenReturn(ScalingRule.ScheduledScalingState.READY_FOR_SCALE_IN.getValue());
        when(mockScalingRule.getClusterSizeNew()).thenReturn(100);

        Cluster cluster = new Cluster();
        cluster.setInstanceId(1);
        cluster.setNumberOfMachines(10);
        when(mockClusterService.getCluster(1)).thenReturn(cluster);

        Instance instance = new Instance();
        instance.setCloudType(1);
        instance.setZone("whatever");
        when(mockInstanceService.getInstance(1)).thenReturn(instance);

        // When
        Job job = itemProcessor.process(mockScalingRule);

        // Then
        Assert.assertThat("1,0", is(job.getServices()));
    }

    /**
     * Test case when sampling period "caught" scheduled scaling period end,
     * but scaling out has not yet been done.
     *
     * Given automatic (scheduled) scaling is turned on,
     * and sampling period "caught" scheduled scaling period start,
     * and scaling state is READY_FOR_SCALE_IN(0),
     * When scaling rule is applied
     * Then don't scale the cluster.
     *
     * @throws Exception
     */
	@Test
    @Ignore
	public void PeriodToInWindowAndNotRequiredScalingInTest() throws Exception {

        // Given
        long now = System.currentTimeMillis();
		when(mockScalingRule.getPeriodFrom()).thenReturn(new Timestamp(now - 300000));
		when(mockScalingRule.getPeriodTo()).thenReturn(new Timestamp(now));
		when(mockScalingRule.getClusterId()).thenReturn(1);	
		when(mockScalingRule.getScheduledScalingState()).thenReturn(ScalingRule.ScheduledScalingState.READY_FOR_SCALE_OUT.getValue());
		when(mockScalingRule.getClusterSizeNew()).thenReturn(100);

        // When, Then
        Assert.assertNull(itemProcessor.process(mockScalingRule));
	}

    /**
     * Test case when scaling state is invalid
     *
     * Given automatic (scheduled) scaling is turned on,
     * and sampling period "caught" scheduled scaling period start,
     * and scaling state is READY_FOR_SCALE_IN(0),
     * When scaling rule is applied
     * Then don't scale the cluster.
     *
     * @throws Exception
     */
    @Test
    @Ignore
    public void InvalidStateTest() throws Exception {

        // Given
        long now = System.currentTimeMillis();
        when(mockScalingRule.getPeriodFrom()).thenReturn(new Timestamp(now - 300000));
        when(mockScalingRule.getPeriodTo()).thenReturn(new Timestamp(now));
        when(mockScalingRule.getClusterId()).thenReturn(1);
        when(mockScalingRule.getScheduledScalingState()).thenReturn(9999);
        when(mockScalingRule.getClusterSizeNew()).thenReturn(100);

        // When, Then
        Assert.assertNull(itemProcessor.process(mockScalingRule));
    }

    /**
     * Test job creation
     *
     * Given itemProcessor is created,
     * and cluster is created,
     * When function createJob() is called
     * Then it returns new job.
     *
     * @throws Exception
     */
    @Test
    public void createJob() throws Exception {

        // Given
        Job job = null;
        Cluster cluster = new Cluster();
        cluster.setInstanceId(1);
        cluster.setNumberOfMachines(10);
        when(mockClusterService.getCluster(1)).thenReturn(cluster);

        Instance instance = new Instance();
        instance.setCloudType(1);
        instance.setZone("whatever");
        when(mockInstanceService.getInstance(1)).thenReturn(instance);

        // When
        job = itemProcessor.createJob(cluster, 1);

        // Then
        Assert.assertNotNull(job);
    }

}

