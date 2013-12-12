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

import org.openinfinity.cloud.domain.configurationtemplate.InstallationModulePackage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import javax.sql.DataSource;
import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

/**
 * @author Vedran Bartonicek
 * @version 1.3.0
 * @since 1.3.0
 */
@Repository
public class InstallationModulePackageRepositoryJdbcImpl implements InstallationModulePackageRepository {

	private JdbcTemplate jdbcTemplate;

    private static final String CREATE_SQL = "insert into installation_module_package_tbl (module_id, package_id) values( ?, ?)";

    private static final String DELETE_BY_MODULE_ID_SQL = "delete from installation_module_package_tbl where module_id = ?";

    private static final String DELETE_BY_PACKAGE_ID_SQL = "delete from installation_module_package_tbl where package_id = ?";

    private static final String UPDATE_SQL = "update installation_module_package_tbl set module_id = ?, package_id = ?";

    private static final String GET_ALL_SQL = "select * from installation_module_package_tbl";

    private static final String GET_BY_ID_SQL = "select * from installation_module_package_tbl where id = ?";

    @Autowired
    public InstallationModulePackageRepositoryJdbcImpl(@Qualifier("cloudDataSource") DataSource dataSource) {
        Assert.notNull(dataSource, "Please define datasource for scaling rule repository.");
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public void create(InstallationModulePackage installationModulePackage) {
        jdbcTemplate.update(CREATE_SQL, installationModulePackage.getModuleId(), installationModulePackage.getPackageId());
    }

    @Override
    public void update(InstallationModulePackage installationModulePackage) {
        jdbcTemplate.update(UPDATE_SQL, installationModulePackage.getModuleId(), installationModulePackage.getPackageId());
    }

    @Override
    public Collection<InstallationModulePackage> loadAll() {
        return jdbcTemplate.query(GET_ALL_SQL, new InstallationModulePackageRowMapper());
    }

    @Override
    public InstallationModulePackage load(BigInteger id) {
        return jdbcTemplate.queryForObject(GET_BY_ID_SQL, new Object[]{id}, new InstallationModulePackageRowMapper());
    }

    @Override
    public void deleteByModule(int moduleId) {
        jdbcTemplate.update(DELETE_BY_MODULE_ID_SQL, moduleId);
    }

    @Override
    public void deleteByPackage(int packageId) {
        jdbcTemplate.update(DELETE_BY_PACKAGE_ID_SQL, packageId);
    }

    private class InstallationModulePackageRowMapper implements RowMapper<InstallationModulePackage> {
		public InstallationModulePackage mapRow(ResultSet resultSet, int rowNum) throws SQLException {
		    return new InstallationModulePackage(resultSet.getInt("moduleId"), resultSet.getInt("packageId"));
		}
	}
	
}
