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
package org.openinfinity.cloud.domain.repository.scaling;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Collections;

import javax.sql.DataSource;

import org.openinfinity.core.annotation.AuditTrail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;
import org.openinfinity.cloud.domain.ScalingRule;
import org.openinfinity.cloud.domain.repository.scaling.ScalingRuleRowMapper;

/**
 * Repository interface implementation of the scaling rules for storing cluster specific rules.
 * 
 * @author Ilkka Leinonen
 * @author Vedran Bartonicek
 * @version 1.0.0
 * @since 1.0.0
 */

@Repository
public class ScalingRuleRepositoryImpl implements ScalingRuleRepository {

	/**
	 * Represents the SQL script for inserting ScalingRule object.
	 */
	private static final String INSERT =
		"insert into scaling_rule_tbl (" +
		"cluster_id, " 		+
		"periodic," 		+
		"scheduled," 		+
		"scaling_state," 	+
		"max_machines, " 	+
		"min_machines, " 	+
		"max_cpu_load, " 	+
		"min_cpu_load, " 	+
		"period_from, " 	+
		"period_to," 		+
		"size_new, " 		+
		"size_original, " 	+
		"job_id)" 			+
		" values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"; //13*?
	
	/**
	 * Represents the SQL parameters for update command 
	 */		
	private static final String UPDATE =
		"periodic = ?, "		+ 
		"scheduled = ?,"		+
		"scaling_state = ?,"	+
		"max_machines = ?, "	+
		"min_machines = ?, " 	+
		"max_cpu_load = ?, " 	+
		"min_cpu_load = ?, " 	+
		"period_from = ?, " 	+
		"period_to = ?, " 		+
		"size_new = ?, " 		+
		"size_original = ?, "   +
		"job_id = ?";		
	
	private static final String UPDATE_SCHEDULED_SCALING =
		"update scaling_rule_tbl set " 	+
		"scaling_state = ?, " 			+
		"size_original = ? " 			+
		"where cluster_id = ?";
	
	private static final String UPDATE_SCHEDULED_UNSCALING =
		"update scaling_rule_tbl set "	+
		"scheduled = ?, " 				+	
		"scaling_state = ? " 			+
		"where cluster_id = ?";
					
	/**
	 * Represents the SQL script for inserting or updating ScalingRule object
	 * if it already exists.
	 */
	private static final String UPSERT = INSERT + " on duplicate key update " + UPDATE;
	
	/**
	 * Represents the SQL script for storing ScalingRule object.
	 */
	private static final String LOAD_BY_CLUSTER_ID = "select * from scaling_rule_tbl where cluster_id = ?";
	
	/**
	 * Represents the SQL script for loading all ScalingRule objects.
	 */
	private static final String LOAD_ALL = "select * from scaling_rule_tbl";
	
	/**
	 * Represents the SQL script for deleting ScalingRule object.
	 */
	private static final String DELETE_BY_ID = "delete from scaling_rule_tbl where cluster_id = ?";
	
	/**
	 * Represents the SQL script for fetching last inserted id of the ScalingRule object.
	 */
	public static String LOAD_LAST_UPDATED_ID = "select last_insert_id()";
	
	/**
	 * Represents the JDBC template util.
	 */
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	@Qualifier("scalingRuleRowMapper")
	private RowMapper<ScalingRule> scalingRuleRowMapper;
	
	@Autowired
	public ScalingRuleRepositoryImpl(@Qualifier("cloudDataSource") DataSource dataSource) {
		Assert.notNull(dataSource, "Please define datasource for scaling rule repository.");
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@AuditTrail
	public void store(final ScalingRule scalingRule) {
		jdbcTemplate.update( UPSERT, new PreparedStatementSetter() {
			public void setValues(PreparedStatement ps) throws SQLException {	
				ps.setInt(1, scalingRule.getClusterId()); 
				ps.setBoolean(2, scalingRule.isPeriodicScalingOn());
				ps.setBoolean(3, scalingRule.isScheduledScalingOn());
				ps.setInt(4, scalingRule.getScheduledScalingState());
				ps.setInt(5, scalingRule.getMaxNumberOfMachinesPerCluster()); 
				ps.setInt(6, scalingRule.getMinNumberOfMachinesPerCluster());
				ps.setInt(7, scalingRule.getMaxClusterCpuLoadPercentage());
				ps.setInt(8, scalingRule.getMinClusterCpuLoadPercentage());
				ps.setTimestamp(9, scalingRule.getPeriodFrom()); 
				ps.setTimestamp(10, scalingRule.getPeriodTo());
				ps.setInt(11, scalingRule.getClusterSizeNew());
				ps.setInt(12, scalingRule.getClusterSizeOriginal());
				ps.setInt(13, scalingRule.getJobId());

				ps.setBoolean(14, scalingRule.isPeriodicScalingOn());
				ps.setBoolean(15, scalingRule.isScheduledScalingOn());
				ps.setInt(16, scalingRule.getScheduledScalingState());
				ps.setInt(17, scalingRule.getMaxNumberOfMachinesPerCluster()); 
				ps.setInt(18, scalingRule.getMinNumberOfMachinesPerCluster());
				ps.setInt(19, scalingRule.getMaxClusterCpuLoadPercentage());
				ps.setInt(20, scalingRule.getMinClusterCpuLoadPercentage());
				ps.setTimestamp(21, scalingRule.getPeriodFrom()); 
				ps.setTimestamp(22, scalingRule.getPeriodTo());
				ps.setInt(23, scalingRule.getClusterSizeNew());
				ps.setInt(24, scalingRule.getClusterSizeOriginal());
				ps.setInt(25, scalingRule.getJobId());
			}
		});			
	}

	@AuditTrail
	public ScalingRule loadByClusterId(int clusterId) {
		ScalingRule scalingRule = (ScalingRule) jdbcTemplate.queryForObject(LOAD_BY_CLUSTER_ID, new Object[]{clusterId}, scalingRuleRowMapper);
		return scalingRule;
	}

	@AuditTrail
	public void deleteByClusterId(int id) {
		jdbcTemplate.update(DELETE_BY_ID, id);
	}
	
	@AuditTrail
	public Collection<ScalingRule> loadAll() {
		Collection<ScalingRule> scalingRules = jdbcTemplate.query(LOAD_ALL, scalingRuleRowMapper);
		return Collections.unmodifiableCollection(scalingRules);
	}

	@AuditTrail
	public void storeStateScheduledScaling(final int numberOfMachines, final int clusterId){
		jdbcTemplate.update( UPDATE_SCHEDULED_SCALING, new PreparedStatementSetter() {
			public void setValues(PreparedStatement ps) throws SQLException {	
				ps.setInt(1, 2); 
				ps.setInt(2, numberOfMachines);
				ps.setInt(3, clusterId);
			}
		});
	}
		
	@AuditTrail
	public void storeStateScheduledUnScaling(final int clusterId){
		jdbcTemplate.update( UPDATE_SCHEDULED_UNSCALING, new PreparedStatementSetter() {
			public void setValues(PreparedStatement ps) throws SQLException {	
				ps.setBoolean(1, false); 
				ps.setInt(2, 0); 
				ps.setInt(3, clusterId);
			}
		});
	}
	
}
