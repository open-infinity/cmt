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
import org.openinfinity.cloud.domain.AvailabilityZone;
import org.openinfinity.cloud.domain.Cluster;
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
 * @author Ossi Hämäläinen
 * @author Ilkka Leinonen
 * @author Juha-Matti Sironen
 * @version 1.0.0 Initial version
 * @since 1.0.0
 */

@Repository("zoneRepository")
public class AvailabilityZoneRepositoryJdbcImpl implements AvailabilityZoneRepository {
	
	private static final Logger LOG = Logger.getLogger(AvailabilityZoneRepositoryJdbcImpl.class.getName());
	
	private JdbcTemplate jdbcTemplate;
	
	private DataSource dataSource;

	@Autowired
	public AvailabilityZoneRepositoryJdbcImpl(@Qualifier("cloudDataSource") DataSource ds) {
		Assert.notNull(ds, "Please define datasource for cluster repository.");
		this.jdbcTemplate = new JdbcTemplate(ds);
		this.dataSource = ds;
	}
	
	@AuditTrail
	public List<AvailabilityZone> getAvailabilityZones(int cloudId) {
		List<AvailabilityZone> zones = this.jdbcTemplate.query("select * from availability_zone_tbl where cloud_id = ?", new Object[] { cloudId }, new ZoneWrapper());
		return zones;
	}

	@AuditTrail
	public List<AvailabilityZone> getAvailabilityZones(String cloudName) {
		List<AvailabilityZone> zones = this.jdbcTemplate.query("select zone.id, zone.name, zone.cloud_id from availability_zone_tbl as zone, cloud_provider_tbl as cloud where zone.cloud_id = cloud.id and upper(cloud.name) = ?", new Object[] { cloudName.toUpperCase() }, new ZoneWrapper());
		return zones;
	}

	private static final class ZoneWrapper implements RowMapper<AvailabilityZone> {
		public AvailabilityZone mapRow(ResultSet rs, int rowNumber) throws SQLException {
			AvailabilityZone zone = new AvailabilityZone();
			zone.setId(rs.getInt("id"));
			zone.setName(rs.getString("name"));
			zone.setCloudId(rs.getInt("cloud_id"));
			return zone;
		}
	}
	
}
