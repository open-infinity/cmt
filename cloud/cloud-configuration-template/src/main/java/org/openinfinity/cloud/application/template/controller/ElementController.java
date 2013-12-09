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

import com.liferay.portal.model.User;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.openinfinity.cloud.comon.web.LiferayService;
import org.openinfinity.cloud.domain.configurationtemplate.ConfigurationElement;
import org.openinfinity.cloud.domain.configurationtemplate.ParameterKey;
import org.openinfinity.cloud.service.configurationtemplate.ConfigurationElementDependencyService;
import org.openinfinity.cloud.service.configurationtemplate.ConfigurationElementService;
import org.openinfinity.cloud.service.configurationtemplate.ParameterKeyService;
import org.openinfinity.cloud.service.configurationtemplate.ParameterValueService;
import org.openinfinity.cloud.util.collection.ListUtil;
import org.openinfinity.cloud.util.http.HttpCodes;
import org.openinfinity.cloud.util.serialization.JsonDataWrapper;
import org.openinfinity.cloud.util.serialization.SerializerUtil;
import org.openinfinity.core.exception.AbstractCoreException;
import org.openinfinity.core.exception.ApplicationException;
import org.openinfinity.core.exception.BusinessViolationException;
import org.openinfinity.core.exception.SystemException;
import org.openinfinity.core.util.ExceptionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.bind.annotation.ResourceMapping;

import javax.portlet.*;
import java.util.*;

/**
 * Spring portlet controller for handling templates.
 *
 * @author Vedran Bartonicek
 * @version 1.3.0
 * @since 1.3.0
 */
@Controller
@RequestMapping("VIEW")
public class ElementController {

    private static final String GET_ELEMENTS = "getElements";
	private static final String GET_ELEMENT = "getElement";
	private static final String GET_DEPENDENCIES = "getDependencies";
	private static final String GET_ALL_DEPENDENCIES = "getAllAvailableDependencies";
	private static final String GET_PARAMETER_KEYS_AND_VALUES = "getParameterKeysAndValues";
	private static final String EDIT_ELEMENT = "editElement";
    private static final String DELETE_ELEMENT = "deleteElement";
    private static final String CREATE_ELEMENT = "createElement";

    private static final Logger LOG = Logger.getLogger(ElementController.class.getName());

    @Autowired
	private ConfigurationElementService elementService;

    @Autowired
    private ConfigurationElementDependencyService dependencyService;

    @Autowired
    private ParameterKeyService parameterKeyService;

    @Autowired
    private ParameterValueService parameterValueService;

    @Autowired
    private LiferayService liferayService;

	@ExceptionHandler({ApplicationException.class, BusinessViolationException.class,
	                   SystemException.class})
    public ModelAndView handleException(RenderRequest renderRequest, RenderResponse renderResponse,
                                        AbstractCoreException abstractCoreException) {
		ModelAndView modelAndView = new ModelAndView("error");
		if (abstractCoreException.isErrorLevelExceptionMessagesIncluded())
			modelAndView.addObject("errorLevelExceptions",
			                        abstractCoreException.getErrorLevelExceptionIds());
		if (abstractCoreException.isWarningLevelExceptionMessagesIncluded())
			modelAndView.addObject("warningLevelExceptions",
			                        abstractCoreException.getWarningLevelExceptionIds());
		if (abstractCoreException.isInformativeLevelExceptionMessagesIncluded())
			modelAndView.addObject("informativeLevelExceptions",
			                        abstractCoreException.getInformativeLevelExceptionIds());

		// TODO
		@SuppressWarnings("unchecked")
        Map<String, Object> userInfo = (Map<String, Object>)
		                                renderRequest.getAttribute(ActionRequest.USER_INFO);
		if (userInfo == null) return new ModelAndView("home");

		return modelAndView;
    }

    @ResourceMapping(GET_ELEMENTS)
    public void getElements(ResourceRequest request, ResourceResponse response, @RequestParam("page") int page, @RequestParam("rows") int rows) throws Exception {
        try {
            User user = liferayService.getUser(request, response);
            if (user == null) return;
            Collection<ConfigurationElement> templates = elementService.loadAll();
            int records = templates.size();
            int mod = records % rows;
            int totalPages = records / rows;
            if (mod > 0) totalPages++;
            List<ConfigurationElement> onePage = ListUtil.sliceList(page, rows, new LinkedList<ConfigurationElement>(templates));
            SerializerUtil.jsonSerialize(response.getWriter(), new JsonDataWrapper(page, totalPages, records, onePage));
        } catch (Exception e) {
            ExceptionUtil.throwSystemException(e);
        }

    }

    @ResourceMapping(GET_ELEMENT)
    public void getElement(ResourceRequest request, ResourceResponse response, @RequestParam("elementId") int elementId) throws Exception {
        try {
            User user = liferayService.getUser(request, response);
            if (user == null) return;
            SerializerUtil.jsonSerialize(response.getWriter(), elementService.load(elementId));
        } catch (Exception e) {
            ExceptionUtil.throwSystemException(e);
        }
    }

    @ResourceMapping(GET_DEPENDENCIES)
    public void getDependencies(ResourceRequest request, ResourceResponse response, @RequestParam("elementId") int elementId) throws Exception {
        try {
            User user = liferayService.getUser(request, response);
            if (user == null) return;
            Collection<ConfigurationElement> availableItems = elementService.loadAll();
            Collection<ConfigurationElement> selectedItems = elementService.loadDependees(elementId);
            SerializerUtil.jsonSerialize(response.getWriter(), new ConfigurationElementContainer(availableItems, selectedItems));
        } catch (Exception e) {
            ExceptionUtil.throwSystemException(e);
        }
    }

    @ResourceMapping(GET_ALL_DEPENDENCIES)
    public void getAllDependencies(ResourceRequest request, ResourceResponse response) throws Exception {
        try {
            // TODO: aspect for authenitaction
            User user = liferayService.getUser(request, response);
            if (user == null) return;
            Collection<ConfigurationElement> availableItems = elementService.loadAll();
            SerializerUtil.jsonSerialize(response.getWriter(), new ConfigurationElementContainer(availableItems, new ArrayList<ConfigurationElement>()));
        } catch (Exception e) {
            ExceptionUtil.throwSystemException(e);
        }
    }

    @ResourceMapping(GET_PARAMETER_KEYS_AND_VALUES)
    public void getParameterKeysAndValues(ResourceRequest request, ResourceResponse response, @RequestParam("elementId") int elementId) throws Exception {
        try {
            User user = liferayService.getUser(request, response);
            if (user == null) return;
            Map<String, Collection<String>> keyValuesMap = new LinkedHashMap<String, Collection<String>>();
            Collection<ParameterKey> keys = parameterKeyService.loadAll(elementId);
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

    @ResourceMapping(EDIT_ELEMENT)
    public void editElement(ResourceRequest request, ResourceResponse response,
                             @RequestParam("element") String elementData,
                             @RequestParam("dependencies") String dependenciesData,
                             @RequestParam("parameters") String parametersData
    ) {
        try {
            User user = liferayService.getUser(request, response);
            if (user == null) return;

            ObjectMapper mapper = new ObjectMapper();
            ConfigurationElement element = mapper.readValue(elementData, ConfigurationElement.class);
            Collection<Integer> dependenciesList = mapper.readValue(dependenciesData, Collection.class);
            Map<String, Collection<String>> keyValuesMap = mapper.readValue(parametersData, new TypeReference<Map<String, Collection<String>>>(){});

            LOG.debug("ConfigurationElement:" + element);
            LOG.debug("dependenciesList:" + dependenciesList);
            LOG.debug("Map kv:" + keyValuesMap);

            elementService.update(element, dependenciesList, keyValuesMap);

        } catch (Exception e) {
            e.printStackTrace();
            response.setProperty(ResourceResponse.HTTP_STATUS_CODE, HttpCodes.HTTP_ERROR_CODE_SERVER_ERROR);
        }
    }

    @ResourceMapping(CREATE_ELEMENT)
    public void createElement(ResourceRequest request, ResourceResponse response,
                            @RequestParam("element") String elementData,
                            @RequestParam("dependencies") String dependenciesData,
                            @RequestParam("parameters") String parametersData
    ) {
        try {
            User user = liferayService.getUser(request, response);
            if (user == null) return;

            ObjectMapper mapper = new ObjectMapper();
            ConfigurationElement element = mapper.readValue(elementData, ConfigurationElement.class);
            Collection<Integer> dependenciesList = mapper.readValue(dependenciesData, Collection.class);
            Map<String, Collection<String>> keyValueMap = mapper.readValue(parametersData, new TypeReference<Map<String, Collection<String>>>(){});

            LOG.debug("ConfigurationElement:" + element);
            LOG.debug("dependenciesList:" + dependenciesList);
            LOG.debug("Map kv:" + keyValueMap);

            elementService.create(element, dependenciesList, keyValueMap);

        } catch (Exception e) {
            e.printStackTrace();
            response.setProperty(ResourceResponse.HTTP_STATUS_CODE, HttpCodes.HTTP_ERROR_CODE_SERVER_ERROR);
        }
    }

    @ResourceMapping(DELETE_ELEMENT)
    public void deleteElement(ResourceRequest request, ResourceResponse response, @RequestParam("id") int elementId) throws Exception {
        try {
            User user = liferayService.getUser(request, response);
            if (user == null) return;
            elementService.delete(elementId);
        } catch (Exception e) {
            ExceptionUtil.throwSystemException(e);
        }
    }
}