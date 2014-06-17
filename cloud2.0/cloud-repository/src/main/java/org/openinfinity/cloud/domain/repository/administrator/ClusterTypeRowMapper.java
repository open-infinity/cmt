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

import java.sql.ResultSet;
import java.sql.SQLException;

import org.openinfinity.cloud.domain.ClusterType;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

/**
 * Row mapper for Cluster Types
 
 * @author Vedran Bartonicek
 * @since 1.0.0
 */
@Component("clusterTypeRowMapper")
public class ClusterTypeRowMapper implements RowMapper<ClusterType>{
	public ClusterType mapRow(ResultSet rs, int rowNumber) throws SQLException {
		ClusterType ct = new ClusterType(
			rs.getInt("id"),
			rs.getInt("configuration_id"),
			rs.getString("name"),
			rs.getString("title"),
			rs.getInt("dependency"),
			rs.getBoolean("replicated"),
			rs.getInt("min_machines"),
			rs.getInt("max_machines"),
			rs.getInt("min_repl_machines"),
			rs.getInt("max_repl_machines"));
		return ct;
	}
}
