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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
import org.openinfinity.cloud.service.administrator.ClusterService;

/**
 * Batch configuration integration tests.
 * 
 * @author Vedran Bartonicek
 * @version 1.0.0
 * @since 1.0.0
 */

@ContextConfiguration(locations={"classpath*:META-INF/spring/test-scheduled-scaler-context.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class SmokeTests {
	private static final Logger LOG = Logger.getLogger(SmokeTests.class.getName());
	@Autowired
	@Qualifier("cloudDataSource")
	DataSource ds;
	
	@Autowired
	@Qualifier("clusterService")
	ClusterService srs;
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	protected IDataSet getDataSet(Timestamp from, Timestamp to) throws Exception
    {
		LOG.debug("getDataSet enter ");

		ReplacementDataSet dataSet = null;
		try{
			URL resourceLocation = Object.class.getResource("/dataset-scale-out.xml");
			if (resourceLocation == null) { 
				throw new FileNotFoundException("/dataset-scale-out.xml");
			}      
	        dataSet = new ReplacementDataSet(new FlatXmlDataSetBuilder().build(new FileInputStream(new File(resourceLocation.toURI())))); 
	        dataSet.addReplacementObject("[from]", from);
	        dataSet.addReplacementObject("[to]", to);
		}
		catch (Exception e){
			LOG.debug("Exception: "+e.getMessage());
		}
		LOG.debug("getDataSet exit");
        return dataSet;
    }
	
	public void prepareDatabase(Timestamp from, Timestamp to){
		IDataSet dataSet;
		try {
			dataSet = getDataSet(from, to);
			IDatabaseConnection dbConn = new DatabaseDataSourceConnection(ds);
			DatabaseOperation.CLEAN_INSERT.execute(dbConn, dataSet);
			LOG.debug("database ready");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/*
	 * Smoke test configured so that system does a scale out, and then scale in
	 */
	@Test
	public void testScaleOutScaleIn() throws Exception {
		try{	
			long now = System.currentTimeMillis();	
			prepareDatabase(new Timestamp(now + 2100), new Timestamp(now + 7200 ));
						
			// -- Scaling expected after 3 sec --	
			Thread.sleep(3000);
			// TODO assert against result DB
			Assert.assertEquals(2, jdbcTemplate.queryForInt("select size_original from scaling_rule_tbl where cluster_id = ?", 1));			
			Assert.assertEquals(2, jdbcTemplate.queryForInt("select scaling_state from scaling_rule_tbl where cluster_id = ?", 1));
			Assert.assertEquals("1,5", jdbcTemplate.queryForObject("select job_services from job_tbl where job_id = 0", String.class));

			// -- Unscaling expected after 5 sec --
			Thread.sleep(5000);
			Assert.assertEquals(0, jdbcTemplate.queryForInt("select scaling_state from scaling_rule_tbl where cluster_id = ?", 1));
			Assert.assertEquals("1,2", jdbcTemplate.queryForObject("select job_services from job_tbl where job_id = 1", String.class));		

		}
		catch (Exception e){
			LOG.debug("debug in test: "+ e.getMessage());
		}
	}
	
}

