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

import org.apache.log4j.Logger;
import org.openinfinity.cloud.domain.configurationtemplate.entity.ConfigurationTemplate;
import org.openinfinity.cloud.domain.repository.configurationtemplate.entity.api.ConfigurationTemplateRepository;
import org.openinfinity.cloud.domain.repository.configurationtemplate.relation.api.TemplateToElementRepository;
import org.openinfinity.cloud.domain.repository.configurationtemplate.relation.api.TemplateToOrganizationRepository;
import org.openinfinity.cloud.service.configurationtemplate.entity.api.ConfigurationTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Vedran Bartonicek
 * @version 1.3.0
 * @since 1.3.0
 */

@Service
public class ConfigurationTemplateServiceImpl implements ConfigurationTemplateService {

    private static final Logger LOG = Logger.getLogger(ConfigurationTemplateServiceImpl.class.getName());

    @Autowired
	private ConfigurationTemplateRepository configurationTemplateRepository;

    @Autowired
    private TemplateToOrganizationRepository templateToOrganizationRepository;

    @Autowired
    private TemplateToElementRepository templateToElementRepository;

    @Override
    @Transactional
    public void create(ConfigurationTemplate ct, List<Integer> elements, List<Integer> organizations) {
        ConfigurationTemplate ct0 = configurationTemplateRepository.create(ct);
        LOG.debug("new id = " + ct.getId() + " " + ct0.getId());
        for(Integer o : organizations){
            templateToOrganizationRepository.create(ct.getId(), o.intValue());
        }
        for(Integer e : elements){
            templateToElementRepository.create(ct.getId(), e.intValue());
        }
    }

    @Override
    public ConfigurationTemplate create(ConfigurationTemplate obj) {
        return null;
    }

    @Override
    @Transactional
    public void delete(BigInteger id) {
        templateToElementRepository.deleteByTemplate(id.intValue());
        templateToOrganizationRepository.deleteByTemplate(id.intValue());
        configurationTemplateRepository.delete(id);
    }

    @Override
    public void delete(ConfigurationTemplate configurationTemplate) {
    }

    @Override
    public void update(ConfigurationTemplate configurationTemplate) {
        configurationTemplateRepository.update(configurationTemplate);
    }

    @Override
    public Collection<ConfigurationTemplate> loadAll() {
        return null;
    }

    @Override
    public ConfigurationTemplate load(BigInteger id) {
        return configurationTemplateRepository.load(id);
    }

    // TODO: probably this function can be optimized
    @Override
    @Transactional
    public List<ConfigurationTemplate> loadAllForOrganizations(List<Long> organizationIds) {
        List<ConfigurationTemplate> aggregatedTemplates = new ArrayList<ConfigurationTemplate>();

        // Search for templates that are mapped to organizations that the user is member of
        for(Long oid : organizationIds){
            List<ConfigurationTemplate> templatesForOrganizations = configurationTemplateRepository.loadForOrganizarion(oid);
            for (ConfigurationTemplate ct : templatesForOrganizations){
                if (!aggregatedTemplates.contains(ct)) {
                    aggregatedTemplates.add(ct);
                }
            }
        }

        // Search for templates without assigned organization
        Collection<ConfigurationTemplate> templatesWithoutOrganization = new LinkedList<ConfigurationTemplate>();
        Collection<ConfigurationTemplate> allTemplates = configurationTemplateRepository.loadAll();
        for (ConfigurationTemplate ct : allTemplates){
            if (templateToOrganizationRepository.loadAllForTemplate(ct.getId()).size() == 0) {
                templatesWithoutOrganization.add(ct);
            }
        }

        aggregatedTemplates.addAll(templatesWithoutOrganization);
        return aggregatedTemplates;
    }

    @Override
    @Transactional
    public void update(ConfigurationTemplate ct, List<Integer> elements, List<Integer> organizations) {
        configurationTemplateRepository.update(ct);
        templateToOrganizationRepository.deleteByTemplate(ct.getId());
        for(Integer o : organizations){
            templateToOrganizationRepository.create(ct.getId(), o.intValue());
        }

        templateToElementRepository.deleteByTemplate(ct.getId());
        for(Integer e : elements){
            templateToElementRepository.create(ct.getId(), e.intValue());
        }
    }
}