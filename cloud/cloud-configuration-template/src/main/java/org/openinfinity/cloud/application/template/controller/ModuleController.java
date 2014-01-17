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

package org.openinfinity.cloud.application.template.controller;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.openinfinity.cloud.annotation.Authenticated;
import org.openinfinity.cloud.application.template.serialization.CollectionsContainer;
import org.openinfinity.cloud.domain.configurationtemplate.entity.InstallationModule;
import org.openinfinity.cloud.domain.configurationtemplate.entity.InstallationPackage;
import org.openinfinity.cloud.domain.configurationtemplate.entity.ParameterKey;
import org.openinfinity.cloud.service.configurationtemplate.entity.api.InstallationModuleService;
import org.openinfinity.cloud.service.configurationtemplate.entity.api.InstallationPackageService;
import org.openinfinity.cloud.service.configurationtemplate.entity.api.ParameterKeyService;
import org.openinfinity.cloud.service.configurationtemplate.entity.api.ParameterValueService;
import org.openinfinity.cloud.util.http.HttpCodes;
import org.openinfinity.cloud.util.serialization.SerializerUtil;
import org.openinfinity.core.util.ExceptionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.portlet.bind.annotation.ResourceMapping;

import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Spring portlet controller for handling Installation Module requests.
 *
 * Handles requests from "Main view, Module tab" and requests from "Module dialog"
 *
 * @author Vedran Bartonicek
 * @version 1.3.0
 * @since 1.3.0
 */
@Controller
@RequestMapping("VIEW")
public class ModuleController extends AbstractController{

	private static final String GET_MODULE = "getModule";
	private static final String GET_PACKAGES_FOR_MODULE = "getPackagesForModule";
	private static final String GET_ALL_PACKAGES = "getAllPackages";
	private static final String GET_PARAMETER_KEYS_AND_VALUES = "getParameterKeysAndValues";
	private static final String EDIT_MODULE = "editModule";
    private static final String DELETE_MODULE = "deleteModule";
    private static final String CREATE_MODULE = "createModule";

    private static final Logger LOG = Logger.getLogger(ModuleController.class.getName());

    @Autowired
	private InstallationModuleService moduleService;

    @Autowired
    private InstallationPackageService packageService;

    @Autowired
    private ParameterKeyService parameterKeyService;

    @Autowired
    private ParameterValueService parameterValueService;


    @Authenticated
    @ResourceMapping(GET_MODULE)
    public void getModule(ResourceRequest request, ResourceResponse response, @RequestParam("moduleId") int moduleId) throws Exception {
        try {
            //User user = liferayService.getUser(request, response);
            //if (user == null) return;
            SerializerUtil.jsonSerialize(response.getWriter(), moduleService.load(BigInteger.valueOf(moduleId)));
        } catch (Exception e) {
            ExceptionUtil.throwSystemException(e);
        }
    }

    @Authenticated
    @ResourceMapping(GET_PARAMETER_KEYS_AND_VALUES)
    public void getParameterKeysAndValues(ResourceRequest request, ResourceResponse response, @RequestParam("moduleId") int moduleId) throws Exception {
        try {
            Map<String, Collection<String>> keyValuesMap = new LinkedHashMap<String, Collection<String>>();
            //Collection<ParameterKey> keys = parameterKeyService.loadAll(moduleId);
            Collection<ParameterKey> keys = parameterKeyService.loadAllForModule(moduleId);
            for (ParameterKey key : keys){
                Collection<String> values = parameterValueService.loadStringValuesForKey(key.getId());
                LOG.debug("Key:" + key.getName() + "Value:" + values);
                keyValuesMap.put(key.getName(), values);
            }
            SerializerUtil.jsonSerialize(response.getWriter(), keyValuesMap);
        } catch (Exception e) {
            ExceptionUtil.throwSystemException(e);
        }
    }

    @Authenticated
    @ResourceMapping(GET_PACKAGES_FOR_MODULE)
    public void getPackagesForModule(ResourceRequest request, ResourceResponse response, @RequestParam("moduleId") int moduleId) throws Exception {
        try {
            Collection<InstallationPackage> availableItems = packageService.loadAll();
            Collection<InstallationPackage> selectedItems = packageService.loadByModule(moduleId);
            SerializerUtil.jsonSerialize(response.getWriter(), new CollectionsContainer<InstallationPackage>(availableItems, selectedItems));
        } catch (Exception e) {
            ExceptionUtil.throwSystemException(e);
        }
    }

    @Authenticated
    @ResourceMapping(GET_ALL_PACKAGES)
    public void getPackages(ResourceRequest request, ResourceResponse response) throws Exception {
        try {
            Collection<InstallationPackage> availableItems = packageService.loadAll();
            Collection<InstallationPackage> selectedItems = new ArrayList<InstallationPackage>();
            SerializerUtil.jsonSerialize(response.getWriter(), new CollectionsContainer<InstallationPackage>(availableItems, selectedItems));
        } catch (Exception e) {
            ExceptionUtil.throwSystemException(e);
        }
    }

    @Authenticated
    @ResourceMapping(EDIT_MODULE)
    public void editModule(ResourceRequest request, ResourceResponse response,
                            @RequestParam("module") String moduleData,
                            @RequestParam("packages") String packagesData,
                            @RequestParam("parameters") String parametersData){
        try {
            ObjectMapper mapper = new ObjectMapper();
            InstallationModule module = mapper.readValue(moduleData, InstallationModule.class);
            Collection<Integer> packages = mapper.readValue(packagesData, Collection.class);
            Map<String, Collection<String>> keyValuesMap = mapper.readValue(parametersData, new TypeReference<Map<String, Collection<String>>>(){});

            LOG.debug("InstallationModule:" + module);
            LOG.debug("dependenciesList:" + packages);
            LOG.debug("Map kv:" + keyValuesMap);

            moduleService.update(module, packages, keyValuesMap);
        } catch (Exception e) {
            e.printStackTrace();
            response.setProperty(ResourceResponse.HTTP_STATUS_CODE, HttpCodes.HTTP_ERROR_CODE_SERVER_ERROR);
        }
    }

    @Authenticated
    @ResourceMapping(CREATE_MODULE)
    public void createModule(ResourceRequest request, ResourceResponse response,
                              @RequestParam("module") String moduleData,
                              @RequestParam("packages") String packagesData,
                              @RequestParam("parameters") String parametersData){
        try {
            ObjectMapper mapper = new ObjectMapper();
            InstallationModule module = mapper.readValue(moduleData, InstallationModule.class);
            Collection<Integer> packages = mapper.readValue(packagesData, Collection.class);
            Map<String, Collection<String>> keyValuesMap = mapper.readValue(parametersData, new TypeReference<Map<String, Collection<String>>>(){});

            LOG.debug("InstallationModule:" + module);
            LOG.debug("dependenciesList:" + packages);
            LOG.debug("Map kv:" + keyValuesMap);

            moduleService.create(module, packages, keyValuesMap);
        } catch (Exception e) {
            e.printStackTrace();
            response.setProperty(ResourceResponse.HTTP_STATUS_CODE, HttpCodes.HTTP_ERROR_CODE_SERVER_ERROR);
        }
    }

    @Authenticated
    @ResourceMapping(DELETE_MODULE)
    public void deleteModule(ResourceRequest request, ResourceResponse response, @RequestParam("id") int moduleId) throws Exception {
        try {
//          if (liferayService.getUser(request, response) == null) return;
            moduleService.delete(BigInteger.valueOf(moduleId));
        } catch (Exception e) {
            ExceptionUtil.throwSystemException(e);
        }
    }
}