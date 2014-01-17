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
package org.openinfinity.cloud.service.configurationtemplate.entity.impl;

import org.openinfinity.cloud.domain.configurationtemplate.entity.ConfigurationElement;
import org.openinfinity.cloud.domain.repository.configurationtemplate.entity.api.ConfigurationElementRepository;
import org.openinfinity.cloud.domain.repository.configurationtemplate.entity.api.ParameterKeyRepository;
import org.openinfinity.cloud.domain.repository.configurationtemplate.entity.api.ParameterValueRepository;
import org.openinfinity.cloud.domain.repository.configurationtemplate.relation.api.ElementToElementRepository;
import org.openinfinity.cloud.domain.repository.configurationtemplate.relation.api.ElementToModuleRepository;
import org.openinfinity.cloud.domain.repository.configurationtemplate.relation.api.TemplateToElementRepository;
import org.openinfinity.cloud.service.configurationtemplate.entity.api.ConfigurationElementService;
import org.openinfinity.core.annotation.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.Collection;


/**
 * @author Vedran Bartonicek
 * @version 1.3.0
 * @since 1.3.0
 */

@Service
public class ConfigurationElementServiceImpl implements ConfigurationElementService {
	@Autowired
	private ConfigurationElementRepository elementRepository;

    @Autowired
    ElementToElementRepository elementDependencyRepository;

    @Autowired
    TemplateToElementRepository templateElementRepository;

    @Autowired
    ElementToModuleRepository elementToModuleRepository;

    @Autowired
    ParameterKeyRepository keyRepository;

    @Autowired
    ParameterValueRepository valueRepository;

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
    public void delete(ConfigurationElement ConfigurationElement) {
        elementRepository.delete(ConfigurationElement);
    }

    @Override
    public void delete(BigInteger id) {
        elementDependencyRepository.deleteByDepenent(id.intValue());
        templateElementRepository.deleteByElement(id.intValue());
        elementToModuleRepository.deleteByElement(id.intValue());
        elementRepository.delete(id);
    }
    @Override
    public Collection<ConfigurationElement> loadAllForTemplate(int templateId) {
        return elementRepository.loadAllForTemplate(templateId);
    }

    @Override
    @Transactional(rollbackFor=Exception.class)
    public void update(ConfigurationElement element, Collection<Integer> dependencies, Collection<Integer> modules){
        elementRepository.update(element);
        elementDependencyRepository.deleteByDepenent(element.getId());
        for(Integer o : dependencies){
            elementDependencyRepository.create(element.getId(), o.intValue());
        }
        elementToModuleRepository.deleteByElement(element.getId());
        for(Integer o : modules){
            elementToModuleRepository.create(element.getId(), o.intValue());
        }
    }

    @Override
    @Transactional(rollbackFor=Exception.class)
    public void create(ConfigurationElement element, Collection<Integer> dependencies, Collection<Integer> modules){
        elementRepository.create(element);
        for (Integer d : dependencies){
            elementDependencyRepository.create(element.getId(), d.intValue());
        }
        for (Integer d : modules){
            elementToModuleRepository.create(element.getId(), d.intValue());
        }
    }

}