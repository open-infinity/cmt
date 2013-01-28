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
	 * Rename the given property
	 */
	public void rename(String organizationId, String oldkey, String newkey);

	/**
	 * Load a property by key.
	 */
	public SharedProperty loadByKey(String organizationId, String key);

	/**
	 * Load all shared properties from the database.
	 */
	public Collection<SharedProperty> loadAll(List<String> organizationIds);

	/**
	 * Delete the property by key.
	 */
	public boolean deleteByKey(String organizationId, String key);
}

