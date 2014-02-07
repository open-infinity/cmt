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

    /**
     * Test situation when scaling out is needed, but not possible
     *
     * When load for cluster is available, and automatic (periodic) scaling is turned on,
     * and maxim cluster size is reached,
     * then send notification email,
     * and don't scale the cluster.
     *
     * @throws Exception
     */
	@Test
	public void sendEmailTest() throws Exception {

        Machine machine = new Machine();
        machine.setClusterId(1);
        machine.setId(1);
        ScalingRule rule = new ScalingRule();
        rule.setPeriodicScalingOn(true);
        rule.setMaxLoad((float)0.9);
        Cluster cluster = new Cluster();
        cluster.setInstanceId(1);
        String[] metricName = {"load-relative.rrd"};
        String period = "shortterm";

        // When load for cluster is available
        when(mockClusterService.getCluster(1)).thenReturn(cluster);
        when(mockHealthMonitoringService.getClusterLoad(machine, metricName, "load", period)).thenReturn((float) 1);

        // and automatic (periodic) scaling is turned on...
		when(mockScalingRuleService.getRule(1)).thenReturn(rule);

        // and maxim cluster size is reached...
        when(mockScalingRuleService.applyScalingRule(1, 1, rule)).thenReturn(Enumerations.ClusterScalingState.REQUIRED_SCALING_IS_NOT_POSSIBLE);

        // then send notification email...
        // TODO: how to test?
        // (How to check if email was sent? At least "org.springframework.mail.MailException" would be thrown if something goes wrong.)

        // and don't scale the cluster.
        Assert.assertNull(itemProcessor.process(machine));
	}


    /**
     * Test situations when load is not available from the cluster
     *
     * When load for cluster is not available, and automatic (periodic) scaling is turned on,
     * and number of past attempts equals threshold,
     * then send notification email,
     * and don't scale the cluster.
     *
     * @throws Exception
     */
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

        // When load for cluster is not available,
        when(mockClusterService.getCluster(1)).thenReturn(cluster);
        when(mockHealthMonitoringService.getClusterLoad(machine, metricName, "load", period)).thenReturn((float) -1);

        // and automatic (periodic) scaling is turned on,
        when(mockScalingRuleService.getRule(1)).thenReturn(rule);

        // then send notification email,
        // TODO: how to test?

        // and don't scale the cluster.
        Assert.assertNull(itemProcessor.process(machine));
    }

    /**
     * Test situations when periodic autoscaling is turned off
     *
     * When Periodic autoscaling is turned off,
     * then don't scale the cluster.
     *
     * @throws Exception
     */
    @Test
    public void periodicScalingNotOnTest() throws Exception {
        Machine machine = new Machine();
        machine.setClusterId(2);
        ScalingRule rule = new ScalingRule();
        rule.setPeriodicScalingOn(false);

        // When Periodic autoscaling is turned off,
        when(mockScalingRuleService.getRule(2)).thenReturn(rule);
        // then don't scale the cluster
        Assert.assertNull(itemProcessor.process(machine));
    }

}

