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

import org.openinfinity.cloud.domain.configurationtemplate.ConfigurationElementDependency;
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
import java.util.List;

/**
 * @author Vedran Bartonicek
 * @version 1.3.0
 * @since 1.3.0
 */

@Repository
public class ConfigurationElementDependencyRepositoryJdbcImpl implements ConfigurationElementDependencyRepository {

    private static final String DELETE_BY_DEPENDENT_ID_SQL = "delete from cfg_element_dependency_tbl where element_from = ?";

    private static final String CREATE_SQL = "insert into cfg_element_dependency_tbl values(?, ?)";


    private JdbcTemplate jdbcTemplate;

    @Autowired
    public ConfigurationElementDependencyRepositoryJdbcImpl(@Qualifier("cloudDataSource") DataSource dataSource) {
        Assert.notNull(dataSource, "Please define datasource for scaling rule repository.");
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @AuditTrail
    @Transactional
    public List<ConfigurationElementDependency> getAll() {
        return null;
    }

    @AuditTrail
    public void deleteByDepenent(int elementFrom){
        jdbcTemplate.update(DELETE_BY_DEPENDENT_ID_SQL, elementFrom);
    }

    @Override
    public void create(int elementFrom , int elementTo) {
        jdbcTemplate.update(CREATE_SQL, elementFrom, elementTo);
    }

    private class DependencyRowMapper implements RowMapper<ConfigurationElementDependency> {
        public ConfigurationElementDependency mapRow(ResultSet resultSet, int rowNum) throws SQLException {
            return new ConfigurationElementDependency(resultSet.getInt("element_from"), resultSet.getInt("element_to"));
        }
    }

}
