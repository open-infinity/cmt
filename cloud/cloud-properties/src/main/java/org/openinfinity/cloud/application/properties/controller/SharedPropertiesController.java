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
package org.openinfinity.cloud.application.properties.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import org.openinfinity.cloud.application.properties.model.OrganizationTreeModel;
import org.openinfinity.cloud.application.properties.model.SharedPropertyTableData;
import org.openinfinity.cloud.domain.Cluster;
import org.openinfinity.cloud.domain.Instance;
import org.openinfinity.cloud.domain.SharedProperty;
import org.openinfinity.cloud.service.administrator.ClusterService;
import org.openinfinity.cloud.service.administrator.InstanceService;
import org.openinfinity.cloud.service.properties.CentralizedPropertiesService;
import org.openinfinity.cloud.util.collection.ListUtil;
import org.openinfinity.cloud.util.serialization.JsonDataWrapper;
import org.openinfinity.cloud.util.serialization.SerializerUtil;
import org.openinfinity.core.annotation.AuditTrail;
import org.openinfinity.core.annotation.Log;
import org.openinfinity.core.exception.ExceptionLevel;
import org.openinfinity.core.util.ExceptionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.ResourceMapping;

import com.liferay.portal.model.Organization;
import com.liferay.portal.service.OrganizationLocalServiceUtil;

/**
 * The controller of Centralized Properties portlet.
 * 
 * @author Ilkka Leinonen
 */
@Qualifier("sharedPropertiesController")
@Controller
@RequestMapping(value = "VIEW") 
public class SharedPropertiesController {
	
	@Autowired
	private InstanceService instanceService;
	
	@Autowired
	private ClusterService clusterService;
	
	@Autowired
	private CentralizedPropertiesService centralizedPropertiesService;
	
	/**
	 * Load instances URL path.
	 */
	private static final String PATH_FOR_VIEW = "VIEW";
	
	/**
	 * Load clusters URL path.
	 */
	private static final String PATH_FOR_LOAD_CLUSTERS = "loadClusters";
	
	/**
	 * Load instances URL path.
	 */
	private static final String PATH_FOR_LOAD_INSTANCES = "loadInstances";

	
	/**
	 * Rollback of deployment URL path.
	 */
	private static final String PATH_FOR_LOAD_PROPERTIES_TABLE = "loadPropertiesTable";

	/**
	 * Returns the basic view including user's organizations and organization deployments to end-user. 
	 * 
	 * @param renderRequest
	 * @param renderResponse
	 * @param model
	 * @return
	 * @throws Throwable
	 */
	@RequestMapping(PATH_FOR_VIEW)
	public String showView(RenderRequest renderRequest, RenderResponse renderResponse, ModelMap model) {
		@SuppressWarnings("unchecked")
		Map<String, Object> userInfo = (Map<String, Object>) renderRequest.getAttribute(ActionRequest.USER_INFO);
		if (userInfo == null) return "home";
		int userId = Integer.valueOf(userInfo.get("liferay.user.id").toString());
		Map<Long, String> organizationMap = loadOrganizationsAndSortByHierarchy(userId);
		model.addAttribute("sharedPropertyModel", new SharedProperty());
		model.addAttribute("organizationMap", organizationMap);
		return "properties";
	}
	
	private Map<Long, String> loadOrganizationsAndSortByHierarchy(int userId) {
		Map<Long, String> organizationMap = new HashMap<Long, String>(); 
		try {
			List<Organization> organizations = OrganizationLocalServiceUtil.getUserOrganizations(userId);
			List<OrganizationTreeModel> organizationTree = new ArrayList<OrganizationTreeModel>();
			for (Organization org : organizations) {
				organizationMap.put(org.getOrganizationId(), org.getName());
				OrganizationTreeModel organizationTreeModel = new OrganizationTreeModel();
				organizationTreeModel.setId(org.getOrganizationId());
				organizationTreeModel.setName(org.getName());
				organizationTree.add(organizationTreeModel);
			}
			Collections.sort(organizationTree);
		} catch (Throwable e) {
			ExceptionUtil.throwApplicationException("Could not fetch organization information.", ExceptionLevel.ERROR, "exception.error.organization.tree.not.accessible");
		} 
		return organizationMap;
	}
	
	/**
	 * Loads created instances bases on organization id.
	 * 
	 * @param response 
	 * @param organizationId
	 * @throws Exception
	 */
	@Log
	@AuditTrail
	@ResourceMapping(PATH_FOR_LOAD_INSTANCES)
	public void loadInstances(ResourceResponse response, @RequestParam("organizationId") long organizationId) throws Exception {
		Collection<Instance> instances = instanceService.getOrganizationInstances(organizationId);
		Map<Integer, String> instanceMap = new HashMap<Integer, String>();
		for (Instance instance : instances) instanceMap.put(instance.getInstanceId(), instance.getName());
		SerializerUtil.jsonSerialize(response.getWriter(), instanceMap);
	} 

	/**
	 * Load created clusters based on instance id.
	 * 
	 * @param response
	 * @param instanceId
	 * @throws Exception
	 */
	@Log
	@AuditTrail
	@ResourceMapping(PATH_FOR_LOAD_CLUSTERS)
	public void loadClusters(ResourceResponse response, @RequestParam("instanceId") int instanceId) throws Exception {
		Collection<Cluster> clusters = clusterService.getClusters(instanceId);
		Map<Integer, String> clusterMap = new HashMap<Integer, String>();
		for (Cluster cluster : clusters) clusterMap.put(cluster.getId(), cluster.getName());
		SerializerUtil.jsonSerialize(response.getWriter(), clusterMap);
	} 
	
	
	/**
	 * Deploys new application to backbone server.
	 * 
	 * @param sharedProperty  Represents the MVC model including deployment information.
	 * @throws Throwable Thrown when system level exception arises.
	 */
	@Log
	@AuditTrail
	@ActionMapping
    public void store(@ModelAttribute("sharedPropertyModel") SharedProperty sharedProperty) throws Throwable {
		centralizedPropertiesService.store(sharedProperty);
	}
	
	/**
	 * Loads deployment data
	 * 
	 * TODO: If we redesign so that we clear away deployment rows from non-existing clusters, a simple SQL can be used to fetch only
	 * deployments from existing clusters.
	 * 
	 * @param deploymentModel Represents the MVC model including deployment information.
	 * @throws Throwable Thrown when system level exception arises.
	 */
	@Log
	@AuditTrail
	@ResourceMapping(PATH_FOR_LOAD_PROPERTIES_TABLE)
	public void loadDeploymentTable(ResourceRequest request, ResourceResponse response,@RequestParam("page") int page, @RequestParam("rows") int rows) throws Exception {
		Map<String, Object> userInfo = (Map<String, Object>) request.getAttribute(ActionRequest.USER_INFO);
		if (userInfo == null) {
			ExceptionUtil.throwApplicationException("User not authenticated");
		}
		Long userId = (Long)userInfo.get("liferay.user.id");
		List<Organization> organizations = OrganizationLocalServiceUtil.getUserOrganizations(userId);
		Collection<Long> organizationIds = new ArrayList<Long>();
		for (Organization organization : organizations) {
			organizationIds.add(organization.getOrganizationId());
		}
		Collection<SharedProperty> sharedProperties = centralizedPropertiesService.loadSharedPropertiesByOrganizationIds(organizationIds);
		List<SharedPropertyTableData> sharedPropertiesDataList = new ArrayList<SharedPropertyTableData>(); 
		for (SharedProperty sharedProperty : sharedProperties) {
			Instance instance = instanceService.getInstance(sharedProperty.getInstanceId());
			Cluster cluster = clusterService.getCluster(sharedProperty.getClusterId());
			SharedPropertyTableData sharedPropertyTableData = new SharedPropertyTableData(sharedProperty, OrganizationLocalServiceUtil.getOrganization(sharedProperty.getOrganizationId()).getName(), instance.getName(), cluster.getName() );
			sharedPropertiesDataList.add(sharedPropertyTableData);	
		}		
		int records = sharedPropertiesDataList.size();
		int mod = records % rows;
		int totalPages = records/rows;
		if(mod > 0) totalPages++;
		@SuppressWarnings("unchecked")
		List<SharedPropertyTableData> onePage = ListUtil.sliceList(page, rows, sharedPropertiesDataList);

		JsonDataWrapper jdw = new JsonDataWrapper(page, totalPages, records, onePage);
		SerializerUtil.jsonSerialize(response.getWriter(), jdw); 
	} 

}
