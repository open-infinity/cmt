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

import java.util.Collection;
import java.util.List;

import javax.sql.DataSource;

import org.openinfinity.cloud.domain.ClusterType;
import org.openinfinity.core.annotation.AuditTrail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

/**
 * Cluster repository interface
 * 
 * @author Vedran Bartonicek
 * @version 1.0.0 Initial version
 * @since 1.0.0
 */

@Repository("clusterTypeRepository")
class ClusterTypeRepositoryJdbcImpl implements ClusterTypeRepository{

	@Autowired
	RowMapper<ClusterType> clusterTypeRowMapper;
	
	private NamedParameterJdbcTemplate jdbcTemplate;
	
	@Autowired
	public ClusterTypeRepositoryJdbcImpl(@Qualifier("cloudDataSource") DataSource ds) {
		Assert.notNull(ds, "Please define datasource for machine repository.");
		this.jdbcTemplate = new NamedParameterJdbcTemplate(ds);
	}
		
	@AuditTrail
	public Collection<ClusterType> getAvailableClusterTypes(List<String> userOrganizations) {
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue("orgNames", userOrganizations);
		return jdbcTemplate.query("select distinct cluster.* from cluster_type_tbl as cluster, acl_cluster_type_tbl as acl " +
				"where acl.org_name in (:orgNames) and acl.cluster_id = cluster.id order by cluster.id", parameters, clusterTypeRowMapper);
	}
	
}
