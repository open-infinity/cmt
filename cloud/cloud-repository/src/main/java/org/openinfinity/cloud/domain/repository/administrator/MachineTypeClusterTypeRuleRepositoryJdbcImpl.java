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

package org.openinfinity.cloud.domain.repository.administrator;

import org.openinfinity.core.annotation.AuditTrail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

import org.openinfinity.cloud.domain.MachineTypeClusterTypeRule;


/**
 * Machine type Cluster type Rule repository implementation; defines allowed machine types for each cluster type.
 * 
 * @author Ari Simanainen
 * @version 1.0.0 Initial version
 * @since 1.0.0
 */

@Repository("machineTypeClusterTypeRuleRepository")
public class MachineTypeClusterTypeRuleRepositoryJdbcImpl implements
		MachineTypeClusterTypeRuleRepository {

	private NamedParameterJdbcTemplate jdbcTemplate;
	private DataSource dataSource;

	@Autowired
	public MachineTypeClusterTypeRuleRepositoryJdbcImpl(@Qualifier("cloudDataSource") DataSource ds) {
		Assert.notNull(ds, "Please define datasource for MachineTypeClusterTypeRuleRepository repository.");
		this.jdbcTemplate = new NamedParameterJdbcTemplate(ds);
		this.dataSource = ds;
	}
		
	@AuditTrail
	@Override
	public List<MachineTypeClusterTypeRule> getMachineTypeClusterTypeRules(int machineTypeId) {
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue("machineTypeId", machineTypeId);
		List<MachineTypeClusterTypeRule> rules = jdbcTemplate.query("select * from machine_type_cluster_type_rule_tbl " +
				"where machine_type_id = :machineTypeId and allowed = true order by cluster_type_id ", parameters, new MachineTypeClusterTypeRuleWrapper());
		return rules;
	}
	
	@AuditTrail
	@Override
	public void addMachineTypeClusterTypeRule(MachineTypeClusterTypeRule rule) {
		SimpleJdbcInsert insert = new SimpleJdbcInsert(dataSource).withTableName("machine_type_cluster_type_rule_tbl");
		Map<String,Object> parameters = new HashMap<String,Object>();
		parameters.put("machine_type_id", rule.getMachineTypeId());
		parameters.put("cluster_type_id", rule.getClusterTypeId());
		parameters.put("allowed", rule.isAllowed());
		insert.execute(parameters);
	}
	
    private static final class MachineTypeClusterTypeRuleWrapper implements RowMapper<MachineTypeClusterTypeRule> {
        public MachineTypeClusterTypeRule mapRow(ResultSet rs, int rowNumber) throws SQLException {
        	MachineTypeClusterTypeRule rule = new MachineTypeClusterTypeRule();
            rule.setMachineTypeId(rs.getInt("machine_type_id"));
            rule.setClusterTypeId(rs.getInt("cluster_type_id"));
            rule.setAllowed(rs.getBoolean("allowed"));
            return rule;
        }
    }
}
