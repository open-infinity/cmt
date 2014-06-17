/*
 * Copyright (c) 2011 the original author or authors.
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

package org.openinfinity.cloud.application.admin.controller;

import com.liferay.portal.model.User;
import org.apache.log4j.Logger;
import org.openinfinity.cloud.common.web.LiferayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

/**
 * Controller for edit requests
 * @author Juha-Matti Sironen
 * @author Ossi Hämäläinen
 * @author Vedran Bartonicek
 * @version 1.0.0 Initial version
 * @since 1.0.0
 */

@Controller(value="cloudadminConfigController")
@RequestMapping(value = "EDIT")
public class CloudAdminConfigController {
	
	private static final Logger LOG = Logger.getLogger(CloudAdminConfigController.class.getName());

    @Autowired
    @Qualifier("liferayService")
    private LiferayService liferayService;

    @RenderMapping
	public String showView(RenderRequest request, RenderResponse response) {
		User user = liferayService.getUser(request);
		if(user == null) {
			return "notlogged";
		}
		return "edit";
	}
	
	protected ModelAndView handleRenderRequestInternal(RenderRequest request, RenderResponse response) throws Exception {
		LOG.debug("CloudAdminConfigController.handleRenderRequestInternal()");
		ModelAndView modelAndView = new ModelAndView("Edit");
		LOG.debug("CloudAdminConfigController.handleRenderRequestInternal() " + modelAndView);
		return modelAndView;
	}
	
	
}
