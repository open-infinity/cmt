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
import org.openinfinity.cloud.domain.repository.administrator.ClusterTypeRepository;
import org.openinfinity.core.annotation.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service("clusterTypeService")
public class ClusterTypeServiceImpl implements ClusterTypeService {
	@Autowired
	@Qualifier("clusterTypeRepository")
	ClusterTypeRepository clusterTypeRepository;
	
	@Log
	public Collection<ClusterType> getAvailableClusterTypes(List<String> userOrganizations) {
		return clusterTypeRepository.getAvailableClusterTypes(userOrganizations);
	}
}