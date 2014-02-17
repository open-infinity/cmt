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

import org.apache.log4j.Logger;
import org.junit.*;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.openinfinity.cloud.autoscaler.notifier.Notifier;
import org.openinfinity.cloud.autoscaler.periodicautoscaler.Failures;
import org.openinfinity.cloud.autoscaler.periodicautoscaler.PeriodicAutoscalerItemProcessor;
import org.openinfinity.cloud.autoscaler.util.ScalingData;
import org.openinfinity.cloud.domain.*;
import org.openinfinity.cloud.service.administrator.ClusterService;
import org.openinfinity.cloud.service.administrator.InstanceService;
import org.openinfinity.cloud.service.healthmonitoring.HealthMonitoringService;
import org.openinfinity.cloud.service.scaling.Enumerations.ScalingState;
import org.openinfinity.cloud.service.scaling.ScalingRuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.*;

/**
 * Unit tests for Periodic Autoscaler.
 * 
 * @author Vedran Bartonicek
 * @version 1.2.2
 * @since 1.2.0
 */

@ContextConfiguration(locations={"classpath*:META-INF/spring/cloud-autoscaler-test-unit-context.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class PeriodicAutoscalerUnitTest {

    private static final Logger LOG = Logger.getLogger(PeriodicAutoscalerUnitTest.class.getName());

    @Rule
    public TestName name = new TestName();

    @InjectMocks
	@Autowired
    PeriodicAutoscalerItemProcessor itemProcessor;

    @Mock
    InstanceService mockInstanceService;

	@Mock
	ClusterService mockClusterService;

    @Mock
    ScalingRuleService mockScalingRuleService;

    @Mock
    HealthMonitoringService mockHealthMonitoringService;

    @Spy
    @Autowired
    Notifier notifier;

	@Before
	public void initMocks() {
		MockitoAnnotations.initMocks(this);
        LOG.debug("ENTER " + name.getMethodName());
	}

    @After
    public void after(){
        LOG.debug("EXIT " + name.getMethodName());
    }

    /**
     * Test case when scaling is required but not possible
     *
     * Given automatic (periodic) scaling is turned on,
     * and load for cluster is available,
     * and scaling is needed according to scaling rule,
     * and maxim cluster size is reached,
     * When batch item is processed
     * Then send notification email,
     * and don't scale the cluster.
     *
     * @throws Exception
     */
	@Test
	public void requiredScalingNotPossibleTest() throws Exception {

        // Given
        Machine machine = new Machine();
        machine.setClusterId(1);
        machine.setId(1);
        ScalingRule rule = new ScalingRule();
        rule.setPeriodicScalingOn(true);
        rule.setMaxLoad((float)0.9);
        Cluster cluster = new Cluster();
        cluster.setInstanceId(1);
        cluster.setId(1);
        int failedAttempts = 0;
        Failures failures = new Failures(1, false);
        itemProcessor.getFailuresMap().put(1, failures);
        when(mockScalingRuleService.getRule(1)).thenReturn(rule);
        when(mockClusterService.getCluster(1)).thenReturn(cluster);
        when(mockHealthMonitoringService.getClusterLoad(machine, PeriodicAutoscalerItemProcessor.METRIC_NAMES, PeriodicAutoscalerItemProcessor.METRIC_TYPE_LOAD, PeriodicAutoscalerItemProcessor.METRIC_PERIOD)).thenReturn((float) 1);
        when(mockScalingRuleService.applyScalingRule(1, 1, rule)).thenReturn(ScalingState.SCALING_OUT_IMPOSSIBLE);

        // When
        Job job = itemProcessor.process(machine);

        // Then
        Assert.assertNull(job);
        verify(notifier).notify(new ScalingData(failedAttempts, cluster), Notifier.NotificationType.SCALING_FAILED);
	}

    /**
     * Test situations when load is NOT available from the cluster, and threshold for number of
     * attempts to get load is reached
     *
     * Given automatic (periodic) scaling is turned on,
     * and load for cluster is not available,
     * and number of past attempts equals threshold,
     * When batch item is processed
     * Then send notification email,
     * and don't scale the cluster.
     *
     * @throws Exception
     */
    @Test
    public void loadNotAvailableThresholdReachedTest() throws Exception {

        // Given
        Machine machine = new Machine();
        machine.setClusterId(1);
        machine.setId(1);
        ScalingRule rule = new ScalingRule();
        rule.setPeriodicScalingOn(true);
        Cluster cluster = new Cluster();
        cluster.setInstanceId(1);
        cluster.setId(1);
        when(mockScalingRuleService.getRule(1)).thenReturn(rule);
        when(mockClusterService.getCluster(1)).thenReturn(cluster);
        when(mockHealthMonitoringService.getClusterLoad(machine, PeriodicAutoscalerItemProcessor.METRIC_NAMES, PeriodicAutoscalerItemProcessor.METRIC_TYPE_LOAD, PeriodicAutoscalerItemProcessor.METRIC_PERIOD)).thenReturn((float) -1);
        int failedAttempts = 1;
        Failures failures = new Failures(1, false);
        itemProcessor.getFailuresMap().put(1, failures);
        ScalingData sd = new ScalingData(failedAttempts, cluster);

        // When
        Job job = itemProcessor.process(machine);

        // Then
        Assert.assertNull(job);
        verify(notifier).notify(sd, Notifier.NotificationType.LOAD_FETCHING_FAILED);
    }

    /**
     * Test situations when load is NOT available from the cluster, and threshold for number of
     * attempts to get load is NOT reached
     *
     * Given that automatic (periodic) scaling is turned on,
     * and load for cluster is not available,
     * and number of past attempts equals threshold,
     * When batch item is processed
     * Then send notification email,
     * and don't scale the cluster.
     *
     * @throws Exception
     */
    @Test
    public void loadNotAvailableBelowThresholdTest() throws Exception {

        // Given
        Machine machine = new Machine();
        machine.setClusterId(1);
        machine.setId(1);
        ScalingRule rule = new ScalingRule();
        rule.setPeriodicScalingOn(true);
        Cluster cluster = new Cluster();
        cluster.setInstanceId(1);
        cluster.setId(1);
        when(mockScalingRuleService.getRule(1)).thenReturn(rule);
        when(mockClusterService.getCluster(1)).thenReturn(cluster);
        when(mockHealthMonitoringService.getClusterLoad(machine, PeriodicAutoscalerItemProcessor.METRIC_NAMES, PeriodicAutoscalerItemProcessor.METRIC_TYPE_LOAD, PeriodicAutoscalerItemProcessor.METRIC_PERIOD)).thenReturn((float) -1);

        // NOTE: threshold is 2 in autoscalertest.properties, and failedAttempts gets increased by 1
        int failedAttempts = 0;
        Failures failures = new Failures(0, false);
        itemProcessor.getFailuresMap().put(1, failures);
        ScalingData sd = new ScalingData(failedAttempts, cluster);

        // When
        Job job = itemProcessor.process(machine);

        // Then
        Assert.assertNull(job);
        verify(notifier, never()).notify(sd, Notifier.NotificationType.LOAD_FETCHING_FAILED);
    }


    /**
     * Test situations when scaling out is required according to the scaling rules,
     * but there were known failed jobs for this rule.
     *
     * Given that automatic (periodic) scaling is turned on,
     * and load for cluster is not available,
     * and number of past attempts equals threshold,
     * When batch item is processed
     * Then don't send notification email,
     * and don't scale the cluster.
     *
     * @throws Exception
     */
    @Test
    public void scalingOutKnownJobErrorTest() throws Exception {

        // Given
        Machine machine = new Machine();
        machine.setClusterId(1);
        machine.setId(1);
        ScalingRule rule = new ScalingRule();
        rule.setPeriodicScalingOn(true);
        Cluster cluster = new Cluster();
        cluster.setInstanceId(1);
        cluster.setId(1);
        when(mockScalingRuleService.getRule(1)).thenReturn(rule);
        when(mockClusterService.getCluster(1)).thenReturn(cluster);
        when(mockHealthMonitoringService.getClusterLoad(machine, PeriodicAutoscalerItemProcessor.METRIC_NAMES, PeriodicAutoscalerItemProcessor.METRIC_TYPE_LOAD, PeriodicAutoscalerItemProcessor.METRIC_PERIOD)).thenReturn((float) 1);
        when(mockScalingRuleService.applyScalingRule(1, 1, rule)).thenReturn(ScalingState.SCALING_ERROR);

        // NOTE: threshold is 2 in autoscalertest.properties, and failedAttempts gets increased by 1
        boolean jobFailureDetected = true;
        Failures failures = new Failures(0, jobFailureDetected);
        itemProcessor.getFailuresMap().put(1, failures);
        ScalingData sd = new ScalingData(1, cluster, rule);

        // When
        Job job = itemProcessor.process(machine);

        // Then
        Assert.assertNull(job);
        verify(notifier, never()).notify(sd, Notifier.NotificationType.PREVIOUS_SCALING_FAILED);
    }

    /**
     * Test situations when scaling out is required according to the scaling rules,
     * but there were known failed jobs for this rule.
     *
     * Given that automatic (periodic) scaling is turned on,
     * and load for cluster is not available,
     * and number of past attempts equals threshold,
     * When batch item is processed
     * Then send notification email,
     * and don't scale the cluster.
     *
     * @throws Exception
     */
    @Test
    public void scalingOutNewJobErrorTest() throws Exception {

        // Given
        Machine machine = new Machine();
        machine.setClusterId(1);
        machine.setId(1);
        ScalingRule rule = new ScalingRule();
        rule.setPeriodicScalingOn(true);
        Cluster cluster = new Cluster();
        cluster.setInstanceId(1);
        cluster.setId(1);
        when(mockScalingRuleService.getRule(1)).thenReturn(rule);
        when(mockClusterService.getCluster(1)).thenReturn(cluster);
        when(mockHealthMonitoringService.getClusterLoad(machine, PeriodicAutoscalerItemProcessor.METRIC_NAMES, PeriodicAutoscalerItemProcessor.METRIC_TYPE_LOAD, PeriodicAutoscalerItemProcessor.METRIC_PERIOD)).thenReturn((float) 1);
        when(mockScalingRuleService.applyScalingRule(1, 1, rule)).thenReturn(ScalingState.SCALING_ERROR);

        // NOTE: threshold is 2 in autoscalertest.properties, and failedAttempts gets increased by 1
        boolean jobFailureDetected = false;
        Failures failures = new Failures(0, jobFailureDetected);
        itemProcessor.getFailuresMap().put(1, failures);
        ScalingData sd = new ScalingData(1, cluster, rule);

        // When
        Job job = itemProcessor.process(machine);

        // Then
        Assert.assertNull(job);
        verify(notifier, times(1)).notify(sd, Notifier.NotificationType.PREVIOUS_SCALING_FAILED);
    }

    /**
     * Test situations when periodic autoscaling is turned off
     *
     * Given Periodic autoscaling is turned off,
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

        // Given
        when(mockScalingRuleService.getRule(2)).thenReturn(rule);

        // Then
        Assert.assertNull(itemProcessor.process(machine));
    }

    /**
     * Test situations when scaling out is required
     *
     * Given scaling is needed according to scaling rule,
     * When batch item is processed
     * Then create a new Job that will increase cluster size by 1 machine
     *
     * @throws Exception
     */
    @Test
    public void requiredScalingOutTest() throws Exception {

        // Given
        Machine machine = new Machine();
        machine.setClusterId(1);
        machine.setId(1);

        Cluster cluster = new Cluster();
        cluster.setInstanceId(9999);
        cluster.setId(1);
        cluster.setNumberOfMachines(100);

        Instance instance = new Instance();
        instance.setCloudType(1);
        instance.setZone("area-51");

        ScalingRule rule = new ScalingRule();
        when(mockInstanceService.getInstance(9999)).thenReturn(instance);
        when(mockClusterService.getCluster(1)).thenReturn(cluster);
        rule.setPeriodicScalingOn(true);
        when(mockScalingRuleService.getRule(1)).thenReturn(rule);
        when(mockHealthMonitoringService.getClusterLoad(machine, PeriodicAutoscalerItemProcessor.METRIC_NAMES, PeriodicAutoscalerItemProcessor.METRIC_TYPE_LOAD, PeriodicAutoscalerItemProcessor.METRIC_PERIOD)).thenReturn((float) 1);
        when(mockScalingRuleService.applyScalingRule(1, 1, rule)).thenReturn(ScalingState.SCALE_OUT);

        // When
        Job job = itemProcessor.process(machine);

        // Then
        Assert.assertNotNull(job);
        Assert.assertThat(job.getJobType(), is("scale_cluster"));
        Assert.assertThat(job.getServices(), is("1,101"));
    }

    /**
     * Test case REQUIRES_SCALING_IN
     *
     * Given that cloud exists,
     * and automatic (periodic) scaling is turned on,
     * and load for cluster is available,
     * and scaling is needed according to scaling rule,
     * When batch item is processed
     * Then create a new Job that will decrease cluster size by 1 machine
     *
     * @throws Exception
     */
    @Test
    public void requiredScalingInTest() throws Exception {

        // Given
        Machine machine = new Machine();
        machine.setClusterId(1);
        machine.setId(1);

        Cluster cluster = new Cluster();
        cluster.setInstanceId(9999);
        cluster.setId(1);
        cluster.setNumberOfMachines(100);

        Instance instance = new Instance();
        instance.setCloudType(1);
        instance.setZone("area-51");

        ScalingRule rule = new ScalingRule();
        when(mockInstanceService.getInstance(9999)).thenReturn(instance);
        when(mockClusterService.getCluster(1)).thenReturn(cluster);
        rule.setPeriodicScalingOn(true);
        when(mockScalingRuleService.getRule(1)).thenReturn(rule);
        when(mockHealthMonitoringService.getClusterLoad(machine, PeriodicAutoscalerItemProcessor.METRIC_NAMES, PeriodicAutoscalerItemProcessor.METRIC_TYPE_LOAD, PeriodicAutoscalerItemProcessor.METRIC_PERIOD)).thenReturn((float) 1);
        when(mockScalingRuleService.applyScalingRule(1, 1, rule)).thenReturn(ScalingState.SCALE_IN);

        // When
        Job job = itemProcessor.process(machine);

        // Then
        Assert.assertNotNull(job);
        Assert.assertThat(job.getJobType(), is("scale_cluster"));
        Assert.assertThat(job.getServices(), is("1,99"));
    }

}

