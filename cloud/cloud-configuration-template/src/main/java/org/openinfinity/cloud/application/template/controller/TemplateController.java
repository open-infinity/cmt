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
import org.openinfinity.cloud.comon.web.LiferayService;
import org.openinfinity.cloud.domain.configurationtemplate.ConfigurationElement;
import org.openinfinity.cloud.domain.configurationtemplate.ConfigurationTemplate;
import org.openinfinity.cloud.domain.configurationtemplate.ConfigurationTemplateOrganization;
import org.openinfinity.cloud.service.configurationtemplate.ConfigurationElementService;
import org.openinfinity.cloud.service.configurationtemplate.ConfigurationTemplateOrganizationService;
import org.openinfinity.cloud.service.configurationtemplate.ConfigurationTemplateService;
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
import org.springframework.web.portlet.bind.annotation.RenderMapping;
import org.springframework.web.portlet.bind.annotation.ResourceMapping;

import javax.portlet.*;
import java.io.IOException;
import java.math.BigInteger;
import java.util.*;

/**
 * Spring portlet controller for handling templates.
 *
 * @author Vedran Bartonicek
 * @version 1.3.0
 * @since 1.3.0
 */

@Controller(value = "templateController")

@RequestMapping("VIEW")
public class TemplateController {

    private static final String PATH_GET_TEMPLATES_FOR_USER = "getTemplatesForUser";
    private static final String PATH_GET_ELEMENTS_FOR_TEMPLATE = "getElementsForTemplate";
    private static final String PATH_GET_ORGANIZATIONS_FOR_TEMPLATE = "getOrganizationsForTemplate";

    private static final String PATH_EDIT_TEMPLATE = "editTemplate";
    private static final String PATH_GET_TEMPLATE = "getTemplate";

    private static final String PATH_CREATE_TEMPLATE = "createTemplate";
    private static final String PATH_DELETE_TEMPLATE = "deleteTemplate";

    private static final Logger LOG = Logger.getLogger(TemplateController.class.getName());

    @Autowired
    private ConfigurationTemplateService configurationTemplateService;

    @Autowired
    private ConfigurationElementService configurationElementService;

    @Autowired
    private ConfigurationElementContainer elementsContainer;

    @Autowired
    private OrganizationContainer organizationContainer;

    @Autowired
    private LiferayService liferayService;

    @Autowired
    private ConfigurationTemplateOrganizationService organizationService;

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

        // TODO, what's this stuff? :vbartoni
        @SuppressWarnings("unchecked")
        Map<String, Object> userInfo =
                (Map<String, Object>) renderRequest.getAttribute(ActionRequest.USER_INFO);
        if (userInfo == null)
            return new ModelAndView("home");

        return modelAndView;
    }

    @ResourceMapping(PATH_GET_TEMPLATES_FOR_USER)
    public void getTemplatesForUser(ResourceRequest request, ResourceResponse response,
                                    @RequestParam("page") int page, @RequestParam("rows") int rows)
            throws Exception {
        try {
            User user = liferayService.getUser(request, response);
            List<Long> organizationIds = liferayService.getOrganizationIds(user);
            List<ConfigurationTemplate> templates = configurationTemplateService.getTemplates(organizationIds);

            int records = templates.size();
            int mod = records % rows;
            int totalPages = records / rows;
            if (mod > 0) totalPages++;
            List<ConfigurationTemplate> onePage = ListUtil.sliceList(page, rows, new LinkedList<ConfigurationTemplate>(templates));

            SerializerUtil.jsonSerialize(response.getWriter(), new JsonDataWrapper(page, totalPages, records, onePage));
        } catch (Exception e) {
            ExceptionUtil.throwSystemException(e);
        }
    }

    @ResourceMapping(PATH_GET_TEMPLATE)
    public void getTemplate(ResourceRequest request, ResourceResponse response, @RequestParam("templateId") int templateId) throws Exception {
        try {
            SerializerUtil.jsonSerialize(response.getWriter(), configurationTemplateService.load(BigInteger.valueOf(templateId)));
        } catch (Exception e) {
            ExceptionUtil.throwSystemException(e);
        }
    }

    @ResourceMapping(PATH_DELETE_TEMPLATE)
    public void deleteTemplate(ResourceRequest request, ResourceResponse response, @RequestParam("templateId") int templateId) throws Exception {
        try {
            configurationTemplateService.delete(templateId);
        } catch (Exception e) {
            ExceptionUtil.throwSystemException(e);
        }
    }

    @ResourceMapping(PATH_GET_ELEMENTS_FOR_TEMPLATE)
    public void getElementIdsForTemplate(ResourceRequest request, ResourceResponse response, @RequestParam("templateId") int templateId) throws Exception {
        try {
            Collection<ConfigurationElement> allElements = configurationElementService.loadAll();
            elementsContainer.setAvailable(allElements);
            Collection<ConfigurationElement> elementsForTemplate = configurationElementService.loadAllForTemplate(templateId);
            elementsContainer.setSelected(elementsForTemplate);
            SerializerUtil.jsonSerialize(response.getWriter(), elementsContainer);

        } catch (Exception e) {
            ExceptionUtil.throwSystemException(e);
        }
    }

    @ResourceMapping(PATH_GET_ORGANIZATIONS_FOR_TEMPLATE)
    public void getOrganizationsForTemplateAndUser(ResourceRequest request, ResourceResponse response, @RequestParam("templateId") int templateId) throws Exception {
        try {
            User user = liferayService.getUser(request, response);
            List<Organization> organizations = liferayService.getOrganizations(user);
            Collection<Organization> selectedOrganizations = new LinkedList<Organization>();

            for (ConfigurationTemplateOrganization cto : organizationService.loadAllForTemplate(templateId)) {
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

    /*
    Note 1: @RequestBody for parameter de-serialization would not work with portlets -vbartoni
    Note 2: @RequestParam("elementsSelected") String[] elementsSelected does not work for some reason.
    That's why the array was sent as json -vbartoni
     */
    @ResourceMapping(PATH_EDIT_TEMPLATE)
    public void editTemplate(ResourceRequest request, ResourceResponse response,
                             @RequestParam("templateId") int templateId,
                             @RequestParam("templateName") String templateName,
                             @RequestParam("templateDescription") String templateDescription,
                             @RequestParam("elementsSelected") String elementsSelected,
                             @RequestParam("organizationsSelected") String organizationsSelected
    ) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            configurationTemplateService.update(new ConfigurationTemplate(templateId, templateName, templateDescription), mapper.readValue(elementsSelected, List.class), mapper.readValue(organizationsSelected, List.class));

        } catch (Exception e) {
            e.printStackTrace();
            response.setProperty(ResourceResponse.HTTP_STATUS_CODE, HttpCodes.HTTP_ERROR_CODE_SERVER_ERROR);
        }
    }

    // Slices a subset from list, the result fits into one jqGgrid page.
    private <T> void sliceAndSerialize(ResourceResponse response, List<T> items, int page, int rows) throws IOException {
        int records = items.size();
        int mod = records % rows;
        int totalPages = records / rows;
        if (mod > 0) totalPages++;
        List<T> itemsCopy = new LinkedList<T>();
        itemsCopy.addAll(items);
        List<T> onePage = ListUtil.sliceList(page, rows, itemsCopy);
        SerializerUtil.jsonSerialize(response.getWriter(), new JsonDataWrapper(page, totalPages, records, onePage));
    }

}