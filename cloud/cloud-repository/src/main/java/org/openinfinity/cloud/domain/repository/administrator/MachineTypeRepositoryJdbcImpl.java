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

package org.openinfinity.cloud.domain.repository.administrator;

import org.openinfinity.cloud.domain.CloudProvider;
import org.openinfinity.cloud.domain.ClusterType;
import org.openinfinity.cloud.domain.MachineType;
import org.openinfinity.core.annotation.AuditTrail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

/**
 * Machine type repository implementation
 * 
 * @author Timo Tapanainen
 * @version 1.0.0 Initial version
 * @since 1.0.0
 */

@Repository("machineTypeRepository")
class MachineTypeRepositoryJdbcImpl implements MachineTypeRepository{

	private NamedParameterJdbcTemplate jdbcTemplate;
	private DataSource dataSource;

	@Autowired
	public MachineTypeRepositoryJdbcImpl(@Qualifier("cloudDataSource") DataSource ds) {
		Assert.notNull(ds, "Please define datasource for machine repository.");
		this.jdbcTemplate = new NamedParameterJdbcTemplate(ds);
		this.dataSource = ds;
	}
		
	@AuditTrail
	public Collection<MachineType> getMachineTypes(List<String> userOrganizations) {
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue("orgNames", userOrganizations);
		return jdbcTemplate.query("select machine.* from machine_type_tbl as machine, acl_machine_type_tbl as acl " +
				"where acl.org_name in (:orgNames) and acl.machine_type_id = machine.id order by machine.id", parameters, new MachineTypeWrapper());
	}

    private static final class MachineTypeWrapper implements RowMapper<MachineType> {
        public MachineType mapRow(ResultSet rs, int rowNumber) throws SQLException {
            MachineType mt = new MachineType();
            mt.setId(rs.getInt("id"));
            mt.setName(rs.getString("name"));
            mt.setSpecification(rs.getString("spec"));
            return mt;
        }
    }


}
