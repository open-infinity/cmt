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
package org.openinfinity.cloud.domain.repository.properties;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.openinfinity.cloud.domain.SharedProperty;
import org.openinfinity.core.annotation.AuditTrail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

/**
 * Repository layer of Centralized Properties.
 * 
 * @author Timo Saarinen
 * @author Ilkka Leinonen
 */
@Service(value="centralizedPropertiesRepository")
@Qualifier("centralizedPropertiesRepository")
public class CentralizedPropertiesRepositoryJdbcImpl implements CentralizedPropertiesRepository {

	private static final Logger logger = Logger.getLogger(CentralizedPropertiesRepositoryJdbcImpl.class.getName());

	// See create_tables.sql for more information
	private static final String WHERE = "where key_column = :key and organization_id = :orgid and instance_id = :insid and cluster_id = :clid";
	private static final String WHERE2 = "where organization_id = :orgid and instance_id = :insid and cluster_id = :clid";
	
	private static final String WHERE3 = "where organization_id = :orgid and instance_id = :insid and cluster_id = :clid and state=0";
	private static final String WHERE4 = "where organization_id = :orgid and instance_id = :insid and cluster_id = :clid and state=-1";
	
	private static final String STORE_SQL = "insert into properties_tbl (organization_id, instance_id, cluster_id, key_column, value_column, changed_last_update) " + 
												"values (:orgid, :insid, :clid, :key, :value, CURRENT_TIMESTAMP)";
	private static final String UPDATE_SQL = "update properties_tbl set key_column = :key, value_column = :value, state = :state, changed_last_update = CURRENT_TIMESTAMP " + WHERE;	
	
	private static final String UPDATE_STATE_BY_UNIQUE_ID = "update properties_tbl set state = :state, changed_last_update = CURRENT_TIMESTAMP where id = :id";		
	
	private static final String UPDATE_STATES_TO_DEPLOYED_SQL = "update properties_tbl set state=1, changed_last_update = CURRENT_TIMESTAMP " + WHERE3;	
	private static final String DELETE_BY_STATE_ORG_INST_CLUS = "delete from properties_tbl " + WHERE4;	

	private static final String DELETE_BY_CLUSTERID = "delete from properties_tbl where cluster_id = :clid";
	
	private static final String LOAD_BY_KEY = "select * from properties_tbl " + WHERE;
	private static final String COUNT_BY_KEY = "select count(*) from properties_tbl " + WHERE;
	private static final String LOAD_ALL_SQL = "select * from properties_tbl";
	private static final String LOAD_ALL_SQL_WHERE = "select * from properties_tbl " + WHERE2;
	private static final String DELETE_BY_KEY = "delete from properties_tbl " + WHERE;
	private static final String LOAD_DISTINCT_SHARED_PROPERTIES_CLUSTERS_SQL = "SELECT DISTINCT * FROM properties_tbl";
	private static final String DELETE_BY_UNIQUE_ID = "delete from properties_tbl where id = :id";
	
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	private JdbcTemplate jdbcTemplate;

	@Autowired
	public CentralizedPropertiesRepositoryJdbcImpl(@Qualifier("cloudDataSource") DataSource dataSource) {
		Assert.notNull(dataSource, "Please define datasource for deployer repository.");
		namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@AuditTrail
	@Override
	public SharedProperty store(SharedProperty prop) {
		MapSqlParameterSource map = new MapSqlParameterSource();
		map.addValue("key", prop.getKey());
		map.addValue("value", prop.getValue());
		map.addValue("orgid", prop.getOrganizationId());
		map.addValue("insid", prop.getInstanceId());
		map.addValue("clid", prop.getClusterId());
		map.addValue("state", prop.getState());		
		int n = namedParameterJdbcTemplate.queryForInt(COUNT_BY_KEY, map);
		if (n == 0) {
			map.addValue("value", prop.getValue());
			namedParameterJdbcTemplate.update(STORE_SQL, map);
		} else {
			namedParameterJdbcTemplate.update(UPDATE_SQL, map);
		}
		return prop;
	}		
	
	@AuditTrail
	@Override
	public Collection<SharedProperty> loadAll(SharedProperty prop) {
		MapSqlParameterSource map = new MapSqlParameterSource();
		map.addValue("orgid", prop.getOrganizationId());
		map.addValue("insid", prop.getInstanceId());
		map.addValue("clid", prop.getClusterId());
		Collection<SharedProperty> props = namedParameterJdbcTemplate.query(LOAD_ALL_SQL_WHERE, map, new SharedPropertyRowMapper());
		return Collections.unmodifiableCollection(props);
	}
	
	@AuditTrail
	@Override
	public Collection<SharedProperty> loadAll() {
		Collection<SharedProperty> props = jdbcTemplate.query(LOAD_ALL_SQL, new SharedPropertyRowMapper());
		return Collections.unmodifiableCollection(props);
	}
	
	@AuditTrail
	@Override
	public Collection<SharedProperty> loadKnownSharedPropertyDeployments() {
		Collection<SharedProperty> props = jdbcTemplate.query(LOAD_DISTINCT_SHARED_PROPERTIES_CLUSTERS_SQL, new SharedPropertyRowMapper());
		return Collections.unmodifiableCollection(props);
	}
	
	@AuditTrail
	@Override
	public SharedProperty load(SharedProperty prop) {
		try {
			MapSqlParameterSource map = new MapSqlParameterSource();
			map.addValue("orgid", prop.getOrganizationId());
			map.addValue("insid", prop.getInstanceId());
			map.addValue("clid", prop.getClusterId());
			map.addValue("key", prop.getKey());
			SharedProperty p = (SharedProperty) namedParameterJdbcTemplate.queryForObject(LOAD_BY_KEY, map, new SharedPropertyRowMapper());
			return p;
		} catch (org.springframework.dao.EmptyResultDataAccessException e) {
			logger.warn("Failed to load shared property by key: " + e.getMessage());
			return null;
		}
	}
	
	@AuditTrail
	@Override
	public boolean delete(SharedProperty prop) {
		MapSqlParameterSource map = new MapSqlParameterSource();
		map.addValue("orgid", prop.getOrganizationId());
		map.addValue("insid", prop.getInstanceId());
		map.addValue("clid", prop.getClusterId());
		map.addValue("key", prop.getKey());
		int n = namedParameterJdbcTemplate.update(DELETE_BY_KEY, map);
		return (n >= 1); 
	}

	@AuditTrail
	@Override
	public void deleteByStateOrgInstClusName(long organizationId, int instanceId, int clusterId) {
		MapSqlParameterSource map = new MapSqlParameterSource();
		map.addValue("orgid", organizationId);
		map.addValue("insid", instanceId);
		map.addValue("clid", clusterId);
		int n = namedParameterJdbcTemplate.update(DELETE_BY_STATE_ORG_INST_CLUS, map);
		//return (n >= 1); 
	}
	
	public void deleteByCluster(int clusterId) {
		MapSqlParameterSource map = new MapSqlParameterSource();
		map.addValue("clid", clusterId);
		int n = namedParameterJdbcTemplate.update(DELETE_BY_CLUSTERID, map);		
	}
	
	
	@AuditTrail
	@Override 
	public void update(SharedProperty prop) {
		MapSqlParameterSource map = new MapSqlParameterSource();
		map.addValue("key", prop.getKey());
		map.addValue("value", prop.getValue());
		map.addValue("orgid", prop.getOrganizationId());
		map.addValue("insid", prop.getInstanceId());
		map.addValue("clid", prop.getClusterId());
		map.addValue("state", prop.getState());
		namedParameterJdbcTemplate.update(UPDATE_SQL, map);
	}
	
	@AuditTrail
	@Override 
	public void updateStatesNewToFinalizedByOrgInstClusName(long organizationId, int instanceId, int clusterId) {
		MapSqlParameterSource map = new MapSqlParameterSource();
		map.addValue("orgid", organizationId);
		map.addValue("insid", instanceId);
		map.addValue("clid", clusterId);
		namedParameterJdbcTemplate.update(UPDATE_STATES_TO_DEPLOYED_SQL, map);		
	}
	
	public boolean updateStateByUniqueId(int id, int state) {
		MapSqlParameterSource map = new MapSqlParameterSource();
		map.addValue("id", id);
		map.addValue("state", state);
		int n = namedParameterJdbcTemplate.update(UPDATE_STATE_BY_UNIQUE_ID, map);
		return (n >= 1); 		
	}
	
	@AuditTrail
	@Override
	public boolean deleteByUniqueId(int id) {
		MapSqlParameterSource map = new MapSqlParameterSource();
		map.addValue("id", id);
		int n = namedParameterJdbcTemplate.update(DELETE_BY_UNIQUE_ID, map);
		return (n >= 1); 
	}
	
	private class SharedPropertyRowMapper implements RowMapper<SharedProperty> {
		@Override
		public SharedProperty mapRow(ResultSet resultSet, int rowNum) throws SQLException {
			SharedProperty sharedProperty = new SharedProperty();
			sharedProperty.setId(resultSet.getInt("id"));
			sharedProperty.setOrganizationId(resultSet.getInt("organization_id"));
			sharedProperty.setInstanceId(resultSet.getInt("instance_id"));
			sharedProperty.setClusterId(resultSet.getInt("cluster_id"));
			sharedProperty.setKey(resultSet.getString("key_column"));
			sharedProperty.setValue(resultSet.getString("value_column"));
			sharedProperty.setState(resultSet.getInt("state"));
			sharedProperty.setPropertyTimestamp(resultSet.getTimestamp("changed_last_update"));
			return sharedProperty;
		}
	}
}
