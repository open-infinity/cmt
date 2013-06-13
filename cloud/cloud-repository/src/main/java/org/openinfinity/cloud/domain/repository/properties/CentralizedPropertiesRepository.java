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
package org.openinfinity.cloud.domain.repository.properties;

import java.util.Collection;
import java.util.List;

import org.openinfinity.cloud.domain.SharedProperty;

/**
 * Repository interface of Centralized Properties portlet.
 * 
 * @author Timo Saarinen
 */
public interface CentralizedPropertiesRepository {

	/**
	 * Inserts or updates the given property in the database.
	 * @param prop
	 * @return Returns the given property as it is.
	 */
	public SharedProperty store(SharedProperty prop);

	/**
	 * Returns all the properties matching the sample. Value part it ignored.
	 */
	public Collection<SharedProperty> loadAll(SharedProperty sample);
	
	public Collection<SharedProperty> loadAll();
	
	public Collection<SharedProperty> loadKnownSharedPropertyDeployments();
	
	/**
	 * Load the property using the given given property as a sample. 
	 * The value part will be ignored.
	 * @param prop Sample property used as part of queries
	 * @return New property created based on the sample and the value
	 */
	public SharedProperty load(SharedProperty sample);

	/**
	 * Delete the given property from database. The value part will be ignored.
	 * @param sample Identifies the property to be deleted
	 * @return true if the property was found, false if not
	 */
	public boolean delete(SharedProperty sample);
}
