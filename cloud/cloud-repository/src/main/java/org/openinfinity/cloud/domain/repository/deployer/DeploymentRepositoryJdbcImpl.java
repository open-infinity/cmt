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
package org.openinfinity.cloud.domain.repository.deployer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;

import javax.sql.DataSource;

import org.openinfinity.cloud.domain.Deployment;
import org.openinfinity.core.annotation.AuditTrail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

/**
 * JDBC Repository implementation of the <code>org.openinfinity.core.cloud.deployer.repository.DeploymentRepository</code> interface.
 * 
 * @author Ilkka Leinonen
 * @version 1.0.0
 * @since 1.0.0
 */
@Repository
public class DeploymentRepositoryJdbcImpl implements DeploymentRepository {

	/**
	 * Represents the JDBC template util.
	 */
	private JdbcTemplate jdbcTemplate;
	
	/**
	 * Represents the SQL script for storing Deployment object.
	 */
	private static final String STORE_SQL = "INSERT INTO DEPLOYMENT (STATE, ORGANIZATION_ID, INSTANCE_ID, CLUSTER_ID, LOCATION, NAME) values (?, ?, ?, ?, ?, ?)";
	
	/**
	 * Represents the SQL script for updating the state of the Deployment object.
	 */
	//private static final String UPDATE_SQL = "UPDATE DEPLOYMENT SET STATE = 0 AND ORGANIZATION_ID = ?, INSTANCE_ID = ?, CLUSTER_ID = ? WHERE STATE = 1";
	private static final String UPDATE_SQL = "UPDATE DEPLOYMENT SET STATE = 0  WHERE NAME = ? AND ORGANIZATION_ID = ? AND INSTANCE_ID = ? AND CLUSTER_ID = ?";

	/**
	 * Represents the SQL script for storing Deployment object.
	 */
	private static final String LOAD_BY_ID = "SELECT * FROM DEPLOYMENT WHERE ID = ? ORDER BY TIMESTAMP DESC";
	
	/**
	 * Represents the SQL script for loading all Deployment object.
	 */
	private static final String LOAD_ALL_SQL = "SELECT * FROM DEPLOYMENT";
	
	/**
	 * Represents the SQL script for loading all Deployment object.
	 */
	private static final String LOAD_ALL_FOR_ORG_SQL = "SELECT * FROM DEPLOYMENT WHERE ORGANIZATION_ID = ?";
	
	/**
	 * Represents the SQL script for deleting Deployment object.
	 */
	private static final String DELETE_BY_ID = "DELETE FROM DEPLOYMENT WHERE ORGANIZATION_ID = ? AND NAME = ?";
	
	/**
	 * Represents the SQL script for fetching last inserted id of the Deployment object.
	 */
	public static String LOAD_LAST_UPDATED_ID = "SELECT LAST_INSERT_ID()";
	
	/**
	 * 
	 * @param dataSource Represents the actual reference to JDBC registry.
	 */
	@Autowired
	public DeploymentRepositoryJdbcImpl(@Qualifier("cloudDataSource") DataSource dataSource) {
		Assert.notNull(dataSource, "Please define datasource for deployer repository.");
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	/**
	 * Stores <code>org.openinfinity.core.cloud.domain.Deployment</code> to registry.
	 * 
	 * @param deployment Represents the deployment information.
	 * @return Deployment Represents the created object with unique id.
	 */
	@AuditTrail
	//@Transactional(isolation=Isolation.REPEATABLE_READ)
	public Deployment store(Deployment deployment) {
		jdbcTemplate.update(UPDATE_SQL, deployment.getName(), deployment.getOrganizationId(), deployment.getInstanceId(), deployment.getClusterId());
		jdbcTemplate.update(STORE_SQL, 1, deployment.getOrganizationId(), deployment.getInstanceId(), deployment.getClusterId(), deployment.getLocation(), deployment.getName());
		deployment.setId(jdbcTemplate.queryForInt(LOAD_LAST_UPDATED_ID));
		return deployment;
	}

	/**
	 * Deletes id based on <code>org.openinfinity.core.cloud.domain.Deployment</code> object.
	 * 
	 * @param deployment Represents the <code>org.openinfinity.core.cloud.domain.Deployment</code> object for deletion.
	 */
	@AuditTrail
	public void delete(Deployment deployment) {
		jdbcTemplate.update(DELETE_BY_ID, deployment.getOrganizationId(), deployment.getName());
	}

	/**
	 * Returns <code>org.openinfinity.core.cloud.domain.Deployment</code> based on deployent id.
	 * 
	 * @param id
	 * @return <code>org.openinfinity.core.cloud.domain.Deployment</code> Represents the object fetched from registry based on deployment id.
	 */
	@AuditTrail
	public Deployment loadById(int id) {
		Deployment deployment = (Deployment) jdbcTemplate.queryForObject(LOAD_BY_ID, new Object[]{id}, new DeploymentRowMapper());
		return deployment;
	}
	
	/**
	 * Returns all <code>org.openinfinity.core.cloud.domain.Deployment</code> objects based on organization id.
	 * 
	 * @param organizationId Represents the organization id for the deployments.
	 * @return Collection<Deployment>
	 */
	@AuditTrail
	public Collection<Deployment> loadAllForOrganization(long organizationId) {
		Collection<Deployment> deployments = jdbcTemplate.query(LOAD_ALL_FOR_ORG_SQL, new Object[]{organizationId}, new DeploymentRowMapper());
		return Collections.unmodifiableCollection(deployments);
	}
	
	/**
	 * Returns all <code>org.openinfinity.core.cloud.domain.Deployment</code> objects.
	 * 
	 * @return Collection<Deployment>
	 */

	@AuditTrail
	public Collection<Deployment> loadAll() {
		Collection<Deployment> deployments = jdbcTemplate.query(LOAD_ALL_SQL, new DeploymentRowMapper());
		return Collections.unmodifiableCollection(deployments);
	}

	@AuditTrail
	public Collection<Deployment> loadDeployments(int page, int rows) {
		// FIXME
		return null;
	}
	
	private class DeploymentRowMapper implements RowMapper<Deployment> {
		
		public Deployment mapRow(ResultSet resultSet, int rowNum) throws SQLException {
			Deployment deployment = new Deployment();
			deployment.setId(resultSet.getInt("ID"));
			deployment.setState(resultSet.getInt("STATE"));
			deployment.setOrganizationId(resultSet.getLong("ORGANIZATION_ID"));
			deployment.setInstanceId(resultSet.getInt("INSTANCE_ID"));
			deployment.setClusterId(resultSet.getInt("CLUSTER_ID"));
			deployment.setLocation(resultSet.getString("LOCATION"));
			deployment.setName(resultSet.getString("NAME"));
			deployment.setDeploymentTimestamp(resultSet.getTimestamp("cur_timestamp"));
			return deployment;
		}
	
	}
	
}