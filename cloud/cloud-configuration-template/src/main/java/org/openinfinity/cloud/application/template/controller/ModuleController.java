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
import org.openinfinity.cloud.domain.configurationtemplate.entity.InstallationModule;
import org.openinfinity.cloud.service.configurationtemplate.entity.api.InstallationModuleService;
import org.openinfinity.cloud.service.configurationtemplate.entity.api.InstallationPackageService;
import org.openinfinity.cloud.service.configurationtemplate.entity.api.ParameterKeyService;
import org.openinfinity.cloud.service.configurationtemplate.entity.api.ParameterValueService;
import org.openinfinity.cloud.util.collection.ListUtil;
import org.openinfinity.cloud.util.http.HttpCodes;
import org.openinfinity.cloud.util.serialization.JsonDataWrapper;
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
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Spring portlet controller for handling installation modules.
 *
 * @author Vedran Bartonicek
 * @version 1.3.0
 * @since 1.3.0
 */
@Controller
@RequestMapping("VIEW")
public class ModuleController extends AbstractController{

    private static final String LOAD_MODULES = "getModules";
	private static final String LOAD_MODULE = "getModule";
	private static final String LOAD_PACKAGES = "getPackages";
	private static final String LOAD_PARAMETER_KEYS_AND_VALUES = "getParameterKeysAndValues";
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
    @ResourceMapping(LOAD_MODULES)
    public void getModules(ResourceRequest request, ResourceResponse response, @RequestParam("page") int page, @RequestParam("rows") int rows) throws Exception {
        try {
            //User user = liferayService.getUser(request, response);
            //if (user == null) return;
            LOG.debug("ENTER getTemplatesForUser, page=" + page + ",rows=" + rows);
            Collection<InstallationModule> modules = moduleService.loadAll();
            LOG.debug("modules=" + modules);

            int records = modules.size();
            int mod = records % rows;
            int totalPages = records / rows;
            if (mod > 0) totalPages++;
            List<InstallationModule> onePage = ListUtil.sliceList(page, rows, new LinkedList<InstallationModule>(modules));
            LOG.debug("onePage=" + onePage);

            SerializerUtil.jsonSerialize(response.getWriter(), new JsonDataWrapper(page, totalPages, records, onePage));
            LOG.debug("EXIT");

        } catch (Exception e) {
            ExceptionUtil.throwSystemException(e);
        }

    }
    @Authenticated
    @ResourceMapping(LOAD_MODULE)
    public void getModule(ResourceRequest request, ResourceResponse response, @RequestParam("moduleId") int moduleId) throws Exception {
        try {
            //User user = liferayService.getUser(request, response);
            //if (user == null) return;
            SerializerUtil.jsonSerialize(response.getWriter(), moduleService.load(BigInteger.valueOf(moduleId)));
        } catch (Exception e) {
            ExceptionUtil.throwSystemException(e);
        }
    }

    /*
    @Authenticated
    @ResourceMapping(GET_DEPENDENCIES)
    public void getDependencies(ResourceRequest request, ResourceResponse response, @RequestParam("moduleId") int moduleId) throws Exception {
        try {
            //User user = liferayService.getUser(request, response);
            //if (user == null) return;
            Collection<InstallationModule> availableItems = moduleService.loadAll();
            Collection<InstallationModule> selectedItems = moduleService.loadDependees(moduleId);
            SerializerUtil.jsonSerialize(response.getWriter(), new InstallationModuleContainer(availableItems, selectedItems));
        } catch (Exception e) {
            ExceptionUtil.throwSystemException(e);
        }
    }


    @Authenticated
    @ResourceMapping(GET_ALL_DEPENDENCIES)
    public void getAllDependencies(ResourceRequest request, ResourceResponse response) throws Exception {
        try {
            // TODO: aspect for authenitaction
            //User user = liferayService.getUser(request, response);
            //if (user == null) return;
            Collection<InstallationModule> availableItems = moduleService.loadAll();
            SerializerUtil.jsonSerialize(response.getWriter(), new InstallationModuleContainer(availableItems, new ArrayList<InstallationModule>()));
        } catch (Exception e) {
            ExceptionUtil.throwSystemException(e);
        }
    }
    */
    /*
    @ResourceMapping(GET_PARAMETER_KEYS_AND_VALUES)
    public void getParameterKeysAndValues(ResourceRequest request, ResourceResponse response, @RequestParam("moduleId") int moduleId) throws Exception {
        try {
            User user = liferayService.getUser(request, response);
            if (user == null) return;
            Map<String, Collection<String>> keyValuesMap = new LinkedHashMap<String, Collection<String>>();
            Collection<ParameterKey> keys = parameterKeyService.loadAll(moduleId);
            for (ParameterKey key : keys){
                Collection<String> values = parameterValueService.loadValues(key.getId());
                LOG.debug("Key:" + key.getName() + "Value:" + values);
                keyValuesMap.put(key.getName(), values);
            }
            SerializerUtil.jsonSerialize(response.getWriter(), keyValuesMap);
        } catch (Exception e) {
            ExceptionUtil.throwSystemException(e);
        }
    }
    */

    /*
    @ResourceMapping(EDIT_MODULE)
    public void editModule(ResourceRequest request, ResourceResponse response,
                             @RequestParam("module") String moduleData,
                             @RequestParam("dependencies") String dependenciesData,
                             @RequestParam("parameters") String parametersData
    ) {
        try {
            User user = liferayService.getUser(request, response);
            if (user == null) return;

            ObjectMapper mapper = new ObjectMapper();
            InstallationModule module = mapper.readValue(moduleData, InstallationModule.class);
            Collection<Integer> dependenciesList = mapper.readValue(dependenciesData, Collection.class);
            Map<String, Collection<String>> keyValuesMap = mapper.readValue(parametersData, new TypeReference<Map<String, Collection<String>>>(){});

            LOG.debug("InstallationModule:" + module);
            LOG.debug("dependenciesList:" + dependenciesList);
            LOG.debug("Map kv:" + keyValuesMap);

            moduleService.update(module, dependenciesList, keyValuesMap);

        } catch (Exception e) {
            e.printStackTrace();
            response.setProperty(ResourceResponse.HTTP_STATUS_CODE, HttpCodes.HTTP_ERROR_CODE_SERVER_ERROR);
        }
    }
    */

    @Authenticated
    @ResourceMapping(EDIT_MODULE)
    public void editModule(ResourceRequest request, ResourceResponse response,
                            @RequestParam("module") String moduleData,
                            @RequestParam("packages") String packagesData,
                            @RequestParam("parameters") String parametersData){
        try {
            //if (liferayService.getUser(request, response) == null) return;
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

    /*
    @ResourceMapping(CREATE_MODULE)
    public void createModule(ResourceRequest request, ResourceResponse response,
                            @RequestParam("module") String moduleData,
                            @RequestParam("dependencies") String dependenciesData,
                            @RequestParam("parameters") String parametersData
    ) {
        try {
            User user = liferayService.getUser(request, response);
            if (user == null) return;

            ObjectMapper mapper = new ObjectMapper();
            InstallationModule module = mapper.readValue(moduleData, InstallationModule.class);
            Collection<Integer> dependenciesList = mapper.readValue(dependenciesData, Collection.class);
            Map<String, Collection<String>> keyValueMap = mapper.readValue(parametersData, new TypeReference<Map<String, Collection<String>>>(){});

            LOG.debug("InstallationModule:" + module);
            LOG.debug("dependenciesList:" + dependenciesList);
            LOG.debug("Map kv:" + keyValueMap);

            moduleService.create(module, dependenciesList, keyValueMap);

        } catch (Exception e) {
            e.printStackTrace();
            response.setProperty(ResourceResponse.HTTP_STATUS_CODE, HttpCodes.HTTP_ERROR_CODE_SERVER_ERROR);
        }
    }
    */

    @Authenticated
    @ResourceMapping(CREATE_MODULE)
    public void createModule(ResourceRequest request, ResourceResponse response,
                              @RequestParam("module") String moduleData,
                              @RequestParam("dependencies") String dependenciesData){
        try {
            //if (liferayService.getUser(request, response) == null) return;
            ObjectMapper mapper = new ObjectMapper();
            //moduleService.create(mapper.readValue(moduleData, InstallationModule.class), mapper.readValue(dependenciesData, Collection.class));

        } catch (Exception e) {
            e.printStackTrace();
            response.setProperty(ResourceResponse.HTTP_STATUS_CODE, HttpCodes.HTTP_ERROR_CODE_SERVER_ERROR);
        }
    }

    @Authenticated
    @ResourceMapping(DELETE_MODULE)
    public void deleteModule(ResourceRequest request, ResourceResponse response, @RequestParam("id") int moduleId) throws Exception {
        try {
//            if (liferayService.getUser(request, response) == null) return;
           // moduleService.delete(moduleId);
        } catch (Exception e) {
            ExceptionUtil.throwSystemException(e);
        }
    }
}