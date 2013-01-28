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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.openinfinity.cloud.domain.AuthorizationRoute;
import org.openinfinity.cloud.domain.ElasticIP;
import org.openinfinity.core.annotation.AuditTrail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

/**
 * Jdbc implementation of AuthorizedRouting database access interface
 * 
 * @author Ossi Hämäläinen
 * @author Ilkka Leinonen
 * @author Juha-Matti Sironen
 * @version 1.0.0 Initial version
 * @since 1.0.0
 */

@Repository("authorizedRoutingRepository")
public class AuthorizedRoutingRepositoryJdbcImpl implements AuthorizedRoutingRepository {
	private static final Logger LOG = Logger.getLogger(AuthorizedRoutingRepositoryJdbcImpl.class.getName());
	
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	public AuthorizedRoutingRepositoryJdbcImpl(@Qualifier("cloudDataSource") DataSource ds) {
		Assert.notNull(ds, "Please define datasource for authorized routing repository.");
		this.jdbcTemplate = new JdbcTemplate(ds);
	}
	
	@AuditTrail
	public void addIP(final AuthorizationRoute ip) {
		jdbcTemplate.update("insert into authorized_ip_tbl (instance_id, cluster_id, cidr_ip, protocol, security_group_name, from_port, to_port) values (?,?,?,?,?,?,?)",
				new PreparedStatementSetter() {
					public void setValues(PreparedStatement ps) throws SQLException {
						ps.setInt(1, ip.getInstanceId());
						ps.setInt(2, ip.getClusterId());
						ps.setString(3, ip.getCidrIp());
						ps.setString(4, ip.getProtocol());
						ps.setString(5, ip.getSecurityGroupName());
						ps.setInt(6, ip.getFromPort());
						ps.setInt(7, ip.getToPort());
					}
				}
		);

	}

	@AuditTrail
	public void deleteInstanceIPs(int instanceId) {
		this.jdbcTemplate.execute("delete from authorized_ip_tbl where instance_id = "+instanceId);

	}

	@AuditTrail
	public List<AuthorizationRoute> getInstanceIPs(int instanceId) {
		List<AuthorizationRoute> list = this.jdbcTemplate.query("select id,instance_id,cluster_id,cidr_ip,protocol,security_group_name,from_port,to_port from authorized_ip_tbl where instance_id = ?", new Object[] {instanceId}, new AuthorizedIPMapper());
		return list;
	}

	@AuditTrail
	public List<AuthorizationRoute> getIPs(int clusterId) {
		List<AuthorizationRoute> list = this.jdbcTemplate.query("select id,instance_id,cluster_id,cidr_ip,protocol,security_group_name,from_port,to_port from authorized_ip_tbl where cluster_id = ?", new Object[] {clusterId}, new AuthorizedIPMapper());
		
		return list;
	}

	@AuditTrail
	public void deleteIP(AuthorizationRoute ip) {
		this.jdbcTemplate.execute("delete from authorized_ip_tbl where id = "+ip.getId());
	}

	@AuditTrail
	public void deleteClusterIPs(int clusterId) {
		this.jdbcTemplate.execute("delete from authorized_ip_tbl where cluster_id = "+clusterId);
	}
	
	@AuditTrail
	public int addUserAuthorizedIP(final AuthorizationRoute ip) {
		final String INSERT_SQL = "insert into user_authorized_ip_tbl (instance_id, cluster_id, cidr_ip, protocol, security_group_name, from_port, to_port) values (?,?,?,?,?,?,?)";
		KeyHolder keyHolder = new GeneratedKeyHolder();
		jdbcTemplate.update(
		    new PreparedStatementCreator() {
		        public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
		            PreparedStatement ps = connection.prepareStatement(INSERT_SQL, new String[] {"id"});
		            ps.setInt(1, ip.getInstanceId());
					ps.setInt(2, ip.getClusterId());
					ps.setString(3, ip.getCidrIp());
					ps.setString(4, ip.getProtocol());
					ps.setString(5, ip.getSecurityGroupName());
					ps.setInt(6, ip.getFromPort());
					ps.setInt(7, ip.getToPort());
					return ps;
		        }
		    },
		    keyHolder);
		  return keyHolder.getKey().intValue(); 
	}

	@AuditTrail
	public void deleteUserAuthorizedIP(int id) {
		this.jdbcTemplate.execute("delete from user_authorized_ip_tbl where id = "+id);
	}

	@AuditTrail
	public List<AuthorizationRoute> getUserAuthorizedIPsForCluster(int clusterId) {
		List<AuthorizationRoute> list = this.jdbcTemplate.query("select * from user_authorized_ip_tbl where cluster_id = ?", new Object[] {clusterId}, new AuthorizedIPMapper());
		return list;
	}

	@AuditTrail
	public void deleteAllUserAuthorizedIPsFromCluster(int clusterId) {
		this.jdbcTemplate.execute("delete from user_authorized_ip_tbl where cluster_id = "+clusterId);
	}
	
	@AuditTrail
	public void addElasticIP(ElasticIP ip) {
		// TODO 
	}

	@AuditTrail
	public List<ElasticIP> getElasticIPs() {
		List<ElasticIP> list = this.jdbcTemplate.query("select * from elastic_ip_tbl where in_use = 0", new ElasticIPMapper());
		return list;
	}
	
	@AuditTrail
	public ElasticIP getElasticIP(int ipId) {
		List<ElasticIP> list = this.jdbcTemplate.query("select * from elastic_ip_tbl where id = ?", new Object[] {ipId}, new ElasticIPMapper());
		ElasticIP ip = DataAccessUtils.singleResult(list);
		return ip;
	}
	
	@AuditTrail
	public ElasticIP getClustersElasticIP(int clusterId) {
		List<ElasticIP> list = this.jdbcTemplate.query("select * from elastic_ip_tbl where cluster_id = ?", new Object[] {clusterId}, new ElasticIPMapper());
		ElasticIP ip = DataAccessUtils.singleResult(list);
		return ip;
	}
		
	@AuditTrail
	public void updateElasticIP(final ElasticIP ip) {
		jdbcTemplate.update("update elastic_ip_tbl set instance_id = ?, cluster_id = ?, machine_id = ?, ip_address = ?, external_ip = ?, in_use = ?, user_id = ?, organization_id = ? where id = ?",
				new PreparedStatementSetter() {
					public void setValues(PreparedStatement ps) throws SQLException {
						ps.setInt(1, ip.getInstanceId());
						ps.setInt(2, ip.getClusterId());
						ps.setInt(3, ip.getMachineId());
						ps.setString(4, ip.getIpAddress());
						ps.setString(5, ip.getExternalIpAddress());
						ps.setInt(6, ip.getInUse());
						ps.setInt(7, ip.getUserId());
						ps.setInt(8, ip.getOrganizationId());
						ps.setInt(9, ip.getId());
					}
				}
		); 	
	}
	
	private static final class AuthorizedIPMapper implements RowMapper<AuthorizationRoute> {
		public AuthorizationRoute mapRow(ResultSet rs, int rowNumber) throws SQLException {
			AuthorizationRoute ip = new AuthorizationRoute();
			ip.setId(rs.getInt("id"));
			ip.setInstanceId(rs.getInt("instance_id"));
			ip.setClusterId(rs.getInt("cluster_id"));
			ip.setCidrIp(rs.getString("cidr_ip"));
			ip.setProtocol(rs.getString("protocol"));
			ip.setSecurityGroupName(rs.getString("security_group_name"));
			ip.setFromPort(rs.getInt("from_port"));
			ip.setToPort(rs.getInt("to_port"));
			return ip;
		}
	}
	
	private static final class ElasticIPMapper implements RowMapper<ElasticIP> {
		public ElasticIP mapRow(ResultSet rs, int rowNumber) throws SQLException {
			ElasticIP ip = new ElasticIP();
			ip.setId(rs.getInt("id"));
			ip.setInstanceId(rs.getInt("instance_id"));
			ip.setClusterId(rs.getInt("cluster_id"));
			ip.setMachineId(rs.getInt("machine_id"));
			ip.setIpAddress(rs.getString("ip_address"));
			ip.setExternalIpAddress(rs.getString("external_ip"));
			ip.setInUse(rs.getInt("in_use"));
			ip.setUserId(rs.getInt("user_id"));
			ip.setOrganizationId(rs.getInt("organization_id"));
			return ip;
		}
	}
	
	private static final class SecurityGroupMapper implements RowMapper<String> {
		public String mapRow(ResultSet rs, int rowNumber) throws SQLException {
			return rs.getString("cluster_security_group_name");
		}
	}

	@Override
	public List<String> getAllSecurityGroupsInInstance(int instanceId) {
		List<String> sgList = jdbcTemplate.query("select cluster_security_group_name from cluster_tbl where instance_id = ?", new Object[] {instanceId}, new SecurityGroupMapper());
		
		return sgList;
	}

}
