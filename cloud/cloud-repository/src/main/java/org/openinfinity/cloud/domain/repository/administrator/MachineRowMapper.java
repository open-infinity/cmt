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

import org.openinfinity.cloud.domain.Machine;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

@Component("machineRowMapper")
public class MachineRowMapper implements RowMapper<Machine> {
	public Machine mapRow(ResultSet rs, int rowNumber) throws SQLException {
		Machine machine = new Machine();
		machine.setId(rs.getInt("machine_id"));
		machine.setInstanceId(rs.getString("machine_instance_id"));
		machine.setClusterId(rs.getInt("machine_cluster_id"));
		machine.setProjectId(rs.getInt("project_id"));
		machine.setName(rs.getString("machine_name"));
		machine.setDnsName(rs.getString("machine_dns_name"));
		machine.setKey(rs.getInt("machine_key"));
		machine.setUserName(rs.getString("machine_username"));
		machine.setRunning(rs.getInt("machine_running"));
		machine.setState(rs.getString("machine_state"));
		machine.setPrivateDnsName(rs.getString("machine_private_dns_name"));
		machine.setType(rs.getString("machine_type"));
		machine.setConfigured(rs.getInt("machine_configured"));
		machine.setCloud(rs.getInt("machine_cloud_type"));
		return machine;
	}
}