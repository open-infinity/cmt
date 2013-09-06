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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.openinfinity.core.annotation.Log;
import org.openinfinity.cloud.domain.configurationtemplate.Template;
import org.openinfinity.cloud.domain.repository.configurationtemplate.TemplateRepository;

/**
 * @author Vedran Bartonicek
 * @version 1.3.0
 * @since 1.3.0
 */

@Service("configurationTemplateService")
public class TemplateServiceImpl implements TemplateService {
	@Autowired
	private TemplateRepository templateRepository;
	
	@Log 
    public Set<Template> getTemplates(List<Long> organizationIds) {
	    Set<Template> templates = new HashSet<Template>();
	    for(Long oid: organizationIds){
	        templates.addAll(templateRepository.getTemplates(oid));
	    }
        return templates;
    }
	
}