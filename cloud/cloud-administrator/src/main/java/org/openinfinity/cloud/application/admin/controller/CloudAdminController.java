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

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.model.Organization;
import com.liferay.portal.model.User;
import com.liferay.portal.service.OrganizationLocalServiceUtil;
import com.liferay.portal.service.UserLocalServiceUtil;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.openinfinity.cloud.common.annotation.Authenticated;
import org.openinfinity.cloud.common.web.LiferayService;
import org.openinfinity.cloud.domain.*;
import org.openinfinity.cloud.domain.configurationtemplate.entity.ConfigurationElement;
import org.openinfinity.cloud.domain.configurationtemplate.entity.ConfigurationTemplate;
import org.openinfinity.cloud.domain.configurationtemplate.entity.InstallationModule;
import org.openinfinity.cloud.domain.configurationtemplate.entity.ParameterKey;
import org.openinfinity.cloud.serialization.*;
import org.openinfinity.cloud.service.administrator.*;
import org.openinfinity.cloud.service.configurationtemplate.entity.api.*;
import org.openinfinity.cloud.util.AdminException;
import org.openinfinity.cloud.util.collection.ListUtil;
import org.openinfinity.cloud.util.http.HttpCodes;
import org.openinfinity.cloud.util.serialization.JsonDataWrapper;
import org.openinfinity.cloud.util.serialization.SerializerUtil;
import org.openinfinity.core.util.ExceptionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.portlet.bind.annotation.RenderMapping;
import org.springframework.web.portlet.bind.annotation.ResourceMapping;

import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import java.io.IOException;
import java.util.*;

/**
 * Controller for handling CloudAdmin requests
 * @author Ossi Hämäläinen
 * @author Juha-Matti Sironen
 * @author Vedran Bartonicek 
 * @author Timo Tapanainen
 * @author Ari Simanainen
 * @version 1.2.1 
 * @since 1.0.0
 */

@Controller(value="cloudadminController")
@RequestMapping(value = "VIEW")
public class CloudAdminController {
	
	private static final Logger LOG = Logger.getLogger(CloudAdminController.class.getName());
	private static final String MSG_INSTANCE_WRITING_ERROR = "Error writing instance data to HTTP response";
	private static final String MSG_HTTP_REPLY_WRITING_ERROR = "Error while writing the http reply";

    @Autowired
    @Qualifier("liferayService")
    private LiferayService liferayService;

	@Autowired
	@Qualifier("instanceService")
	private InstanceService instanceService;
	
	@Autowired
	@Qualifier("clusterService")
	private ClusterService clusterService;
	
	@Autowired
	@Qualifier("clusterTypeService")
	private ClusterTypeService clusterTypeService;

    @Autowired
    @Qualifier("machineTypeService")
    private MachineTypeService machineTypeService;

    @Autowired
	@Qualifier("cloudProviderService")
	private CloudProviderService cloudService;
	
	@Autowired
	@Qualifier("keyService")
	private KeyService keyService;
	
	@Autowired
	@Qualifier("jobService")
	private JobService jobService;
	
	@Autowired
	@Qualifier("availabilityZoneService")
	private AvailabilityZoneService zoneService;

    @Autowired
    private ConfigurationTemplateService templateService;

    @Autowired
    private ConfigurationElementService elementService;

    @Autowired
    private InstallationModuleService moduleService;

    @Autowired
    private ParameterKeyService parameterKeyService;

    @Autowired
    private ParameterValueService parameterValueService;

	@RenderMapping
	public String showView(RenderRequest request, RenderResponse response) {
		User user = liferayService.getUser(request);
		if(user == null) {
			response.setProperty(ResourceResponse.HTTP_STATUS_CODE, HttpCodes.HTTP_ERROR_CODE_USER_NOT_LOGGED_IN);
			return "notlogged";
		}	
		return "mainview";
	}
	
	@ResourceMapping("instanceList")
	public void getInstanceList(ResourceRequest request, ResourceResponse response) throws Exception {
		LOG.debug("getInstanceList()");
		User user = liferayService.getUser(request, response);
		if (user == null) return;
		Collection<Instance> instanceList = instanceService.getInstances(user.getUserId());
		SerializerUtil.jsonSerialize(response.getWriter(), instanceList);
	}
	
	@SuppressWarnings("rawtypes")
	@ResourceMapping("instanceTable")
	public void getInstanceTable(ResourceRequest request, ResourceResponse response, @RequestParam("page") int page, @RequestParam("rows") int rows) throws Exception {
		LOG.debug("getInstanceTable()");
		User user  = liferayService.getUser(request, response);
		if (user == null) return;
		List<Organization> organizationList = null;
		List<Organization> subOrganizationList = null;
		try {
			organizationList = user.getOrganizations();
			subOrganizationList = OrganizationLocalServiceUtil.getSuborganizations(organizationList);
		} catch (PortalException e) {
			LOG.error("Could not get organizations for user "+user.getFullName()+": "+e.getLocalizedMessage());
		} catch (SystemException e) {
			LOG.error("Something is wrong: "+e.getLocalizedMessage());
		}
		Collection<Long> orgIdList = new ArrayList<Long>();
		if (organizationList != null) {
			for (Organization organization : organizationList) {
				LOG.info("Adding organization " + organization.getName() + " to user " + user.getScreenName());
				orgIdList.add(organization.getOrganizationId());
			}
		}
		if (subOrganizationList != null) {
			for (Organization organization : subOrganizationList) {
				LOG.info("Adding organization " + organization.getName() + " to user " + user.getScreenName());
				orgIdList.add(organization.getOrganizationId());
			}
		}
		
		Collection<Instance> instanceList = instanceService.getUserInstances(orgIdList);
		
		if(instanceList == null || instanceList.size() == 0) {
			SerializerUtil.jsonSerialize(response.getWriter(), new JsonDataWrapper(page, 0, 0, new ArrayList<Instance>()));
			return;
		}	
		
		// Add organization name and user name to the list
		for(Instance instance : instanceList) {
			if(instance != null) {
				User tempUser = UserLocalServiceUtil.getUserById(instance.getUserId());
				instance.setUserName(tempUser.getScreenName());
				if(instance.getOrganizationid() != 0) {
					Organization tempO = OrganizationLocalServiceUtil.getOrganization(instance.getOrganizationid());
					if(tempO != null) {
						instance.setOrganizationName(tempO.getName());
					}
				}	
			}
		} 
		int records = instanceList.size();
		int mod = records % rows;
		int totalPages = records/rows;
		if(mod > 0) totalPages++;

		// Slice a subset from all deployments, the result fits into one jqGgrid page
		@SuppressWarnings("unchecked")
		List<Instance> onePage = ListUtil.sliceList(page, rows, (List)instanceList);
		SerializerUtil.jsonSerialize(response.getWriter(), new JsonDataWrapper(page, totalPages, records, onePage));
	}
	
	@ResourceMapping("errorResponse")
	public void errorResponse(ResourceRequest request, ResourceResponse response, @RequestParam("code") int code) throws Exception {
		LOG.debug("errorResponse()");
		
		if(code == 0) {
			response.setProperty(ResourceResponse.HTTP_STATUS_CODE, "421");
			response.getWriter().write("custom error from controller, no response code defined so using 421 status");
		} else {
			response.setProperty(ResourceResponse.HTTP_STATUS_CODE, "" + code);
			response.getWriter().write("custom error from controller, response code defined : " + code);		
		}
	}
	
	
	@ResourceMapping("getInstanceKey")
	public void getInstanceKey(ResourceRequest request, ResourceResponse response, @RequestParam("id") int instanceId) throws IOException {
		LOG.debug("getInstanceKey()");
		User user = liferayService.getUser(request, response);
		if (user == null) return;	
		Instance instance = instanceService.getInstance(instanceId);
		if(instance == null) {
			LOG.error("Instance is null, returning");
			return;
		}
		if(instance.getUserId() != (int)user.getUserId()) {
			LOG.error("User different than instance owner, will not give the private key");
			response.setProperty(ResourceResponse.HTTP_STATUS_CODE, "421");
			response.getWriter().write("User different than instance owner, will not give the private key");
			return;
		}
		Key key = keyService.getKeyByInstanceId(instanceId);
		response.reset();
		response.setContentType("application/octet-stream");
		response.setProperty("Content-disposition", "attachment; filename=\"instance"+instanceId+".key\"");
		byte [] keyBytes = key.getSecret_key().getBytes();
		try {
			response.getPortletOutputStream().write(keyBytes);
		} catch (IOException e) {
			ExceptionUtil.throwApplicationException("MSG_KEY_SENDING_ERROR", e);
		}
	}
	
	
	@ResourceMapping("instance")
	public void getInstance(ResourceRequest request, ResourceResponse response, @RequestParam("id") int instanceId) throws Exception {
		LOG.debug("getInstance()");
		if (liferayService.getUser(request, response) == null) return;
		Instance instance = instanceService.getInstance(instanceId);
		LOG.info("Found instance : "+instance.getName() + " Id: "+instance.getInstanceId());	
		ObjectMapper mapper = new ObjectMapper();
		try {
			mapper.writeValue(response.getWriter(), instance);
		} catch (Exception e) {
			ExceptionUtil.throwApplicationException(MSG_INSTANCE_WRITING_ERROR);
			response.setProperty(ResourceResponse.HTTP_STATUS_CODE, "421");
			response.getWriter().write("No instance was found with instanceId["+instanceId+"]");
		}
	}
	
	@ResourceMapping("getCloudProviders")
	public void getCloudProviders(ResourceRequest request, ResourceResponse response) throws Exception {
		LOG.debug("getCloudProviders()");
        User user = liferayService.getUser(request, response);
		if (user == null) return;
        List<String> userOrgNames = liferayService.getOrganizationNames(user);
		Collection<CloudProvider> providers = cloudService.getCloudProviders(userOrgNames);
		try {
			SerializerUtil.jsonSerialize(response.getWriter(), providers);
		} catch (Exception e) {
			LOG.error("Could not send json coded list of the cloud providers");
			response.setProperty(ResourceResponse.HTTP_STATUS_CODE, "421");
			response.getWriter().write("Error locating cloud providers.");
		}
	}
	
	@ResourceMapping("getCloudZones")
	public void getCloudZones(ResourceRequest request, ResourceResponse response, @RequestParam("cloud") int cloudId) throws Exception {
		LOG.debug("getCloudZones()");
        User user = liferayService.getUser(request, response);
        if (user == null) return;
        List<String> userOrgNames = liferayService.getOrganizationNames(user);
        Collection<AvailabilityZone> zones = zoneService.getAvailabilityZones(cloudId, userOrgNames);
		try {
			SerializerUtil.jsonSerialize(response.getWriter(), zones);
		} catch (Exception e) {
			LOG.error("Could not send json coded list of the zones");
			response.setProperty(ResourceResponse.HTTP_STATUS_CODE, "421");
			response.getWriter().write("Error locating zones.");
		}
	}
	
	@ResourceMapping("getAvailableServices")
	public void getAvailableServices(ResourceRequest request, ResourceResponse response, @RequestParam("id") int instanceId) throws Exception {
		LOG.debug("getAvailableServices()");
		
		User user = liferayService.getUser(request, response);
		if (user == null) return;

		Instance instance = instanceService.getInstance(instanceId);
		Collection<Cluster> clusterList = clusterService.getClusters(instance.getInstanceId());
		
		List<String> userOrgNames = liferayService.getOrganizationNames(user);
		LOG.info("user organizations: " + userOrgNames);
		Collection<ClusterType> clusterTypeList = clusterTypeService.getAvailableClusterTypes(userOrgNames);
		HashMap<Integer,String> serviceMap = new HashMap<Integer,String>();
		for (ClusterType clusterType : clusterTypeList){
			serviceMap.put(clusterType.getId(), clusterType.getTitle());
		}

		Iterator<Cluster> i = clusterList.iterator();	
		while(i.hasNext()) {
			Cluster cluster = i.next();
			serviceMap.remove(cluster.getType());
		}
		SerializerUtil.jsonSerialize(response.getWriter(), serviceMap);
	}
	
	@ResourceMapping("newService")
    public void newService(ResourceRequest request, ResourceResponse response, @RequestParam Map<String, String> pm){
        try{            
            User user = liferayService.getUser(request, response);
            if (user == null) throw new AdminException("User not logged in");
            
            Instance i = instanceService.getInstance(Integer.parseInt(pm.get("instanceid"))); 
            jobService.addJob(parseNewServiceRequestParams(
                                 new Job("add_service", i.getInstanceId(), i.getCloudType(), JobService.CLOUD_JOB_CREATED),
                                 pm));
            
        } catch (Exception e) {
            LOG.error("Error adding new service: " + e.getMessage(), e);
            response.setProperty(ResourceResponse.HTTP_STATUS_CODE, "421");
            try {
                response.getWriter().write(e.getMessage());
            } catch (IOException ioe) {
                LOG.error("Error while writing the http reply: "+ioe.getMessage());
            }
        } 
    }
	
	@ResourceMapping("deleteInstance")
	public void deleteInstance(ResourceRequest request, ResourceResponse response, @RequestParam("id") int instanceId) throws Exception {
		LOG.info("deleteInstance() for instance id: " + instanceId);

		if (liferayService.getUser(request, response) == null) return;
		Instance instance = instanceService.getInstance(instanceId);
		if(instance != null) {
			instanceService.updateInstanceStatus(instanceId, "Deleting");		
			jobService.addJob(
				new Job("delete_instance", instance.getInstanceId(), instance.getCloudType(), JobService.CLOUD_JOB_CREATED));	
		}
	}
	
	@ResourceMapping("addInstance")
	public void addInstance(ResourceRequest request, ResourceResponse response, @RequestParam Map<String, String> pm){
		try{			
			User user = liferayService.getUser(request, response);
			if (user == null) throw new AdminException("User not logged in");
			
			Instance i = new Instance();
			i.setName(pm.get("instancename"));
			i.setUserId((int)user.getUserId());
			i.setZone(pm.get("zone"));
			
			long[] orgIds = user.getOrganizationIds();
			if (orgIds.length > 0) i.setOrganizationid(orgIds[0]);
			
			i.setCloudType(Integer.parseInt(pm.get("cloudtype")));
			i.setStatus("Starting");
            instanceService.addInstance(i);

			jobService.addJob(parseNewServiceRequestParams(
			    new Job("create_instance", i.getInstanceId(), i.getCloudType(), JobService.CLOUD_JOB_CREATED),
			    pm));
        
		} catch (Exception e) {
			LOG.error("Error setting up the instance: "+e.getMessage(), e);
			response.setProperty(ResourceResponse.HTTP_STATUS_CODE, "421");
			try {
				response.getWriter().write(e.getMessage());
			} catch (IOException ioe) {
				LOG.error("Error while writing the http reply: "+ioe.getMessage());
			}
		} 
	}

    @ResourceMapping("addEnvironment")
    public void addEnvironment(ResourceRequest request, ResourceResponse response,
                               @RequestParam("requestData") String requestData){
        try{

            LOG.error("addEnvironment() ENTER");

            User user = liferayService.getUser(request, response);
            if (user == null) throw new AdminException("User not logged in");

            LOG.info("RequestData: " + requestData);

            ObjectMapper mapper = new ObjectMapper();
            /*
            AddEnvironmentParametersContainer environmentParams = mapper.readValue(requestData, AddEnvironmentParametersContainer.class);
            LOG.debug("Environment parameters: " + environmentParams);

            EnvironmentDataContainer environment = environmentParams.getEnvironment();
            Instance i = new Instance();
            i.setName(environment.getName());
            i.setUserId((int)user.getUserId());
            i.setZone(environment.getZone());

            long[] orgIds = user.getOrganizationIds();
            if (orgIds.length > 0) i.setOrganizationid(orgIds[0]);

            i.setCloudType(environment.getType());
            i.setStatus("Starting");
            */

            //instanceService.addInstance(i);

            /*
            jobService.addJob(parseNewServiceRequestParams(
                    new Job("create_instance", i.getInstanceId(), i.getCloudType(), JobService.CLOUD_JOB_CREATED),
                    configurations));
            */

            /*
            Instance i = new Instance();
            i.setName(pm.get("instancename"));
            i.setUserId((int)user.getUserId());
            i.setZone(pm.get("zone"));

            long[] orgIds = user.getOrganizationIds();
            if (orgIds.length > 0) i.setOrganizationid(orgIds[0]);

            i.setCloudType(Integer.parseInt(pm.get("cloudtype")));
            i.setStatus("Starting");
            instanceService.addInstance(i);

            jobService.addJob(parseNewServiceRequestParams(
                    new Job("create_instance", i.getInstanceId(), i.getCloudType(), JobService.CLOUD_JOB_CREATED),
                    pm));
            */
        } catch (Exception e) {
            LOG.error("Error setting up the instance: "+e.getMessage(), e);
            response.setProperty(ResourceResponse.HTTP_STATUS_CODE, "421");
            try {
                response.getWriter().write(e.getMessage());
            } catch (IOException ioe) {
                LOG.error("Error while writing the http reply: "+ioe.getMessage());
            }
        }
    }
	
	@ResourceMapping("availableClusters")
	public void instanceContent(ResourceRequest request, ResourceResponse response, @RequestParam("instanceId") int instanceId) throws Exception {
		if (liferayService.getUser(request, response) == null) return;
		Collection<Cluster> clusterList = clusterService.getClusters(instanceId);
		if(clusterList !=  null) SerializerUtil.jsonSerialize(response.getWriter(), clusterList);
		else return;
	}

	@ResourceMapping("instanceStatus")
	public void instanceStatus(ResourceRequest request, ResourceResponse response, @RequestParam("instanceId") int instanceId) throws Exception {
		if (liferayService.getUser(request, response) == null) return;
		try {
			Instance instance = instanceService.getInstance(instanceId);	
			if(instance != null) response.getWriter().write(instance.getStatus());
			else response.getWriter().write("notfound");
		}
		catch(Exception e){
			ExceptionUtil.throwApplicationException(MSG_HTTP_REPLY_WRITING_ERROR, e);
		}
		return;
	}
	
	@ResourceMapping("getClusterTypes")
	public void getClusterTypes(ResourceRequest request, ResourceResponse response) throws Exception {
		User user = liferayService.getUser(request, response);
		if (user == null) return;
		List<String> userOrgNames = liferayService.getOrganizationNames(user);
		LOG.info("user organizations: " + userOrgNames);
		Collection<ClusterType> clusterTypeList = clusterTypeService.getAvailableClusterTypes(userOrgNames);
		if(clusterTypeList !=  null) SerializerUtil.jsonSerialize(response.getWriter(), clusterTypeList);
		else return;
	}
	
	@ResourceMapping("getMachineTypes")
	public void getMachineTypes(ResourceRequest request, ResourceResponse response) throws Exception {
        User user = liferayService.getUser(request, response);
        if (user == null) return;
        List<String> userOrgNames = liferayService.getOrganizationNames(user);
        SerializerUtil.jsonSerialize(response.getWriter(), machineTypeService.getMachineTypes(userOrgNames));
	}

    @ResourceMapping("getTemplates")
    public void getTemplates(ResourceRequest request, ResourceResponse response) throws Exception {
        LOG.debug("getTemplates()");
        User user = liferayService.getUser(request, response);
        if (user == null) return;
        try {
            List<Long> organizationIds = liferayService.getOrganizationIds(user);
            List<ConfigurationTemplate> templates = templateService.loadAllForOrganizations(organizationIds);
            SerializerUtil.jsonSerialize(response.getWriter(), templates);
        } catch (Exception e) {
            LOG.error("Could not send json coded list of the templates");
            response.setProperty(ResourceResponse.HTTP_STATUS_CODE, "421");
            response.getWriter().write("Error locating templates.");
        }
    }

    /**
     * Gets ConfigurationElement, and InstallationModules and key-value parameters related to it
     */
    @Authenticated
    @ResourceMapping("getElementsForTemplate")
    public void getElementsForTemplate(ResourceRequest request, ResourceResponse response,
                                       @RequestParam("templateId") int templateId) throws Exception {
        LOG.debug("getElementsForTemplate()");

        // TODO: move the stuff below to service layer
        try {
            Collection<ElementContainer> elementContainers = new ArrayList<ElementContainer>();

            // For each element create and populate ElementContainer and add it to the list
            for(ConfigurationElement e : elementService.loadAllForTemplate(templateId)){
                Collection <ModuleContainer> moduleContainers = new ArrayList<ModuleContainer>();

                // For each module create and populate ModuleContainer and add it to the list
                for(InstallationModule m : moduleService.loadModules(e.getId())){
                    Collection<ParametersContainer> parametersContainers = new ArrayList<ParametersContainer>();

                    // For each key create and populate ParametersContainer and add it to the list
                    for (ParameterKey pk : parameterKeyService.loadAllForModule(m.getId())){
                        parametersContainers.add(new ParametersContainer(pk, parameterValueService.loadStringValuesForKey(pk.getId())));
                    }
                    moduleContainers.add(new ModuleContainer(m, parametersContainers));
                }
                elementContainers.add(new ElementContainer(e, elementService.loadDependeeIds(e.getId()), moduleContainers));
            }
            SerializerUtil.jsonSerialize(response.getWriter(), elementContainers);
        } catch (Exception e) {
            LOG.error("Could not send json coded list of the elements");
            response.setProperty(ResourceResponse.HTTP_STATUS_CODE, "421");
            response.getWriter().write("Error locating elements.");
        }
    }

	// Helper functions
	
	private void checkPlatformExistance(Collection<Integer> clusterTypes, int type) throws AdminException{
	    if (clusterTypes.contains(new Integer(type))){
	        throw new AdminException("Cluster type already created for the instance"); 
	    }
	}
	  
    // TODO: USe Json for making better structure of data.
    // There are properties repeating for each machine.          
	private Job parseNewServiceRequestParams(Job job, Map<String, String> pm) throws AdminException{
	    	    
	    Collection<Integer> instanceClusterTypes = clusterService.getClusterTypes(job.getInstanceId());
        boolean withEcmService = "true".equals(pm.get("ecm"));
        boolean withIgService = "true".equals(pm.get("ig"));
        
        job.setZone(pm.get("zone"));
            
        if ("true".equals(pm.get("bigdata"))) {
            checkPlatformExistance(instanceClusterTypes, ClusterService.CLUSTER_TYPE_BIGDATA);
            job.addService(ClusterService.SERVICE_NAME[ClusterService.CLUSTER_TYPE_BIGDATA],
                           pm.get("bigdataclustersize"),
                           pm.get("bigdatamachinesize"),
                           pm.get("bigdataimagetype"),
                           pm.get("bigdataesbvolumesize"));
            job.setExtraData("replicationSize: " + pm.get("bigdatareplclustersize"));
            
        }
        if ("true".equals(pm.get("nosql"))) {
            checkPlatformExistance(instanceClusterTypes, ClusterService.CLUSTER_TYPE_NOSQL);
            job.addService(ClusterService.SERVICE_NAME[ClusterService.CLUSTER_TYPE_NOSQL], 
                           pm.get("nosqlclustersize"), 
                           pm.get("nosqlmachinesize"),
                           pm.get("nosqlimagetype"),
                           pm.get("nosqlesbvolumesize"));
            job.setExtraData("replicationSize: " + pm.get("nosqlreplclustersize"));
        }
        
        if ("true".equals(pm.get("rdbms"))) {
            boolean dbExists = instanceClusterTypes.contains(new Integer(ClusterService.CLUSTER_TYPE_DATABASE));
            boolean portalExists = instanceClusterTypes.contains(new Integer(ClusterService.CLUSTER_TYPE_PORTAL));
            boolean mqExists = instanceClusterTypes.contains(new Integer(ClusterService.CLUSTER_TYPE_MULE_MQ));
            
            if (!dbExists){
                job.addService(ClusterService.SERVICE_NAME[ClusterService.CLUSTER_TYPE_DATABASE],
                        pm.get("rdbmsclustersize"),
                        pm.get("rdbmsmachinesize"),
                        pm.get("rdbmsimagetype"),
                        pm.get("rdbmsesbvolumesize"));
            }
            else if(!( ("true".equals(pm.get("portal")) && mqExists) || ("true".equals(pm.get("mq")) && portalExists))) {
                throw new AdminException("DB already created for the instance"); 
            }          
        }
        
        if ("true".equals(pm.get("portal"))){
            checkPlatformExistance(instanceClusterTypes, ClusterService.CLUSTER_TYPE_PORTAL);
            job.addService(ClusterService.SERVICE_NAME[ClusterService.CLUSTER_TYPE_PORTAL], 
                           pm.get("portalclustersize"),
                           pm.get("portalmachinesize"),
                           pm.get("portalimagetype"),
                           pm.get("portalesbvolumesize"));
            if (withIgService && !withEcmService){
                checkPlatformExistance(instanceClusterTypes, ClusterService.CLUSTER_TYPE_IDENTITY_GATEWAY);
                job.setExtraData(JobService.EXTRA_DATA_PORTAL_IG);
            }
            else if (withEcmService && withIgService){
                checkPlatformExistance(instanceClusterTypes, ClusterService.CLUSTER_TYPE_ECM);
                job.setExtraData(JobService.EXTRA_DATA_PORTAL_IG_ECM);
            }
            else job.setExtraData(JobService.EXTRA_DATA_PORTAL);       
        }
        
        if ("true".equals(pm.get("mq"))){
            checkPlatformExistance(instanceClusterTypes, ClusterService.CLUSTER_TYPE_MULE_MQ);
            job.addService(ClusterService.SERVICE_NAME[ClusterService.CLUSTER_TYPE_MULE_MQ],
                           pm.get("mqclustersize"),
                           pm.get("mqmachinesize"),
                           pm.get("mqimagetype"),
                           pm.get("mqesbvolumesize"));
        }
        
        if("true".equals(pm.get("bas"))){ 
            checkPlatformExistance(instanceClusterTypes, ClusterService.CLUSTER_TYPE_BAS);
            job.addService(ClusterService.SERVICE_NAME[ClusterService.CLUSTER_TYPE_BAS],
                           pm.get("basclustersize"),
                           pm.get("basmachinesize"),
                           pm.get("basimagetype"),
                           pm.get("basesbvolumesize"));
        }
        
        if(withIgService){
            checkPlatformExistance(instanceClusterTypes, ClusterService.CLUSTER_TYPE_IDENTITY_GATEWAY);
            job.addService(ClusterService.SERVICE_NAME[ClusterService.CLUSTER_TYPE_IDENTITY_GATEWAY], 
                           pm.get("igclustersize"),
                           pm.get("igmachinesize"),
                           pm.get("igimagetype"),
                           pm.get("igesbvolumesize"));
        }
        
        if("true".equals(pm.get("ee"))){ 
            checkPlatformExistance(instanceClusterTypes, ClusterService.CLUSTER_TYPE_EE);
            job.addService(ClusterService.SERVICE_NAME[ClusterService.CLUSTER_TYPE_EE],
                           pm.get("eeclustersize"), 
                           pm.get("eemachinesize"),
                           pm.get("eeimagetype"),
                           pm.get("eeesbvolumesize"));
        }
        
        if(withEcmService){ 
            checkPlatformExistance(instanceClusterTypes, ClusterService.CLUSTER_TYPE_ECM);
            job.addService(ClusterService.SERVICE_NAME[ClusterService.CLUSTER_TYPE_ECM], 
                           pm.get("ecmclustersize"), 
                           pm.get("ecmmachinesize"),
                           pm.get("ecmimagetype"),
                           pm.get("ecmesbvolumesize"));
        }
        
        return job;
    }
}