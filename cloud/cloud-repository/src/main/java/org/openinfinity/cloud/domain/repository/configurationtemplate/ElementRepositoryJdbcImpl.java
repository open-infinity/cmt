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

import org.openinfinity.cloud.domain.configurationtemplate.Element;
import org.openinfinity.core.annotation.AuditTrail;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * JDBC Repository implementation of the <code>org.openinfinity.core.cloud.deployer.repository.DeploymentRepository</code> interface.
 * 
 * @author Vedran Bartonicek
 * @version 1.3.0
 * @since 1.3.0
 */
@Repository
public class ElementRepositoryJdbcImpl implements ElementRepository {

	private JdbcTemplate jdbcTemplate;
	private static final String GET_ALL_SQL = 
	        "SELECT * FROM CONFIGURATION_ELEMENT_TABLE";

    @Override
    public Element create(Element product) {
        return null;
    }

    @Override
    public void update(Element product) {
    }

    @AuditTrail
    @Transactional
    public List<Element> loadAll() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Element load(BigInteger id) {
        return null;      }

    @Override
    public void delete(Element product) {
    }

    private class ElementRowMapper implements RowMapper<Element> {
		
		public Element mapRow(ResultSet resultSet, int rowNum) throws SQLException {    
		    return new Element(resultSet.getInt("id"),
		                       resultSet.getInt("type"),
		                       resultSet.getString("name"),
		                       resultSet.getString("version"),
		                       resultSet.getString("description"),
		                       resultSet.getInt("parameterKey"),
		                       resultSet.getInt("minMachines"),
		                       resultSet.getInt("maxMachines"),
                               resultSet.getBoolean("replicated"),
                               resultSet.getInt("minReplicationMachines"),
		                       resultSet.getInt("maxReplicationMachines"));
		}
	}

}
