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

package org.openinfinity.cloud.service.administrator;

import java.util.Collection;
import java.util.Collections;

import org.openinfinity.cloud.domain.CloudProvider;
import org.openinfinity.cloud.domain.repository.administrator.CloudProviderRepository;
import org.openinfinity.core.annotation.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * Cluster service interface implementation for building clusters inside a cloud environment.
 * 
 * @version 1.0.0 Initial version
 * @since 1.0.0
 */
@Service("cloudProviderService")
public class CloudProviderServiceImpl implements CloudProviderService {
	
	@Autowired
	@Qualifier("cloudRepository")
	CloudProviderRepository cloudRepository;
			
	@Log
	public Collection<CloudProvider> getCloudProviders() {
		return Collections.unmodifiableCollection(cloudRepository.getCloudProviders());
	}
	
}