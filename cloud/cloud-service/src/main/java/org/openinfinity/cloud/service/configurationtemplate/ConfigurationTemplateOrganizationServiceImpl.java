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
import org.openinfinity.cloud.domain.configurationtemplate.ConfigurationTemplateOrganization;
import org.openinfinity.cloud.domain.repository.configurationtemplate.ConfigurationTemplateOrganizationRepository;
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
public class ConfigurationTemplateOrganizationServiceImpl implements ConfigurationTemplateOrganizationService {
	private static final Logger LOGGER = Logger.getLogger(ConfigurationTemplateOrganizationServiceImpl.class.getName());

	@Autowired
	private ConfigurationTemplateOrganizationRepository configurationTemplateOrganizationRepository;

    @Override
    public ConfigurationTemplateOrganization create(ConfigurationTemplateOrganization organization) {
        return configurationTemplateOrganizationRepository.create(organization);
    }

    @Override
    public void update(ConfigurationTemplateOrganization organization) {
        configurationTemplateOrganizationRepository.update(organization);
    }

    @Log
    public Collection<ConfigurationTemplateOrganization> loadAll() {
        return configurationTemplateOrganizationRepository.loadAll();
    }

    @Override
    public ConfigurationTemplateOrganization load(BigInteger id) {
        return configurationTemplateOrganizationRepository.load(id);
    }

    @Override
    public void delete(ConfigurationTemplateOrganization organization) {
        configurationTemplateOrganizationRepository.delete(organization);
    }

    @Override
    public Collection<ConfigurationTemplateOrganization> loadAllForTemplate(int templateId){
       return configurationTemplateOrganizationRepository.loadAllForTemplate(templateId);
    }



}