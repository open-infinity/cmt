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
package org.openinfinity.cloud.service.properties;

import java.util.Collection;

import org.openinfinity.cloud.domain.Deployment;
import org.openinfinity.cloud.domain.SharedProperty;

/**
 * Service interface of Centralized Properties portlet.
 * 
 * @author Timo Saarinen
 * @author Ilkka Leinonen
 */
public interface CentralizedPropertiesService {
	
	static final int PROPERTIES_STATE_NEW = 0;
	static final int PROPERTIES_STATE_DEPLOYED = 1;	
	static final int PROPERTIES_STATE_DELETED = -1;	

	static final int PROPERTIES_STATE_ERROR = 15;		
	static final int PROPERTIES_STATE_TERMINATED = -11;

	/**
	 * Save or create the given property in database.
	 */
	public SharedProperty store(SharedProperty prop);

	/**
	 * Load a property by key. Key and value fields of the sample is ignored.
	 */
	public Collection<SharedProperty> loadAll(SharedProperty sample);

	/**
	 * Loads all properties.
	 */
	public Collection<SharedProperty> loadAll();	
	
	public Collection<SharedProperty> loadKnownSharedPropertyDeployments();
	
	/**
	 * Load a property by key. Value of the sample is ignored.
	 */
	public SharedProperty load(SharedProperty sample);

	/**
	 * Delete the property by key. Value field is ignored.
	 * @return true if found, false if not
	 */
	public boolean delete(SharedProperty prop);

	
	/**
	 * Delete the properties defined by organization, instance ,cluster id in deleted state.
	 */
	public void deleteByStateOrgInstClusName(long organizationId, int instanceId, int clusterId);
	
	
	/**
	 * Update the property.
	 */
	public void update(SharedProperty prop);

	/**
	 * Update the properties states by organization, instance ,cluster id in new state to deployed state.
	 */
	public void updateStatesNewToFinalizedByOrgInstClusName(long organizationId, int instanceId, int clusterId);

	/**
	 * Updates shared property state by unique id.
	 * @param id Represents the unique id.
	 * @param state state to be updated to.
	 * @return true if update was successful.
	 */
	public boolean updateStateByUniqueId(int id, int state);
	
	
	/**
	 * Change key part of the given property.
	 * @return true if found, false if not 
	 */
	public boolean rename(SharedProperty prop, String newkey);
	
	/**
	 * Loads shared properties by organization id.
	 */
	public Collection<SharedProperty> loadSharedPropertiesByOrganizationIds(Collection<Long> organizationIds);

	/**
	 * Deletes shared property by unique id.
	 * @param id Represents the unique id.
	 * @return true if delete was successful.
	 */
	public boolean deleteByUniqueId(int id);
	
	/**
	 * Deletes properties for defined cluster.
	 * @param clusterId
	 */
	public void terminatePropertiesForCluster(int clusterId);
	

}

