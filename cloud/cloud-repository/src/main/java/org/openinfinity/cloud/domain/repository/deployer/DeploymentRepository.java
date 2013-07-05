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

import java.util.Collection;

import org.openinfinity.cloud.domain.Deployment;
import org.openinfinity.cloud.domain.DeploymentStatus;

/**
 * CRUD interface for storing <code>org.openinfinity.core.cloud.domain.Deployment</code> objects.
 * 
 * @author Ilkka Leinonen
 * @author Tommi Siitonen
 * @version 1.0.0
 * @since 1.0.0
 */
public interface DeploymentRepository {
	
	/**
	 * Stores <code>org.openinfinity.core.cloud.domain.Deployment</code> to registry.
	 * 
	 * @param deployment Represents the deployment information.
	 * @return Deployment Represents the created object with unique id.
	 */
	Deployment store(Deployment deployment);

	/**
	 * Stores new deployment <code>org.openinfinity.core.cloud.domain.Deployment</code> to registry
	 * and updates existing deployment in NOT_DEPLOYED-CREATED state with same name and target to state NOT_DEPLOYED
	 * 
	 * @param deployment Represents the deployment information.
	 * @return Deployment Represents the created object with unique id.
	 */
	Deployment storeAndUpdate(Deployment deployment);

	/**
	 * Updates deployment <code>org.openinfinity.core.cloud.domain.Deployment</code>  location and to DEPLOYED state 
	 * 
	 * @param deployment Represents the deployment information.
	 * @return Deployment Represents the created object with unique id.
	 */
	void updateLocationAndState(Deployment deployment);
	
	
	/**
	 * Updates existing deployment <code>org.openinfinity.core.cloud.domain.Deployment</code>  in DEPLOYED state 
	 * with same name and target to defined state to registry
	 * 
	 * @param deployment Represents the deployment information.
	 * @param newState Represents the state to be updated to.
	 */
	void updateExistingDeployedDeploymentState(Deployment deployment, int newState);

	/**
	 * Update Deployment state by clusterId.
	 * @param clusterId
	 * @param newState
	 */
	public void updateDeploymentStateByClusterId(int clusterId, int newState);
	
	/**
	 * Update DeploymentStatus state by clusterId.
	 * @param clusterId
	 * @param newState
	 */
	public void updateDeploymentStatusStateByClusterId(int clusterId, int newState);
	
	
	/**
	 * Returns all <code>org.openinfinity.core.cloud.domain.Deployment</code> objects based on organization id.
	 * 
	 * @param organizationId Represents the organization id for the deployments.
	 * @return Collection<Deployment>
	 */
	Collection<Deployment> loadAllForOrganization(long organizationId);
	
	/**
	 * Returns all <code>org.openinfinity.core.cloud.domain.Deployment</code> objects.
	 * 
	 * @return Collection<Deployment>
	 */
	Collection<Deployment> loadAll();
	
	/**
	 * Returns set of <code>org.openinfinity.core.cloud.domain.Deployment</code> objects.
	 * 
	 * @param page represents a table page.
 	 * @param rows represents number of rows per page.
	 * @return Collection<Deployment>
	 */
	Collection<Deployment> loadDeployments(int page, int rows);
	
	/**
	 * Returns <code>org.openinfinity.core.cloud.domain.Deployment</code> based on deployment id.
	 * 
	 * @param id
	 * @return <code>org.openinfinity.core.cloud.domain.Deployment</code> Represents the object fetched from registry based on deployment id.
	 */
	Deployment loadById(int id);

	/**
	 * Returns <code>org.openinfinity.core.cloud.domain.Deployment</code> based on deployment id.
	 * 
	 * @param id
	 * @return <code>org.openinfinity.core.cloud.domain.Deployment</code> Represents the object fetched from registry based on deployment id.
	 * @return Collection<Deployment> Represents the objects fetched from registry based on instanceid, organizationId, clusterid and deployment name.
	 */
	Collection<Deployment> loadByOrgInstClusName(long organizationId, int instanceId, int clusterId, String name);
	
	/**
	 * Returns <code>org.openinfinity.core.cloud.domain.Deployment</code> based on deployment id.
	 * 
	 * @param deployment Represents the deployment information to be compared.
	 * @return <code>org.openinfinity.core.cloud.domain.Deployment</code> Represents the object fetched from registry based on deployment id.
	 * @return Collection<Deployment> Represents the objects fetched from registry based on instanceid, organizationId, clusterid and deployment name.
	 */
	Collection<Deployment> loadByOrgInstClusNameDeployedNewer(Deployment deployment);
	
	/**
	 * Updates <code>org.openinfinity.core.cloud.domain.Deployment</code> to registry.
	 * 
	 * @param deployment Represents the deployment information.
	 * 
	 */
	void updateDeployment(Deployment deployment);
	
	/**
	 * Update deployment status object.
	 */
	void updateDeploymentStateById(int deploymentId, int state);

	/**
	 * Update deployment status object.
	 */
	void updateDeploymentStateByOrganizationIdAndName(int organizationId, String name, int state);	
	
	/**
	 * Update DeploymentStatus object states.
	 */	
	void updateDeploymentStatusStatesFromToByDeploymentId(int from, int to, int deploymentId);
	
	
	/**
	 * Deletes id based on <code>org.openinfinity.core.cloud.domain.Deployment</code> object.
	 * 
	 * @param deployment Represents the <code>org.openinfinity.core.cloud.domain.Deployment</code> object for deletion.
	 */
	void delete(Deployment deployment);
	
	/**
	 * Persists deployment status object.
	 */
	void storeDeploymentStatus(DeploymentStatus deploymentStatus);
	
	/**
	 * Executes queries based on cluster id to persistent memory containing deployment status information.
	 */
	public Collection<DeploymentStatus> loadDeploymentStatusesByClusterId(long clusterId);

	/**
	 * Executes queries based on deployment id to persistent memory containing deployment status information.
	 */
	public Collection<DeploymentStatus> loadDeploymentStatusesByDeploymentId(long clusterId);		
	
}
