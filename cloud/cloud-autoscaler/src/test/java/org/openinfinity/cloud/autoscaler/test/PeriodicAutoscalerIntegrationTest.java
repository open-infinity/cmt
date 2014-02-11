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
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openinfinity.cloud.autoscaler.test.util.DatabaseUtils;
import org.openinfinity.cloud.autoscaler.test.util.HttpGateway;
import org.openinfinity.cloud.domain.Cluster;
import org.openinfinity.cloud.domain.Instance;
import org.openinfinity.cloud.domain.Job;
import org.openinfinity.cloud.service.administrator.ClusterService;
import org.openinfinity.cloud.service.administrator.InstanceService;
import org.openinfinity.cloud.service.administrator.JobService;
import org.openinfinity.cloud.service.scaling.ScalingRuleService;
import org.openinfinity.core.exception.SystemException;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.sql.DataSource;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.Timestamp;

import static org.hamcrest.CoreMatchers.is;


/**
 * Integration tests for Periodic Autoscaler.
 * 
 * @author Vedran Bartonicek
 * @version 1.2.2
 * @since 1.2.0
 */
@ContextConfiguration(locations={"classpath*:META-INF/spring/cloud-autoscaler-test-integration-context.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class PeriodicAutoscalerIntegrationTest {

	private static final String MOCK_SERVER_PATH = "src/test/python/mock-rrd-server.py";

    private static final String URL_LOAD_LOW = "http://127.0.0.1:8181/test/load/low";

    private static final String URL_LOAD_HIGH = "http://127.0.0.1:8181/test/load/high";

    private static final String URL_LOAD_MEDIUM = "http://127.0.0.1:8181/test/load/medium";

    private static final int CLUSTER_ID = 1;

    private static final int JOB_UNDEFINED = -1;

    private static final int REQUEST_ATTEMPTS = 50;

    private static final int TIME_ONE_HOUR = 3600000;

    private static final int TIME_ONE_SECOND = 1000;

    private static final int TIME_REQUEST_WAIT = 200;

    Process process = null;

    @Autowired
    @Qualifier("periodicJobLauncherTestUtils")
    private JobLauncherTestUtils jobLauncherTestUtils;

	@Autowired
	@Qualifier("cloudDataSource")
	DataSource dataSource;
	
	@Autowired
	@Qualifier("clusterService")
	ClusterService clusterService;

    @Autowired
    @Qualifier("instanceService")
    InstanceService instanceService;
	
	@Autowired
    @Qualifier("scalingRuleService")
	ScalingRuleService scalingRuleService;
	
    @Autowired
    @Qualifier("jobService")
    JobService jobService;

	@Before
	public void before() throws InterruptedException, URISyntaxException, DataSetException, IOException {

        // Initialize database
        long now = System.currentTimeMillis();
        Timestamp from = new Timestamp(now - TIME_ONE_HOUR);
        Timestamp to = new Timestamp(now);
        DatabaseUtils.updateTestDatabase(DatabaseUtils.initDataSet(this, DatabaseUtils.SQL_SCALE_OUT, from, to), dataSource);

        // Initialize mock rrd server
        process = new ProcessBuilder("python2", MOCK_SERVER_PATH).start();
        Thread.sleep(TIME_ONE_SECOND);
	}

    @After()
    public void after(){
        process.destroy();
    }

    /**
     * Test case for situations when scaling is not needed
     *
     * Given that instance, cluster and machines exists,
     * and scaling rule for cluster is defined,
     * and automatic (periodic) scaling is turned on,
     * and load is between  max_load and min_load,
     * and rrd server is running,
     * When batch job executes,
     * Then batch job status is BatchStatus.COMPLETED,
     * and no new (worker)job was created.
     *
     * @throws Exception
     */
    @Test
    public void scaleNotNeededTest() throws Exception {

        // Given
        org.junit.Assert.assertThat(configureRrdServer(URL_LOAD_MEDIUM), is(true));

        // When
        JobExecution jobExecution = jobLauncherTestUtils.launchJob();

        // Then
        Assert.assertEquals(jobExecution.getStatus(), BatchStatus.COMPLETED);
        Assert.assertEquals(JOB_UNDEFINED, scalingRuleService.getRule(CLUSTER_ID).getJobId());
    }

    /**
     * Test case for situations when scaling out is required
     *
     * Given that instance, cluster and machines exists,
     * and scaling rule for cluster is defined,
     * and automatic (periodic) scaling is turned on,
     * and load is above max_load threshold,
     * When batch job executes,
     * Then batch job status is BatchStatus.COMPLETED,
     * and new (worker)job was created which scales out cluster by 1 machine.
     *
     * @throws Exception
     */
    @Test
    public void scaleOutTest() throws Exception {

        // Given
        org.junit.Assert.assertThat(configureRrdServer(URL_LOAD_HIGH), is(true));

        // When
        JobExecution jobExecution = jobLauncherTestUtils.launchJob();

        // Then
        Assert.assertEquals(jobExecution.getStatus(), BatchStatus.COMPLETED);
        int lastScaleOutJobId = scalingRuleService.getRule(CLUSTER_ID).getJobId();
        Assert.assertFalse(JOB_UNDEFINED == lastScaleOutJobId);
        Assert.assertEquals("1,3", jobService.getJob(lastScaleOutJobId).getServices());
    }

    /**
     * Test case for situations when scaling in is required
     *
     * Given that instance, cluster and machines exists,
     * and scaling rule for cluster is defined,
     * and automatic (periodic) scaling is turned on,
     * and load is above max_load threshold,
     * When batch job executes,
     * Then batch job status is BatchStatus.COMPLETED,
     * and new (worker)job was created which scales in cluster by 1 machine.
     *
     * @throws Exception
     */
    @Test
    public void scaleInTest() throws Exception {

        // Given
        org.junit.Assert.assertThat(configureRrdServer(URL_LOAD_LOW), is(true));

        // When
        JobExecution jobExecution = jobLauncherTestUtils.launchJob();

        // Then
        Assert.assertEquals(jobExecution.getStatus(), BatchStatus.COMPLETED);
        int lastScaleOutJobId = scalingRuleService.getRule(CLUSTER_ID).getJobId();
        Assert.assertFalse(JOB_UNDEFINED == lastScaleOutJobId);
        Assert.assertEquals("1,1", jobService.getJob(lastScaleOutJobId).getServices());
    }

    /**
     * Test case for situations when scaling out is required,
     * but scaling is already ongoing
     *
     * Given that instance, cluster and machines exists,
     * and scaling rule for cluster is defined,
     * and automatic (periodic) scaling is turned on,
     * and load is above max_load threshold,
     * When batch job executes,
     * Then batch job status is BatchStatus.COMPLETED,
     * and new (worker)job was not created.
     *
     * @throws Exception
     */
    @Test
    public void scaleOngoingTest() throws Exception {

        // Given
        org.junit.Assert.assertThat(configureRrdServer(URL_LOAD_HIGH), is(true));

        Cluster cluster = clusterService.getCluster(CLUSTER_ID);
        Instance instance = instanceService.getInstance(cluster.getInstanceId());
        Job job = new Job("scale_cluster", cluster.getInstanceId(), instance.getCloudType(), JobService.CLOUD_JOB_CREATED, instance.getZone(),
                Integer.toString(cluster.getId()), cluster.getNumberOfMachines() + 1);

        scalingRuleService.storeJobId(CLUSTER_ID, jobService.addJob(job));

        // When
        JobExecution jobExecution = jobLauncherTestUtils.launchJob();

        // Then
        Assert.assertEquals(jobExecution.getStatus(), BatchStatus.COMPLETED);
        Assert.assertEquals(job.getJobId(), scalingRuleService.getRule(CLUSTER_ID).getJobId());
    }

    private boolean configureRrdServer(String loadType) throws InterruptedException{
        boolean success = false;
        for (int counter = 1; counter <= REQUEST_ATTEMPTS; counter++){
            try{
                HttpGateway.get(loadType);
                success = true;
                break;
            }
            catch (SystemException e){
                Thread.sleep(TIME_REQUEST_WAIT);
                counter ++;
            }
        }
        return success;
    }
}

