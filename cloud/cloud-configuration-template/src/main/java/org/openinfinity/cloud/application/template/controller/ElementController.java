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
import org.openinfinity.cloud.application.template.serialization.CollectionsContainer;
import org.openinfinity.cloud.application.template.serialization.ConfigurationElementContainer;
import org.openinfinity.cloud.common.annotation.Authenticated;
import org.openinfinity.cloud.domain.configurationtemplate.entity.ConfigurationElement;
import org.openinfinity.cloud.domain.configurationtemplate.entity.InstallationModule;
import org.openinfinity.cloud.service.configurationtemplate.entity.api.ConfigurationElementService;
import org.openinfinity.cloud.service.configurationtemplate.entity.api.InstallationModuleService;
import org.openinfinity.cloud.service.configurationtemplate.entity.api.ParameterKeyService;
import org.openinfinity.cloud.service.configurationtemplate.entity.api.ParameterValueService;
import org.openinfinity.cloud.service.configurationtemplate.relation.api.ElementToElementService;
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

/**
 * Spring portlet controller for handling Configuration Element requests.
 *
 * Handles requests from "Main view, Element tab" and requests from "Element dialog"
 *
 * @author Vedran Bartonicek
 * @version 1.3.0
 * @since 1.3.0
 */
@Controller
@RequestMapping("VIEW")
public class ElementController extends AbstractController{

    private static final String CREATE_ELEMENT = "createElement";private static final String GET_ELEMENT = "getElement";
	private static final String GET_DEPENDENCIES_FOR_ELEMENT = "getDependencies";
	private static final String GET_ALL_DEPENDENCIES = "getAllDependencies";
    private static final String GET_ALL_MODULES = "getAllModules";
    private static final String GET_MODULES_FOR_ELEMENT = "getModulesForElement";
    private static final String EDIT_ELEMENT = "editElement";
    private static final String DELETE_ELEMENT = "deleteElement";

    private static final Logger LOG = Logger.getLogger(ElementController.class.getName());

    @Autowired
	private ConfigurationElementService elementService;

    @Autowired
    private ElementToElementService dependencyService;

    @Autowired
    InstallationModuleService moduleService;

    @Autowired
    private ParameterKeyService parameterKeyService;

    @Autowired
    private ParameterValueService parameterValueService;

    @Authenticated
    @ResourceMapping(GET_ELEMENT)
    public void getElement(ResourceRequest request, ResourceResponse response, @RequestParam("elementId") int elementId) throws Exception {
        try {
            SerializerUtil.jsonSerialize(response.getWriter(), elementService.load(BigInteger.valueOf(elementId)));
        }
        catch (Exception e) {
            ExceptionUtil.throwSystemException(e);
        }
    }

    /*
     * Get dependencies for element
     */
    @Authenticated
    @ResourceMapping(GET_DEPENDENCIES_FOR_ELEMENT)
    public void getDependencies(ResourceRequest request, ResourceResponse response, @RequestParam("elementId") int elementId) throws Exception {
        try {
            Collection<ConfigurationElement> availableItems = elementService.loadAll();
            Collection<ConfigurationElement> selectedItems = elementService.loadDependees(elementId);
            SerializerUtil.jsonSerialize(response.getWriter(), new ConfigurationElementContainer(availableItems, selectedItems));
        }
        catch (Exception e) {
            ExceptionUtil.throwSystemException(e);
        }
    }

    /*
    * Get all elements
    */
    @Authenticated
    @ResourceMapping(GET_ALL_DEPENDENCIES)
    public void getAllDependencies(ResourceRequest request, ResourceResponse response) throws Exception {
        try {
            Collection<ConfigurationElement> availableItems = elementService.loadAll();
            SerializerUtil.jsonSerialize(response.getWriter(), new ConfigurationElementContainer(availableItems, new ArrayList<ConfigurationElement>()));
        }
        catch (Exception e) {
            ExceptionUtil.throwSystemException(e);
        }
    }

    @Authenticated
    @ResourceMapping(GET_ALL_MODULES)
    public void getAllModules(ResourceRequest request, ResourceResponse response) throws Exception {
        try {
            Collection<InstallationModule> availableItems = moduleService.loadAll();
            SerializerUtil.jsonSerialize(response.getWriter(), new CollectionsContainer<InstallationModule>(availableItems, new ArrayList<InstallationModule>()));
        }
        catch (Exception e) {
            ExceptionUtil.throwSystemException(e);
        }
    }

    @Authenticated
    @ResourceMapping(GET_MODULES_FOR_ELEMENT)
    public void getModules(ResourceRequest request, ResourceResponse response, @RequestParam("elementId") int elementId) throws Exception {
        try {
            Collection<InstallationModule> availableItems = moduleService.loadAll();
            Collection<InstallationModule> selectedItems = moduleService.loadModules(elementId);
            SerializerUtil.jsonSerialize(response.getWriter(), new CollectionsContainer<InstallationModule>(availableItems, selectedItems));
        }
        catch (Exception e) {
            ExceptionUtil.throwSystemException(e);
        }
    }

    @Authenticated
    @ResourceMapping(EDIT_ELEMENT)
    public void editElement(ResourceRequest request, ResourceResponse response,
                            @RequestParam("element") String elementData,
                            @RequestParam("dependees") String dependeesData,
                            @RequestParam("modules") String modulesData)
    {
        try {
            ObjectMapper mapper = new ObjectMapper();
            ConfigurationElement element = mapper.readValue(elementData, ConfigurationElement.class);
            Collection<Integer> dependeees = mapper.readValue(dependeesData, Collection.class);
            Collection<Integer> modules = mapper.readValue(modulesData, Collection.class);
            elementService.update(element, dependeees, modules);
        }
        catch (Exception e) {
            ExceptionUtil.throwSystemException(e);
        }
    }

    @Authenticated
    @ResourceMapping(CREATE_ELEMENT)
    public void createElement(ResourceRequest request, ResourceResponse response,
                              @RequestParam("element") String elementData,
                              @RequestParam("dependees") String dependeesData,
                              @RequestParam("modules") String modulesData){
        try {
            ObjectMapper mapper = new ObjectMapper();
            ConfigurationElement element = mapper.readValue(elementData, ConfigurationElement.class);
            Collection<Integer> dependeees = mapper.readValue(dependeesData, Collection.class);
            Collection<Integer> modules = mapper.readValue(modulesData, Collection.class);

            elementService.create(element, dependeees, modules);

        }
        catch (Exception e) {
            ExceptionUtil.throwSystemException(e);
        }
    }

    @Authenticated
    @ResourceMapping(DELETE_ELEMENT)
    public void deleteElement(ResourceRequest request, ResourceResponse response, @RequestParam("id") int elementId) throws Exception {
        try {
            elementService.delete(BigInteger.valueOf(elementId));
        }
        catch (Exception e) {
            ExceptionUtil.throwSystemException(e);
        }
    }
}