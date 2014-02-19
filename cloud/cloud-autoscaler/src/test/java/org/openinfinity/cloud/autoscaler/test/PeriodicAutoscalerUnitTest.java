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
import org.openinfinity.cloud.autoscaler.periodicautoscaler.ClusterProcessingState;
import org.openinfinity.cloud.autoscaler.periodicautoscaler.PeriodicAutoscalerItemProcessor;
import org.openinfinity.cloud.autoscaler.util.ScalingData;
import org.openinfinity.cloud.domain.*;
import org.openinfinity.cloud.service.administrator.ClusterService;
import org.openinfinity.cloud.service.administrator.InstanceService;
import org.openinfinity.cloud.service.healthmonitoring.HealthMonitoringService;
import org.openinfinity.cloud.service.scaling.Enumerations.ScalingStatus;
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

    private ScalingRule rule;

    private Cluster cluster;

    private Machine machine;

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
        initData();
        when(mockScalingRuleService.getRule(1)).thenReturn(rule);
        when(mockClusterService.getCluster(1)).thenReturn(cluster);
        when(mockHealthMonitoringService.getClusterLoad(machine, PeriodicAutoscalerItemProcessor.METRIC_NAMES, PeriodicAutoscalerItemProcessor.METRIC_TYPE_LOAD, PeriodicAutoscalerItemProcessor.METRIC_PERIOD)).thenReturn((float) -1);
        ClusterProcessingState clusterProcessingState = new ClusterProcessingState(1, false, false, false, false, false);
        itemProcessor.getProcessingStatusMap().put(1, clusterProcessingState);
        ScalingData sd = new ScalingData(1, cluster);

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
        initData();
        when(mockScalingRuleService.getRule(1)).thenReturn(rule);
        when(mockClusterService.getCluster(1)).thenReturn(cluster);
        when(mockHealthMonitoringService.getClusterLoad(machine, PeriodicAutoscalerItemProcessor.METRIC_NAMES, PeriodicAutoscalerItemProcessor.METRIC_TYPE_LOAD, PeriodicAutoscalerItemProcessor.METRIC_PERIOD)).thenReturn((float) -1);

        // NOTE: threshold is 2 in autoscalertest.properties, and failedAttempts gets increased by 1
        ClusterProcessingState clusterProcessingState = new ClusterProcessingState(0, false, false, false, false, false);
        itemProcessor.getProcessingStatusMap().put(1, clusterProcessingState);
        ScalingData sd = new ScalingData(0, cluster);

        // When
        Job job = itemProcessor.process(machine);

        // Then
        Assert.assertNull(job);
        verify(notifier, never()).notify(sd, Notifier.NotificationType.LOAD_FETCHING_FAILED);
    }

    /**
     * Test situations when scaling out is required according to the scaling rules,
     * but there were new failed jobs for this rule.
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
        initData();
        when(mockScalingRuleService.getRule(1)).thenReturn(rule);
        when(mockClusterService.getCluster(1)).thenReturn(cluster);
        when(mockHealthMonitoringService.getClusterLoad(machine, PeriodicAutoscalerItemProcessor.METRIC_NAMES, PeriodicAutoscalerItemProcessor.METRIC_TYPE_LOAD, PeriodicAutoscalerItemProcessor.METRIC_PERIOD)).thenReturn((float) 1);
        when(mockScalingRuleService.applyScalingRule(1, 1, rule)).thenReturn(ScalingStatus.ERROR_SCALING_JOB_ERROR);
        ClusterProcessingState clusterProcessingState = new ClusterProcessingState(0, false, false, false, false,false);
        itemProcessor.getProcessingStatusMap().put(1, clusterProcessingState);
        ScalingData sd = new ScalingData(1, cluster, rule);

        // When
        Job job = itemProcessor.process(machine);

        // Then
        Assert.assertNull(job);
        verify(notifier, times(1)).notify(sd, Notifier.NotificationType.SCALING_JOB_ERROR);
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
        initData();
        when(mockScalingRuleService.getRule(1)).thenReturn(rule);
        when(mockClusterService.getCluster(1)).thenReturn(cluster);
        when(mockHealthMonitoringService.getClusterLoad(machine, PeriodicAutoscalerItemProcessor.METRIC_NAMES, PeriodicAutoscalerItemProcessor.METRIC_TYPE_LOAD, PeriodicAutoscalerItemProcessor.METRIC_PERIOD)).thenReturn((float) 1);
        when(mockScalingRuleService.applyScalingRule(1, 1, rule)).thenReturn(ScalingStatus.ERROR_SCALING_JOB_ERROR);
        ClusterProcessingState clusterProcessingState = new ClusterProcessingState(0, true, false, false, false, false);
        itemProcessor.getProcessingStatusMap().put(1, clusterProcessingState);
        ScalingData sd = new ScalingData(1, cluster, rule);

        // When
        Job job = itemProcessor.process(machine);

        // Then
        Assert.assertNull(job);
        verify(notifier, never()).notify(sd, Notifier.NotificationType.SCALING_JOB_ERROR);
    }

    /**
     * Test situations when scaling out is required according to the scaling rules,
     * but in cluster there are detected machines that are not configured.
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
    public void clusterNewConfigurationErrorTest() throws Exception {

        // Given
        initData();
        when(mockScalingRuleService.getRule(1)).thenReturn(rule);
        when(mockClusterService.getCluster(1)).thenReturn(cluster);
        when(mockHealthMonitoringService.getClusterLoad(machine, PeriodicAutoscalerItemProcessor.METRIC_NAMES, PeriodicAutoscalerItemProcessor.METRIC_TYPE_LOAD, PeriodicAutoscalerItemProcessor.METRIC_PERIOD)).thenReturn((float) 1);
        when(mockScalingRuleService.applyScalingRule(1, 1, rule)).thenReturn(ScalingStatus.ERROR_MACHINE_CONFIGURATION_FAILURE);
        ClusterProcessingState clusterProcessingState = new ClusterProcessingState(0, false, false, false, false, false);
        itemProcessor.getProcessingStatusMap().put(1, clusterProcessingState);
        ScalingData sd = new ScalingData(1, cluster, rule);

        // When
        Job job = itemProcessor.process(machine);

        // Then
        Assert.assertNull(job);
        verify(notifier, times(1)).notify(sd, Notifier.NotificationType.MACHINE_CONFIGURATION_ERROR);
    }

    /**
     * Test situations when scaling out is required according to the scaling rules,
     * but in cluster there are detected machines that are not configured.
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
    public void clusterKnownConfigurationErrorTest() throws Exception {

        // Given
        initData();
        when(mockScalingRuleService.getRule(1)).thenReturn(rule);
        when(mockClusterService.getCluster(1)).thenReturn(cluster);
        when(mockHealthMonitoringService.getClusterLoad(machine, PeriodicAutoscalerItemProcessor.METRIC_NAMES, PeriodicAutoscalerItemProcessor.METRIC_TYPE_LOAD, PeriodicAutoscalerItemProcessor.METRIC_PERIOD)).thenReturn((float) 1);
        when(mockScalingRuleService.applyScalingRule(1, 1, rule)).thenReturn(ScalingStatus.ERROR_MACHINE_CONFIGURATION_FAILURE);
        ClusterProcessingState clusterProcessingState = new ClusterProcessingState(0, false, true, false, false,false);
        itemProcessor.getProcessingStatusMap().put(1, clusterProcessingState);
        ScalingData sd = new ScalingData(1, cluster, rule);

        // When
        Job job = itemProcessor.process(machine);

        // Then
        Assert.assertNull(job);
        verify(notifier, never()).notify(sd, Notifier.NotificationType.MACHINE_CONFIGURATION_ERROR);
    }

    /**
     * Test situations when it is detected that scaling rule for cluster is invalid.
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
    public void clusterNewInvalidScalingRuleTest() throws Exception {

        // Given
        initData();
        when(mockScalingRuleService.getRule(1)).thenReturn(rule);
        when(mockClusterService.getCluster(1)).thenReturn(cluster);
        when(mockHealthMonitoringService.getClusterLoad(machine, PeriodicAutoscalerItemProcessor.METRIC_NAMES, PeriodicAutoscalerItemProcessor.METRIC_TYPE_LOAD, PeriodicAutoscalerItemProcessor.METRIC_PERIOD)).thenReturn((float) 1);
        when(mockScalingRuleService.applyScalingRule(1, 1, rule)).thenReturn(ScalingStatus.ERROR_INVALID_RULE);
        ClusterProcessingState clusterProcessingState = new ClusterProcessingState(0, false, false, false, false, false);
        itemProcessor.getProcessingStatusMap().put(1, clusterProcessingState);
        ScalingData sd = new ScalingData(1, cluster, rule);

        // When
        Job job = itemProcessor.process(machine);

        // Then
        Assert.assertNull(job);
        verify(notifier, times(1)).notify(sd, Notifier.NotificationType.SCALING_RULE_INVALID);
    }

    /**
     * Test situations when it is detected that scaling rule for cluster is invalid,
     * but that was already known.
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
    public void clusterKnownInvalidScalingRuleTest() throws Exception {

        // Given
        initData();
        when(mockScalingRuleService.getRule(1)).thenReturn(rule);
        when(mockClusterService.getCluster(1)).thenReturn(cluster);
        when(mockHealthMonitoringService.getClusterLoad(machine, PeriodicAutoscalerItemProcessor.METRIC_NAMES, PeriodicAutoscalerItemProcessor.METRIC_TYPE_LOAD, PeriodicAutoscalerItemProcessor.METRIC_PERIOD)).thenReturn((float) 1);
        when(mockScalingRuleService.applyScalingRule(1, 1, rule)).thenReturn(ScalingStatus.ERROR_INVALID_RULE);
        ClusterProcessingState clusterProcessingState = new ClusterProcessingState(0, false, false, true, false, false);
        itemProcessor.getProcessingStatusMap().put(1, clusterProcessingState);
        ScalingData sd = new ScalingData(1, cluster, rule);

        // When
        Job job = itemProcessor.process(machine);

        // Then
        Assert.assertNull(job);
        verify(notifier, never()).notify(sd, Notifier.NotificationType.SCALING_RULE_INVALID);
    }

    /**
     * Test case when scaling is required but not possible because
     * it was detected that scaling rule limit prevents scaling.
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
    public void clusterNewScalingRuleLimitTest() throws Exception {

        // Given
        initData();
        when(mockScalingRuleService.getRule(1)).thenReturn(rule);
        when(mockClusterService.getCluster(1)).thenReturn(cluster);
        when(mockHealthMonitoringService.getClusterLoad(machine, PeriodicAutoscalerItemProcessor.METRIC_NAMES, PeriodicAutoscalerItemProcessor.METRIC_TYPE_LOAD, PeriodicAutoscalerItemProcessor.METRIC_PERIOD)).thenReturn((float) 1);
        when(mockScalingRuleService.applyScalingRule(1, 1, rule)).thenReturn(ScalingStatus.ERROR_SCALING_RULE_LIMIT);
        ClusterProcessingState clusterProcessingState = new ClusterProcessingState(0, false, false, false, false, false);
        itemProcessor.getProcessingStatusMap().put(1, clusterProcessingState);
        ScalingData sd = new ScalingData(1, cluster, rule);

        // When
        Job job = itemProcessor.process(machine);

        // Then
        Assert.assertNull(job);
        verify(notifier, times(1)).notify(sd, Notifier.NotificationType.SCALING_RULE_LIMIT);
    }

    /**
     * Test case when scaling is required but not possible because
     * it was known that scaling rule limit prevents scaling.
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
    public void clusterKnownScalingRuleLimitTest() throws Exception {

        // Given
        initData();
        when(mockScalingRuleService.getRule(1)).thenReturn(rule);
        when(mockClusterService.getCluster(1)).thenReturn(cluster);
        when(mockHealthMonitoringService.getClusterLoad(machine, PeriodicAutoscalerItemProcessor.METRIC_NAMES, PeriodicAutoscalerItemProcessor.METRIC_TYPE_LOAD, PeriodicAutoscalerItemProcessor.METRIC_PERIOD)).thenReturn((float) 1);
        when(mockScalingRuleService.applyScalingRule(1, 1, rule)).thenReturn(ScalingStatus.ERROR_SCALING_RULE_LIMIT);
        ClusterProcessingState clusterProcessingState = new ClusterProcessingState(0, false, false, false, true, false);
        itemProcessor.getProcessingStatusMap().put(1, clusterProcessingState);
        ScalingData sd = new ScalingData(1, cluster, rule);

        // When
        Job job = itemProcessor.process(machine);

        // Then
        Assert.assertNull(job);
        verify(notifier, never()).notify(sd, Notifier.NotificationType.SCALING_RULE_LIMIT);
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
        when(mockScalingRuleService.applyScalingRule(1, 1, rule)).thenReturn(ScalingStatus.SCALING_OUT_REQUIRED);

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
        when(mockScalingRuleService.applyScalingRule(1, 1, rule)).thenReturn(ScalingStatus.SCALING_IN_REQUIRED);

        // When
        Job job = itemProcessor.process(machine);

        // Then
        Assert.assertNotNull(job);
        Assert.assertThat(job.getJobType(), is("scale_cluster"));
        Assert.assertThat(job.getServices(), is("1,99"));
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

    private void initData(){
        machine = new Machine();
        machine.setClusterId(1);
        machine.setId(1);
        rule = new ScalingRule();
        rule.setPeriodicScalingOn(true);
        cluster = new Cluster();
        cluster.setInstanceId(1);
        cluster.setId(1);
    }

}

