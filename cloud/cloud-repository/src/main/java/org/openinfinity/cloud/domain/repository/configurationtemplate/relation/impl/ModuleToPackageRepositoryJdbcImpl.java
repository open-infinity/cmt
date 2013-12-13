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

import org.openinfinity.cloud.domain.configurationtemplate.relation.ModuleToPackage;
import org.openinfinity.cloud.domain.repository.configurationtemplate.relation.api.ModuleToPackageRepository;
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
public class ModuleToPackageRepositoryJdbcImpl implements ModuleToPackageRepository {

	private JdbcTemplate jdbcTemplate;

    private static final String CREATE_SQL = "insert into installation_module_package_tbl (module_id, package_id) values( ?, ?)";

    private static final String DELETE_BY_MODULE_ID_SQL = "delete from installation_module_package_tbl where module_id = ?";

    private static final String DELETE_BY_PACKAGE_ID_SQL = "delete from installation_module_package_tbl where package_id = ?";

    private static final String UPDATE_SQL = "update installation_module_package_tbl set module_id = ?, package_id = ?";

    private static final String LOAD_ALL_SQL = "select * from installation_module_package_tbl";

    private static final String LOAD_BY_ID_SQL = "select * from installation_module_package_tbl where id = ?";

    @Autowired
    public ModuleToPackageRepositoryJdbcImpl(@Qualifier("cloudDataSource") DataSource dataSource) {
        Assert.notNull(dataSource, "Please define datasource for scaling rule repository.");
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public void create(ModuleToPackage moduleToPackage) {
        jdbcTemplate.update(CREATE_SQL, moduleToPackage.getModuleId(), moduleToPackage.getPackageId());
    }

    @Override
    public void update(ModuleToPackage moduleToPackage) {
        jdbcTemplate.update(UPDATE_SQL, moduleToPackage.getModuleId(), moduleToPackage.getPackageId());
    }

    @Override
    public Collection<ModuleToPackage> loadAll() {
        return jdbcTemplate.query(LOAD_ALL_SQL, new ModuleToPackageRowMapper());
    }

    @Override
    public ModuleToPackage load(BigInteger id) {
        return jdbcTemplate.queryForObject(LOAD_BY_ID_SQL, new Object[]{id}, new ModuleToPackageRowMapper());
    }

    @Override
    public void deleteByModule(int moduleId) {
        jdbcTemplate.update(DELETE_BY_MODULE_ID_SQL, moduleId);
    }

    @Override
    public void deleteByPackage(int packageId) {
        jdbcTemplate.update(DELETE_BY_PACKAGE_ID_SQL, packageId);
    }

    private class ModuleToPackageRowMapper implements RowMapper<ModuleToPackage> {
		public ModuleToPackage mapRow(ResultSet resultSet, int rowNum) throws SQLException {
		    return new ModuleToPackage(resultSet.getInt("moduleId"), resultSet.getInt("packageId"));
		}
	}
	
}
