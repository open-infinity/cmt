/*
 * Copyright (c) 2011-2013 the original author or authors.
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
package org.openinfinity.cloud.domain.repository.usage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.openinfinity.cloud.domain.UsageHour;
import org.openinfinity.core.annotation.AuditTrail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

/**
 * JDBC implementation of the usage reporting interface of the virtual machine usage.
 * 
 * @author Ilkka Leinonen
 * @version 1.0.0
 * @since 1.2.0
 */
@Repository
public class UsageHourRepositoryJdbcImpl implements UsageHourRepository {
 
	/**
	 * Represents the SQL script for loading usage hours by organization id and by usage period.
	 */
	private static final String LOAD_USAGE_HOURS_PER_PERIOD_AND_BY_ORGANIZATION_SQL = "SELECT * from usage_hours_tbl WHERE organization_id = ? AND cur_timestamp >= ? AND cur_timestamp <= ?";

	/**
	 * Represents the SQL script for storing usage hour information.
	 */
	private static final String STORE_SQL = "INSERT INTO usage_hours_tbl" +
			"(organization_id," +
			"instance_id," +
			"cluster_id," +
			"platform_id," +
			"cluster_type_title," +
			"machine_id," +
			"machine_type_id," +
			"machine_type_name," +
			"machine_type_spec," +
			"machine_machine_type, " +
			"cluster_ebs_image_used," +
			"cluster_ebs_volumes_used," +
			"state)" +
			"values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	
	/**
	 * Represents the SQL script for loading usage hour information.
	 */
	private static final String LOAD_BY_MACHINE_ID = "SELECT * FROM usage_hours_tbl WHERE machine_id = ? ORDER BY cur_timestamp DESC";
	
	/**
	 * Represents the SQL script for loading all UsageHour object by given organization id.
	 */
	private static final String LOAD_ALL_BY_ORG_SQL = "SELECT * FROM usage_hours_tbl WHERE ORGANIZATION_ID = ?";
	
	/**
	 * Represents the JDBC template util.
	 */
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	public UsageHourRepositoryJdbcImpl(@Qualifier("cloudDataSource") DataSource dataSource) {
		Assert.notNull(dataSource, "Please define datasource for usage repository.");
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	@AuditTrail
	public void store(UsageHour usageHours) {
		jdbcTemplate.update(STORE_SQL,
				usageHours.getOrganizationId(),
				usageHours.getInstanceId(),
				usageHours.getClusterId(),
				usageHours.getPlatformId(),
				usageHours.getClusterTypeTitle(),
				usageHours.getMachineId(),
				usageHours.getMachineTypeId(),
				usageHours.getMachineTypeName(),
				usageHours.getMachineTypeSpec(),
				usageHours.getMachineMachineType(),
				usageHours.getClusterEbsImageUsed(),
				usageHours.getClusterEbsVolumesUsed(),
				usageHours.getVirtualMachineState().getValue());
	}

	@AuditTrail
	public UsageHour loadByMachineId(int machineId) {
		List<UsageHour> usageList = jdbcTemplate.query(LOAD_BY_MACHINE_ID, new Object[]{machineId}, new UsageHourRowMapper());
		return DataAccessUtils.singleResult(usageList);
	}

	@AuditTrail
	public Collection<UsageHour> loadUsageHoursByOrganizationIdAndUsagePeriod(long organizationId, Date startTime, Date endTime) {
		List<UsageHour> usageHours = jdbcTemplate.query(LOAD_USAGE_HOURS_PER_PERIOD_AND_BY_ORGANIZATION_SQL, new Object[]{organizationId, startTime, endTime}, new UsageHourRowMapper());
		return Collections.unmodifiableCollection(usageHours);
	}
	
	@AuditTrail
	public Collection<UsageHour> loadUsageHoursByOrganizationId(long organizationId) {
		List<UsageHour> usageHours = jdbcTemplate.query(LOAD_ALL_BY_ORG_SQL, new Object[]{organizationId}, new UsageHourRowMapper());
		return Collections.unmodifiableCollection(usageHours);
	}
	
	private class UsageHourRowMapper implements RowMapper<UsageHour> {

		public UsageHour mapRow(ResultSet resultSet, int rowNum) throws SQLException {
			UsageHour usageHour = new UsageHour();
			usageHour.setId(resultSet.getLong("id"));
			usageHour.setOrganizationId(resultSet.getLong("organization_id"));
			usageHour.setInstanceId(resultSet.getInt("instance_id"));
			usageHour.setClusterId(resultSet.getInt("cluster_id"));
			usageHour.setPlatformId(resultSet.getInt("platform_id"));
			usageHour.setClusterTypeTitle(resultSet.getString("cluster_type_title"));
			usageHour.setMachineId(resultSet.getInt("machine_id"));
			usageHour.setMachineTypeId(resultSet.getInt("machine_type_id"));
			usageHour.setMachineTypeName(resultSet.getString("machine_type_name"));
			usageHour.setMachineTypeSpec(resultSet.getString("machine_type_spec"));
			usageHour.setMachineMachineType(resultSet.getString("machine_machine_type"));
			usageHour.setClusterEbsImageUsed(resultSet.getInt("cluster_ebs_image_used"));
			usageHour.setClusterEbsVolumesUsed(resultSet.getInt("cluster_ebs_volumes_used"));
			usageHour.setVirtualMachineState(UsageHour.getVirtualMachineStateWithNumericValue(resultSet.getInt("state")));
			usageHour.setTimeStamp(resultSet.getTimestamp("cur_timestamp"));
			return usageHour;
		}
	
	}

}
