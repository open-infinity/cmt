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
package org.openinfinity.cloud.application.connectionpool;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.LinkedList;
import java.util.Map;

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
import org.openinfinity.cloud.domain.TomcatJdbcPoolSettings;
import org.openinfinity.cloud.service.connectionpool.ConnectionPoolService;

/**
 * The controller of Connection Pool Manager portlet.
 * 
 * @author Timo Saarinen
 */
@Controller(value="connectionPoolManagerController")
@Qualifier("connectionPoolManagerController")
@RequestMapping(value = "VIEW")
public class ConnectionPoolController {

	private static final Logger logger = Logger.getLogger(ConnectionPoolController.class.getName());
	private static TomcatJdbcPoolSettings settings;
	
	@Autowired
	@Qualifier("connectionPoolManagerManagerService")
	private ConnectionPoolService service;

	synchronized TomcatJdbcPoolSettings settingsInstance() {
		if (settings == null) {
			settings = service.load();
		}
		return settings;
	}
	
	@RequestMapping("VIEW")
	public String showView(RenderRequest renderRequest, RenderResponse renderResponse, Model model) throws Throwable {
		model.addAttribute("settings", settingsInstance());
		return "settings";
	}

	@ResourceMapping("savePropertyValue")
	public void saveProperty(ResourceRequest request, ResourceResponse response, 
			@RequestParam("id") String id, 
			@RequestParam("value") String value) throws Exception 
	{
		try {
			logger.info("saveProperty: id=" + id + " value=" + value);
			
			// Save value to db
			TomcatJdbcPoolSettings p = settingsInstance();
			if ("connpool_defaultAutoCommit".equals(id)) {
				p.setDefaultAutoCommit(new Boolean(value));
			} else if ("connpool_defaultReadOnly".equals(id)) {
				p.setDefaultReadOnly(new Boolean(value));
			} else if ("connpool_defaultTransactionIsolation".equals(id)) {
				p.setDefaultTransactionIsolation(value);
			} else if ("connpool_defaultCatalog".equals(id)) {
				p.setDefaultCatalog(empty2null(value));
			} else if ("connpool_driverClassName".equals(id)) {
				p.setDriverClassName(empty2null(value));
			} else if ("connpool_username".equals(id)) {
				p.setUsername(empty2null(value));
			} else if ("connpool_password".equals(id)) {
				p.setPassword(empty2null(value));
			} else if ("connpool_maxActive".equals(id)) {
				p.setMaxActive(new Integer(value));
			} else if ("connpool_maxIdle".equals(id)) {
				p.setMaxIdle(new Integer(value));
			} else if ("connpool_minIdle".equals(id)) {
				p.setMinIdle(new Integer(value));
			} else if ("connpool_initialSize".equals(id)) {
				p.setInitialSize(new Integer(value));
			} else if ("connpool_maxWait".equals(id)) {
				p.setMaxWait(new Integer(value));
			} else if ("connpool_testOnBorrow".equals(id)) {
				p.setTestOnBorrow(new Boolean(value));
			} else if ("connpool_testOnRun".equals(id)) {
				p.setTestOnRun(new Boolean(value));
			} else if ("connpool_testWhileIdle".equals(id)) {
				p.setTestWhileIdle(new Boolean(value));
			} else if ("connpool_validationQuery".equals(id)) {
				p.setValidationQuery(value);
			} else if ("connpool_validatorClassName".equals(id)) {
				p.setValidatorClassName(value);
			} else if ("connpool_timeBetweenEvictionRunsMillis".equals(id)) {
				p.setTimeBetweenEvictionRunsMillis(new Integer(value));
			} else if ("connpool_numTestsPerEvictionRun".equals(id)) {
				p.setNumTestsPerEvictionRun(new Integer(value));
			} else if ("connpool_minEvictableIdleTimeMillis".equals(id)) {
				p.setMinEvictableIdleTimeMillis(new Integer(value));
			} else if ("connpool_accessToUnderlyingConnectionAllowed".equals(id)) {
				p.setAccessToUnderlyingConnectionAllowed(new Boolean(value));
			} else if ("connpool_removeAbandoned".equals(id)) {
				p.setRemoveAbandoned(new Boolean(value));
			} else if ("connpool_removeAbandonedTimeout".equals(id)) {
				p.setRemoveAbandonedTimeout(new Integer(value));
			} else if ("connpool_logAbandoned".equals(id)) {
				p.setLogAbandoned(new Boolean(value));
			} else if ("connpool_connectionProperties".equals(id)) {
				p.setConnectionProperties(empty2null(value));
			} else if ("connpool_poolPreparedStatements".equals(id)) {
				p.setPoolPreparedStatements(new Boolean(value));
			} else if ("connpool_maxOpenPreparedStatements".equals(id)) {
				p.setMaxOpenPreparedStatements(new Integer(value));
			} else if ("connpool_validationInterval".equals(id)) {
				p.setValidationInterval(new Long(value));
			} else if ("connpool_jmxEnabled".equals(id)) {
				p.setJmxEnabled(new Boolean(value));
			} else if ("connpool_fairQueue".equals(id)) {
				p.setFairQueue(new Boolean(value));
			} else if ("connpool_abandonWhenPercentageFull".equals(id)) {
				p.setAbandonWhenPercentageFull(new Integer(value));
			} else if ("connpool_maxAge".equals(id)) {
				p.setMaxAge(new Long(value));
			} else if ("connpool_useEquals".equals(id)) {
				p.setUseEquals(new Boolean(value));
			} else if ("connpool_suspectTimeout".equals(id)) {
				p.setSuspectTimeout(new Integer(value));
			} else if ("connpool_alternateUsernameAllowed".equals(id)) {
				p.setAlternateUsernameAllowed(new Boolean(value));
			}
			service.store(p);
			
			response.setContentType("text/html");
			PrintWriter pw = new PrintWriter(response.getWriter());
			pw.print(value);
			pw.flush();
	
		} catch (NumberFormatException e) {
			logger.error("Number format error: " + e.getMessage());
		}
	}
	
	/**
	 * Converts empty strings to null.
	 */
	String empty2null(String s) {
		if (s != null) {
			return s;
		} else {
			return null;
		}
	}
}
