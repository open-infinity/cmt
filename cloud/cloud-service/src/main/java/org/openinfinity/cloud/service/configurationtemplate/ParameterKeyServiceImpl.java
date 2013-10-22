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

import java.util.List;

import org.apache.log4j.Logger;
import org.openinfinity.cloud.domain.configurationtemplate.ParameterKey;
import org.openinfinity.cloud.domain.repository.configurationtemplate.ParameterKeyRepository;
import org.openinfinity.core.annotation.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Vedran Bartonicek
 * @version 1.3.0
 * @since 1.3.0
 */

@Service("configurationElementParameterKeyService")
public class ParameterKeyServiceImpl implements ParameterKeyService {
	private static final Logger LOGGER = Logger.getLogger(ParameterKeyServiceImpl.class.getName());

	@Autowired
	private ParameterKeyRepository parameterKeyRepository;
	
	@Log
    public List<ParameterKey> getAll() {
        return parameterKeyRepository.getAll();
    }
	
}