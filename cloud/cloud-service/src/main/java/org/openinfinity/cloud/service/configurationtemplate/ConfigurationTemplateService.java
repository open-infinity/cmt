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
import org.openinfinity.cloud.service.common.AbstractCrudServiceInterface;

import java.util.List;


/**
 * ConfigurationTemplate interface for management of cloud configuration templates.
 * 
 * @author Vedran Bartonicek
 * @version 1.3.0
 * @since 1.3.0
 */
public interface ConfigurationTemplateService extends AbstractCrudServiceInterface<ConfigurationTemplate> {
	
    List<ConfigurationTemplate> getTemplates(List<Long> organizationIds);

    void create(ConfigurationTemplate ct, List<Integer> elements, List<Integer> organizations);

    void delete(int templateId);

    void update(ConfigurationTemplate ct, List<Integer> elements, List<Integer> organizations);


}