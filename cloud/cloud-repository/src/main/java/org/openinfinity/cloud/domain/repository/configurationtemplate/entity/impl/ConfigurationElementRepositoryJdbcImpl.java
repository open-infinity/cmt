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
package org.openinfinity.cloud.domain.repository.configurationtemplate.entity.impl;

import org.openinfinity.cloud.domain.configurationtemplate.entity.ConfigurationElement;
import org.openinfinity.cloud.domain.repository.configurationtemplate.entity.api.ConfigurationElementRepository;
import org.openinfinity.core.annotation.AuditTrail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import javax.sql.DataSource;
import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Vedran Bartonicek
 * @version 1.3.0
 * @since 1.3.0
 */
@Repository
public class ConfigurationElementRepositoryJdbcImpl implements ConfigurationElementRepository {

    private static final String DELETE_SQL = "delete from cfg_element_tbl where id = ?";

    private static final String LOAD_ALL_SQL = "select * from cfg_element_tbl";

	private static final String LOAD_BY_ID_SQL = "select * from cfg_element_tbl where id = ?";

    private static final String LOAD_ALL_FOR_TEMPLATE_SQL =
            "select cfg_element_tbl.* from cfg_element_tbl " +
            "inner join cfg_template_element_tbl on " +
            "cfg_element_tbl.id = cfg_template_element_tbl.element_id " +
            "where cfg_template_element_tbl.template_id = ?";

    private static final String LOAD_DEPENDEES_SQL =
            "select cfg_element_tbl.* from cfg_element_tbl " +
            "inner join cfg_element_dependency_tbl on " +
            "cfg_element_tbl.id = cfg_element_dependency_tbl.element_to " +
            "where cfg_element_dependency_tbl.element_from = ?";

    private static final String UPDATE_SQL = "update cfg_element_tbl set type = ?, name = ?,  version = ?, description = ?, " +
            "minMachines = ?, maxMachines = ?, replicated = ?, minReplicationMachines = ?, maxReplicationMachines = ? where id = ?";

    private JdbcTemplate jdbcTemplate;

    private DataSource dataSource;

    @Autowired
    public ConfigurationElementRepositoryJdbcImpl(@Qualifier("cloudDataSource") DataSource dataSource) {
        Assert.notNull(dataSource, "Please define datasource.");
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.dataSource = dataSource;
    }

    @Override
    public ConfigurationElement create(ConfigurationElement element) {
        SimpleJdbcInsert insert = new SimpleJdbcInsert(dataSource).withTableName("cfg_element_tbl").usingGeneratedKeyColumns("id");
        Map<String,Object> parameters = new HashMap<String,Object>();
        parameters.put("type", element.getName());
        parameters.put("name", element.getDescription());
        parameters.put("version", element.getVersion());
        parameters.put("description", element.getDescription());
        parameters.put("minMachines", element.getMinMachines());
        parameters.put("maxMachines", element.getMaxMachines());
        parameters.put("replicated", element.isReplicated());
        parameters.put("minReplicationMachines", element.getMinReplicationMachines());
        parameters.put("maxReplicationMachines", element.getMaxReplicationMachines());
        Number newId = insert.executeAndReturnKey(parameters);
        element.setId(newId.intValue());
        return element;
    }

    @Override
    @AuditTrail
    public void update(ConfigurationElement e) {
        jdbcTemplate.update(UPDATE_SQL, e.getType(), e.getName(), e.getVersion(), e.getDescription(), e.getMinMachines(), e.getMaxMachines(), e.isReplicated(),
                e.getMinReplicationMachines(), e.getMaxReplicationMachines(), e.getId());
    }

    @Override
    @AuditTrail
    public Collection<ConfigurationElement> loadAll() {
        return jdbcTemplate.query(LOAD_ALL_SQL, new ConfigurationElementRowMapper());
    }

    @Override
    @AuditTrail
    public Collection<ConfigurationElement> loadAllForTemplate(int templateId) {
        return jdbcTemplate.query(LOAD_ALL_FOR_TEMPLATE_SQL, new Object[] {templateId}, new ConfigurationElementRowMapper());
    }

    @Override
    @AuditTrail
    public Collection<ConfigurationElement> loadDependees(int elementId) {
        return jdbcTemplate.query(LOAD_DEPENDEES_SQL, new Object[]{elementId}, new ConfigurationElementRowMapper());
    }

    @Override
    @AuditTrail
    public ConfigurationElement load(BigInteger id) {
        return jdbcTemplate.queryForObject(LOAD_BY_ID_SQL, new Object[] {id.intValue()}, new ConfigurationElementRowMapper());
    }

    @Override
    @AuditTrail
    public void delete(ConfigurationElement configurationElement) {
        jdbcTemplate.update(DELETE_SQL, configurationElement.getId());
    }

    @Override
    public void delete(BigInteger id) {
        jdbcTemplate.update(DELETE_SQL, id.intValue());
    }

    private class ConfigurationElementRowMapper implements RowMapper<ConfigurationElement> {
		public ConfigurationElement mapRow(ResultSet resultSet, int rowNum) throws SQLException {
		    return new ConfigurationElement(resultSet.getInt("id"),
		                       resultSet.getInt("type"),
		                       resultSet.getString("name"),
		                       resultSet.getString("version"),
		                       resultSet.getString("description"),
		                       resultSet.getInt("minMachines"),
		                       resultSet.getInt("maxMachines"),
                               resultSet.getBoolean("replicated"),
                               resultSet.getInt("minReplicationMachines"),
		                       resultSet.getInt("maxReplicationMachines"));
		}
	}

}
