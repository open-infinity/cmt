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
import org.openinfinity.cloud.domain.configurationtemplate.ParameterValue;
import org.openinfinity.cloud.domain.repository.configurationtemplate.ParameterValueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.Collection;
import java.util.LinkedList;


/**
 * @author Vedran Bartonicek
 * @version 1.3.0
 * @since 1.3.0
 */

@Service("configurationElementParameterValueService")
public class ParameterValueServiceImpl implements ParameterValueService {
	private static final Logger LOGGER = Logger.getLogger(ParameterValueServiceImpl.class.getName());

	@Autowired
	private ParameterValueRepository parameterValueRepository;
	
    @Override
    public ParameterValue create(ParameterValue obj) {
        return null;
    }

    @Override
    public void update(ParameterValue obj) {
    }

    @Override
    public Collection<ParameterValue> loadAll() {
        return null;
    }

    @Override
    public ParameterValue load(BigInteger id) {
        return null;
    }

    @Override
    public void delete(ParameterValue obj) {
    }

    @Override
    public Collection<ParameterValue> loadParameterValues(int parameterKeyId) {
        return parameterValueRepository.loadAll(parameterKeyId);
    }

    @Override
    public Collection<String> loadValues(int parameterKeyId) {
        Collection<ParameterValue> parameterValues = parameterValueRepository.loadAll(parameterKeyId);
        LinkedList<String> values = new LinkedList<String>();
        for (ParameterValue pv : parameterValues){
            values.add(pv.getValue());
        }
        return values;
    }
}