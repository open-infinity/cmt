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

import org.openinfinity.cloud.domain.configurationtemplate.entity.ParameterValue;
import org.openinfinity.cloud.domain.repository.configurationtemplate.entity.api.ParameterValueRepository;
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
public class ParameterValueRepositoryJdbcImpl implements ParameterValueRepository {

    private static final String GET_ALL_FOR_KEY_SQL = "select * from parameter_value_tbl where parameter_key_id = ?";

    private static final String DELETE_FOR_KEY_SQL = "delete from parameter_value_tbl where parameter_key_id = ?";

    private JdbcTemplate jdbcTemplate;

    private DataSource dataSource;

    @Autowired
    public ParameterValueRepositoryJdbcImpl(@Qualifier("cloudDataSource") DataSource dataSource) {
        Assert.notNull(dataSource, "Please define data source");
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.dataSource = dataSource;
    }

    @Override
    public ParameterValue create(ParameterValue value) {
        SimpleJdbcInsert insert = new SimpleJdbcInsert(dataSource).withTableName("parameter_value_tbl").usingGeneratedKeyColumns("id");
        Map<String,Object> parameters = new HashMap<String,Object>();
        parameters.put("parameter_key_id", value.getParameterKeyId());
        parameters.put("parameter_value", value.getValue());
        Number newId = insert.executeAndReturnKey(parameters);
        value.setId(newId.intValue());
        return value;
    }

    @Override
    @AuditTrail
    public void update(ParameterValue value) {
    }

    @Override
    @AuditTrail
    public Collection<ParameterValue> loadAll() {
        return null;
    }

    @Override
    @AuditTrail
    public Collection<ParameterValue> loadAll(int parameterKeyId) {
        return jdbcTemplate.query(GET_ALL_FOR_KEY_SQL, new Object[] {parameterKeyId}, new ParameterValueRowMapper());
    }

    @Override
    @AuditTrail
    public ParameterValue load(BigInteger id) {
        return null;
    }

    @Override
    @AuditTrail
    public void delete(ParameterValue value) {
    }

    @Override
    public void delete(BigInteger id) {
    }

    @Override
    @AuditTrail
    public void deleteByKeyId(int keyId){
        jdbcTemplate.update(DELETE_FOR_KEY_SQL, keyId);
    }

    private class ParameterValueRowMapper implements RowMapper<ParameterValue> {
		
		public ParameterValue mapRow(ResultSet resultSet, int rowNum) throws SQLException {    
		    return new ParameterValue(resultSet.getInt("id"),
		                       resultSet.getInt("parameter_key_id"),
		                       resultSet.getString("parameter_value"));
		}
	}

}
