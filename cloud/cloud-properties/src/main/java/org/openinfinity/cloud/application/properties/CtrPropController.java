/*
 * Copyright (c) 2012 the original author or authors.
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
package org.openinfinity.cloud.application.properties;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.portlet.ActionRequest;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.portlet.bind.annotation.RenderMapping;
import org.springframework.web.portlet.bind.annotation.ResourceMapping;

import org.apache.log4j.Logger;
import org.openinfinity.cloud.domain.SharedProperty;
import org.openinfinity.cloud.service.properties.CentralizedPropertiesService;

import com.liferay.portal.service.OrganizationLocalServiceUtil;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portal.model.Organization;
import com.liferay.portal.model.User;

/**
 * The controller of Centralized Properties portlet.
 * 
 * @author Timo Saarinen
 */
@Controller(value="CtrPropController")
@RequestMapping(value = "VIEW") 
public class CtrPropController {

	private static final Logger logger = Logger.getLogger(CtrPropController.class.getName());
	
	@Autowired
	@Qualifier("centralizedPropertiesService")
	private CentralizedPropertiesService service;
	
	@RenderMapping
	public String showView(RenderRequest renderRequest, RenderResponse renderResponse, Model model) throws Throwable {
		try {
			
			/* FIXME
			List<SharedProperty> props = new LinkedList<SharedProperty>();
			
			props.addAll(service.loadAll());

			// Ossi:
			// Eli tuo getOrganizations() antaa vaan ne organisaatiot jotka käyttäjälle 
			// on laitettu, tolla suborganizations jutulla saa myös sitten kaikki 
			// niitten alla olevat organisaatiot. Ne tarvitaan koska meillähän oli 
			// se tilanne että pääorganisaatiotasolla oleva käyttäjä näkee kaikki 
			// alapuolella olevat organisaatiot ja niitten instanssit.
			//
			// Nuo listat kun yhistää ja sitten vertaa sitä instanssi taulun 
			// organisaatiokenttiin ni saa ne mitä saa näyttää. CloudAdmin puolella 
			// kannattanee käyttää noita valmiita service layereita (löytyy sieltä 
			// modulaarisesta buildilta) siellä on valmiit funkkarit ja domain oliot 
			// noille.
			User user = PortalUtil.getUser(renderRequest);
			List<Organization> organizationList    = user.getOrganizations();
			List<Organization> subOrganizationList = OrganizationLocalServiceUtil.getSuborganizations(organizationList);

			// Merge and sort lists
			SortedSet<Organization> combined = new TreeSet<Organization>();
			combined.addAll(organizationList);
			combined.addAll(subOrganizationList);
			
			// Add attributes to the model
			model.addAttribute("props", props);
			model.addAttribute("user", user);
			model.addAttribute("organizations", combined);
		*/
			return "list";
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Failed to fetch properties from database.", e);
			return "db-error";
		}
	}
	
	@ResourceMapping("changeOrganization")
	public void changeOrganization(ResourceRequest request, ResourceResponse response, 
			@RequestParam("organizationId") String organizationId) throws Exception 
	{
		logger.info("changeOrganization: organizationId=" + organizationId);
		
		response.setContentType("text/plain");
		PrintWriter pw = new PrintWriter(response.getWriter());
		pw.print("");
		pw.flush();
	}

	@ResourceMapping("changePropertyKey")
	public void changeKey(ResourceRequest request, ResourceResponse response, 
			@RequestParam("id") String id, 
			@RequestParam("value") String newkey, 
			@RequestParam("oldvalue") String oldkey) throws Exception 
	{
		logger.info("changeKey: id=" + id + " key=" + newkey + " oldkey=" + oldkey);

		/* FIXME
		if ((newkey != null && !newkey.equals(oldkey)) && service.loadByKey(newkey) == null) {
			// Create a new object if key is given
			if ("".equals(oldkey)) {
				SharedProperty p = new SharedProperty(newkey, "-");
				service.store(p);
			}
	
			// Handle content
			response.setContentType("text/html");
			PrintWriter pw = new PrintWriter(response.getWriter());
			pw.print(newkey);
			pw.flush();
			service.rename(oldkey, newkey);
			
			// Delete the database object if the key is cleared
			if ("".equals(newkey)) {
				service.deleteByKey(oldkey);
			}
		} else {
			// Non-unique keys are not allowed
			response.setContentType("text/html");
			PrintWriter pw = new PrintWriter(response.getWriter());
			pw.print(oldkey);
			pw.flush();
		}
		*/
	}

	@ResourceMapping("savePropertyValue")
	public void saveProperty(ResourceRequest request, ResourceResponse response, 
			@RequestParam("id") String id, 
			@RequestParam("key") String key, 
			@RequestParam("value") String value) throws Exception 
	{
		logger.info("saveProperty: id=" + id + " key=" + key + " value=" + value);
		/* FIXME
		if (!("".equals(key))) {
			response.setContentType("text/html");
			PrintWriter pw = new PrintWriter(response.getWriter());
			pw.print(value);
			pw.flush();

			// Save value to db
			SharedProperty p = new SharedProperty(key, value);
			service.store(p);
		}
		*/
	}

	@ResourceMapping("deleteProperty")
	public void deleteProperty(ResourceRequest request, ResourceResponse response, 
			@RequestParam("id") String id, 
			@RequestParam("key") String key) throws Exception 
	{
		logger.info("deleteProperty: id=" + id + " key=" + key);
		/* FIXME
		response.setContentType("text/html");
		PrintWriter pw = new PrintWriter(response.getWriter());
		pw.print(key);
		pw.flush();
		service.deleteByKey(key);
		*/
	}

}