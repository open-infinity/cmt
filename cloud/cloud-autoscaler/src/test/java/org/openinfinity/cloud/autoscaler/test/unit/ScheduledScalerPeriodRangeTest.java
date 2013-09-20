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

package org.openinfinity.cloud.autoscaler.test.unit;

import static org.mockito.Mockito.when;

import java.sql.Timestamp;

import javax.sql.DataSource;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.experimental.theories.DataPoint;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openinfinity.cloud.autoscaler.gateway.HttpGateway;
import org.openinfinity.cloud.autoscaler.scheduledscaler.ScheduledScalerItemProcessor;
import org.openinfinity.cloud.autoscaler.test.util.DatabaseUtils;
import org.openinfinity.cloud.domain.ScalingRule;
import org.openinfinity.cloud.service.administrator.ClusterService;
import org.openinfinity.cloud.service.administrator.JobService;
import org.openinfinity.cloud.service.scaling.ScalingRuleService;

/**
 * @author Vedran Bartonicek
 * @version 1.3.0
 * @since 1.2.0
 */

@ContextConfiguration(locations={"classpath*:META-INF/spring/cloud-autoscaler-test-context.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
//@RunWith(Theories.class)
public class ScheduledScalerPeriodRangeTest {

	@InjectMocks
	@Autowired
	ScheduledScalerItemProcessor itemProcessor;
	
	@Mock
	ScalingRule mockScalingRule;
	
	@Value("${deltaPlus}")
    int deltaPlus;
    
    @Value("${deltaMinus}")
    int deltaMinus;
    /*
    @DataPoint
    public static int param = 300000;
    
    @Theory
	public void WindowBeforePeriodTest(final int param) throws Exception {
		MockitoAnnotations.initMocks(this);
		long now = System.currentTimeMillis();						
		when(mockScalingRule.getPeriodFrom()).thenReturn(new Timestamp(now + 300000));
		when(mockScalingRule.getPeriodTo()).thenReturn(new Timestamp(now + 600000));	
		when(mockScalingRule.getClusterId()).thenReturn(1);	
		when(mockScalingRule.getScheduledScalingState()).thenReturn(1);	
		when(mockScalingRule.getClusterSizeNew()).thenReturn(100);	

		Assert.assertNull(itemProcessor.process(mockScalingRule));	
	}
	*/
    @Test
	public void WindowBeforePeriodTest() throws Exception {
		
	}
    
}

