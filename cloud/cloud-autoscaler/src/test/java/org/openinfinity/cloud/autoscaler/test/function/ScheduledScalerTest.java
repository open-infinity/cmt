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

import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.openinfinity.cloud.autoscaler.test.util.DatabaseUtils;
import org.openinfinity.cloud.domain.ScalingRule;
import org.openinfinity.cloud.service.administrator.ClusterService;
import org.openinfinity.cloud.service.administrator.JobService;
import org.openinfinity.cloud.service.scaling.ScalingRuleService;

/**
 * Cloud-autoscaler periodic scaler, functional tests
 * 
 * @author Vedran Bartonicek
 * @version 1.3.0
 * @since 1.2.0
 */

@ContextConfiguration(locations={"classpath*:META-INF/spring/cloud-autoscaler-test-context.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class ScheduledScalerTest {
	private static final Logger LOG = Logger.getLogger(ScheduledScalerTest.class.getName());

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
			DatabaseUtils.updateTestDatabase(DatabaseUtils.initDataSet(this), dataSource);
				  
			Thread.sleep(3000);
			ScalingRule scalingRule = scalingRuleService.getRule(CLUSTER_ID);
			Assert.assertEquals(2, scalingRule.getClusterSizeOriginal());
			Assert.assertEquals(2, scalingRule.getScheduledScalingState());
			Assert.assertEquals("1,5", jobService.getJob(JOB_ID).getServices());
			
			Thread.sleep(5000);
			scalingRule = scalingRuleService.getRule(CLUSTER_ID);

			Assert.assertEquals(0, scalingRuleService.getRule(CLUSTER_ID).getScheduledScalingState());
            Assert.assertEquals("1,2", jobService.getJob(JOB_ID + 1).getServices());  
		}
		catch (Exception e){
            e.printStackTrace();
		}
	}
	
}

