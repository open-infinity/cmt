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

import org.openinfinity.cloud.comon.web.LiferayService;
import org.openinfinity.cloud.service.configurationtemplate.ConfigurationElementService;
import org.openinfinity.core.exception.AbstractCoreException;
import org.openinfinity.core.exception.ApplicationException;
import org.openinfinity.core.exception.BusinessViolationException;
import org.openinfinity.core.exception.SystemException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.bind.annotation.ResourceMapping;

import javax.portlet.*;
import java.util.Map;

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
	
	private static final String GET_ALL_ELEMENTS = "getAllElements";
	
	@Autowired
	private ConfigurationElementService configurationElementService;

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

    @ResourceMapping(GET_ALL_ELEMENTS)
    public void getAllElementIds(ResourceRequest request, ResourceResponse response) throws Exception {
        /*try {
            User user = liferayService.getUser(request, response);
            if (user == null) return;

            SerializerUtil.jsonSerialize(response.getWriter(), new ConfigurationElementContainer(configurationElementService.loadAll(), null));
        } catch (Exception e) {
            ExceptionUtil.throwSystemException(e);
        }
        */
    }
		
}