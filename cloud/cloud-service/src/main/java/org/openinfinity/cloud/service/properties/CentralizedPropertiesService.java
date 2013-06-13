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
import java.util.List;

import org.openinfinity.cloud.domain.SharedProperty;

/**
 * Service interface of Centralized Properties portlet.
 * 
 * @author Timo Saarinen
 */
public interface CentralizedPropertiesService {

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
	 * Change key part of the given property.
	 * @return true if found, false if not 
	 */
	public boolean rename(SharedProperty prop, String newkey);
}
