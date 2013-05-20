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
package org.openinfinity.cloud.application.deployer.controller;

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

import org.openinfinity.cloud.application.deployer.model.DeploymentModel;
import org.openinfinity.cloud.application.deployer.model.DeploymentTableData;
import org.openinfinity.cloud.application.deployer.model.OrganizationTreeModel;
import org.openinfinity.cloud.domain.Cluster;
import org.openinfinity.cloud.domain.Deployment;
import org.openinfinity.cloud.domain.Instance;
import org.openinfinity.cloud.service.administrator.ClusterService;
import org.openinfinity.cloud.service.administrator.InstanceService;
import org.openinfinity.cloud.service.deployer.DeployerService;
import org.openinfinity.cloud.util.collection.ListUtil;
import org.openinfinity.cloud.util.serialization.JsonDataWrapper;
import org.openinfinity.cloud.util.serialization.SerializerUtil;
import org.openinfinity.core.annotation.AuditTrail;
import org.openinfinity.core.annotation.Log;
import org.openinfinity.core.exception.AbstractCoreException;
import org.openinfinity.core.exception.ApplicationException;
import org.openinfinity.core.exception.BusinessViolationException;
import org.openinfinity.core.exception.ExceptionLevel;
import org.openinfinity.core.exception.SystemException;
import org.openinfinity.core.util.ExceptionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.ResourceMapping;

import com.liferay.portal.model.Organization;
import com.liferay.portal.service.OrganizationLocalServiceUtil;

/**
 * Portlet controller for handling deployments.
 * 
 * @author Ilkka Leinonen
 * @author Vedran Bartonicek
 * @version 1.0.0
 * @since 1.0.0
 */
@Qualifier("tclouddeployer")
@Controller(value="tclouddeployerController")
@RequestMapping("VIEW")
public class DeployerController {
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
	private static final String PATH_FOR_DEPLOYMENT_ROLLBACK = "deploymentRollback";
	
	/**
	 * Rollback of deployment URL path.
	 */
	private static final String PATH_FOR_LOAD_DEPLYMENT_TABLE = "loadDeploymentTable";

	/**
	 * Undeployment URL path.
	 */
	private static final String PATH_FOR_UNDEPLOY = "undeployDeployment";
	
	/**
	 * Delete deployment URL path.
	 */
	private static final String PATH_FOR_DEPLOYMENT_DELETE = "deleteDeployment";
	
	/**
	 * Autowired field for <code>org.openinfinity.core.deployer.service.DeployerService</code>.
	 */
	@Autowired
	private DeployerService deployerService;
	
	@Autowired
	private InstanceService instanceService;
	
	@Autowired
	private ClusterService clusterService;

	@ExceptionHandler({ApplicationException.class, BusinessViolationException.class, SystemException.class})
    public ModelAndView handleException (RenderRequest renderRequest, RenderResponse renderResponse, AbstractCoreException abstractCoreException) {
		ModelAndView modelAndView = new ModelAndView("error");
		if (abstractCoreException.isErrorLevelExceptionMessagesIncluded()) 
			modelAndView.addObject("errorLevelExceptions", abstractCoreException.getErrorLevelExceptionIds());
		if (abstractCoreException.isWarningLevelExceptionMessagesIncluded()) 
			modelAndView.addObject("warningLevelExceptions", abstractCoreException.getWarningLevelExceptionIds());
		if (abstractCoreException.isInformativeLevelExceptionMessagesIncluded()) 
			modelAndView.addObject("informativeLevelExceptions", abstractCoreException.getInformativeLevelExceptionIds());
		
		Map<String, Object> userInfo = (Map<String, Object>) renderRequest.getAttribute(ActionRequest.USER_INFO);
		if (userInfo == null) return new ModelAndView("home");

		return modelAndView;
    }
	
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
		Collection<Deployment> deployments = new ArrayList<Deployment>();
		int userId = Integer.valueOf(userInfo.get("liferay.user.id").toString());
		Map<Long, String> organizationMap = loadOrganizationsAndSortByHierarchy(userId);
		for (Map.Entry<Long, String> entry : organizationMap.entrySet()) {
			deployments.addAll(deployerService.loadDeploymentsForOrganization(entry.getKey()));
		}
		model
			.addAttribute("deploymentModel", new DeploymentModel())
			.addAttribute("organizationMap", organizationMap)
			.addAttribute("deployments", deployments);
		return "deployer";
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
	 * Makes rollback to earlier version.
	 * 
	 * @param response
	 * @param deploymentId
	 * @throws Exception
	 */
	@Log
	@AuditTrail
	@ResourceMapping(PATH_FOR_DEPLOYMENT_ROLLBACK)
	public void rollbackDeployment(ResourceResponse response, @RequestParam("deploymentId") long deploymentId) throws Exception {
		//System.out.println("rollbacking deployment: " + deploymentId);
		//Collection<Cluster> clusters = deployerService.rollback(deployment).getClusters()(deploymentId);
		//Map<Integer, String> clusterMap = new HashMap<Integer, String>();
		//for (Cluster cluster : clusters) clusterMap.put(cluster.getId(), cluster.getName());
		//SerializerUtil.jsonSerialize(response.getWriter(), clusterMap);
	}
	
	/**
	 * Undeploys deployment 
	 * 
	 * TODO: 
	 * 
	 * @param deploymentModel Represents the MVC model including deployment information.
	 * @throws Throwable Thrown when system level exception arises.
	 */
	@Log
	@AuditTrail
	@ResourceMapping(PATH_FOR_UNDEPLOY)
	public void undeployDeploymentFromCluster(ResourceResponse response, @RequestParam("deploymentId") int deploymentId,
			@RequestParam("deploymentName") String deploymentName) throws Exception {
		System.out.println("UNDEPLOYING deployment. Deployment name <" + deploymentName+">, deploymentId=<"+deploymentId+">.");
		
		// verify current state
		Deployment deployment = deployerService.loadDeploymentById(deploymentId);
		//Deployment deployment = deployerService.loadDeploymentsForOrganization(organizationId);
		if (deployment.getState()==DeployerService.DEPLOYMENT_STATE_DEPLOYED) {
			// deploymenstatuses processed uin reader
			// deployment state should be updated to undeployed when all the deploymentstatuses are processed
			// 		-done in reader
			// update deployment state to be undeployed
			deployment.setState(DeployerService.DEPLOYMENT_STATE_UNDEPLOY);
			deployerService.updateDeploymentState(deployment);
		} else {
			// return alert
			System.out.println("UNDEPLOYING deployment. Deployment <"+deploymentId+"> was not in DEPLOYED state. Nothing done.");			
		}

	} 	
	
	/**
	 * Delete deployment 
	 * 
	 * TODO: 
	 * 
	 * @param deploymentModel Represents the MVC model including deployment information.
	 * @throws Throwable Thrown when system level exception arises.
	 */
	@Log
	@AuditTrail
	@ResourceMapping(PATH_FOR_DEPLOYMENT_DELETE)
	public void deleteDeployment(ResourceResponse response, @RequestParam("deploymentId") int deploymentId) throws Exception {
		System.out.println("DELETING deployment: <"+deploymentId+">.");

		// verify current state
		
		// only undeployed deployment can be deleted. otherwise return instruction to undeploy
		Deployment deployment = deployerService.loadDeploymentById(deploymentId);
		if (deployment.getState()==DeployerService.DEPLOYMENT_STATE_UNDEPLOYED) {
			// deployment state should be updated to DELETED when all the deploymentstatuses are processed
			// update deployment state to be undeployed
			//deployment.setState(DeployerService.DEPLOYMENT_STATE_TO_BE_DELETED);
			//deployerService.updateDeploymentState(deployment);
			// just deletes object in walrus and updates deployment state immediately 
			deployerService.deleteObject(deployment);
		} else {
			// need to be undeployed first
			// TODO: implement response handling
		}
		
	} 	
	
	
	/**
	 * Deploys new application to backbone server.
	 * 
	 * @param deploymentModel  Represents the MVC model including deployment information.
	 * @throws Throwable Thrown when system level exception arises.
	 */
	@Log
	@AuditTrail
	@ActionMapping
    public void deploy(@ModelAttribute("deploymentModel") DeploymentModel deploymentModel) throws Throwable {
    	Deployment deployment = new Deployment();
		deployment.setClusterId(deploymentModel.getClusterId());
		deployment.setInstanceId(deploymentModel.getInstanceId());
		deployment.setOrganizationId(deploymentModel.getOrganizationId());
		deployment.setName(deploymentModel.getName().toLowerCase().trim());
		deployment.setInputStream(deploymentModel.getFileData().getInputStream());
		deployerService.deploy(deployment);
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
	@ResourceMapping(PATH_FOR_LOAD_DEPLYMENT_TABLE)
	public void loadDeploymentTable(ResourceRequest request, ResourceResponse response,@RequestParam("page") int page, @RequestParam("rows") int rows) throws Exception {
		Collection<Deployment> deployments = deployerService.loadDeployments();
		List<DeploymentTableData> deploymentDataList = new ArrayList<DeploymentTableData>(); 
		for (Deployment deployment : deployments){
			Instance instance = instanceService.getInstance(deployment.getInstanceId());
			Cluster cluster = clusterService.getCluster(deployment.getClusterId());
			// only deployments for existing instances and clusters are currently returned. should we show those as undeployed
			//if (instance == null || cluster == null) continue;
			// update deployment status as undeployed if not that already
			if (instance == null || cluster == null) {
				if (deployment.getState()==DeployerService.DEPLOYMENT_STATE_DEPLOYED) {					
					//deployment.setState(DeployerService.DEPLOYMENT_STATE_UNDEPLOYED);
					// let's leave this for batch processing for now
					continue;
				}
				else {
					// show also deployments with no targets and not in deployed state 
					// figure out how null instance and cluster should be shown and remove continue 
					continue;
				}
			}
			deploymentDataList.add(new DeploymentTableData(
				deployment, 
				OrganizationLocalServiceUtil.getOrganization(deployment.getOrganizationId()).getName(),
				instance.getName(), 
				cluster.getName() 
			));	
		}
		
		int records = deploymentDataList.size();
		int mod = records % rows;
		int totalPages = records/rows;
		if(mod > 0) totalPages++;
		@SuppressWarnings("unchecked")
		List<DeploymentTableData> onePage = ListUtil.sliceList(page, rows, deploymentDataList);

		// Create JSON 
		JsonDataWrapper jdw = new JsonDataWrapper(page, totalPages, records, onePage);
		SerializerUtil.jsonSerialize(response.getWriter(), jdw); 
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
		
}