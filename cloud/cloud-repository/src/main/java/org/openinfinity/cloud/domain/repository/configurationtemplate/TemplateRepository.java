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
package org.openinfinity.cloud.domain.repository.configurationtemplate;

import org.openinfinity.cloud.domain.configurationtemplate.Template;
import org.openinfinity.cloud.domain.repository.common.AbstractCrudRepositoryInterface;

import java.util.List;

/**
 * CRUD interface for storing <code>org.openinfinity.core.cloud.domain.template</code> objects.
 * 
 * @author Vedran Bartonicek
 * @version 1.3.0
 * @since 1.3.0
 */
public interface TemplateRepository extends AbstractCrudRepositoryInterface<Template> {
	
    List<Template> getTemplates(Long oid);

}