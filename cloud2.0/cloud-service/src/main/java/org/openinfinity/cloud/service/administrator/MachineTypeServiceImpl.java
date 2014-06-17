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

import org.openinfinity.cloud.domain.ClusterType;
import org.openinfinity.cloud.domain.MachineType;
import org.openinfinity.cloud.domain.repository.administrator.ClusterTypeRepository;
import org.openinfinity.cloud.domain.repository.administrator.MachineTypeRepository;
import org.openinfinity.core.annotation.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

/**
 * Machine type service implementation
 *
 * @author Timo Tapanainen
 * @version 1.0.0 Initial version
 * @since 1.0.0
 */
@Service("machineTypeService")
public class MachineTypeServiceImpl implements MachineTypeService {
	@Autowired
	@Qualifier("machineTypeRepository")
    MachineTypeRepository machineTypeRepository;
	
	@Log
	public Collection<MachineType> getMachineTypes(List<String> userOrganizations) {
		return machineTypeRepository.getMachineTypes(userOrganizations);
	}
}