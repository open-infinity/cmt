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
import org.apache.log4j.Logger;
import org.dbunit.database.DatabaseDataSourceConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ReplacementDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
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
	private static final Logger LOG = Logger.getLogger(SystemTests.class.getName());
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
    
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	private static final int CLUSTER_ID = 1;    
	
	private static final int JOB_ID = 0;    
		
	protected IDataSet getDataSet(Timestamp from, Timestamp to) throws Exception
    {
		ReplacementDataSet dataSet = null;
		try{
			URL resourceLocation = Object.class.getResource("/dataset-scale-out.xml");
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
	
	public void prepareTestDatabase(Timestamp from, Timestamp to){
		IDataSet dataSet;
		try {
			dataSet = getDataSet(from, to);
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
			long now = System.currentTimeMillis();	
			prepareTestDatabase(new Timestamp(now + 2100), new Timestamp(now + 7200 ));
				  
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
	
	@Test
    public void periodicScaler_scaleOutScaleIn() throws Exception {
        try{    
            // 
            
            
            //
            
            
            //
            
            
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
	
	
}

