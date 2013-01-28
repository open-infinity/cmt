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
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.util.Assert;

import org.apache.log4j.Logger;
import org.openinfinity.cloud.domain.SharedProperty;
import org.springframework.stereotype.Service;

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
	
	private static final String STORE_SQL = "insert into cloud_properties_tbl (key_column, value_column, organization_id) values (:key, :value, :orgid)";
	private static final String UPDATE_SQL = "update cloud_properties_tbl set key_column = :key, value_column = :value where key_column = :key and organization_id = :orgid";
	private static final String LOAD_BY_KEY = "select * from cloud_properties_tbl where key_column = :key and organization_id = :orgid";
	private static final String COUNT_BY_KEY = "select count(*) from cloud_properties_tbl where key_column = :key and organization_id in (:orgids)";
	private static final String LOAD_ALL_SQL = "select * from cloud_properties_tbl where organization_id in (:orgids)";
	private static final String DELETE_BY_KEY = "delete from cloud_properties_tbl where key_column = :key and organization_id in (:orgids)";

	private NamedParameterJdbcTemplate jdbcTemplate;

	@Autowired
	public CentralizedPropertiesRepositoryJdbcImpl(@Qualifier("cloudDataSource") DataSource dataSource) {
		Assert.notNull(dataSource, "Please define datasource for deployer repository.");
		jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	@Override
	public SharedProperty store(SharedProperty prop) {
		MapSqlParameterSource map = new MapSqlParameterSource();
		map.addValue("key", prop.getKey());
		map.addValue("orgid", prop.getOrganizationId());
		int n = jdbcTemplate.queryForInt(COUNT_BY_KEY, map);
		if (n == 0) {
			map.addValue("value", prop.getValue());
			jdbcTemplate.update(STORE_SQL, map);
		} else {
			jdbcTemplate.update(UPDATE_SQL, map);
		}
		return prop;
	}
	
	@Override
	public Collection<SharedProperty> loadAll(List<String> organizationIds) {
		MapSqlParameterSource map = new MapSqlParameterSource();
		map.addValue("orgids", organizationIds);
		Collection<SharedProperty> props = jdbcTemplate.query(LOAD_ALL_SQL, map, new SharedPropertyRowMapper());
		return Collections.unmodifiableCollection(props);
	}
	
	@Override
	public SharedProperty loadByKey(String organizationId, String key) {
		try {
			MapSqlParameterSource map = new MapSqlParameterSource();
			map.addValue("key", key);
			map.addValue("orgid", organizationId);
			SharedProperty p = (SharedProperty) jdbcTemplate.queryForObject(LOAD_BY_KEY, map, new SharedPropertyRowMapper());
			return p;
		} catch (org.springframework.dao.EmptyResultDataAccessException e) {
			logger.warn("Failed to load shared property by key: " + e.getMessage());
			return null;
		}
	}
	
	@Override
	public boolean deleteByKey(String organizationId, String key) {
		MapSqlParameterSource map = new MapSqlParameterSource();
		map.addValue("key", key);
		map.addValue("orgid", organizationId);
		int n = jdbcTemplate.update(DELETE_BY_KEY, map);
		return (n >= 1); 
	}
	
	private class SharedPropertyRowMapper implements RowMapper<SharedProperty> {

		@Override
		public SharedProperty mapRow(ResultSet resultSet, int rowNum) throws SQLException {
			SharedProperty p = new SharedProperty();
			p.setOrganizationId(resultSet.getString("ORGANIZATION_ID"));
			p.setKey(resultSet.getString("KEY_COLUMN"));
			p.setValue(resultSet.getString("VALUE_COLUMN"));
			return p;
		}
	
	}
}
