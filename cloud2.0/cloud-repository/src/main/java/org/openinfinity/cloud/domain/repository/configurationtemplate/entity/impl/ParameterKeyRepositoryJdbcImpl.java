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

import org.openinfinity.cloud.domain.configurationtemplate.entity.ParameterKey;
import org.openinfinity.cloud.domain.repository.configurationtemplate.entity.api.ParameterKeyRepository;
import org.openinfinity.core.annotation.AuditTrail;
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
public class ParameterKeyRepositoryJdbcImpl implements ParameterKeyRepository {

	private static final String GET_ALL_FOR_MODULE_SQL = "select * from parameter_key_tbl where module_id = ?";

    private static final String FIND_BY_NAME_SQL = "select * from parameter_key_tbl where name = ?";

    private static final String DELETE_SQL = "delete from parameter_key_tbl where module_id = ?";

    private JdbcTemplate jdbcTemplate;

    private DataSource dataSource;

    @Autowired
    public ParameterKeyRepositoryJdbcImpl(@Qualifier("cloudDataSource") DataSource dataSource) {
        Assert.notNull(dataSource, "Please define datasource.");
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.dataSource = dataSource;
    }

    @Override
    public Collection<ParameterKey> loadAllForModule(int moduleId) {
        return jdbcTemplate.query(GET_ALL_FOR_MODULE_SQL, new Object[] {moduleId}, new ParameterKeyMapper());
    }

    @Override
    public ParameterKey create(ParameterKey key) {
        SimpleJdbcInsert insert = new SimpleJdbcInsert(dataSource).withTableName("parameter_key_tbl").usingGeneratedKeyColumns("id");
        Map<String,Object> parameters = new HashMap<String,Object>();
        parameters.put("module_id", key.getModuleId());
        parameters.put("name", key.getName());
        Number newId = insert.executeAndReturnKey(parameters);
        key.setId(newId.intValue());
        return key;
    }

    @Override
    @AuditTrail
    public void update(ParameterKey product) {
    }

    @Override
    @AuditTrail
    public Collection<ParameterKey> loadAll() {
        return null;
    }

    @Override
    @AuditTrail
    public ParameterKey load(BigInteger id) {
        return null;
    }

    @Override
    @AuditTrail
    public void delete(ParameterKey product) {
    }

    @Override
    @AuditTrail
    public void delete(BigInteger id) {
    }

    @Override
    @AuditTrail
    public void deleteByModuleId(int moduleId){
        jdbcTemplate.update(DELETE_SQL, moduleId);
    }

    @Override
    @AuditTrail
    public int findIdByName(String name){
        int res  = -1;
        try {
            res = jdbcTemplate.queryForObject(FIND_BY_NAME_SQL, new Object[] {name}, new ParameterKeyMapper()).getId();
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return res;
    }

    private class ParameterKeyMapper implements RowMapper<ParameterKey> {
		public ParameterKey mapRow(ResultSet resultSet, int rowNum) throws SQLException {
		    return new ParameterKey(resultSet.getInt("id"), resultSet.getInt("module_id"), resultSet.getString("name"));
		}
	}

}
