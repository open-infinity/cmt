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
import org.openinfinity.cloud.domain.configurationtemplate.entity.InstallationModule;
import org.openinfinity.cloud.domain.configurationtemplate.entity.ParameterKey;
import org.openinfinity.cloud.domain.configurationtemplate.entity.ParameterValue;
import org.openinfinity.cloud.domain.configurationtemplate.relation.ModuleToPackage;
import org.openinfinity.cloud.domain.repository.configurationtemplate.entity.api.InstallationModuleRepository;
import org.openinfinity.cloud.domain.repository.configurationtemplate.entity.api.ParameterKeyRepository;
import org.openinfinity.cloud.domain.repository.configurationtemplate.entity.api.ParameterValueRepository;
import org.openinfinity.cloud.domain.repository.configurationtemplate.relation.api.ModuleToPackageRepository;
import org.openinfinity.cloud.service.configurationtemplate.entity.api.InstallationModuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.Collection;
import java.util.Map;

/**
 * @author Vedran Bartonicek
 * @version 1.3.0
 * @since 1.3.0
 */

@Service
public class InstallationModuleServiceImpl implements InstallationModuleService {
	private static final Logger LOGGER = Logger.getLogger(InstallationModuleServiceImpl.class.getName());

    @Autowired
    InstallationModuleRepository moduleRepository;

    @Autowired
    ModuleToPackageRepository modulePackageRepository;

    @Autowired
    ParameterKeyRepository keyRepository;

    @Autowired
    ParameterValueRepository valueRepository;

    @Override
    public InstallationModule create(InstallationModule obj) {
        return null;
    }

    @Override
    public void update(InstallationModule obj) {
    }

    @Override
    public void update(InstallationModule module, Collection<Integer> packages, Map<String, Collection<String>> parameters) {
        moduleRepository.update(module);

        // Delete packages, then put new packages to database
        modulePackageRepository.deleteByModule(module.getId());
        for(Integer o : packages){
            modulePackageRepository.create(new ModuleToPackage(module.getId(), o.intValue()));
        }

        // Delete values for each key, then delete keys
        for(ParameterKey key : keyRepository.loadAllForModule(module.getId())){
            valueRepository.deleteByKeyId(key.getId());
        }
        keyRepository.deleteByModuleId(module.getId());

        // Store new parameters
        storeKeysAndValues(parameters, module);
    }

    @Override
    public Collection<InstallationModule> loadAll() {
        return moduleRepository.loadAll();
    }

    @Override
    public InstallationModule load(BigInteger id) {
        return null;
    }

    @Override
    public void delete(InstallationModule obj) {
    }

    @Override
    public void delete(BigInteger id) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    private void storeKeysAndValues(Map<String, Collection<String>> parameters, InstallationModule module){
        for (String name : parameters.keySet()){
            ParameterKey key = new ParameterKey(module.getId(), name);
            keyRepository.create(key);
            for (String value : parameters.get(name)){
                valueRepository.create(new ParameterValue(key.getId(), value));
            }
        }
    }
}