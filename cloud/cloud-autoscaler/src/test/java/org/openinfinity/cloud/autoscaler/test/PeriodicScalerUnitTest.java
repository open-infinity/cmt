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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openinfinity.cloud.autoscaler.periodicscaler.PeriodicScalerItemProcessor;
import org.openinfinity.cloud.domain.Cluster;
import org.openinfinity.cloud.domain.Machine;
import org.openinfinity.cloud.domain.ScalingRule;
import org.openinfinity.cloud.service.administrator.ClusterService;
import org.openinfinity.cloud.service.healthmonitoring.HealthMonitoringService;
import org.openinfinity.cloud.service.scaling.Enumerations;
import org.openinfinity.cloud.service.scaling.ScalingRuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.mockito.Mockito.when;

/**
 * Unit tests for Periodic autoscaler.
 * 
 * @author Vedran Bartonicek
 * @version 1.2.2
 * @since 1.2.0
 */

@ContextConfiguration(locations={"classpath*:META-INF/spring/cloud-autoscaler-test-unit-context.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class PeriodicScalerUnitTest {

	@InjectMocks
	@Autowired
    PeriodicScalerItemProcessor itemProcessor;

	@Mock
	ClusterService mockClusterService;

    @Mock
    ScalingRuleService mockScalingRuleService;

    @Mock
    HealthMonitoringService mockHealthMonitoringService;

	@Before
	public void initMocks() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void sendEmailTest() throws Exception {
        Machine machine = new Machine();
        machine.setClusterId(1);
        machine.setId(1);

        ScalingRule rule = new ScalingRule();
        rule.setPeriodicScalingOn(true);
        Cluster cluster = new Cluster();
        cluster.setInstanceId(1);

        String[] metricName = {"load-relative.rrd"};
        String period = "shortterm";

        when(mockClusterService.getCluster(1)).thenReturn(cluster);
        when(mockHealthMonitoringService.getClusterLoad(machine, metricName, "load", period)).thenReturn((float) 1);
		when(mockScalingRuleService.getRule(1)).thenReturn(rule);
        when(mockScalingRuleService.calculateScalingState(rule, 1, 1)).thenReturn(Enumerations.ClusterScalingState.REQUIRED_SCALING_IS_NOT_POSSIBLE);
		Assert.assertNull(itemProcessor.process(machine));
	}

    @Test
    public void loadNotAvailableTest() throws Exception {
        Machine machine = new Machine();
        machine.setClusterId(1);
        machine.setId(1);

        ScalingRule rule = new ScalingRule();
        rule.setPeriodicScalingOn(true);
        Cluster cluster = new Cluster();
        cluster.setInstanceId(1);

        String[] metricName = {"load-relative.rrd"};
        String period = "shortterm";

        when(mockClusterService.getCluster(1)).thenReturn(cluster);
        when(mockHealthMonitoringService.getClusterLoad(machine, metricName, "load", period)).thenReturn((float) -1);
        Assert.assertNull(itemProcessor.process(machine));
    }

    @Test
    public void periodicScalingNotOnTest() throws Exception {
        Machine machine = new Machine();
        machine.setClusterId(2);
        ScalingRule rule = new ScalingRule();
        rule.setPeriodicScalingOn(false);
        when(mockScalingRuleService.getRule(2)).thenReturn(rule);
        Assert.assertNull(itemProcessor.process(machine));
    }

}

