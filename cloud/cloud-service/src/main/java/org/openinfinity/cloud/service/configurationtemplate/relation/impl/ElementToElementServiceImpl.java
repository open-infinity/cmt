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
import org.openinfinity.cloud.domain.configurationtemplate.relation.ElementToElement;
import org.openinfinity.cloud.domain.repository.configurationtemplate.relation.api.ElementToElementRepository;
import org.openinfinity.cloud.service.configurationtemplate.relation.api.ElementToElementService;
import org.openinfinity.core.annotation.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.List;



/**
 * @author Vedran Bartonicek
 * @version 1.3.0
 * @since 1.3.0
 */

@Service
public class ElementToElementServiceImpl implements ElementToElementService {
	private static final Logger LOGGER = Logger.getLogger(ElementToElementServiceImpl.class.getName());

    @Autowired
	private ElementToElementRepository elementToElementRepository;

	@Log
    public List<ElementToElement> loadAll() {
        return elementToElementRepository.loadAll();
    }

    @Override
    public ElementToElement create(ElementToElement obj) {
        return null;
    }

    @Override
    public void update(ElementToElement obj) {
    }

    @Override
    public ElementToElement load(BigInteger id) {
        return null;
    }

    @Override
    public void delete(ElementToElement obj) {
    }

    @Override
    public void delete(BigInteger id) {
    }

}