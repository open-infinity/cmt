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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.openinfinity.cloud.domain.Cluster;
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
 * Jdbc implementation of Cluster repository interface
 * 
 * @author Ossi Hämäläinen
 * @author Ilkka Leinonen
 * @version 1.0.0 Initial version
 * @since 1.0.0
 */

@Repository("clusterRepository")
public class ClusterRepositoryJdbcImpl implements ClusterRepository {
	
	private static final Logger LOG = Logger.getLogger(ClusterRepositoryJdbcImpl.class.getName());
	
	private JdbcTemplate jdbcTemplate;
	
	private DataSource dataSource;

	@Autowired
	public ClusterRepositoryJdbcImpl(@Qualifier("cloudDataSource") DataSource ds) {
		Assert.notNull(ds, "Please define datasource for cluster repository.");
		this.jdbcTemplate = new JdbcTemplate(ds);
		this.dataSource = ds;
	}
	
	@AuditTrail
	public void addCluster(Cluster cluster) {
		SimpleJdbcInsert insert = new SimpleJdbcInsert(dataSource).withTableName("cluster_tbl").usingGeneratedKeyColumns("cluster_id");
		Map<String,Object> parameters = new HashMap<String,Object>();
		parameters.put("cluster_name", cluster.getName());
		parameters.put("cluster_number_of_machines", cluster.getNumberOfMachines());
		parameters.put("cluster_lb_name", cluster.getLbName());
		parameters.put("cluster_lb_dns", cluster.getLbDns());
		parameters.put("instance_id", cluster.getInstanceId());
		parameters.put("cluster_type", cluster.getType());
		parameters.put("cluster_pub", cluster.getPublished());
		parameters.put("cluster_live", cluster.getLive());
		parameters.put("cluster_lb_instance_id", cluster.getLbInstanceId());
		parameters.put("cluster_security_group_id", cluster.getSecurityGroupId());
		parameters.put("cluster_security_group_name", cluster.getSecurityGroupName());
		parameters.put("cluster_multicast_address", cluster.getMulticastAddress());
		parameters.put("cluster_machine_type", cluster.getMachineType());
		parameters.put("cluster_ebs_image_used", cluster.getEbsImageUsed());
		parameters.put("cluster_ebs_volumes_used", cluster.getEbsVolumesUsed());
		LOG.info(parameters.toString());
		Number newId = insert.executeAndReturnKey(parameters);
		LOG.info("Key: "+newId);
		cluster.setId(newId.intValue());

	}

	@AuditTrail
	public void updateCluster(final Cluster cluster) {
		jdbcTemplate.update("update cluster_tbl set cluster_name = ?, cluster_number_of_machines = ?, cluster_lb_name = ?, cluster_lb_dns = ?, instance_id = ?, cluster_type = ?, cluster_pub = ?, cluster_live = ?, cluster_lb_instance_id = ?, cluster_security_group_id = ?, cluster_security_group_name = ?, cluster_multicast_address = ?, cluster_machine_type = ?, cluster_ebs_image_used = ?, cluster_ebs_volumes_used = ? where cluster_id = ?", 
				new PreparedStatementSetter() {
					public void setValues(PreparedStatement ps) throws SQLException {
						ps.setString(1, cluster.getName());
						ps.setInt(2, cluster.getNumberOfMachines());
						ps.setString(3, cluster.getLbName());
						ps.setString(4, cluster.getLbDns());
						ps.setInt(5, cluster.getInstanceId());
						ps.setInt(6, cluster.getType());
						ps.setInt(7, cluster.getPublished());
						ps.setInt(8, cluster.getLive());
						ps.setString(9, cluster.getLbInstanceId());
						ps.setString(10, cluster.getSecurityGroupId());
						ps.setString(11, cluster.getSecurityGroupName());
						ps.setString(12, cluster.getMulticastAddress());
						ps.setInt(13, cluster.getMachineType());
						ps.setInt(14, cluster.getEbsImageUsed());
						ps.setInt(15, cluster.getEbsVolumesUsed());
						ps.setInt(16, cluster.getId());
					}
				}
		);

	}

	@AuditTrail
	public List<Cluster> getClusters(int instanceId) {
		List<Cluster> clusters = this.jdbcTemplate.query("select * from cluster_tbl where instance_id = ?", new Object[] { instanceId }, new ClusterWrapper());
		return clusters;
	}

	@AuditTrail
	public Cluster getCluster(int clusterId) {
		List<Cluster> clusters = this.jdbcTemplate.query("select * from cluster_tbl where cluster_id = ?", new Object[] {clusterId}, new ClusterWrapper());
		Cluster cluster = DataAccessUtils.singleResult(clusters);
		return cluster;
	}

	@AuditTrail
	public void deleteCluster(Cluster cluster) {
		jdbcTemplate.update("delete from cluster_tbl where cluster_id = "+cluster.getId());

	}

	@AuditTrail
	public List<Cluster> getClusters() {
		List<Cluster> clusters = this.jdbcTemplate.query("select * from cluster_tbl", new ClusterWrapper());
		return clusters;
	}

	@AuditTrail
	public void updatePublished(final int id, final int pubValue) {
		jdbcTemplate.update("update cluster_tbl set cluster_pub = ? where cluster_id = ?", 
				new PreparedStatementSetter() {
					public void setValues(PreparedStatement ps) throws SQLException {
						ps.setInt(1, pubValue);
						ps.setInt(2, id);
					}
				}
			);

	}

	@Override
	public Cluster getClusterByLoadBalancerId(int loadBalancerId) {
		List<Cluster> clusters = this.jdbcTemplate.query("select * from cluster_tbl where cluster_lb_instance_id = ?", new Object[] {loadBalancerId}, new ClusterWrapper());
		Cluster cluster = DataAccessUtils.singleResult(clusters);
		return cluster;
	}
	
	private static final class ClusterWrapper implements RowMapper<Cluster> {
		public Cluster mapRow(ResultSet rs, int rowNumber) throws SQLException {
			Cluster cluster = new Cluster();
			cluster.setId(rs.getInt("cluster_id"));
			cluster.setName(rs.getString("cluster_name"));
			cluster.setNumberOfMachines(rs.getInt("cluster_number_of_machines"));
			cluster.setLbName(rs.getString("cluster_lb_name"));
			cluster.setLbDns(rs.getString("cluster_lb_dns"));
			cluster.setInstanceId(rs.getInt("instance_id"));
			cluster.setType(rs.getInt("cluster_type"));
			cluster.setPublished(rs.getInt("cluster_pub"));
			cluster.setLive(rs.getInt("cluster_live"));
			cluster.setLbInstanceId(rs.getString("cluster_lb_instance_id"));
			cluster.setSecurityGroupId(rs.getString("cluster_security_group_id"));
			cluster.setSecurityGroupName(rs.getString("cluster_security_group_name"));
			cluster.setMulticastAddress(rs.getString("cluster_multicast_address"));
			cluster.setMachineType(rs.getInt("cluster_machine_type"));
			cluster.setEbsImageUsed(rs.getInt("cluster_ebs_image_used"));
			cluster.setEbsVolumesUsed(rs.getInt("cluster_ebs_volumes_used"));
			return cluster;
		}
	}
	
}
