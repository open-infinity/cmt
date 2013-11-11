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
import org.openinfinity.cloud.domain.repository.configurationtemplate.ConfigurationElementRepository;
import org.openinfinity.core.annotation.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.Collection;



/**
 * @author Vedran Bartonicek
 * @version 1.3.0
 * @since 1.3.0
 */

@Service
public class ConfigurationElementServiceImpl implements ConfigurationElementService {
	private static final Logger LOGGER = Logger.getLogger(ConfigurationElementServiceImpl.class.getName());

	@Autowired
	private ConfigurationElementRepository configurationElementRepository;

    @Override
    public ConfigurationElement create(ConfigurationElement ConfigurationElement) {
        return configurationElementRepository.create(ConfigurationElement);
    }

    @Override
    public void update(ConfigurationElement ConfigurationElement) {
        configurationElementRepository.delete(ConfigurationElement);
    }

    @Log
    public Collection<ConfigurationElement> loadAll() {
        return configurationElementRepository.loadAll();
    }

    @Override
    public ConfigurationElement load(BigInteger id) {
        return configurationElementRepository.load(id);
    }

    @Override
    public void delete(ConfigurationElement ConfigurationElement) {
        configurationElementRepository.delete(ConfigurationElement);
    }

    @Override
    public Collection<ConfigurationElement> loadAllForTemplate(int templateId) {
        return configurationElementRepository.loadAllForTemplate(templateId);
    }
}