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

import org.openinfinity.cloud.domain.configurationtemplate.entity.ConfigurationTemplate;
import org.openinfinity.cloud.domain.repository.configurationtemplate.entity.api.ConfigurationTemplateRepository;
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

    private static final String DELETE_SQL = "delete from cfg_template_tbl where id = ?";

    private static final String UPDATE_SQL = "update cfg_template_tbl set name = ?, description = ? where id = ?";

    private static final String LOAD_ALL_SQL = "select * from cfg_template_tbl";

    private static final String LOAD_BY_ID_SQL = "select * from cfg_template_tbl where id = ?";

    private static final String LOAD_ALL_FOR_ORGANIZATION_SQL =
        "select cfg_template_tbl.id, cfg_template_tbl.name, " + 
        "cfg_template_tbl.description from cfg_template_tbl " +
        "inner join cfg_template_organization_tbl on " +
        "cfg_template_tbl.id = cfg_template_organization_tbl.template_id " +
        "where organization_id = ?";

    private DataSource dataSource;

    @Autowired
    public ConfigurationTemplateRepositoryJdbcImpl(@Qualifier("cloudDataSource") DataSource dataSource) {
        Assert.notNull(dataSource, "Please define datasource.");
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.dataSource = dataSource;
    }
    
    @AuditTrail
    @Transactional
    public List<ConfigurationTemplate> loadForOrganizarion(Long oid) {
        return jdbcTemplate.query(LOAD_ALL_FOR_ORGANIZATION_SQL, new Object[] {oid}, new TemplateRowMapper());
    }

    @Override
    public ConfigurationTemplate create(ConfigurationTemplate configurationTemplate) {
        SimpleJdbcInsert insert = new SimpleJdbcInsert(dataSource).withTableName("cfg_template_tbl").usingGeneratedKeyColumns("id");
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
        return jdbcTemplate.query(LOAD_ALL_SQL, new TemplateRowMapper());
    }

    @Override
    public ConfigurationTemplate load(BigInteger id) {
        return jdbcTemplate.queryForObject(LOAD_BY_ID_SQL, new Object[] {id}, new TemplateRowMapper());
    }

    @Override
    public void delete(ConfigurationTemplate configurationTemplate) {
        jdbcTemplate.update(DELETE_SQL, configurationTemplate.getId());
    }

    @Override
    public void delete(BigInteger id) {
        jdbcTemplate.update(DELETE_SQL, id.intValue());
    }

    private class TemplateRowMapper implements RowMapper<ConfigurationTemplate> {
		public ConfigurationTemplate mapRow(ResultSet resultSet, int rowNum) throws SQLException {    
		    return new ConfigurationTemplate(resultSet.getInt("id"),
		                        resultSet.getString("name"),
		                        resultSet.getString("description"));
		}
	}
	
}
