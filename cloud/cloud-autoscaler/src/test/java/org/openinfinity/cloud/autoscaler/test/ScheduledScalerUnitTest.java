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

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.*;

import java.sql.Timestamp;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.openinfinity.cloud.autoscaler.scheduledscaler.ScheduledScalerItemProcessor;
import org.openinfinity.cloud.domain.Cluster;
import org.openinfinity.cloud.domain.Instance;
import org.openinfinity.cloud.domain.ScalingRule;
import org.openinfinity.cloud.service.administrator.ClusterService;
import org.openinfinity.cloud.service.administrator.InstanceService;
import org.openinfinity.cloud.service.scaling.ScalingRuleService;

/**
 * Unit tests for Scheduled scaler.
 * 
 * @author Vedran Bartonicek
 * @version 1.3.0
 * @since 1.2.0
 */

@ContextConfiguration(locations={"classpath*:META-INF/spring/cloud-autoscaler-test-unit-context.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class ScheduledScalerUnitTest {

	@InjectMocks
	@Autowired
	ScheduledScalerItemProcessor itemProcessor;

	@Autowired
	@Spy
	ScalingRuleService scalingRuleService;

	@Mock
	ClusterService mockClusterService;

	@Mock
	InstanceService mockInstanceService;

	@Mock
	ScalingRule mockScalingRule;

	@Value("${deltaPlus}")
    int deltaPlus;
    
    @Value("${deltaMinus}")
    int deltaMinus;
    
	@Before
	public void initMocks() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void WindowBeforePeriodTest() throws Exception {
		long now = System.currentTimeMillis();						
		when(mockScalingRule.getPeriodFrom()).thenReturn(new Timestamp(now + 300000));
		when(mockScalingRule.getPeriodTo()).thenReturn(new Timestamp(now + 600000));	
		when(mockScalingRule.getClusterId()).thenReturn(1);	
		when(mockScalingRule.getScheduledScalingState()).thenReturn(1);	
		when(mockScalingRule.getClusterSizeNew()).thenReturn(100);	

		Assert.assertNull(itemProcessor.process(mockScalingRule));	
	}

	@Test
	public void WindowAfterPeriodTest() throws Exception {
		long now = System.currentTimeMillis();						
		when(mockScalingRule.getPeriodFrom()).thenReturn(new Timestamp(now - 600000));
		when(mockScalingRule.getPeriodTo()).thenReturn(new Timestamp(now - 300000));	
		when(mockScalingRule.getClusterId()).thenReturn(1);	
		when(mockScalingRule.getScheduledScalingState()).thenReturn(1);	
		when(mockScalingRule.getClusterSizeNew()).thenReturn(100);	

		Assert.assertNull(itemProcessor.process(mockScalingRule));	
	}

	@Test
	public void WindowInvalidFromAfterToPeriodTest() throws Exception {
		long now = System.currentTimeMillis();						
		when(mockScalingRule.getPeriodFrom()).thenReturn(new Timestamp(now+1));
		when(mockScalingRule.getPeriodTo()).thenReturn(new Timestamp(now-1));
		when(mockScalingRule.getClusterId()).thenReturn(1);	
		when(mockScalingRule.getScheduledScalingState()).thenReturn(1);	
		when(mockScalingRule.getClusterSizeNew()).thenReturn(100);	

		Assert.assertNull(itemProcessor.process(mockScalingRule));	
	}

	@Test
	public void WindowInvalidFromEqualsToPeriodTest() throws Exception {
		long now = System.currentTimeMillis();						
		when(mockScalingRule.getPeriodFrom()).thenReturn(new Timestamp(now));
		when(mockScalingRule.getPeriodTo()).thenReturn(new Timestamp(now));
		when(mockScalingRule.getClusterId()).thenReturn(1);	
		when(mockScalingRule.getScheduledScalingState()).thenReturn(1);	
		when(mockScalingRule.getClusterSizeNew()).thenReturn(100);	

		Assert.assertNull(itemProcessor.process(mockScalingRule));	
	}

	@Test
	public void PeriodFromInWindowAndNotRequiredScalingOutTest() throws Exception {
		long now = System.currentTimeMillis();						
		when(mockScalingRule.getPeriodFrom()).thenReturn(new Timestamp(now));
		when(mockScalingRule.getPeriodTo()).thenReturn(new Timestamp(now + 300000));
		when(mockScalingRule.getClusterId()).thenReturn(1);	
		when(mockScalingRule.getScheduledScalingState()).thenReturn(0);	
		when(mockScalingRule.getClusterSizeNew()).thenReturn(100);	

		Assert.assertNull(itemProcessor.process(mockScalingRule));	
	}

	@Test
	public void PeriodFromInWindowAndRequiredScalingOutTest() throws Exception {
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

	@Test
	public void PeriodToInWindowAndNotRequiredScalingInTest() throws Exception {
		long now = System.currentTimeMillis();						
		when(mockScalingRule.getPeriodFrom()).thenReturn(new Timestamp(now - 300000));
		when(mockScalingRule.getPeriodTo()).thenReturn(new Timestamp(now));
		when(mockScalingRule.getClusterId()).thenReturn(1);	
		when(mockScalingRule.getScheduledScalingState()).thenReturn(3);	
		when(mockScalingRule.getClusterSizeNew()).thenReturn(100);		

		Assert.assertNull(itemProcessor.process(mockScalingRule));	
	}

	@Test
	public void PeriodToInWindowAndRequiredScalingInTest() throws Exception {
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

