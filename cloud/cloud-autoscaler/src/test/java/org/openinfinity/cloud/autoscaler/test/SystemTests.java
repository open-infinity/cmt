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

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.sql.Timestamp;
import javax.sql.DataSource;
import junit.framework.Assert;
import org.dbunit.database.DatabaseDataSourceConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ReplacementDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.dbunit.util.fileloader.DataFileLoader;
import org.dbunit.util.fileloader.FlatXmlDataFileLoader;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.openinfinity.cloud.autoscaler.gateway.HttpGateway;
import org.openinfinity.cloud.domain.ScalingRule;
import org.openinfinity.cloud.service.administrator.ClusterService;
import org.openinfinity.cloud.service.administrator.JobService;
import org.openinfinity.cloud.service.scaling.ScalingRuleService;

/**
 * Batch configuration integration tests.
 * 
 * @author Vedran Bartonicek
 * @version 1.0.0
 * @since 1.0.0
 */

@ContextConfiguration(locations={"classpath*:test-autoscaler-context.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class SystemTests {
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
	private static final int JOB_ID = 0;
	private static final String MOCK_SERVER_PATH = "src/test/python/mock-rrd-server.py";
	private static final int AUTOSCALER_PERIOD_MS = 10000;
	private static final String URL_LOAD_LOW = "http://127.0.0.1:8181/test/load/low";
	private static final String URL_LOAD_HIGH = "http://127.0.0.1:8181/test/load/high";
    private static final String URL_LOAD_MEDIUM = "http://127.0.0.1:8181/test/load/medium";
    private static final int JOB_UNDEFINED = -1;
    		
	protected IDataSet initDataSet() throws Exception
    {
        long now = System.currentTimeMillis();
	    Timestamp from = new Timestamp(now + 2100);
	    Timestamp to = new Timestamp(now + 7200 );
		ReplacementDataSet dataSet = null;
		
		try{
			URL resourceLocation = Object.class.getResource("/dataset-init-scale-out.xml");
	        dataSet = new ReplacementDataSet(new FlatXmlDataSetBuilder().
	            build(new FileInputStream(new File(resourceLocation.toURI())))); 
	        dataSet.addReplacementObject("[from]", from);
	        dataSet.addReplacementObject("[to]", to);
		}
		catch (Exception e){
		    e.printStackTrace();
		}
        return dataSet;
    }
	
	protected IDataSet addMachineDataSet() throws Exception
    {
	    IDataSet dataSet = null;   
        try{
            DataFileLoader loader = new FlatXmlDataFileLoader();
            dataSet = loader.load("/dataset-machine.xml");
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return dataSet;
    }
	
	
	public void updateTestDatabase(IDataSet dataSet){
		try {
			IDatabaseConnection dbConn = new DatabaseDataSourceConnection(dataSource);
			DatabaseOperation.CLEAN_INSERT.execute(dbConn, dataSet);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/*
	 * Scheduled scaler scaling out and in system test.
	 * 
	 * Database is configured so that scheduled scaler would perform scale out on a cluster.
	 * After the scheduled scale period expires, the scheduled scaler performs scale in to original
	 * size on a cluster.
	 * 
	 * Expect jobs created, and scaling rule table updates
	 */
	@Test
	public void scheduledScaler_scaleOutScaleIn() throws Exception {
		try{	
		    // Init
			updateTestDatabase(initDataSet());
				  
			// Test
			Thread.sleep(3000);
			ScalingRule scalingRule = scalingRuleService.getRule(CLUSTER_ID);
			Assert.assertEquals(2, scalingRule.getClusterSizeOriginal());
			Assert.assertEquals(2, scalingRule.getScheduledScalingState());
			Assert.assertEquals("1,5", jobService.getJob(JOB_ID).getServices());
			
			Thread.sleep(5000);
			Assert.assertEquals(0, scalingRuleService.getRule(CLUSTER_ID).getScheduledScalingState());
            Assert.assertEquals("1,2", jobService.getJob(JOB_ID + 1).getServices());  
		}
		catch (Exception e){
            e.printStackTrace();
		}
	}
	
	/*
     * Periodic scaler scaling out and in system test.
     * 
     * A mock rrd server and a database are configured so that periodic scaler would perform scale out
     * on a cluster.
     * After that the mock server is configured to report low load, and scaler should perform
     * scaling in on a cluster.
     */
	@Test
    public void periodicScaler_scaleOutScaleIn() throws Exception {
        try{  
            // Init 
            updateTestDatabase(initDataSet());          
            
            ProcessBuilder pb = new ProcessBuilder("python", MOCK_SERVER_PATH);
            Process p = pb.start();          
                  
            HttpGateway.get(URL_LOAD_MEDIUM);
            
            // Test 
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
               
            // Cleanup 
            p.destroy();         
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }	
}

