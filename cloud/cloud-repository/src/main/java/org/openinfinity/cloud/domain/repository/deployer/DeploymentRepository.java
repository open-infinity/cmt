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

/**
 * CRUD interface for storing <code>org.openinfinity.core.cloud.domain.Deployment</code> objects.
 * 
 * @author Ilkka Leinonen
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
	 * Deletes id based on <code>org.openinfinity.core.cloud.domain.Deployment</code> object.
	 * 
	 * @param deployment Represents the <code>org.openinfinity.core.cloud.domain.Deployment</code> object for deletion.
	 */
	void delete(Deployment deployment);

}
