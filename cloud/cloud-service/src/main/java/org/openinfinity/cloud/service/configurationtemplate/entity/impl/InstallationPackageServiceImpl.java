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
import org.openinfinity.cloud.domain.configurationtemplate.entity.InstallationPackage;
import org.openinfinity.cloud.service.configurationtemplate.entity.api.InstallationPackageService;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.Collection;

/**
 * @author Vedran Bartonicek
 * @version 1.3.0
 * @since 1.3.0
 */

@Service
public class InstallationPackageServiceImpl implements InstallationPackageService {
	private static final Logger LOGGER = Logger.getLogger(InstallationPackageServiceImpl.class.getName());


    @Override
    public InstallationPackage create(InstallationPackage obj) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void update(InstallationPackage obj) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Collection<InstallationPackage> loadAll() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public InstallationPackage load(BigInteger id) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void delete(InstallationPackage obj) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void delete(BigInteger id) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}