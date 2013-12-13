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

import org.openinfinity.cloud.domain.configurationtemplate.entity.InstallationPackage;
import org.openinfinity.cloud.domain.repository.configurationtemplate.entity.api.InstallationPackageRepository;
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
public class InstallationPackageRepositoryJdbcImpl implements InstallationPackageRepository {

	private JdbcTemplate jdbcTemplate;

    private static final String DELETE_SQL = "delete from installation_package_tbl where id = ?";

    private static final String UPDATE_SQL = "update installation_package_tbl set name = ?, version = ?, description = ?";

    private static final String GET_ALL_SQL = "select * from installation_package_tbl";

    private static final String GET_BY_ID_SQL = "select * from installation_package_tbl where id = ?";

    private DataSource dataSource;

    @Autowired
    public InstallationPackageRepositoryJdbcImpl(@Qualifier("cloudDataSource") DataSource dataSource) {
        Assert.notNull(dataSource, "Please define datasource.");
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.dataSource = dataSource;
    }

    @Override
    public InstallationPackage create(InstallationPackage installationPackage) {
        SimpleJdbcInsert insert = new SimpleJdbcInsert(dataSource).withTableName("installation_package_tbl").usingGeneratedKeyColumns("id");
        Map<String,Object> parameters = new HashMap<String,Object>();
        parameters.put("name", installationPackage.getName());
        parameters.put("version", installationPackage.getVersion());
        parameters.put("description", installationPackage.getDescription());
        Number newId = insert.executeAndReturnKey(parameters);
        installationPackage.setId(newId.intValue());
        return installationPackage;
    }

    @Override
    public void update(InstallationPackage installationPackage) {
        jdbcTemplate.update(UPDATE_SQL, installationPackage.getName(), installationPackage.getVersion(), installationPackage.getDescription());
    }

    @Override
    public Collection<InstallationPackage> loadAll() {
        return jdbcTemplate.query(GET_ALL_SQL, new InstallationPackageRowMapper());
    }

    @Override
    public InstallationPackage load(BigInteger id) {
        return jdbcTemplate.queryForObject(GET_BY_ID_SQL, new Object[]{id}, new InstallationPackageRowMapper());
    }

    @Override
    public void delete(InstallationPackage installationPackage) {
        jdbcTemplate.update(DELETE_SQL, installationPackage.getId());
    }

    @Override
    public void delete(BigInteger id) {
        jdbcTemplate.update(DELETE_SQL, id.intValue());    }

    private class InstallationPackageRowMapper implements RowMapper<InstallationPackage> {
		public InstallationPackage mapRow(ResultSet resultSet, int rowNum) throws SQLException {    
		    return new InstallationPackage(
                    resultSet.getInt("id"),
                    resultSet.getString("version"),
                    resultSet.getString("name"),
                    resultSet.getString("description"));
		}
	}
	
}
