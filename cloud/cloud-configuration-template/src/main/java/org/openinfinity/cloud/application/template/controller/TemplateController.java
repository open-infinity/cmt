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

import com.liferay.portal.model.Organization;
import com.liferay.portal.model.User;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.openinfinity.cloud.application.template.serialization.ConfigurationElementContainer;
import org.openinfinity.cloud.application.template.serialization.OrganizationContainer;
import org.openinfinity.cloud.common.web.LiferayService;
import org.openinfinity.cloud.common.annotation.Authenticated;
import org.openinfinity.cloud.domain.configurationtemplate.entity.ConfigurationElement;
import org.openinfinity.cloud.domain.configurationtemplate.entity.ConfigurationTemplate;
import org.openinfinity.cloud.domain.configurationtemplate.relation.TemplateToOrganization;
import org.openinfinity.cloud.service.configurationtemplate.entity.api.ConfigurationElementService;
import org.openinfinity.cloud.service.configurationtemplate.entity.api.ConfigurationTemplateService;
import org.openinfinity.cloud.service.configurationtemplate.relation.api.TemplateToOrganizationService;
import org.openinfinity.cloud.util.http.HttpCodes;
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
import org.springframework.web.portlet.bind.annotation.RenderMapping;
import org.springframework.web.portlet.bind.annotation.ResourceMapping;

import javax.portlet.*;
import java.math.BigInteger;
import java.util.*;

/**
 * Spring portlet controller for handling Configuration Template requests.
 *
 * Handles requests from "Main view, Template tab" and requests from "Template dialog"
 *
 * @author Vedran Bartonicek
 * @version 1.3.0
 * @since 1.3.0
 */

@Controller(value = "templateController")
@RequestMapping("VIEW")
public class TemplateController extends AbstractController{

    private static final String CREATE_TEMPLATE = "createTemplate";
    private static final String GET_TEMPLATE = "getTemplate";
    private static final String GET_ELEMENTS_FOR_TEMPLATE = "getElementsForTemplate";
    private static final String GET_ORGANIZATIONS_FOR_TEMPLATE = "getOrganizationsForTemplate";
    private static final String GET_ALL_ELEMENTS = "getAllAvailableElements";
    private static final String GET_ALL_ORGANIZATIONS = "getAllOrganizations";
    private static final String EDIT_TEMPLATE = "editTemplate";
    private static final String DELETE_TEMPLATE = "deleteTemplate";

    private static final Logger LOG = Logger.getLogger(TemplateController.class.getName());

    @Autowired
    private ConfigurationTemplateService configurationTemplateService;

    @Autowired
    private ConfigurationElementService configurationElementService;

    @Autowired
    private OrganizationContainer organizationContainer;

    @Autowired
    private LiferayService liferayService;

    @Autowired
    private TemplateToOrganizationService organizationService;

    @RenderMapping
    public String showView(RenderRequest request, RenderResponse response) {
        User user = liferayService.getUser(request);
        if (user == null) {
            LOG.debug("User = null");
            response.setProperty(ResourceResponse.HTTP_STATUS_CODE, HttpCodes.HTTP_ERROR_CODE_USER_NOT_LOGGED_IN);
            return "notlogged";
        }
        return "main";
    }

    // TODO -> to some util class? :vbartoni
    @ExceptionHandler({ApplicationException.class, BusinessViolationException.class, SystemException.class})
    public ModelAndView handleException(RenderRequest renderRequest, RenderResponse renderResponse, AbstractCoreException abstractCoreException) {
        ModelAndView modelAndView = new ModelAndView("error");
        if (abstractCoreException.isErrorLevelExceptionMessagesIncluded())
            modelAndView.addObject("errorLevelExceptions", abstractCoreException.getErrorLevelExceptionIds());
        if (abstractCoreException.isWarningLevelExceptionMessagesIncluded())
            modelAndView.addObject("warningLevelExceptions", abstractCoreException.getWarningLevelExceptionIds());
        if (abstractCoreException.isInformativeLevelExceptionMessagesIncluded())
            modelAndView.addObject("informativeLevelExceptions", abstractCoreException.getInformativeLevelExceptionIds());

        @SuppressWarnings("unchecked")
        Map<String, Object> userInfo =
                (Map<String, Object>) renderRequest.getAttribute(ActionRequest.USER_INFO);
        if (userInfo == null)
            return new ModelAndView("error");

        return modelAndView;
    }

    @Authenticated
    @ResourceMapping(GET_TEMPLATE)
    public void getTemplate(ResourceRequest request, ResourceResponse response, @RequestParam("templateId") int templateId) throws Exception {
        try {
            SerializerUtil.jsonSerialize(response.getWriter(), configurationTemplateService.load(BigInteger.valueOf(templateId)));
        } catch (Exception e) {
            ExceptionUtil.throwSystemException(e);
        }
    }

    @Authenticated
    @ResourceMapping(DELETE_TEMPLATE)
    public void deleteTemplate(ResourceRequest request, ResourceResponse response, @RequestParam("id") int templateId) throws Exception {
        try {
            configurationTemplateService.delete(BigInteger.valueOf(templateId));
        } catch (Exception e) {
            ExceptionUtil.throwSystemException(e);
        }
    }

    @Authenticated
    @ResourceMapping(GET_ELEMENTS_FOR_TEMPLATE)
    public void getElementsForTemplate(ResourceRequest request, ResourceResponse response, @RequestParam("templateId") int templateId) throws Exception {
        try {
            SerializerUtil.jsonSerialize(response.getWriter(), new ConfigurationElementContainer(configurationElementService.loadAll(), configurationElementService.loadAllForTemplate(templateId)));

        } catch (Exception e) {
            ExceptionUtil.throwSystemException(e);
        }
    }

    @Authenticated
    @ResourceMapping(GET_ALL_ELEMENTS)
    public void getAllAvailableElements(ResourceRequest request, ResourceResponse response) throws Exception {
        try {
            LOG.debug("ENTER getAllAvailableElements");
            SerializerUtil.jsonSerialize(response.getWriter(), new ConfigurationElementContainer(configurationElementService.loadAll(), new ArrayList<ConfigurationElement>()));
        } catch (Exception e) {
            ExceptionUtil.throwSystemException(e);
        }
    }

    @ResourceMapping(GET_ORGANIZATIONS_FOR_TEMPLATE)
    public void getOrganizationsForTemplate(ResourceRequest request, ResourceResponse response, @RequestParam("templateId") int templateId) throws Exception {
        try {
            User user = liferayService.getUser(request, response);
            if (user == null) return;
            List<Organization> organizations = liferayService.getOrganizations(user);
            Collection<Organization> selectedOrganizations = new LinkedList<Organization>();

            // Note: the code below was needed to avoid duplicates in selectedOrganizations.
            for (TemplateToOrganization cto : organizationService.loadAllForTemplate(templateId)) {
                for (Organization o : organizations) {
                    if (o.getOrganizationId() == cto.getOrganizationId()) {
                        selectedOrganizations.add(o);
                    }
                }
            }
            SerializerUtil.jsonSerialize(response.getWriter(), organizationContainer.construct(organizations, selectedOrganizations));

        } catch (Exception e) {
            ExceptionUtil.throwSystemException(e);
        }
    }

    @ResourceMapping(GET_ALL_ORGANIZATIONS)
    public void getAllOrganizations(ResourceRequest request, ResourceResponse response) throws Exception {
        try {
            User user = liferayService.getUser(request, response);
            if (user == null) return;
            List<Organization> organizations = liferayService.getOrganizations(user);
            SerializerUtil.jsonSerialize(response.getWriter(), organizationContainer.construct(organizations, new ArrayList<Organization>()));
        } catch (Exception e) {
            ExceptionUtil.throwSystemException(e);
        }
    }

    /*
    Note 1: @RequestBody for parameter de-serialization would not work with portlets -vbartoni
    Note 2: @RequestParam("elementsSelected") String[] elementsSelected does not work for some reason.
    That's why the array was sent as json -vbartoni
     */

    @Authenticated
    @ResourceMapping(EDIT_TEMPLATE)
    public void editTemplate(ResourceRequest request, ResourceResponse response,
                             @RequestParam("template") String templateData,
                             @RequestParam("elements") String elementData,
                             @RequestParam("organizations") String organizationData) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            ConfigurationTemplate template = mapper.readValue(templateData, ConfigurationTemplate.class);
            configurationTemplateService.update(new ConfigurationTemplate(template.getId(), template.getName(), template.getDescription()), mapper.readValue(elementData, List.class), mapper.readValue(organizationData, List.class));

        } catch (Exception e) {
            e.printStackTrace();
            response.setProperty(ResourceResponse.HTTP_STATUS_CODE, HttpCodes.HTTP_ERROR_CODE_SERVER_ERROR);
        }
    }
    @Authenticated
    @ResourceMapping(CREATE_TEMPLATE)
    public void createTemplate(ResourceRequest request, ResourceResponse response,
                               @RequestParam("template") String templateData,
                               @RequestParam("elements") String elementData,
                               @RequestParam("organizations") String organizationData) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            ConfigurationTemplate template = mapper.readValue(templateData, ConfigurationTemplate.class);
            configurationTemplateService.create(new ConfigurationTemplate(template.getName(), template.getDescription()), mapper.readValue(elementData, List.class), mapper.readValue(organizationData, List.class));
        } catch (Exception e) {
            e.printStackTrace();
            response.setProperty(ResourceResponse.HTTP_STATUS_CODE, HttpCodes.HTTP_ERROR_CODE_SERVER_ERROR);
        }
    }

}