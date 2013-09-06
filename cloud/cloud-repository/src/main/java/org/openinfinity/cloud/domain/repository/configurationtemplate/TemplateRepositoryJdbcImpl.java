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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import org.openinfinity.cloud.domain.configurationtemplate.Template;
import org.openinfinity.core.annotation.AuditTrail;

/**
 * JDBC Repository implementation of the <code>org.openinfinity.core.cloud.deployer.repository.DeploymentRepository</code> interface.
 * 
 * @author Vedran Bartonicek
 * @version 1.3.0
 * @since 1.3.0
 */
@Repository
public class TemplateRepositoryJdbcImpl implements TemplateRepository {

	private JdbcTemplate jdbcTemplate;
	
    private static final String GET_ALL_FOR_ORGANIZATION_SQL = 
        "select configuration_template_tbl.id, configuration_template_tbl.name, " + 
        "configuration_template_tbl.description from configuration_template_tbl " +
        "inner join configuration_template_organization_tbl on " +
        "configuration_template_tbl.id = configuration_template_organization_tbl.template_id " +
        "where organization_id = ?";
    
    @Autowired
    public TemplateRepositoryJdbcImpl(@Qualifier("cloudDataSource") DataSource dataSource) {
        Assert.notNull(dataSource, "Please define datasource for scaling rule repository.");
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }
    
    @AuditTrail
    @Transactional
    public List<Template> getTemplates(Long oid) {
        return jdbcTemplate.query(GET_ALL_FOR_ORGANIZATION_SQL,
                                  new Object[] {oid},
                                  new TemplateRowMapper());
    }

	private class TemplateRowMapper implements RowMapper<Template> {
		public Template mapRow(ResultSet resultSet, int rowNum) throws SQLException {    
		    return new Template(resultSet.getInt("id"),
		                        resultSet.getString("name"),
		                        resultSet.getString("description"));
		}
	}
	
}
