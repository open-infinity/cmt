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

import org.openinfinity.cloud.domain.configurationtemplate.InstallationModule;
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
public class InstallationModuleRepositoryJdbcImpl implements InstallationModuleRepository {

	private JdbcTemplate jdbcTemplate;

    private static final String DELETE_SQL = "delete from installation_module_tbl where id = ?";

    private static final String UPDATE_SQL = "update installation_module_tbl set element_id = ?, name = ?, version = ?, description = ?";

    private static final String GET_ALL_SQL = "select * from installation_module_tbl";

    private static final String GET_BY_ID_SQL = "select * from installation_module_tbl where id = ?";

    private DataSource dataSource;

    @Autowired
    public InstallationModuleRepositoryJdbcImpl(@Qualifier("cloudDataSource") DataSource dataSource) {
        Assert.notNull(dataSource, "Please define datasource for scaling rule repository.");
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.dataSource = dataSource;
    }

    @Override
    public InstallationModule create(InstallationModule installationModule) {
        SimpleJdbcInsert insert = new SimpleJdbcInsert(dataSource).withTableName("installation_module_tbl").usingGeneratedKeyColumns("id");
        Map<String,Object> parameters = new HashMap<String,Object>();
        parameters.put("element_id", installationModule.getElementId());
        parameters.put("name", installationModule.getName());
        parameters.put("version", installationModule.getVersion());
        parameters.put("description", installationModule.getDescription());
        Number newId = insert.executeAndReturnKey(parameters);
        installationModule.setId(newId.intValue());
        return installationModule;
    }

    @Override
    public void update(InstallationModule installationModule) {
        jdbcTemplate.update(UPDATE_SQL, installationModule.getElementId(), installationModule.getName(), installationModule.getVersion(), installationModule.getDescription());
    }

    @Override
    public Collection<InstallationModule> loadAll() {
        return jdbcTemplate.query(GET_ALL_SQL, new InstallationModuleRowMapper());
    }

    @Override
    public InstallationModule load(BigInteger id) {
        return jdbcTemplate.queryForObject(GET_BY_ID_SQL, new Object[] {id}, new InstallationModuleRowMapper());
    }

    @Override
    public void delete(InstallationModule installationModule) {
        jdbcTemplate.update(DELETE_SQL, installationModule.getId());
    }

    private class InstallationModuleRowMapper implements RowMapper<InstallationModule> {
		public InstallationModule mapRow(ResultSet resultSet, int rowNum) throws SQLException {    
		    return new InstallationModule(
                    resultSet.getInt("id"),
                    resultSet.getInt("element_id"),
                    resultSet.getString("version"),
                    resultSet.getString("name"),
                    resultSet.getString("description"));
		}
	}
	
}
