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
package org.openinfinity.cloud.service.configurationtemplate.relation.impl;

import org.apache.log4j.Logger;
import org.openinfinity.cloud.domain.configurationtemplate.relation.TemplateToElement;
import org.openinfinity.cloud.domain.repository.configurationtemplate.relation.api.TemplateToElementRepository;
import org.openinfinity.cloud.service.configurationtemplate.relation.api.TemplateToElementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;


/**
 * @author Vedran Bartonicek
 * @version 1.3.0
 * @since 1.3.0
 */

@Service
public class TemplateToElementServiceImpl implements TemplateToElementService {
	private static final Logger LOGGER = Logger.getLogger(TemplateToElementServiceImpl.class.getName());

	@Autowired
	private TemplateToElementRepository templateToElementRepository;

    @Override
    public void create(int templateId, int elementId) {
        templateToElementRepository.create(templateId, elementId);
    }

    @Override
    public Collection<TemplateToElement> loadAllForTemplate(int templateId){
       return templateToElementRepository.loadAllForTemplate(templateId);
    }

}