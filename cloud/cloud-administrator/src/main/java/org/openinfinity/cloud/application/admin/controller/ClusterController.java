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

package org.openinfinity.cloud.application.admin.controller;

import com.amazonaws.auth.AWSCredentials;
import com.liferay.portal.model.User;
import org.apache.log4j.Logger;
import org.openinfinity.cloud.common.web.LiferayService;
import org.openinfinity.cloud.domain.*;
import org.openinfinity.cloud.service.administrator.*;
import org.openinfinity.cloud.service.scaling.ScalingRuleService;
import org.openinfinity.cloud.util.http.HttpCodes;
import org.openinfinity.cloud.util.serialization.JsonDataWrapper;
import org.openinfinity.cloud.util.serialization.SerializerUtil;
import org.openinfinity.core.util.ExceptionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.portlet.bind.annotation.ResourceMapping;

import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.*;

/**
 * Controller for handling Cluster related CloudAdmin requests
 * @author Ossi Hämäläinen
 * @author Vedran Bartonicek
 * @version 1.0.0 Initial version
 * @since 1.0.0
 */

@Controller(value="clusterController")
@RequestMapping(value = "VIEW")
public class ClusterController {
	private static final Logger LOG = Logger.getLogger(ClusterController.class.getName());
	
	private static final int JOB_ID_UNDEFINED = -1;
	
	private static final int CLUSTER_SIZE_UNDEFINED = -1;
    @Autowired
    @Qualifier("liferayService")
    private LiferayService liferayService;

    @Autowired
	@Qualifier("clusterService")
	private ClusterService clusterService;
	
	@Autowired
	@Qualifier("machineService")
	private MachineService machineService;
	
	@Autowired
	@Qualifier("instanceService")
	private InstanceService instanceService;
	
	@Autowired
	@Qualifier("cloudCredentials")
	private AWSCredentials eucaCredentials;
	
	@Autowired
	@Qualifier("authorizationRoutingService")
	private AuthorizationRoutingService arService;
	
	@Autowired
	@Qualifier("jobService")
	private JobService jobService;
	
	@Autowired
	@Qualifier("scalingRuleService")
	private ScalingRuleService scalingRuleService;
		
	@Value("${endpoint}")
	private String ec2EndPoint;
		
	@Value("${securityGroupOwner}")
	private String securityGroupOwner;

	@ResourceMapping("getClusters")
	public void getClusters(ResourceRequest request, ResourceResponse response) throws Exception  {
		Collection<Cluster> clusters = clusterService.getClusters();
		LOG.info("Found "+clusters.size()+" clusters");
		SerializerUtil.jsonSerialize(response.getWriter(), new JsonDataWrapper(1, 1, 1, clusters));		
	}
	
	@ResourceMapping("getElasticIPList")
	public void getElasticIPList(ResourceRequest request, ResourceResponse response) throws IOException {
		if (liferayService.getUser(request, response) == null) return;
		SerializerUtil.jsonSerialize(response.getWriter(), arService.getElasticIPList());		
	}
	
	@ResourceMapping("getElasticIPForCluster")
	public void getElasticIPList(ResourceRequest request, ResourceResponse response, @RequestParam("clusterId") int clusterId) throws Exception {
		if (liferayService.getUser(request, response) == null) return;
		Machine lb = clusterService.getClustersLoadBalancer(clusterId);
		if(lb == null) 
			response.setProperty(ResourceResponse.HTTP_STATUS_CODE, HttpCodes.HTTP_ERROR_CODE_SERVER_ERROR);
		else{
			try{
				SerializerUtil.jsonSerialize(response.getWriter(), arService.getClustersElasticIP(clusterId));		
			}
			catch(Exception e)
			{
				ExceptionUtil.throwSystemException(e);
				SerializerUtil.jsonSerialize(response.getWriter(), -1);		
			}
		}
	}
		
	@ResourceMapping("setElasticIP")
	public void setElasticIP(ResourceRequest request, ResourceResponse response, @RequestParam("clusterId") int clusterId, @RequestParam("ipId") int ipId) {
		User user = liferayService.getUser(request, response);
		if (user == null) return;
		try {
			Cluster c = clusterService.getCluster(clusterId);
			Instance tapInstance = instanceService.getInstance(c.getInstanceId());
			Machine lb = clusterService.getClustersLoadBalancer(clusterId);
			ElasticIP ip = arService.getElasticIP(ipId);
			if(lb == null) ExceptionUtil.throwApplicationException("No load balancer found for cluster:" + clusterId);
			EC2Wrapper ec2 = new EC2Wrapper();
			if(tapInstance.getCloudType() == InstanceService.CLOUD_TYPE_AMAZON) {
				ec2.setEndpoint(ec2EndPoint);
				ec2.init(eucaCredentials, tapInstance.getCloudType());
			} else if(tapInstance.getCloudType() == InstanceService.CLOUD_TYPE_EUCALYPTUS) {
				ec2.setEndpoint(ec2EndPoint);
				ec2.init(eucaCredentials, tapInstance.getCloudType());
			}
			ec2.assignElasticIPToInstance(lb.getInstanceId(), ip.getIpAddress());
			ip.setClusterId(clusterId);
			ip.setMachineId(lb.getId());
			ip.setInstanceId(tapInstance.getInstanceId());
			ip.setInUse(1);
			ip.setUserId((int)user.getUserId());
			ip.setOrganizationId((int)user.getOrganizationIds()[0]);
			arService.updateElasticIP(ip);
		} catch (Exception e) {
			ExceptionUtil.throwSystemException(e);
			response.setProperty(ResourceResponse.HTTP_STATUS_CODE, HttpCodes.HTTP_ERROR_CODE_SERVER_ERROR);	
		}
	}
	
	@ResourceMapping("removeElasticIP")
	void removeElasticIP(ResourceRequest request, ResourceResponse response, @RequestParam("clusterId") int clusterId) throws IOException {
		if (liferayService.getUser(request, response) == null) return;
		try{
			Cluster c = clusterService.getCluster(clusterId);	
			Instance tapInstance = instanceService.getInstance(c.getInstanceId());
			ElasticIP eip = arService.getClustersElasticIP(clusterId);
			EC2Wrapper ec2 = new EC2Wrapper();
			if (tapInstance.getCloudType() == InstanceService.CLOUD_TYPE_AMAZON) {
				ec2.setEndpoint(ec2EndPoint);
				ec2.init(eucaCredentials, tapInstance.getCloudType());
			} else if (tapInstance.getCloudType() == InstanceService.CLOUD_TYPE_EUCALYPTUS) {
				ec2.setEndpoint(ec2EndPoint);
				ec2.init(eucaCredentials, tapInstance.getCloudType());
			}		
			if (eip == null) ExceptionUtil.throwApplicationException("No elastic IP found for cluster:" + clusterId);
			ec2.removeElasticIPFromInstance(eip.getIpAddress());
			arService.freeElasticIP(eip);
		} catch(Exception e){
			e.printStackTrace();
			response.setProperty(ResourceResponse.HTTP_STATUS_CODE, HttpCodes.HTTP_ERROR_CODE_SERVER_ERROR);	
		}
	}
	
	@ResourceMapping("getUserAuthorizedIPsForCluster")
	public void getUserAuthorizedIPsForCluster(ResourceRequest request, ResourceResponse response, 
		@RequestParam("clusterId") String clusterIdString) throws RuntimeException, Exception {
		
		if (liferayService.getUser(request, response) == null) return;
		int clusterId = Integer.parseInt(clusterIdString);
		arService.getUserAuthorizedIPsForCluster(clusterId);
		SerializerUtil.jsonSerialize(response.getWriter(), arService.getUserAuthorizedIPsForCluster(clusterId));			
	}
	
	@ResourceMapping("deleteUserAuthorizedIP")
	public void deleteUserAuthorizedIP(ResourceRequest request, ResourceResponse response,
			@RequestParam("ipId") int ipId,
			@RequestParam("clusterId") int clusterId, 
			@RequestParam("cidrIp") String cidrIp,
			@RequestParam("portFrom") int portFrom,
			@RequestParam("portTo") int portTo,
			@RequestParam("protocol") String protocol){		
		try{
			if (liferayService.getUser(request, response) == null) return;
			Cluster c = clusterService.getCluster(clusterId);
			Instance tapInstance = instanceService.getInstance(c.getInstanceId());
			
			// TODO this can go to util function
			EC2Wrapper ec2 = new EC2Wrapper();
			if(tapInstance.getCloudType() == InstanceService.CLOUD_TYPE_AMAZON) {
				ec2.setEndpoint(ec2EndPoint);
				ec2.init(eucaCredentials, tapInstance.getCloudType());
			} else if(tapInstance.getCloudType() == InstanceService.CLOUD_TYPE_EUCALYPTUS) {
				ec2.setEndpoint(ec2EndPoint);
				ec2.init(eucaCredentials, tapInstance.getCloudType());
			}
			String securityGroupName = clusterService.getCluster(clusterId).getSecurityGroupName();
			ec2.revokeIPs(securityGroupName, cidrIp, portFrom, portTo, protocol);
			arService.deleteUserAuthorizedIP(ipId);
		} catch(Exception e){
			e.printStackTrace();
			response.setProperty(ResourceResponse.HTTP_STATUS_CODE, HttpCodes.HTTP_ERROR_CODE_SERVER_ERROR);		
		}	
	}
		 
	@ResourceMapping("addUserAuthorizedIP")	
	public void addUserAuthorizedIP(ResourceRequest request, ResourceResponse response, 
		@RequestParam("clusterId") int clusterId,
		@RequestParam("cidrIp") String cidrIp,
		@RequestParam("portFrom") int fromPort,
		@RequestParam("portTo") int toPort,
		@RequestParam("protocol") String protocol) throws RuntimeException, Exception {
		try{
			if (liferayService.getUser(request, response) == null) return;
			String securityGroupName = clusterService.getCluster(clusterId).getSecurityGroupName();
			
			Cluster c = clusterService.getCluster(clusterId);
			Instance tapInstance = instanceService.getInstance(c.getInstanceId());
			
			EC2Wrapper ec2 = new EC2Wrapper();
			if(tapInstance.getCloudType() == InstanceService.CLOUD_TYPE_AMAZON) {
				ec2.setEndpoint(ec2EndPoint);
				ec2.init(eucaCredentials, tapInstance.getCloudType());
			} else if(tapInstance.getCloudType() == InstanceService.CLOUD_TYPE_EUCALYPTUS) {
				ec2.setEndpoint(ec2EndPoint);
				ec2.init(eucaCredentials, tapInstance.getCloudType());
			}
			ec2.authorizeIPs(securityGroupName, cidrIp, fromPort, toPort, protocol);
			AuthorizationRoute ar = new AuthorizationRoute(tapInstance.getInstanceId(), clusterId, cidrIp, protocol, securityGroupName, fromPort, toPort);
			int ipId = arService.addUserAuthorizedIP(ar);
			LOG.debug("arService.addUserAuthorizedIP(ar) "+ipId);
			
			SerializerUtil.jsonSerialize(response.getWriter(), ipId);	
		} catch(Exception e){
			e.printStackTrace();
			response.setProperty(ResourceResponse.HTTP_STATUS_CODE, HttpCodes.HTTP_ERROR_CODE_SERVER_ERROR);		
		}	
	}
	
	@ResourceMapping("updatePublished")
	public void updatePublished(ResourceRequest request, ResourceResponse response, @RequestParam("clusterId") int clusterId, @RequestParam("pubId") int pubId) throws Exception {
		if (liferayService.getUser(request, response) == null) return;
		LOG.info("Updating cluster ("+clusterId+") to publish status: "+pubId);
		Cluster cluster = clusterService.getCluster(clusterId);
		if(cluster == null) {
			return;
		}
		Instance tapInstance = instanceService.getInstance(cluster.getInstanceId());
		if(tapInstance == null) {
			return;
		}
		EC2Wrapper ec2 = new EC2Wrapper();
		if(tapInstance.getCloudType() == InstanceService.CLOUD_TYPE_AMAZON) {
			ec2.setEndpoint(ec2EndPoint);
			ec2.init(eucaCredentials, tapInstance.getCloudType());
		} else if(tapInstance.getCloudType() == InstanceService.CLOUD_TYPE_EUCALYPTUS) {
			ec2.setEndpoint(ec2EndPoint);
			ec2.init(eucaCredentials, tapInstance.getCloudType());
		}
		
		arService.updateAuthorizedIPs(cluster, pubId, ec2, securityGroupOwner);
		clusterService.updatePublished(clusterId, pubId);
	}

	@ResourceMapping("scaleCluster")
	public void scaleCluster(
			ResourceRequest request,
			ResourceResponse response,
			@RequestParam("cluster") int clusterId, 
			@RequestParam("periodicScalingOn") boolean periodicScalingOn,
			@RequestParam("scheduledScalingOn") boolean scheduledScalingOn,
			@RequestParam("maxNumberOfMachinesPerCluster") int maxNumberOfMachinesPerCluster,		
			@RequestParam("minNumberOfMachinesPerCluster") int minNumberOfMachinesPerCluster,
			@RequestParam("maxLoad") float maxLoad, 
			@RequestParam("minLoad") float minLoad,		 
			@RequestParam("periodFrom") long periodFrom, 
			@RequestParam("periodTo") long periodTo,
			@RequestParam("scheduledClusterSize") int scheduledClusterSize, 
			@RequestParam("manualScaling") boolean manualScaling, 
			@RequestParam("manualScalingNewSize") int manualScalingNewSize){
		
		Assert.notNull(liferayService.getUser(request, response));	
		ScalingRule scalingRule = new ScalingRule(
				clusterId,
				periodicScalingOn,
				scheduledScalingOn,
				1,
				maxNumberOfMachinesPerCluster,
				minNumberOfMachinesPerCluster,
				maxLoad,
				minLoad,
				new Timestamp(periodFrom),
				new Timestamp(periodTo),
				scheduledClusterSize,
				CLUSTER_SIZE_UNDEFINED, 
				JOB_ID_UNDEFINED);
		
		if (manualScaling && manualScalingNewSize != clusterService.getCluster(scalingRule.getClusterId()).getNumberOfMachines()){
			Job scalingJob = createScalingJob(scalingRule, manualScalingNewSize);
			scalingRule.setJobId(jobService.addJob(scalingJob));	
		}
		scalingRuleService.store(scalingRule);
	}
	
	@ResourceMapping("getClusterInfo")
	public void getClusterInfo(ResourceRequest request, ResourceResponse response, @RequestParam("clusterId") int clusterId) throws Exception {
		if (liferayService.getUser(request, response) == null) return;
		LOG.info("Getting cluster information, clusterId: "+clusterId);
		Cluster cluster = clusterService.getCluster(clusterId);
		TreeMap<String,String> clusterData = new TreeMap<String,String>();
		clusterData.put("Cluster name", cluster.getName());
		clusterData.put("# of machines", Integer.toString(cluster.getNumberOfMachines()));
		clusterData.put("Cluster visibility", ClusterService.CLUSTER_PUBLISH_STATUS_NAME[cluster.getPublished()-1]);
		if(cluster.getLbDns() != null) {
			clusterData.put("URL", "<a href=\"http://"+cluster.getLbDns()+"\" target=\"_blank\">http://"+cluster.getLbDns()+"</a>");
		}
		SerializerUtil.jsonSerialize(response.getWriter(), clusterData);			
	}
	
	@ResourceMapping("getClusterStatus")
	public void getClusterStatus(ResourceRequest request, ResourceResponse response, @RequestParam("clusterId") int clusterId) throws Exception {
		if (liferayService.getUser(request, response) == null) return;
		Cluster cluster = clusterService.getCluster(clusterId);
		if(cluster == null) {
			return;
		}
		Collection<Machine> machines = machineService.getMachinesInCluster(clusterId);
		if(machines == null) {
			return;
		}
		TreeMap<String,Integer> clusterStatus = new TreeMap<String,Integer>();
		Integer running = 0;
		Integer configured = 0;
		Integer error = 0;
		Iterator<Machine> i = machines.iterator();
		while(i.hasNext()) {
			Machine m = i.next();
			if (m.getType() != null && (!m.getType().equalsIgnoreCase("loadbalancer") && !m.getType().equalsIgnoreCase("manager"))) {
				if (m.getState() != null && m.getState().equalsIgnoreCase("running")) {
					running++;
				}
				if (m.getConfigured() == MachineService.MACHINE_CONFIGURE_READY) {
					configured++;
				} else if (m.getConfigured() == MachineService.MACHINE_CONFIGURE_ERROR) {
					error++;
				}
			}
		}
		clusterStatus.put("total_machines", cluster.getNumberOfMachines());
		clusterStatus.put("running_machines", running);
		clusterStatus.put("configured_machines", configured);
		clusterStatus.put("machines_with_errors", error);
		clusterStatus.put("starting_machines", (cluster.getNumberOfMachines() - configured));
		SerializerUtil.jsonSerialize(response.getWriter(), clusterStatus);		
	}
	
	@ResourceMapping("deleteCluster")
	public void deleteCluster(ResourceRequest request, ResourceResponse response, @RequestParam("clusterId")int clusterId) throws Exception {
		if (liferayService.getUser(request, response) == null) ExceptionUtil.throwBusinessViolationException("User not logged in");
		Cluster cluster = clusterService.getCluster(clusterId);
		if (cluster == null) ExceptionUtil.throwApplicationException("Cluster with id " + clusterId + " not found in DB");
		int instanceId = cluster.getInstanceId();
		Instance instance = instanceService.getInstance(instanceId);
		if (instance == null) ExceptionUtil.throwApplicationException("Instance with id " + instanceId + " not found in DB");
		jobService.addJob(new Job("delete_cluster", instanceId,	instance.getCloudType(), JobService.CLOUD_JOB_CREATED, instance.getZone(), Integer.toString(clusterId)));	
	}

	@ResourceMapping("getClusterScalingRule")
	public void getClusterScalingRule(ResourceRequest request, ResourceResponse response, @RequestParam("clusterId") int clusterId) throws Exception {
		if (liferayService.getUser(request, response) == null) return;
		
		Cluster cluster = clusterService.getCluster(clusterId);
		if (cluster == null) ExceptionUtil.throwApplicationException("Cluster with cluster_id: " + clusterId + "not found in DB");
			
		Map<String, Object> scalingRuleData = new HashMap<String, Object>();

		try{
			ScalingRule scalingRule = scalingRuleService.getRule(clusterId);
			scalingRuleData.put("ruleDefined", true);
			scalingRuleData.put("size", cluster.getNumberOfMachines());
			scalingRuleData.put("periodic", scalingRule.isPeriodicScalingOn());
			scalingRuleData.put("minMachines", scalingRule.getMinNumberOfMachinesPerCluster());
			scalingRuleData.put("maxMachines", scalingRule.getMaxNumberOfMachinesPerCluster());
			scalingRuleData.put("minLoad", scalingRule.getMinLoad());
			scalingRuleData.put("maxLoad", scalingRule.getMaxLoad());
			scalingRuleData.put("scheduled", scalingRule.isScheduledScalingOn());
			scalingRuleData.put("periodFrom", scalingRule.getPeriodFrom().getTime());
			scalingRuleData.put("periodTo", scalingRule.getPeriodTo().getTime());
			scalingRuleData.put("scheduledSize", scalingRule.getClusterSizeNew());
		}
		catch(Exception e){
		//TODO: catch loadByClusterId exceptions, ignore put exceptions
			e.printStackTrace();
			scalingRuleData.put("ruleDefined", false);
		}	
		SerializerUtil.jsonSerialize(response.getWriter(), scalingRuleData);
	}
		
	public int putValueInRange(int value, int min, int max){  
	    if (value < min){
	    	return min;
	    }
	    else if (value > max){
	    	return max;
	    }
	    else return value;
	}
	
	private Job createScalingJob(ScalingRule scalingRule, int manualScalingNewSize){	
		Cluster cluster = clusterService.getCluster(scalingRule.getClusterId());
		Instance instance = instanceService.getInstance(cluster.getInstanceId());
					
		return new Job("scale_cluster", 
					   cluster.getInstanceId(),
                	   instance.getCloudType(), 
			           JobService.CLOUD_JOB_CREATED,
			           instance.getZone(),
			           Integer.toString(scalingRule.getClusterId()),
			           manualScalingNewSize);		
	}
	
}
