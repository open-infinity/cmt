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
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.mockito.MockitoAnnotations;
import org.openinfinity.cloud.autoscaler.gateway.HttpGateway;
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
//@RunWith(SpringJUnit4ClassRunner.class)
@RunWith(Theories.class)
public class PeriodicScalerUnitTest {

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
    		
    @DataPoint
    public static String USERNAME_WITH_SLASH = "optimus/prime";

    @Theory
    public void filenameIncludesUsername(String username) {
        //assumeThat(username, not("e"));
    	Assert.assertEquals(username,USERNAME_WITH_SLASH);
    }
}

