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
package org.openinfinity.cloud.service.configurationtemplate;

import org.apache.log4j.Logger;
import org.openinfinity.cloud.domain.configurationtemplate.ConfigurationElement;
import org.openinfinity.cloud.domain.configurationtemplate.ParameterValue;
import org.openinfinity.cloud.domain.repository.configurationtemplate.ConfigurationElementDependencyRepository;
import org.openinfinity.cloud.domain.repository.configurationtemplate.ConfigurationElementRepository;
import org.openinfinity.core.annotation.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.Collection;
import java.util.Map;


/**
 * @author Vedran Bartonicek
 * @version 1.3.0
 * @since 1.3.0
 */

@Service
public class ConfigurationElementServiceImpl implements ConfigurationElementService {
	private static final Logger LOGGER = Logger.getLogger(ConfigurationElementServiceImpl.class.getName());

	@Autowired
	private ConfigurationElementRepository elementRepository;

    @Autowired
    ConfigurationElementDependencyRepository elementDependencyRepository;

    @Override
    public ConfigurationElement create(ConfigurationElement ConfigurationElement) {
        return elementRepository.create(ConfigurationElement);
    }

    @Override
    public void update(ConfigurationElement ConfigurationElement) {
        elementRepository.delete(ConfigurationElement);
    }

    @Log
    public Collection<ConfigurationElement> loadAll() {
        return elementRepository.loadAll();
    }

    @Log
    public Collection<ConfigurationElement> loadDependees(int elementId) {
        return elementRepository.loadDependees(elementId);
    }

    @Override
    public ConfigurationElement load(BigInteger id) {
        return elementRepository.load(id);
    }

    @Override
    public ConfigurationElement load(int id) {
        return elementRepository.load(id);
    }

    @Override
    public void delete(ConfigurationElement ConfigurationElement) {
        elementRepository.delete(ConfigurationElement);
    }

    @Override
    public Collection<ConfigurationElement> loadAllForTemplate(int templateId) {
        return elementRepository.loadAllForTemplate(templateId);
    }

    @Override
    public void update(ConfigurationElement element, Collection<Integer> dependencies, Map<String, Collection<ParameterValue>> keyValues){
        elementRepository.update(element);
        elementDependencyRepository.deleteByDepenent(element.getId());
        for(Integer o : dependencies){
            elementDependencyRepository.create(element.getId(), o.intValue());
        }
    }

}