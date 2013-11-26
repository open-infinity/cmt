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
package org.openinfinity.cloud.domain.repository.configurationtemplate;

import org.openinfinity.cloud.domain.configurationtemplate.ConfigurationElement;
import org.openinfinity.core.annotation.AuditTrail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.sql.DataSource;
import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

/**
 * @author Vedran Bartonicek
 * @version 1.3.0
 * @since 1.3.0
 */
@Repository
public class ConfigurationElementRepositoryJdbcImpl implements ConfigurationElementRepository {

	private static final String GET_ALL_SQL = "select * from configuration_element_tbl";

	private static final String GET_BY_ID_SQL = "select * from configuration_element_tbl where id = ?";

    private static final String GET_ALL_FOR_TEMPLATE_SQL =
            "select configuration_element_tbl.* from configuration_element_tbl " +
            "inner join configuration_template_element_tbl on " +
            "configuration_element_tbl.id = configuration_template_element_tbl.element_id " +
            "where configuration_template_element_tbl.template_id = ?";

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public ConfigurationElementRepositoryJdbcImpl(@Qualifier("cloudDataSource") DataSource dataSource) {
        Assert.notNull(dataSource, "Please define datasource for scaling rule repository.");
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }
    @Override
    public ConfigurationElement create(ConfigurationElement configurationElement) {
        return null;
    }

    @Override
    public void update(ConfigurationElement configurationElement) {
    }

    @AuditTrail
    @Transactional
    public Collection<ConfigurationElement> loadAll() {
        return jdbcTemplate.query(GET_ALL_SQL, new ConfigurationElementRowMapper());
    }

    @AuditTrail
    @Transactional
    public Collection<ConfigurationElement> loadAllForTemplate(int templateId) {
        return jdbcTemplate.query(GET_ALL_FOR_TEMPLATE_SQL, new Object[] {templateId}, new ConfigurationElementRowMapper());
    }

    @Override
    public ConfigurationElement load(BigInteger id) {
        return null;
    }

    @AuditTrail
    @Transactional
    public ConfigurationElement load(int templateId) {
        return jdbcTemplate.queryForObject(GET_BY_ID_SQL, new Object[] {templateId}, new ConfigurationElementRowMapper());
    }

    @Override
    public void delete(ConfigurationElement configurationElement) {
    }

    private class ConfigurationElementRowMapper implements RowMapper<ConfigurationElement> {
		public ConfigurationElement mapRow(ResultSet resultSet, int rowNum) throws SQLException {
		    return new ConfigurationElement(resultSet.getInt("id"),
		                       resultSet.getInt("type"),
		                       resultSet.getString("name"),
		                       resultSet.getString("version"),
		                       resultSet.getString("description"),
		                       resultSet.getInt("parameterKey"),
		                       resultSet.getInt("minMachines"),
		                       resultSet.getInt("maxMachines"),
                               resultSet.getBoolean("replicated"),
                               resultSet.getInt("minReplicationMachines"),
		                       resultSet.getInt("maxReplicationMachines"));
		}
	}

}
