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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.openinfinity.cloud.domain.Instance;
import org.openinfinity.cloud.domain.InstanceParameter;
import org.openinfinity.core.annotation.AuditTrail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

/**
 * Jdbc implementation of Instance repository interface
 
 * @author Ossi Hämäläinen
 * @author Ilkka Leinonen
 * @author Juha-Matti Sironen
 * @version 1.0.0 Initial version
 * @since 1.0.0
 */

@Repository("instanceRepository")
public class InstanceRepositoryJdbcImpl implements InstanceRepository {
	
	private static final Logger LOG = Logger.getLogger(InstanceRepositoryJdbcImpl.class.getName());
	
	private JdbcTemplate jdbcTemplate;
	private DataSource dataSource;
	
	@Autowired
	public InstanceRepositoryJdbcImpl(@Qualifier("cloudDataSource") DataSource ds) {
		Assert.notNull(ds, "Please define datasource for instance repository.");
		this.jdbcTemplate = new JdbcTemplate(ds);
		this.dataSource = ds;
	}
	
	@AuditTrail
	public Instance getInstance(int instanceId) {
		List<Instance> instances = this.jdbcTemplate.query("select * from instance_tbl where instance_active = 1 and instance_id = ?", new Object[] { instanceId }, new InstanceMapper());
		Instance instance = DataAccessUtils.singleResult(instances);
		if (instance!=null){
			instance.setParameters(getParameters(instance.getInstanceId()));
		}
		return instance;
	}
	
	@AuditTrail
	public Instance getInstanceAlsoPassive(int instanceId) {
		List<Instance> instances = this.jdbcTemplate.query("select * from instance_tbl where instance_id = ?", new Object[] { instanceId }, new InstanceMapper());
		Instance instance = DataAccessUtils.singleResult(instances);
		return instance;
	}
	
	@AuditTrail
	public Collection<Instance> getInstances(Long userId) {
		Collection<Instance> instances = this.jdbcTemplate.query("select * from instance_tbl where instance_active = 1 and user_id = ?", new Object[] { userId }, new InstanceMapper());
		if (instances!=null){
			for (Instance instance:instances){
				instance.setParameters(getParameters(instance.getInstanceId()));
			}
		}
		return instances;
	}
	
	@AuditTrail
	public Collection<Instance> getOrganizationInstances(Long organizationId) {
		Collection<Instance> instances = this.jdbcTemplate.query("select * from instance_tbl where instance_active = 1 and organization_id = ?", new Object[] {organizationId}, new InstanceMapper());
		if (instances!=null){
			for (Instance instance:instances){
				instance.setParameters(getParameters(instance.getInstanceId()));
			}
		}
		return instances;
	}

	@AuditTrail
	public Instance getInstance(String name) {
		Collection<Instance> instances = this.jdbcTemplate.query("select * from instance_tbl where instance_active = 1 and instance_name = ?", new Object[] { name }, new InstanceMapper());
		Instance instance = DataAccessUtils.singleResult(instances);
		if (instance!=null){
			instance.setParameters(getParameters(instance.getInstanceId()));
		}
		return instance;
	}

	@AuditTrail
	public Instance getInstance(int userId, String name) {
		Collection<Instance> instances = this.jdbcTemplate.query("select * from instance_tbl where instance_active = 1 and instance_name = ? and user_id = ?", new Object[] { name, userId }, new InstanceMapper());
		Instance instance = DataAccessUtils.singleResult(instances);
		if (instance!=null){
			instance.setParameters(getParameters(instance.getInstanceId()));
		}
		return instance;
	}
	
	@AuditTrail
	public Instance getInstanceByMachineId(int machineId) {
		Collection<Instance> instances = this.jdbcTemplate.query("select * from instance_tbl where instance_id = (select instance_id from cluster_tbl where cluster_id = (select machine_cluster_id from machine_tbl where machine_id = ?))", new Object[] {machineId}, new InstanceMapper());
		Instance instance = DataAccessUtils.singleResult(instances);
		if (instance!=null){
			instance.setParameters(getParameters(instance.getInstanceId()));
		}
		return instance;
	}
	
	@AuditTrail
	public void updateInstanceStatus(int instanceId, String status) {
		this.jdbcTemplate.execute("update instance_tbl set instance_status = '"+status+"' where instance_id = "+instanceId);
	}

	@AuditTrail
	public void addInstance(Instance instance) {
		SimpleJdbcInsert insert = new SimpleJdbcInsert(dataSource).withTableName("instance_tbl").usingGeneratedKeyColumns("instance_id");
		Map<String,Object> parameters = new HashMap<String,Object>();
		parameters.put("user_id", instance.getUserId());
		parameters.put("instance_name", instance.getName());
		parameters.put("cloud_type", instance.getCloudType());
		parameters.put("cloud_zone", instance.getZone());
		parameters.put("organization_id", instance.getOrganizationid());
		parameters.put("instance_status", instance.getStatus());
		parameters.put("instance_active", 1);
		Number newId = insert.executeAndReturnKey(parameters);
		LOG.info("Instance id: "+newId);
		instance.setInstanceId(newId.intValue());
		
		if (instance.getParameters()!=null){
			for (InstanceParameter parameter:instance.getParameters()){
				SimpleJdbcInsert paraminsert = new SimpleJdbcInsert(dataSource).withTableName("instance_parameter_tbl").usingGeneratedKeyColumns("id");
				Map<String,Object> parametersmap = new HashMap<String,Object>();
				parametersmap.put("instance_id", newId);
				parametersmap.put("pkey", parameter.getKey());
				parametersmap.put("pvalue", parameter.getValue());
				paraminsert.executeAndReturnKey(parametersmap);
			}
		}		
	}

	@AuditTrail
	public void deleteInstance(int instanceId) {
		this.jdbcTemplate.execute("update instance_tbl set instance_active = 0 where instance_id = "+instanceId);
	}

	private List<InstanceParameter> getParameters(int instanceId){
		List<InstanceParameter> parameters= this.jdbcTemplate.query("select * from instance_parameter_tbl where instance_id = ?", new Object[] {instanceId}, new InstanceParameterMapper());		
		return parameters;
	}
	
	private static final class InstanceMapper implements RowMapper<Instance> {

		public Instance mapRow(ResultSet rs, int rowNum) throws SQLException {
			Instance instance = new Instance();
			instance.setInstanceId(rs.getInt("instance_id"));
			instance.setUserId(rs.getInt("user_id"));
			instance.setName(rs.getString("instance_name"));
			instance.setCloudType(rs.getInt("cloud_type"));
			instance.setZone(rs.getString("cloud_zone"));
			instance.setOrganizationid(rs.getLong("organization_id"));
			instance.setStatus(rs.getString("instance_status"));
			return instance;
		}
		
	}
	
	private static final class InstanceParameterMapper implements RowMapper<InstanceParameter> {

		public InstanceParameter mapRow(ResultSet rs, int rowNumber) throws SQLException {
			InstanceParameter parameter = new InstanceParameter();
			parameter.setInstanceId(rs.getInt("instance_id"));
			parameter.setId(rs.getInt("id"));
			parameter.setKey(rs.getString("pkey"));
			parameter.setValue(rs.getString("pvalue"));
			return parameter;
		}		
	}

	@Override
	public Collection<Instance> getAllActiveInstances() {
		Collection<Instance> instances = this.jdbcTemplate.query("select * from instance_tbl where instance_active = 1", new InstanceMapper());
		if (instances!=null){
			for (Instance instance:instances){
				instance.setParameters(getParameters(instance.getInstanceId()));
			}
		}
		return instances;
	}
}
