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

package org.openinfinity.cloud.domain.repository.administrator;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.openinfinity.cloud.domain.CloudProvider;
import org.openinfinity.core.annotation.AuditTrail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

/**
 * Jdbc implementation of Cluster repository interface
 * 
 * @version 1.0.0 Initial version
 * @since 1.0.0
 */

@Repository("cloudRepository")
public class CloudProviderRepositoryJdbcImpl implements CloudProviderRepository {
	
	private static final Logger LOG = Logger.getLogger(CloudProviderRepositoryJdbcImpl.class.getName());
	
	private JdbcTemplate jdbcTemplate;
	
	private DataSource dataSource;

	@Autowired
	public CloudProviderRepositoryJdbcImpl(@Qualifier("cloudDataSource") DataSource ds) {
		Assert.notNull(ds, "Please define datasource for cluster repository.");
		this.jdbcTemplate = new JdbcTemplate(ds);
		this.dataSource = ds;
	}

	@AuditTrail
	public List<CloudProvider> getCloudProviders() {
		List<CloudProvider> clouds = this.jdbcTemplate.query("select * from cloud_provider_tbl", new CloudProviderWrapper());
		return clouds;
	}
	
	private static final class CloudProviderWrapper implements RowMapper<CloudProvider> {
		public CloudProvider mapRow(ResultSet rs, int rowNumber) throws SQLException {
			CloudProvider cloud = new CloudProvider();
			cloud.setId(rs.getInt("id"));
			cloud.setName(rs.getString("name"));
			return cloud;
		}
	}
	
}
