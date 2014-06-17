/*
 * Copyright (c) 2013 the original author or authors.
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

package org.openinfinity.cloud.service.administrator;

import java.util.Collection;
import java.util.List;

import org.openinfinity.cloud.domain.ClusterType;

/**
 * ClusterType service interface provides access to cluster types and their configuration 
 * 
 * @author Vedran Bartonicek
 * @version 1.0.0 Initial version
 * @since 1.0.0
 */

public interface ClusterTypeService {
	static final String[] MACHINE_TYPES = {
	  			"Cores: 1, RAM: 1GB, Disk: 100GB",
	  			"Cores: 2, RAM: 2GB, Disk: 200GB",
	  			"Cores: 4, RAM: 4GB, Disk: 400GB",
	  			"Cores: 8, RAM: 8GB, Disk: 800GB",
	  			"Cores: 16, RAM: 16GB, Disk: 1000GB"};

	
	Collection<ClusterType> getAvailableClusterTypes(List<String> userOrganizations);
}
