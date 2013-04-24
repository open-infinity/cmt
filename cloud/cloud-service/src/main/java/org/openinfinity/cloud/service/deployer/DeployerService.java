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
package org.openinfinity.cloud.service.deployer;

import java.io.InputStream;
import java.util.Collection;

import org.openinfinity.cloud.domain.Deployment;
import org.openinfinity.cloud.domain.DeploymentStatus;
import org.openinfinity.cloud.domain.Instance;
import org.openinfinity.cloud.domain.Cluster;

/**
 * Deployer interface for deploying new applications inside a cloud environment.
 * 
 * @author Ilkka Leinonen
 * @author Tommi Siitonen
 * @version 1.0.0
 * @since 1.0.0
 */
public interface DeployerService {
	
	/**
	 * Deploys new <code>org.openinfinity.core.cloud.deployer.domain.Deployment</code>
	 * 
	 * @param deployment Represents the data of the platform.
	 * @return <code>org.openinfinity.core.cloud.deployer.domain.Deployment</code> Represents the actual reference to deployment metadata.
	 */
	Deployment deploy(Deployment deployment);
	
	/**
	 * Loads all <code>org.openinfinity.core.cloud.deployer.domain.Deployment</code> objects based on organization id.
	 * 
	 * @param organizationId Represents the organization and it's deployments based on organization id. 
	 * @return <code>org.openinfinity.core.cloud.deployer.domain.Deployment</code> Represents the collection of items.
	 */
	Collection<Deployment> loadDeploymentsForOrganization(long organizationId); 

	/**
	 * Returns all <code>org.openinfinity.core.cloud.domain.Deployment</code> objects.
	 * 
	 * @return Collection<Deployment>
	 */
	Collection<Deployment> loadDeployments();
	
	/**
	 * Returns set of <code>org.openinfinity.core.cloud.domain.Deployment</code> objects.
	 * 
	 * @param page represents a table page.
 	 * @param rows represents number of rows per page.
	 * @return Collection<Deployment>
	 */
	Collection<Deployment> loadDeployments(int page, int rows);
	
	/**
	 * Rollbacks an earlier installation of a <code>org.openinfinity.core.cloud.deployer.domain.Deployment</code> object.
	 *  
	 * @param deployment Represents the deployment to be rolled back.
	 */
	void rollback(Deployment deployment);
	
	/**
	 * Returns collection of <code>org.openinfinity.cloud.domain.Instance</code> objects.
	 * 
	 * @param organizationId Represents the orgazanition id.
	 * @return <code>org.openinfinity.core.cloud.domain.Instance</code> Represents collection of instances.
	 */
	Collection<Instance> loadInstances(long organizationId);

	/**
	 * Returns collection of <code>org.openinfinity.cloud.domain.Cluster</code>.
	 * 
	 * @param instanceId Represents the instance id.
	 * @return <code>org.openinfinity.cloud.domain.Cluster</code> Represents collection of clusters.
	 */
	Collection<Cluster> loadClusters(int instanceId);
	
	/**
	 * Loads stream based on bucket name and key.
	 * 
	 * @param bucketName
	 * @param key
	 * @return
	 */
	public InputStream load(String bucketName, String key);

	/**
	 * Stores deployment status object.
	 * 
	 * @param deploymentStatus Represents the state of deployment on specified machine.
	 */
	void storeDeploymentStatus(DeploymentStatus deploymentStatus);

	/**
	 * Loads deployment statuses of specified cluster.
	 * 
	 * @param clusterId Represents unique cluster id.
	 * @return Collection<DeploymentStatus> Represents collection of deployment statuses of specified cluster.
	 */
	Collection<DeploymentStatus> loadDeploymentStatusesForCluster(int clusterId);
	
	
	/**
	 * Loads deployment statuses of specified cluster.
	 * 
	 * @param deploymentId Represents unique deployment id.
	 * @return Collection<DeploymentStatus> Represents collection of deployment statuses of specified deployment.
	 */
	Collection<DeploymentStatus> loadDeploymentStatusesForDeployment(int deploymentId);
	
}