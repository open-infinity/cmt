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

import org.openinfinity.cloud.domain.configurationtemplate.ConfigurationTemplate;
import org.openinfinity.core.annotation.AuditTrail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.sql.DataSource;
import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Vedran Bartonicek
 * @version 1.3.0
 * @since 1.3.0
 */
@Repository
public class ConfigurationTemplateRepositoryJdbcImpl implements ConfigurationTemplateRepository {

	private JdbcTemplate jdbcTemplate;

    private static final String CREATE_SQL = "insert into configuration_template_tbl (name, description) values(?, ?)";

    private static final String DELETE_SQL = "delete from configuration_template_tbl where id = ?";

    private static final String UPDATE_SQL = "update configuration_template_tbl set name = ?, description = ? where id = ?";

    private static final String GET_ALL_SQL = "select * from configuration_template_tbl";

    private static final String GET_ALL_FOR_ORGANIZATION_SQL =
        "select configuration_template_tbl.id, configuration_template_tbl.name, " + 
        "configuration_template_tbl.description from configuration_template_tbl " +
        "inner join configuration_template_organization_tbl on " +
        "configuration_template_tbl.id = configuration_template_organization_tbl.template_id " +
        "where organization_id = ?";

    private static final String GET_BY_ID_SQL = "select * from configuration_template_tbl where id = ?";

    private DataSource dataSource;

    @Autowired
    public ConfigurationTemplateRepositoryJdbcImpl(@Qualifier("cloudDataSource") DataSource dataSource) {
        Assert.notNull(dataSource, "Please define datasource for scaling rule repository.");
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.dataSource = dataSource;
    }
    
    @AuditTrail
    @Transactional
    public List<ConfigurationTemplate> getTemplates(Long oid) {
        return jdbcTemplate.query(GET_ALL_FOR_ORGANIZATION_SQL,
                                  new Object[] {oid},
                                  new TemplateRowMapper());
    }

    @Override
    public ConfigurationTemplate create(ConfigurationTemplate configurationTemplate) {
        SimpleJdbcInsert insert = new SimpleJdbcInsert(dataSource).withTableName("configuration_template_tbl").usingGeneratedKeyColumns("id");
        Map<String,Object> parameters = new HashMap<String,Object>();
        parameters.put("name", configurationTemplate.getName());
        parameters.put("description", configurationTemplate.getDescription());
        Number newId = insert.executeAndReturnKey(parameters);
        configurationTemplate.setId(newId.intValue());
        return configurationTemplate;
    }

    @Override
    public void update(ConfigurationTemplate configurationTemplate) {
        jdbcTemplate.update(UPDATE_SQL, configurationTemplate.getName(), configurationTemplate.getDescription(), configurationTemplate.getId());
    }

    @Override
    public Collection<ConfigurationTemplate> loadAll() {
        return jdbcTemplate.query(GET_ALL_SQL, new TemplateRowMapper());
    }

    @Override
    public ConfigurationTemplate load(BigInteger id) {
        return jdbcTemplate.queryForObject(GET_BY_ID_SQL, new Object[] {id}, new TemplateRowMapper());
    }

    @Override
    public void delete(ConfigurationTemplate configurationTemplate) {
        jdbcTemplate.update(DELETE_SQL, configurationTemplate.getId());
    }

    @Override
    public void delete(int templateId) {
        jdbcTemplate.update(DELETE_SQL, templateId);
    }

    private class TemplateRowMapper implements RowMapper<ConfigurationTemplate> {
		public ConfigurationTemplate mapRow(ResultSet resultSet, int rowNum) throws SQLException {    
		    return new ConfigurationTemplate(resultSet.getInt("id"),
		                        resultSet.getString("name"),
		                        resultSet.getString("description"));
		}
	}
	
}
