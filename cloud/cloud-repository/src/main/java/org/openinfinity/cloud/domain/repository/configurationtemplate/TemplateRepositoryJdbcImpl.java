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
package org.openinfinity.cloud.domain.repository.configurationtemplate;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.openinfinity.cloud.domain.configurationtemplate.Template;
import org.openinfinity.core.annotation.AuditTrail;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * JDBC Repository implementation of the <code>org.openinfinity.core.cloud.deployer.repository.DeploymentRepository</code> interface.
 * 
 * @author Ilkka Leinonen
 * @author Tommi Siitonen
 * @version 1.1.0
 * @since 1.0.0
 */
@Repository
public class TemplateRepositoryJdbcImpl implements
TemplateRepository {

	private JdbcTemplate jdbcTemplate;
	private static final String GET_BY_ORGANIZATION_SQL = 
	        "SELECT * FROM CONFIGURATION_TEMPLATE_TABLE WHERE ORGANIZATION_ID = ?";
	
    
    @AuditTrail
    @Transactional
    public Template getByOrganization(int id) {
        return jdbcTemplate.queryForObject(GET_BY_ORGANIZATION_SQL,
                                           new Object[]{id},
                                           new ConfigurationTemplateRowMapper());
    }

	private class ConfigurationTemplateRowMapper implements RowMapper<Template> {
		
		public Template mapRow(ResultSet resultSet, int rowNum) throws SQLException {    
		    return new Template(resultSet.getInt("id"),
		                                     resultSet.getString("name"),
		                                     resultSet.getString("description"));
		}
	
	}
	
}
