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

package org.openinfinity.cloud.autoscaler.test.function;

import javax.sql.DataSource;

import junit.framework.Assert;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.openinfinity.cloud.autoscaler.test.util.DatabaseUtils;
import org.openinfinity.cloud.autoscaler.test.util.HttpGateway;
import org.openinfinity.cloud.domain.ScalingRule;
import org.openinfinity.cloud.service.administrator.ClusterService;
import org.openinfinity.cloud.service.administrator.JobService;
import org.openinfinity.cloud.service.scaling.ScalingRuleService;

/**
 * Functional tests for Periodic scaler.
 * 
 * @author Vedran Bartonicek
 * @version 1.3.0
 * @since 1.2.0
 */

@ContextConfiguration(locations={"classpath*:META-INF/spring/cloud-autoscaler-test-context.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class PeriodicScalerFunctionalTest {

	@Autowired
	@Qualifier("cloudDataSource")
	DataSource dataSource;
	
	@Autowired
	@Qualifier("clusterService")
	ClusterService clusterService;
	
	@Autowired
    @Qualifier("scalingRuleService")
	ScalingRuleService scalingRuleService;
	
    @Autowired
    @Qualifier("jobService")
    JobService jobService;
	
	private static final int CLUSTER_ID = 1;    
	private static final String MOCK_SERVER_PATH = "src/test/python/mock-rrd-server.py";
	private static final int AUTOSCALER_PERIOD_MS = 10000;
	private static final String URL_LOAD_LOW = "http://127.0.0.1:8181/test/load/low";
	private static final String URL_LOAD_HIGH = "http://127.0.0.1:8181/test/load/high";
    private static final String URL_LOAD_MEDIUM = "http://127.0.0.1:8181/test/load/medium";
    private static final int JOB_UNDEFINED = -1;
    		
	/*
     * Periodic scaler scaling out and in system test.
     * 
     * A mock rrd server and a database are configured so that periodic scaler would perform scale out
     * on a cluster.
     * After that the mock server is configured to report low load, and scaler should perform
     * scaling in on a cluster.
     */
	@Test
	@Ignore
    public void scaleOutScaleIn() throws Exception {
        
    	DatabaseUtils.updateTestDatabase(DatabaseUtils.initDataSet(this), dataSource);          
        
        ProcessBuilder pb = new ProcessBuilder("python", MOCK_SERVER_PATH);
        Process p = pb.start();          
              
        HttpGateway.get(URL_LOAD_MEDIUM);
        
        Thread.sleep((int)(AUTOSCALER_PERIOD_MS * 1.1));
        ScalingRule scalingRule = scalingRuleService.getRule(CLUSTER_ID);
        Assert.assertEquals(JOB_UNDEFINED, scalingRule.getJobId());
                  
        HttpGateway.get(URL_LOAD_HIGH);
        Thread.sleep((int)(AUTOSCALER_PERIOD_MS * 1.1));
        
        scalingRule = scalingRuleService.getRule(CLUSTER_ID);
        int lastScaleOutJobId = scalingRule.getJobId();
        Assert.assertFalse(JOB_UNDEFINED == lastScaleOutJobId);
        Assert.assertEquals("1,3", jobService.getJob(lastScaleOutJobId).getServices()); 
        
        HttpGateway.get(URL_LOAD_MEDIUM);
        jobService.updateStatus(lastScaleOutJobId, 10);

        Thread.sleep((int)(AUTOSCALER_PERIOD_MS * 1.1));
        scalingRule = scalingRuleService.getRule(CLUSTER_ID);
        Assert.assertEquals(lastScaleOutJobId, scalingRule.getJobId());
        
        HttpGateway.get(URL_LOAD_LOW);
        Thread.sleep((int)(AUTOSCALER_PERIOD_MS * 2));
        
        scalingRule = scalingRuleService.getRule(CLUSTER_ID);
        int lastScaleInJobId = scalingRule.getJobId();
        Assert.assertFalse(lastScaleOutJobId == lastScaleInJobId);
        Assert.assertEquals("1,1", jobService.getJob(lastScaleInJobId).getServices());       
           
        p.destroy();         
    }	
}

