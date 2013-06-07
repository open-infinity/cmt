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
import org.openinfinity.cloud.domain.repository.properties.CentralizedPropertiesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * Service layer of Centralized Properties.
 * 
 * @author Timo Saarinen
 */
@Service(value="centralizedPropertiesService")
@Qualifier("centralizedPropertiesService")
public class CentralizedPropertiesServiceImpl implements CentralizedPropertiesService {

	@Autowired
	@Qualifier("centralizedPropertiesRepository")
	private CentralizedPropertiesRepository repository;
	
	@Override
	public SharedProperty store(SharedProperty prop) {
		return repository.store(prop);
	}

	public Collection<SharedProperty> loadAll(SharedProperty sample) {
		return repository.loadAll(sample);
	}

	@Override
	public SharedProperty load(SharedProperty prop) {
		return repository.load(prop);
	}

	@Override
	public boolean delete(SharedProperty p) {
		return repository.delete(p);
	}

	@Override
	public boolean rename(SharedProperty prop, String newkey) {
		SharedProperty old = repository.load(prop);
		if (repository.delete(prop)) {
			old.setKey(newkey);
			repository.store(old);
			return true;
		}
		return false;
	}
	
}
