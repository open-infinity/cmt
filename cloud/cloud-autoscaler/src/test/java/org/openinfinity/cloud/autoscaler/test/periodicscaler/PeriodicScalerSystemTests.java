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

package org.openinfinity.cloud.autoscaler.test.periodicscaler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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

@ContextConfiguration(locations={"classpath*:META-INF/spring/test-periodic-scaler-context.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class PeriodicScalerSystemTests {
	@Autowired
	@Qualifier("cloudDataSource")
	DataSource ds;
	
	@Autowired
	@Qualifier("clusterService")
	ClusterService srs;
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	
	/*
	 * 
	 */
	@Test
	public void testScaleOutScaleIn() throws Exception {
		try{	
			long now = System.currentTimeMillis();	
		}
		catch (Exception e){
		    e.printStackTrace();
		}
	}
	
}

