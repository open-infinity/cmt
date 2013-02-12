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

import org.apache.log4j.Logger;
import org.openinfinity.cloud.domain.AvailabilityZone;
import org.openinfinity.core.annotation.AuditTrail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Jdbc implementation of Availability zone repository interface
 * 
 * @author Timo Tapanainen
 * @version 1.0.0 Initial version
 * @since 1.0.0
 */

@Repository("zoneRepository")
public class AvailabilityZoneRepositoryJdbcImpl implements AvailabilityZoneRepository {
	
	private static final Logger LOG = Logger.getLogger(AvailabilityZoneRepositoryJdbcImpl.class.getName());
	
	private NamedParameterJdbcTemplate jdbcTemplate;
	
	private DataSource dataSource;

	@Autowired
	public AvailabilityZoneRepositoryJdbcImpl(@Qualifier("cloudDataSource") DataSource ds) {
		Assert.notNull(ds, "Please define datasource for cluster repository.");
		this.jdbcTemplate = new NamedParameterJdbcTemplate(ds);
		this.dataSource = ds;
	}
	
	@AuditTrail
	public List<AvailabilityZone> getAvailabilityZones(int cloudId, List<String> userOrgNames) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("orgNames", userOrgNames);
        parameters.addValue("cloudId", cloudId);
        List<AvailabilityZone> zones = this.jdbcTemplate.query("select zone.* from availability_zone_tbl as zone, acl_availability_zone_tbl as acl where acl.org_name in (:orgNames) and acl.zone_id = zone.id and zone.cloud_id = :cloudId order by zone.id", parameters, new ZoneWrapper());
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
