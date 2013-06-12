/*
 * Copyright (c) 2012 the original author or authors.
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

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.openinfinity.cloud.domain.Machine;
import org.openinfinity.core.annotation.AuditTrail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

/**
 * Jdbc implementation of Machine repository interface
 
 * @author Ossi Hämäläinen
 * @author Ilkka Leinonen
 * @author Vedran Bartonicek
 * @version 1.0.0 Initial version
 * @since 1.0.0
 */

@Repository("machineRepository")
public class MachineRepositoryJdbcImpl implements MachineRepository {
	
    private static final String GET_ALL_CLUSTER_MACHINES_EXCEPT_TYPE = 
        "select * from machine_tbl where machine_cluster_id = ? and machine_type != ?";
    	
	@Autowired
	RowMapper<Machine> machineRowMapper;
	
	private JdbcTemplate jdbcTemplate;
	private DataSource dataSource;
	
	@Autowired
	public MachineRepositoryJdbcImpl(@Qualifier("cloudDataSource") DataSource ds) {
		Assert.notNull(ds, "Please define datasource for machine repository.");
		this.jdbcTemplate = new JdbcTemplate(ds);
		this.dataSource = ds;
	}

	@AuditTrail
	public void addMachine(Machine machine) {
		SimpleJdbcInsert insert = new SimpleJdbcInsert(dataSource).withTableName("machine_tbl").usingGeneratedKeyColumns("machine_id");
		Map<String,Object> parameters = new HashMap<String,Object>();
		parameters.put("machine_instance_id", machine.getInstanceId());
		parameters.put("project_id", machine.getProjectId());
		parameters.put("machine_cluster_id", machine.getClusterId());
		parameters.put("machine_name", machine.getName());
		parameters.put("machine_dns_name", machine.getDnsName());
		parameters.put("machine_key", machine.getKey());
		parameters.put("machine_username", machine.getUserName());
		parameters.put("machine_running", machine.getRunning());
		parameters.put("machine_state", machine.getState());
		parameters.put("active", 1);
		parameters.put("machine_private_dns_name", machine.getPrivateDnsName());
		parameters.put("machine_type", machine.getType());
		parameters.put("machine_configured", machine.getConfigured());
		parameters.put("machine_cloud_type", machine.getCloud());
		parameters.put("machine_extra_ebs_volume_id", machine.getEbsVolumeId());
		parameters.put("machine_extra_ebs_volume_device", machine.getEbsVolumeDevice());
		parameters.put("machine_extra_ebs_volume_size", machine.getEbsVolumeSize());
		
		Number newId = insert.executeAndReturnKey(parameters);
		machine.setId(newId.intValue());
	}

	@AuditTrail
	public List<Machine> getMachinesInCluster(int clusterId) {
		List<Machine> machines = this.jdbcTemplate.query("select * from machine_tbl where machine_cluster_id = ?", new Object[] {clusterId}, machineRowMapper);
		return machines;
	}
	
	@AuditTrail
    public List<Machine> getMachinesInClusterExceptType(int clusterId, String machineType) {
        List<Machine> machines = this.jdbcTemplate.query(GET_ALL_CLUSTER_MACHINES_EXCEPT_TYPE, 
                                                         new Object[] {clusterId, machineType},
                                                         machineRowMapper);
        return machines;
    }

    public List<Machine> getMachinesInClusterNotConfigured(int clusterId) {
        List<Machine> machines = this.jdbcTemplate.query("select * from machine_tbl where machine_cluster_id = ? and machine_configured != 3",
                                                         new Object[] {clusterId}, machineRowMapper);
        //@AuditTrail removed to avoid exception being caught, logged and rethrown
        // Result validation done below instead:
        if ( machines.isEmpty() ){
            return null;
        }else if ( machines.size() > 0 ) { 
            return machines;
        }
        return null;
    }
	
	
	@AuditTrail
	public List<Machine> getMachinesNeedingConfigure() {
		List<Machine> machines = this.jdbcTemplate.query("select * from machine_tbl where machine_configured = 0", machineRowMapper);
		return machines;
	}

	@AuditTrail
	public List<Machine> getMachinesNeedingUpdate(int cloudType) {
		List<Machine> machines = this.jdbcTemplate.query(
				"select * from machine_tbl where " +
				"(machine_state = 'pending' or machine_state = 'shutting-down' or TIMESTAMPDIFF(MINUTE, machine_last_update, NOW()) >= 5) " +
				"and machine_cloud_type = ?", new Object[] {cloudType}, machineRowMapper);
		return machines;
	}

	@AuditTrail
	public void updateMachineConfigure(final int id, final int configured) {
		this.jdbcTemplate.update("update machine_tbl set machine_configured = ? where machine_id = ?", 
				new PreparedStatementSetter() {
					public void setValues(PreparedStatement ps) throws SQLException {
						ps.setInt(1, configured);
						ps.setInt(2, id);
					}
				}
			);

	}

	@AuditTrail
	public Machine getMachineByInstanceId(String instanceId) {
		List<Machine> machines = this.jdbcTemplate.query("select * from machine_tbl where machine_instance_id = ?", new Object[] { instanceId }, machineRowMapper);
		Machine machine = DataAccessUtils.singleResult(machines);
		return machine;
	}

	@AuditTrail
	public void updateMachine(final Machine machine) {
		jdbcTemplate.update("update machine_tbl set machine_instance_id = ?, machine_name = ?, machine_dns_name = ?, machine_username = ?, machine_running = ?, machine_state = ?, machine_cluster_id = ?, machine_private_dns_name = ?, machine_type = ?, machine_configured = ?, machine_last_update = NOW(), machine_cloud_type = ?, machine_extra_ebs_volume_id = ?, machine_extra_ebs_volume_device = ?, machine_extra_ebs_volume_size = ? where machine_id = ?",
				new PreparedStatementSetter() {
					public void setValues(PreparedStatement ps) throws SQLException {
						ps.setString(1, machine.getInstanceId());
						ps.setString(2, machine.getName());
						ps.setString(3, machine.getDnsName());
						ps.setString(4, machine.getUserName());
						ps.setInt(5, machine.getRunning());
						ps.setString(6, machine.getState());
						ps.setInt(7, machine.getClusterId());
						ps.setString(8, machine.getPrivateDnsName());
						ps.setString(9, machine.getType());
						ps.setInt(10, machine.getConfigured());
						ps.setInt(11, machine.getCloud());
						ps.setString(12, machine.getEbsVolumeId());
						ps.setString(13, machine.getEbsVolumeDevice());
						ps.setInt(14, machine.getEbsVolumeSize());
						ps.setInt(15, machine.getId());
						
					}
				}
		);
	}

	@AuditTrail
	public void removeMachine(int id) {
		jdbcTemplate.update("delete from machine_tbl where machine_id = "+id );
	}

	@AuditTrail
	public void removeMachine(String instanceId) {
		jdbcTemplate.update("update machine_tbl set active = 0, machine_running = 0 where machine_instance_id = '"+instanceId+"'" );
	}

	@AuditTrail
	public Machine getMachine(int id) {
		List<Machine> machines = this.jdbcTemplate.query("select * from machine_tbl where machine_id = ?", new Object[] { id }, machineRowMapper);
		Machine machine = DataAccessUtils.singleResult(machines);
		return machine;
	}

	@AuditTrail
	public List<Machine> getMachines() {
		List<Machine> machines = this.jdbcTemplate.query("select * from machine_tbl where active = 1", machineRowMapper);
		return machines;
	}

	@AuditTrail
	public List<Machine> getMachines(int offset, int rows, int instanceId) {
		List<Machine> machines = this.jdbcTemplate.query("select * from machine_tbl where active = 1 and machine_cluster_id in (select cluster_id from cluster_tbl where instance_id = ?) LIMIT ?,?", new Object[] {instanceId, offset, rows}, machineRowMapper);
		return machines;
	}

	@AuditTrail
	public int getNumberOfMachines() {
		return jdbcTemplate.queryForInt("select count(*) from machine_tbl where active = 1");
	}
	
	@AuditTrail
	public int getNumberOfMachines(int instanceId) {
		return jdbcTemplate.queryForInt("select count(*) from machine_tbl where active = 1 and machine_cluster_id in (select cluster_id from cluster_tbl where instance_id = ?)", new Object[] {instanceId});
	}

	@AuditTrail
	public List<Machine> searchMachines(int projectId) {
		String query = "select * from machine_tbl where active = 1 and project_id = "+projectId;
		List<Machine> machines = this.jdbcTemplate.query(query, machineRowMapper);
		return machines;
	}

	@AuditTrail
	public Machine getMachine(String instanceId) {
		List<Machine> machines = this.jdbcTemplate.query("select * from machine_tbl where machine_instance_id = ?", new Object[] { instanceId }, machineRowMapper);
		Machine machine = DataAccessUtils.singleResult(machines);
		return machine;
	}

	@AuditTrail
	public void stopMachine(int id) {
		jdbcTemplate.update("update machine_tbl set machine_running = 0 where machine_id = "+id );
	}

	@AuditTrail
	public void startMachine(int id) {
		jdbcTemplate.update("update machine_tbl set machine_running = 1 where machine_id = "+id );
	}
	
	@AuditTrail
	public List<Machine> getBigDataMachinesNeedingConfigure() {
		List<Machine> machines = this.jdbcTemplate.query("select * from machine_tbl where machine_configured = 10", machineRowMapper);
		return machines;
	}

	@AuditTrail
	public Machine getClusterManagementMachine(int clusterId) {
		List<Machine> machines = this.jdbcTemplate.query("select * from machine_tbl where machine_cluster_id = ? and machine_type = 'manager'", new Object[] { clusterId }, machineRowMapper);
		Machine machine = DataAccessUtils.singleResult(machines);
		return machine;
	}

	@AuditTrail
	public Machine getBigDataManager(int clusterId) {
		List<Machine> machines = this.jdbcTemplate.query("select * from machine_tbl where machine_type = 'manager' and machine_cluster_id = ?", new Object[] {clusterId}, machineRowMapper);
		Machine machine = DataAccessUtils.singleResult(machines);
		return machine;
	}
		
}
