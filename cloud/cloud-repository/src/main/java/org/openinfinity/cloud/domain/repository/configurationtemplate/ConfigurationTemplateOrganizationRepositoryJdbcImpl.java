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

import org.openinfinity.cloud.domain.configurationtemplate.ConfigurationTemplateOrganization;
import org.openinfinity.core.annotation.AuditTrail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

/**
 * @author Vedran Bartonicek
 * @version 1.3.0
 * @since 1.3.0
 */
@Repository
public class ConfigurationTemplateOrganizationRepositoryJdbcImpl implements ConfigurationTemplateOrganizationRepository {

    private static final String CREATE_SQL = "insert into cfg_template_organization_tbl values(?, ?)";

    private static final String LOAD_ALL_FOR_TEMPLATE_SQL = "select * from cfg_template_organization_tbl where template_id = ?";

    private static final String DELETE_BY_ORGANIZATION_ID_SQL = "delete from cfg_template_organization_tbl where organization_id = ?";

    private static final String DELETE_BY_TEMPLATE_ID_SQL = "delete from cfg_template_organization_tbl where template_id = ?";

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public ConfigurationTemplateOrganizationRepositoryJdbcImpl(@Qualifier("cloudDataSource") DataSource dataSource) {
        Assert.notNull(dataSource, "Please define datasource for scaling rule repository.");
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public void create(int templateId, int organizationId) {
        jdbcTemplate.update(CREATE_SQL, templateId, organizationId);
    }

    @AuditTrail
    @Transactional
    public List<ConfigurationTemplateOrganization> loadAll() {
        return null;
    }

    @Override
    public Collection<ConfigurationTemplateOrganization> loadAllForTemplate(int templateId){
        return jdbcTemplate.query(LOAD_ALL_FOR_TEMPLATE_SQL, new Object[] {templateId}, new OrganizationRowMapper());
    }

    @Override
    public void deleteByOrganization(int organizationId) {
        jdbcTemplate.update(DELETE_BY_ORGANIZATION_ID_SQL, organizationId);

    }

    @Override
    public void deleteByTemplate(int templateId) {
        jdbcTemplate.update(DELETE_BY_TEMPLATE_ID_SQL, templateId);
    }

    private class OrganizationRowMapper implements RowMapper<ConfigurationTemplateOrganization> {
		
		public ConfigurationTemplateOrganization mapRow(ResultSet resultSet, int rowNum) throws SQLException {
		    return new ConfigurationTemplateOrganization(resultSet.getLong("template_id"), resultSet.getInt("organization_id"));
		}
	}

}
