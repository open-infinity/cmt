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

import org.openinfinity.cloud.domain.configurationtemplate.relation.ElementToModule;
import org.openinfinity.cloud.domain.repository.configurationtemplate.relation.api.ElementToModuleRepository;
import org.openinfinity.core.annotation.AuditTrail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * @author Vedran Bartonicek
 * @version 1.3.0
 * @since 1.3.0
 */

@Repository
public class ElementToModuleRepositoryJdbcImpl implements ElementToModuleRepository {

    private static final String DELETE_BY_ELEMENT_ID_SQL = "delete from cfg_element_module_tbl where element_id = ?";

    private static final String DELETE_BY_MODULE_ID_SQL = "delete from cfg_element_module_tbl where module_id = ?";

    private static final String CREATE_SQL = "insert into cfg_element_module_tbl values(?, ?)";


    private JdbcTemplate jdbcTemplate;

    @Autowired
    public ElementToModuleRepositoryJdbcImpl(@Qualifier("cloudDataSource") DataSource dataSource) {
        Assert.notNull(dataSource, "Please define datasource.");
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @AuditTrail
    @Override
    public List<ElementToModule> loadAll() {
        return null;
    }

    @AuditTrail
    public void deleteByElement(int elementId){
        jdbcTemplate.update(DELETE_BY_ELEMENT_ID_SQL, elementId);
    }

    @AuditTrail
    public void deleteByModule(int moduleId){
        jdbcTemplate.update(DELETE_BY_MODULE_ID_SQL, moduleId);
    }

    @AuditTrail
    @Override
    public void create(int elementId , int moduleId) {
        jdbcTemplate.update(CREATE_SQL, elementId, moduleId);
    }

    private class DependencyRowMapper implements RowMapper<ElementToModule> {
        public ElementToModule mapRow(ResultSet resultSet, int rowNum) throws SQLException {
            return new ElementToModule(resultSet.getInt("element_id"), resultSet.getInt("module_id"));
        }
    }

}
