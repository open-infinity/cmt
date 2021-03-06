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
package org.openinfinity.cloud.domain.repository.configurationtemplate.relation.impl;

import org.openinfinity.cloud.domain.configurationtemplate.relation.TemplateToElement;
import org.openinfinity.cloud.domain.repository.configurationtemplate.relation.api.TemplateToElementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

/**
 * @author Vedran Bartonicek
 * @version 1.3.0
 * @since 1.3.0
 */
@Repository
public class TemplateToElementRepositoryJdbcImpl implements TemplateToElementRepository {

	private JdbcTemplate jdbcTemplate;

    private static final String CREATE_SQL = "insert into cfg_template_element_tbl values(?, ?)";

    private static final String LOAD_ALL_FOR_TEMPLATE_SQL = "select * from cfg_template_element_tbl where template_id = ?";

    private static final String DELETE_BY_ELEMENT_ID_SQL = "delete from cfg_template_element_tbl where element_id = ?";

    private static final String DELETE_BY_TEMPLATE_ID_SQL = "delete from cfg_template_element_tbl where template_id = ?";

    @Autowired
    public TemplateToElementRepositoryJdbcImpl(@Qualifier("cloudDataSource") DataSource dataSource) {
        Assert.notNull(dataSource, "Please define datasource.");
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public void create(int templateId, int elementId) {
        jdbcTemplate.update(CREATE_SQL, templateId, elementId);
    }

    @Override
    public Collection<TemplateToElement> loadAllForTemplate(int templateId){
        return jdbcTemplate.query(LOAD_ALL_FOR_TEMPLATE_SQL, new Object[] {templateId}, new ElementRowMapper());
    }

    @Override
    public void deleteByElement(int elementId) {
        jdbcTemplate.update(DELETE_BY_ELEMENT_ID_SQL, elementId);

    }

    @Override
    public void deleteByTemplate(int templateId) {
        jdbcTemplate.update(DELETE_BY_TEMPLATE_ID_SQL, templateId);
    }

    private class ElementRowMapper implements RowMapper<TemplateToElement> {
		
		public TemplateToElement mapRow(ResultSet resultSet, int rowNum) throws SQLException {
		    return new TemplateToElement(resultSet.getInt("template_id"), resultSet.getInt("element_id"));
		}
	}

}
