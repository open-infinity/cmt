/*
 * Copyright (c) 2012 the original author or authors.
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

package org.openinfinity.cloud.autoscaler.test.scheduledscaler;

import static org.junit.Assert.assertNotNull;

import java.io.FileInputStream;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.dbunit.database.DatabaseDataSourceConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
/*import org.dbunit.IDatabaseTester;
import org.dbunit.JdbcDatabaseTester;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;*/
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openinfinity.cloud.domain.Cluster;
import org.openinfinity.cloud.service.administrator.ClusterService;
import org.openinfinity.cloud.service.scaling.ScalingRuleService;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Batch configuration integration tests.
 * 
 * @author Vedran Bartonicek
 * @version 1.0.0
 * @since 1.0.0
 */

//@ContextConfiguration(locations={"classpath*:META-INF/spring/test-scheduled-scaler-context.xml"})
//@RunWith(SpringJUnit4ClassRunner.class)
public class ScaleOutTests {
	/*private static final Logger LOG = Logger.getLogger(SmokeTests.class.getName());
	@Autowired
	private JobLauncher jobLauncher;

	@Autowired
	@Qualifier("scheduledJob")
	private Job job;
	
	@Autowired
	@Qualifier("cloudDataSource")
	DataSource ds;
	
	@Autowired
	@Qualifier("clusterService")
	ClusterService srs;
	
	@Autowired
	private JdbcTemplate jdbcTemplate;

	
		
	@Before
	protected void setUp() throws Exception
    {
		//IDataSet dataSet = getDataSet();
		//IDatabaseConnection dbConn = new DatabaseDataSourceConnection(ds);
		//DatabaseOperation.CLEAN_INSERT.execute(dbConn, dataSet);
    }
	
	protected IDataSet getDataSet() throws Exception
    {
        return new  FlatXmlDataSetBuilder().build(new FileInputStream("classpath*:*dataset-scale-out.xml"));
    }
	
	@Test
	public void testSimpleProperties() throws Exception {
		IDataSet dataSet = getDataSet();
		IDatabaseConnection dbConn = new DatabaseDataSourceConnection(ds);
		DatabaseOperation.CLEAN_INSERT.execute(dbConn, dataSet);
		Cluster c = srs.getCluster(1);
		LOG.error("CLUSTER = " + c.getName());
		assertNotNull(jobLauncher);
	}

	// This is a test case to keep the context alive long enough so that Task scheduler can run few rounds
	@Test
	public void have_a_napp() throws Exception {
		assertNotNull(job);
		LOG.error("---sleep start---");
		Thread.sleep(3000);
		LOG.error("---sleep end---");
		
		//IDataSet databaseDataSet = getConnection().createDataSet();
		//ITable actualTable = databaseDataSet.getTable("TABLE_NAME");

	}*/
	 	
}

