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
package org.openinfinity.cloud.service.healthmonitoring;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.client.HttpClient;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.openinfinity.cloud.domain.*;
import org.openinfinity.cloud.service.administrator.ClusterService;
import org.openinfinity.cloud.service.administrator.MachineService;
import org.openinfinity.cloud.util.http.HttpHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 
 * @author Ivan Bilechyk
 * @author Ilkka Leinonen
 * @author Timo Saarinen
 * @author Nishant Gupta
 * @author Vedran Bartonicek
 */
@Service("healthMonitoringService")
public class HealthMonitoringServiceImpl implements HealthMonitoringService {

	@Autowired
	private ClusterService clusterService;
	
	@Autowired
	private MachineService machineService;

    @Autowired
    private HttpClient client;

    @Value("${connTimeout}")
	private String connTimeout;
   
	private static final int RRD_PORT = 8181;
	private static final int RRA_STEP = 10;
	private static final String CONTEXT_PATH = "monitoring";
	private static final String PROTOCOL = "http://";
	private static final String HM_GROUPNAME_PREFIX = "cluster_";
    
	private Map<Integer,String> clusterMasterMap = new ConcurrentHashMap<Integer,String>();
	private List<Machine> badMachines = new ArrayList<Machine>();
	private List <Cluster> badClusters = new ArrayList<Cluster>();
    private Map<String,String> groupMachineMap = new HashMap<String,String>();
    private Map<String,String> hostnameIpMap = new HashMap<String,String>();
    private long previousClusterCheckTime = 0L;
    
    // Nishant: Removed the autowired bean and create dynamically based on cluster content
    //    @Autowired
    //    private RequestBuilder requestBuilder;
    // FIXME: it is wasteful to every time create a new RequestBuilder -> use again the autowired
    
    private static final Logger LOG = Logger.getLogger(HealthMonitoringServiceImpl.class.getName());


    @Override
    public NodeListResponse getHostList() {	
    	List<Node> activeNodes = new ArrayList<Node>();
    	List<Node> inactiveNodes = new ArrayList<Node>();
    	client.getParams().setIntParameter("http.connection.timeout", new Integer(this.connTimeout)); 	
    	NodeListResponse finalNodeResponse = new NodeListResponse();
    	Collection<Cluster> clusters = clusterService.getClusters();
    	long timeElapsed = (System.currentTimeMillis() - previousClusterCheckTime)/1000;
    	for (Cluster cluster : clusters) {
    		if(!badClusters.contains(cluster) || timeElapsed >= 120) {
    			int clusterId = cluster.getId();
    			Collection<Machine> machinesInCluster = machineService.getMachinesInCluster(clusterId);
    			for(Machine machine : machinesInCluster) {
    				if(!badMachines.contains(machine)) {
    					List<Node> allNodes = new ArrayList<Node>();
    					String url = getRequestBuilder(machine.getDnsName()).buildHostListRequest(new Request());
    					String response = HttpHelper.executeHttpRequest(client, url);
    					LOG.debug("Request for machine "+machine.getDnsName()+" = " +url);
    					LOG.debug("getHostList response for machine "+machine.getDnsName()+" = " +response);
    					NodeListResponse nodeResponse = toObject(response, NodeListResponse.class);
    					if(nodeResponse != null && nodeResponse.getResponseStatus() == AbstractResponse.STATUS_OK) {
    						if (nodeResponse.getActiveNodes() != null) {
    							activeNodes.addAll(nodeResponse.getActiveNodes());
    							allNodes.addAll(nodeResponse.getActiveNodes());
    						}
    						if (nodeResponse.getInactiveNodes() != null) {
    							inactiveNodes.addAll(nodeResponse.getInactiveNodes());
    							allNodes.addAll(nodeResponse.getInactiveNodes());
    						}
    						clusterMasterMap.put(clusterId,machine.getDnsName());
    						badMachines.remove(machine);
    						badClusters.remove(cluster);
    						for(Node node : allNodes) { 
    							if (!groupMachineMap.containsKey(node.getGroupName()))
    								groupMachineMap.put(node.getGroupName(),machine.getDnsName());
    							hostnameIpMap.put(node.getNodeName(), machine.getDnsName());
    						}                   
    						break;
    					} else {
    						LOG.debug("Machine:"+machine.getDnsName()+" is not responding");
    						badMachines.add(machine);
    						clusterMasterMap.remove(clusterId);
    					}
    				}
    			}
    			if(badMachines.containsAll(machinesInCluster)) {
    				badMachines.removeAll(machinesInCluster);
    				badClusters.add(cluster);
    				LOG.debug("Cluster:"+clusterId+"(Name:"+cluster.getName()+"LBDNS:"+cluster.getLbDns()+") is not responding");
    			}
    			if(badClusters.contains(cluster)) {
    				previousClusterCheckTime = System.currentTimeMillis();
    				LOG.debug("Checking for bad cluster:"+clusterId+"(Name:"+cluster.getName()+"LBDNS:"+cluster.getLbDns()+") at "+new Date());
    			}
    		}
    	}
    	finalNodeResponse.setActiveNodes(activeNodes);
    	finalNodeResponse.setInactiveNodes(inactiveNodes);
    	if (finalNodeResponse.getActiveNodes() != null) {
    		Collections.sort(finalNodeResponse.getActiveNodes());
    	}
    	if (finalNodeResponse.getInactiveNodes() != null) {
    		Collections.sort(finalNodeResponse.getInactiveNodes());
    	}
    	LOG.debug("getHostList exit");
    	return finalNodeResponse;
    }
    
    @Override
    public MetricTypesResponse getMetricTypes(Request request) {
        MetricTypesResponse response = null;
        if (StringUtils.isNotBlank(request.getSourceName())) {
    		String sourceName = "";
        	if(groupMachineMap.get(request.getSourceName()) != null){
        		sourceName = groupMachineMap.get(request.getSourceName());
        	} else {
        		sourceName = hostnameIpMap.get(request.getSourceName());
        	}        		
        	
            String url = getRequestBuilder(sourceName).buildMetricTypesRequest(request);
            String responseStr = HttpHelper.executeHttpRequest(client, url);
            response = toObject(responseStr, MetricTypesResponse.class);
            if (response.getMetricTypes() != null) {
                Collections.sort(response.getMetricTypes());
            }
    		LOG.debug("Request for machine "+url);
        } else {
        	response = new MetricTypesResponse();
            response.setResponseStatus(AbstractResponse.STATUS_PARAM_FAIL);
        }

        LOG.debug("Returning final MetricTypesResponse = " +response.getMetricTypes());
        return response;
    }

    @Override
    public MetricNamesResponse getMetricNames(Request request) {
        MetricNamesResponse response = null;
        if (StringUtils.isNotBlank(request.getSourceName()) && StringUtils.isNotBlank(request.getMetricType())) {
    		String sourceName = "";
        	if(groupMachineMap.get(request.getSourceName()) != null){
        		sourceName = groupMachineMap.get(request.getSourceName());
        	} else {
        		sourceName = hostnameIpMap.get(request.getSourceName());
        	}  
        	
            String url = getRequestBuilder(sourceName).buildMetricNamesRequest(request);
            String responseStr = HttpHelper.executeHttpRequest(client, url);
            response = toObject(responseStr, MetricNamesResponse.class);
            if (response.getMetricNames() != null) {
                Collections.sort(response.getMetricNames());
            }
    		LOG.debug("Request for machine "+url);
        } else {
            response = new MetricNamesResponse();
            response.setResponseStatus(AbstractResponse.STATUS_PARAM_FAIL);
        }
        LOG.debug("Returning final MetricNamesResponse = " +response.getMetricNames());
        return response;
    }

    @Override
    public HealthStatusResponse getHealthStatus(String sourceName, String sourceType, String metricType,
            String[] metricNames, Date startTime, Date endTime) {
        HealthStatusResponse response = null;
        if (StringUtils.isNotBlank(sourceName) && StringUtils.isNotBlank(metricType)
                && ArrayUtils.isNotEmpty(metricNames)) {
        	String hostName = "";
        	if(groupMachineMap.get(sourceName)  != null ){
        		hostName = groupMachineMap.get(sourceName);
        	} else {
        		hostName = hostnameIpMap.get(sourceName);
        	}  	
        	String url = getRequestBuilder(hostName).buildHealthStatusRequest(
        	new Request(sourceName, sourceType, metricType,
                            metricNames, startTime, endTime, 100L));
            String responseStr = HttpHelper.executeHttpRequest(client, url);
            response = toObject(responseStr, HealthStatusResponse.class);
    		LOG.debug("Request for machine "+url);
        } else {
            response = new HealthStatusResponse();
            response.setResponseStatus(AbstractResponse.STATUS_PARAM_FAIL);
        }
        return response;
    }
    
    // FIXME: this function needs refactoring  - there is lot of repeating code around.
    @Override
    public HealthStatusResponse getClusterHealthStatus(Machine machine, String metricType, String[] metricNames, Date date) {
        HealthStatusResponse response = null;
        if (StringUtils.isNotBlank(metricType) && ArrayUtils.isNotEmpty(metricNames) && machine != null) { 
            String groupName = HM_GROUPNAME_PREFIX + machine.getClusterId();
            String url = getRequestBuilder(machine.getDnsName()).buildHealthStatusRequest(
                new Request(groupName, Request.SOURCE_GROUP, metricType, metricNames, date, date, 1L));
            String responseStr = HttpHelper.executeHttpRequest(client, url);
            
            // FIXME: handling in case that response is of type error
            response = toObject(responseStr, HealthStatusResponse.class);
            
            LOG.debug("Request for machine "+url);
        } else {
            response = new HealthStatusResponse();
            response.setResponseStatus(AbstractResponse.STATUS_PARAM_FAIL);
        }
        return response;
    }
    
    @Override
    public HealthStatusResponse getLatestAvailabaleClusterHealthStatus(Machine machine, String metricType, String[] metricNames, Date date) {
        HealthStatusResponse response = null;

        // Validate input parameters
        if (StringUtils.isNotBlank(metricType) && ArrayUtils.isNotEmpty(metricNames) && machine != null && machine.getClusterId() > 0) {

            // Form request
            String groupName = HM_GROUPNAME_PREFIX + machine.getClusterId();
            String url = getRequestBuilder(machine.getDnsName()).buildLastHealthStatusRequest( new Request(groupName, Request.SOURCE_GROUP, metricType, metricNames, date, date, RRA_STEP));
            LOG.debug("Request for machine " + url);

            // Execute request
            String responseStr = HttpHelper.executeHttpRequest(client, url);

            // Handle response
            if (responseStr == null) {
                response = new HealthStatusResponse();
                response.setResponseStatus(AbstractResponse.STATUS_NODE_FAIL);
            }
            else {
                response = toObject(responseStr, HealthStatusResponse.class);
                response.setResponseStatus(AbstractResponse.STATUS_OK);
            }

        } else {
            response = new HealthStatusResponse();
            response.setResponseStatus(AbstractResponse.STATUS_PARAM_FAIL);
        }
        return response;
    }

    @Override
    public float getClusterLoad(Machine machine, String[] metricName, String metricType, String period){

        // Ask for Group health status from http-rrd server
        HealthStatusResponse response = getLatestAvailabaleClusterHealthStatus(machine, metricType, metricName, new Date());

        // If response status is OK, extract load
        if (response.getResponseStatus() == AbstractResponse.STATUS_OK){
            List<HealthStatusResponse.SingleHealthStatus> metrics = response.getMetrics();
            if (metrics.size() > 0){
                Map<String, List<RrdValue>> values = metrics.get(0).getValues();
                List<RrdValue> loadRrd = values.get(period);
                if (loadRrd != null){
                    return loadRrd.get(0).getValue().floatValue();
                }
            }
        }

        // Load not available
        return -1;
    }

    @Override
    public String getHealthStatus() {
        throw new UnsupportedOperationException("Method is not implemented yet");
    }

    private <T extends AbstractResponse> T toObject(String source, Class<T> expectedType) {
        if (source == null || StringUtils.isEmpty(source)) {
            T newInstance = null;
            try {
                newInstance = expectedType.newInstance();
                newInstance.setResponseStatus(AbstractResponse.STATUS_RRD_FAIL);
            } catch (InstantiationException e) {
                LOG.error(e.getMessage(), e);
            } catch (IllegalAccessException e) {
                LOG.error(e.getMessage(), e);
            }
            return newInstance;
        }
        ObjectMapper mapper = new ObjectMapper();
        T obj = null;
        try {
            //LOG.debug("================================================");
            LOG.debug("Now: " +  System.currentTimeMillis() + " source:" + source);
            //LOG.debug("================================================");
            obj = mapper.readValue(source, expectedType);
        } catch (Exception e) {
            LOG.error("Exception occurred while converting from String to Object. ", e);
        }

        return obj;
    }

    @Override
    public MetricBoundariesResponse getMetricBoundaries(String sourceName,String metricType) {
        MetricBoundariesResponse response = null;
        if (StringUtils.isNotBlank(metricType)) {
        	String hostName = "";
			// Nishant : Create request builder object dynamically for the machine or group for which metric boundaries is required.
			// If the souce name is present in groupMachine map then the request is for a group and the HTTP request will be 
			// sent to the corresponding machine.
			//****************************************************       
        	if(groupMachineMap.get(sourceName)  != null ){
        		hostName = groupMachineMap.get(sourceName);
        	} else {
        		hostName = hostnameIpMap.get(sourceName);
        	}  
        	String url = "";
        	url = getRequestBuilder(hostName).buildMetricBoundariesRequest(new Request(sourceName, metricType));
            String responseStr = HttpHelper.executeHttpRequest(client, url);
            response = toObject(responseStr, MetricBoundariesResponse.class);
    		LOG.debug("Request for machine "+url);
        } else {
            response = new MetricBoundariesResponse();
            response.setResponseStatus(AbstractResponse.STATUS_PARAM_FAIL);
        }
        LOG.debug("Returning final getMetricBoundaries = " +response.getBoundaries());
        return response;
    }

    @Override
    public GroupListResponse getGroupList() {
    	GroupListResponse finalGroupListResponse = new GroupListResponse();
		finalGroupListResponse.setGroups(new HashMap<String, Set<String>>());

		Collection<String> masterMachines =new ArrayList<String>();
        for (String machine:clusterMasterMap.values()){
            masterMachines.add(machine);
        }
        
		for(String sourceName : masterMachines) {
	        String url = getRequestBuilder(sourceName).buildGroupListRequest(new Request());
	        String response = HttpHelper.executeHttpRequest(client, url);
			
	        GroupListResponse groupListResponse = toObject(response, GroupListResponse.class);
	        if(groupListResponse.getGroups() != null)
	        	finalGroupListResponse.getGroups().putAll(groupListResponse.getGroups());
			LOG.debug("Request for machine "+url);
		}
		LOG.debug("Returning final GroupListResponse = " +finalGroupListResponse.getGroups());
		if(finalGroupListResponse.getGroups().isEmpty()) {
			finalGroupListResponse.setResponseStatus(AbstractResponse.STATUS_PARAM_FAIL);
			return finalGroupListResponse;
		}
		return finalGroupListResponse;
    }

    @Override
    public NotificationResponse getNotifications(Long startTime, Long endTime) {
    	NotificationResponse finalNotificationResponse = new NotificationResponse();
    	List<Notification> notifications = new ArrayList<Notification>(); 	
    	finalNotificationResponse.setNotifications(notifications);
    	
    	Collection<String> masterMachines=new ArrayList<String>();
        for (String machine:clusterMasterMap.values()){
            masterMachines.add(machine);
        }
		for(String sourceName : masterMachines) {
	    	// Nishant : Create request builder object dynamically for the machine for which notifications are required.
			//****************************************************
	        String url = getRequestBuilder(sourceName).buildNotificationsRequest(startTime, endTime);
	        String response = HttpHelper.executeHttpRequest(client, url);
			LOG.debug("Request for machine "+url);
			LOG.debug("Returning NotificationResponse = " +response);    
	        NotificationResponse notificationResponse = toObject(response, NotificationResponse.class);
	        if(notificationResponse.getNotifications() != null)
	        	finalNotificationResponse.getNotifications().addAll(notificationResponse.getNotifications());
		}
		
		if (finalNotificationResponse.getNotifications().isEmpty()) {
			finalNotificationResponse.setResponseStatus(AbstractResponse.STATUS_PARAM_FAIL);
		}
		Comparator<? super Notification> notificationsComparator = new Comparator<Notification>() {
			@Override
			public int compare(Notification n1, Notification n2) {
				// TODO Auto-generated method stub
				return n1.getFileModificationTime().compareTo(n2.getFileModificationTime());
			}			
		};
		Collections.sort(finalNotificationResponse.getNotifications(), notificationsComparator);
		Collections.reverse(finalNotificationResponse.getNotifications());
		return finalNotificationResponse;
    }
    
    private RequestBuilder getRequestBuilder(String sourceName) {  
		RequestBuilder requestBuilder = new RequestBuilder();
		requestBuilder.setHostName(sourceName);
		requestBuilder.setPort(RRD_PORT);
		requestBuilder.setContextPath(CONTEXT_PATH);
		requestBuilder.setProtocol(PROTOCOL);
		return requestBuilder;
    } 
    
    public Map<Integer,String> getClusterMasterMap(){
        return clusterMasterMap; 
    }

}
