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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.openinfinity.cloud.autoscaler.scheduledautoscaler.ScheduledAutoscalerItemProcessor;
import org.openinfinity.cloud.domain.Cluster;
import org.openinfinity.cloud.domain.Instance;
import org.openinfinity.cloud.domain.ScalingRule;
import org.openinfinity.cloud.service.administrator.ClusterService;
import org.openinfinity.cloud.service.administrator.InstanceService;
import org.openinfinity.cloud.service.scaling.ScalingRuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.sql.Timestamp;

import static org.mockito.Mockito.verify;
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

	@Autowired
	@Spy
	ScalingRuleService scalingRuleService;

	@Mock
	ClusterService mockClusterService;

	@Mock
	InstanceService mockInstanceService;

	@Mock
	ScalingRule mockScalingRule;

	@Before
	public void initMocks() {
		MockitoAnnotations.initMocks(this);
	}

    /**
     * Test case when sampling period is before scheduled period
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
	public void WindowBeforePeriodTest() throws Exception {

        // Given
		long now = System.currentTimeMillis();						
		when(mockScalingRule.getPeriodFrom()).thenReturn(new Timestamp(now + 300000));
		when(mockScalingRule.getPeriodTo()).thenReturn(new Timestamp(now + 600000));	
		when(mockScalingRule.getClusterId()).thenReturn(1);	
		when(mockScalingRule.getScheduledScalingState()).thenReturn(1);	
		when(mockScalingRule.getClusterSizeNew()).thenReturn(100);	

        // When, Then
		Assert.assertNull(itemProcessor.process(mockScalingRule));
	}

    /**
     * Test case when sampling period is after scheduled period
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
	public void WindowAfterPeriodTest() throws Exception {

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
     * Test case when sampling period is after scheduled period
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
	public void WindowInvalidFromAfterToPeriodTest() throws Exception {

        // Given
        long now = System.currentTimeMillis();
		when(mockScalingRule.getPeriodFrom()).thenReturn(new Timestamp(now+1));
		when(mockScalingRule.getPeriodTo()).thenReturn(new Timestamp(now-1));
		when(mockScalingRule.getClusterId()).thenReturn(1);	
		when(mockScalingRule.getScheduledScalingState()).thenReturn(1);	
		when(mockScalingRule.getClusterSizeNew()).thenReturn(100);

        // When, Then
        Assert.assertNull(itemProcessor.process(mockScalingRule));
	}

    /**
     * Test case when sampling period is after scheduled period
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
	public void WindowInvalidFromEqualsToPeriodTest() throws Exception {

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
     * Test case when sampling period is after scheduled period
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
	public void PeriodFromInWindowAndNotRequiredScalingOutTest() throws Exception {

        // Given
        long now = System.currentTimeMillis();
		when(mockScalingRule.getPeriodFrom()).thenReturn(new Timestamp(now));
		when(mockScalingRule.getPeriodTo()).thenReturn(new Timestamp(now + 300000));
		when(mockScalingRule.getClusterId()).thenReturn(1);	
		when(mockScalingRule.getScheduledScalingState()).thenReturn(0);	
		when(mockScalingRule.getClusterSizeNew()).thenReturn(100);

        // When, Then
        Assert.assertNull(itemProcessor.process(mockScalingRule));
	}

    /**
     * Test case when sampling period is after scheduled period
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
	public void PeriodFromInWindowAndRequiredScalingOutTest() throws Exception {

        // Given
        long now = System.currentTimeMillis();
		when(mockScalingRule.getPeriodFrom()).thenReturn(new Timestamp(now));
		when(mockScalingRule.getPeriodTo()).thenReturn(new Timestamp(now + 300000));
		when(mockScalingRule.getClusterId()).thenReturn(1);	
		when(mockScalingRule.getScheduledScalingState()).thenReturn(1);	
		when(mockScalingRule.getClusterSizeNew()).thenReturn(100);	

		Cluster cluster = new Cluster();
		cluster.setInstanceId(1);
		cluster.setNumberOfMachines(10);
		when(mockClusterService.getCluster(1)).thenReturn(cluster);	

		Instance instance = new Instance();
		instance.setCloudType(1);
		instance.setZone("whatever");
		when(mockInstanceService.getInstance(1)).thenReturn(instance);	

		Assert.assertNotNull(itemProcessor.process(mockScalingRule));	
		verify(scalingRuleService).storeScalingOutParameters(10,1);
	}

    /**
     * Test case when sampling period is after scheduled period
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
	public void PeriodToInWindowAndNotRequiredScalingInTest() throws Exception {

        // Given
        long now = System.currentTimeMillis();
		when(mockScalingRule.getPeriodFrom()).thenReturn(new Timestamp(now - 300000));
		when(mockScalingRule.getPeriodTo()).thenReturn(new Timestamp(now));
		when(mockScalingRule.getClusterId()).thenReturn(1);	
		when(mockScalingRule.getScheduledScalingState()).thenReturn(3);	
		when(mockScalingRule.getClusterSizeNew()).thenReturn(100);

        // When, Then
        Assert.assertNull(itemProcessor.process(mockScalingRule));
	}

    /**
     * Test case when sampling period is after scheduled period
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
	public void PeriodToInWindowAndRequiredScalingInTest() throws Exception {

        // Given
        long now = System.currentTimeMillis();
		when(mockScalingRule.getPeriodFrom()).thenReturn(new Timestamp(now - 300000));
		when(mockScalingRule.getPeriodTo()).thenReturn(new Timestamp(now));
		when(mockScalingRule.getClusterId()).thenReturn(1);	
		when(mockScalingRule.getScheduledScalingState()).thenReturn(0);	
		when(mockScalingRule.getClusterSizeNew()).thenReturn(100);	

		Cluster cluster = new Cluster();
		cluster.setInstanceId(1);
		cluster.setNumberOfMachines(10);
		when(mockClusterService.getCluster(1)).thenReturn(cluster);	

		Instance instance = new Instance();
		instance.setCloudType(1);
		instance.setZone("whatever");
		when(mockInstanceService.getInstance(1)).thenReturn(instance);	

		Assert.assertNotNull(itemProcessor.process(mockScalingRule));	
		verify(scalingRuleService).storeScalingInParameters(1);
	}

}

