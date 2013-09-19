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

import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import javax.portlet.ActionRequest;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.bind.annotation.RenderMapping;
import org.springframework.web.portlet.bind.annotation.ResourceMapping;
import org.apache.log4j.Logger;

import com.liferay.portal.model.User;

import org.openinfinity.cloud.domain.Instance;
import org.openinfinity.cloud.domain.configurationtemplate.Template;
import org.openinfinity.cloud.service.configurationtemplate.TemplateService;
import org.openinfinity.cloud.service.liferay.LiferayService;
import org.openinfinity.cloud.util.collection.ListUtil;
import org.openinfinity.cloud.util.http.HttpCodes;
import org.openinfinity.cloud.util.serialization.JsonDataWrapper;
import org.openinfinity.cloud.util.serialization.SerializerUtil;
import org.openinfinity.core.exception.AbstractCoreException;
import org.openinfinity.core.exception.ApplicationException;
import org.openinfinity.core.exception.BusinessViolationException;
import org.openinfinity.core.exception.SystemException;
import org.openinfinity.core.util.ExceptionUtil;

/**
 * Spring portlet controller for handling templates.
 * 
 * @author Vedran Bartonicek
 * @version 1.3.0
 * @since 1.3.0
 */

@Controller(value="configurationTemplateController")

@RequestMapping("VIEW")
public class TemplateController {
	
	private static final String PATH_GET_TEMPLATES_FOR_USER = "getTemplatesForUser";
    private static final String PATH_GET_TEMPLATES_FOR_ORGANIZATION = "getTemplatesForOrganization";
    private static final String PATH_EDIT_TEMPLATE = "editTemplate";
    private static final String PATH_ADD_TEMPLATE = "addTemplate";
    private static final String PATH_DELETE_TEMPLATE = "deleteTemplate";
    private static final String PATH_USE_TEMPLATE = "useTemplate";

    private static final Logger LOG = Logger.getLogger(TemplateController.class.getName());

	@Autowired
	@Qualifier("configurationTemplateService")
	private TemplateService templateService;
	
	@Autowired
	LiferayService liferayService;
	
	@RenderMapping
    public String showView(RenderRequest request, RenderResponse response) {
        User user = liferayService.getUser(request);
        if(user == null) {
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
        try{
            User user = liferayService.getUser(request, response);           
            List<Long> organizationIds = liferayService.getOrganizationIds(user);     
            LOG.debug("organizationIds");
            LOG.debug(organizationIds);
            Set<Template> templates = templateService.getTemplates(organizationIds);   
            LOG.debug("templates received");
            Assert.notNull(templates);
            LOG.debug("Jsonization start");

            int records = templates.size();
            int mod = records % rows;
            int totalPages = records/rows;
            if(mod > 0) totalPages++;

            // Slice a subset from all deployments, the result fits into one jqGgrid page
            List<Template> temp = new LinkedList<Template>();
            temp.addAll(templates);
            List<Template> onePage = ListUtil.sliceList(page, rows, temp);
            
            SerializerUtil.jsonSerialize(response.getWriter(), 
                                         new JsonDataWrapper(page, totalPages, records, onePage));
            LOG.debug("Jsonization end");

        } catch (Exception e){
            ExceptionUtil.throwSystemException(e);  
        }
    } 
    
}