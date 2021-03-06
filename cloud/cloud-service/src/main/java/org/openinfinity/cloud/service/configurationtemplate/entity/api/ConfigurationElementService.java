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
package org.openinfinity.cloud.service.configurationtemplate.entity.api;

import org.openinfinity.cloud.domain.configurationtemplate.entity.ConfigurationElement;
import org.openinfinity.cloud.service.common.AbstractCrudServiceInterface;

import java.util.Collection;


/**
 * Template interface for management of cloud configuration tempaltes.
 * 
 * @author Vedran Bartonicek
 * @version 1.3.0
 * @since 1.3.0
 */
public interface ConfigurationElementService extends AbstractCrudServiceInterface<ConfigurationElement> {

    Collection<ConfigurationElement> loadAllForTemplate(int templateId);

    Collection<ConfigurationElement> loadDependees(int elementId);

    Collection<Integer> loadDependeeIds(int elementId);

    void update(ConfigurationElement element, Collection<Integer> dependencies, Collection<Integer> modules);

    void create(ConfigurationElement element, Collection<Integer> dependencies, Collection<Integer> modules);

}