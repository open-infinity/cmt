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

import org.openinfinity.cloud.domain.configurationtemplate.ConfigurationTemplate;
import org.openinfinity.cloud.domain.repository.configurationtemplate.ConfigurationTemplateRepository;
import org.openinfinity.core.annotation.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Vedran Bartonicek
 * @version 1.3.0
 * @since 1.3.0
 */

@Service
public class ConfigurationTemplateServiceImpl implements ConfigurationTemplateService {
	@Autowired
	private ConfigurationTemplateRepository configurationTemplateRepository;
	
    @Override
    public ConfigurationTemplate create(ConfigurationTemplate configurationTemplate) {
        return null;
    }

    @Override
    public void update(ConfigurationTemplate configurationTemplate) {
    }

    @Override
    public Collection<ConfigurationTemplate> loadAll() {
        return null;
    }

    @Override
    public ConfigurationTemplate load(BigInteger id) {
        return configurationTemplateRepository.load(id);
    }

    @Override
    public void delete(ConfigurationTemplate configurationTemplate) {
    }

    @Log
    @Override
    public Set<ConfigurationTemplate> getTemplates(List<Long> organizationIds) {
        Set<ConfigurationTemplate> templates = new HashSet<ConfigurationTemplate>();
        for(Long oid : organizationIds){
            templates.addAll(configurationTemplateRepository.getTemplates(oid));
        }
        return templates;
    }
}