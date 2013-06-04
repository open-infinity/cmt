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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.openinfinity.cloud.domain.Cluster;
import org.openinfinity.cloud.domain.Key;
import org.openinfinity.cloud.domain.Machine;
import org.openinfinity.cloud.service.administrator.ClusterService;
import org.openinfinity.cloud.service.administrator.EC2Wrapper;
import org.openinfinity.cloud.service.administrator.InstanceService;
import org.openinfinity.cloud.service.administrator.KeyService;
import org.openinfinity.cloud.service.administrator.MachineService;
import org.openinfinity.cloud.util.AdminException;
import org.openinfinity.cloud.util.LiferayService;
import org.openinfinity.cloud.util.serialization.JsonDataWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.portlet.bind.annotation.ResourceMapping;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.Reservation;

/**
 * Controller for handling Machine related CloudAdmin requests
 * @author Ossi Hämäläinen
 * @author Juha-Matti Sironen
 * @version 1.0.0 Initial version
 * @since 1.0.0
 */

@Controller(value="machineController")
@RequestMapping(value = "VIEW")
public class MachineController {
	private static final Logger LOG = Logger.getLogger(MachineController.class.getName());

    @Autowired
    @Qualifier("liferayService")
    private LiferayService liferayService;

	@Autowired
	@Qualifier("machineService")
	private MachineService machineService;
	
	@Autowired
	@Qualifier("clusterService")
	private ClusterService clusterService;
	
	@Autowired
	@Qualifier("instanceService")
	private InstanceService instanceService;
	
	@Autowired
	@Qualifier("keyService")
	private KeyService keyService;
	
	@Autowired
	@Qualifier("cloudCredentials")
	private AWSCredentials eucaCredentials;
	
	@Value("${endpoint}")
	private String ec2EndPoint;
	
	@ResourceMapping("machineList")
	public void getMachines(ResourceRequest request, ResourceResponse response, @RequestParam("page") int page, @RequestParam("rows") int rows, @RequestParam("instanceId") int instanceId) throws Exception {
		LOG.info("Inside getMachineList in the controller, page: "+page+", rows: "+rows +", instanceId: " +instanceId);
		if (liferayService.getUser(request, response) == null) return;
		
		org.openinfinity.cloud.domain.Instance toasInstance = instanceService.getInstance(instanceId);
		if(toasInstance == null) {
			JsonDataWrapper jdw = new JsonDataWrapper(page, 0, 0, new ArrayList<Machine>());
			ObjectMapper mapper = new ObjectMapper();
			try {
				mapper.writeValue(response.getWriter(), jdw);
			} catch (Exception e) {
				
			}
			return;
		}
		EC2Wrapper ec2 = new EC2Wrapper();
		if(toasInstance.getCloudType() == InstanceService.CLOUD_TYPE_AMAZON) {
			ec2.setEndpoint(ec2EndPoint);
			ec2.init(eucaCredentials, toasInstance.getCloudType());
		} else if(toasInstance.getCloudType() == InstanceService.CLOUD_TYPE_EUCALYPTUS) {
			ec2.setEndpoint(ec2EndPoint);
			ec2.init(eucaCredentials, toasInstance.getCloudType());
		} 
		
		int offset = (page -1) * rows;
		Collection<Machine> machines = machineService.getMachines(offset, rows, instanceId);
		updateMachines(ec2, machines);
		machines = machineService.getMachines(offset, rows, instanceId);
		int records = machineService.getNumberOfMachines(instanceId);
		int jaannos = records % rows;
		int pages = records/rows;
		if(jaannos > 0) {
			pages++;
		}
		JsonDataWrapper jdw = new JsonDataWrapper(page, pages, records, machines);
		ObjectMapper mapper = new ObjectMapper();
		try {
			mapper.writeValue(response.getWriter(), jdw);
		} catch (Exception e) {
			
		} 
	}
	
	@ResourceMapping("keyList")
	public void getKeys(ResourceRequest request, ResourceResponse response) throws IOException {
		LOG.info("Inside getKeylist in the machine controller");
		if (liferayService.getUser(request, response) == null) return;
		List<Key> keyList = keyService.getKeys();
		ObjectMapper mapper = new ObjectMapper();
		try {
			mapper.writeValue(response.getWriter(), keyList);
		} catch (Exception e) {
			
		}
	}
	
	@ResourceMapping("machine")
	public void getMachine(ResourceRequest request, ResourceResponse response, @RequestParam("id") int id) throws IOException {
		if (liferayService.getUser(request, response) == null) return;
		LOG.info("Machine id: "+id);
		Machine machine = machineService.getMachine(id);
		
		if(machine != null) {
			ObjectMapper mapper = new ObjectMapper();
			try {
				mapper.writeValue(response.getWriter(), machine);
			} catch (Exception e) {
				
			}
		}
	}
	
	@ResourceMapping("terminateMachine")
	public void terminateMachine(ResourceRequest request, ResourceResponse response, @RequestParam("id") int id) throws Exception {
		if (liferayService.getUser(request, response) == null) return;
		LOG.info("Terminating machine id: "+id);
		Machine machine = machineService.getMachine(id);
		if(machine == null) {
			LOG.error("Can't find machine "+id);
			return;
		}
		org.openinfinity.cloud.domain.Instance toasInstance = instanceService.getInstanceByMachineId(id);
		
		EC2Wrapper ec2 = new EC2Wrapper();
		if(toasInstance.getCloudType() == InstanceService.CLOUD_TYPE_AMAZON) {
			ec2.setEndpoint(ec2EndPoint);
			ec2.init(eucaCredentials, toasInstance.getCloudType());
		} else if(toasInstance.getCloudType() == InstanceService.CLOUD_TYPE_EUCALYPTUS) {
			ec2.setEndpoint(ec2EndPoint);
			ec2.init(eucaCredentials, toasInstance.getCloudType());
		}
		
		
		if(machine != null) {
			if(machine.getClusterId() != 0) {
				LOG.info("Cluster id is available, getting cluster information");
				Cluster cluster = clusterService.getCluster(machine.getClusterId());
				if(cluster != null) {
					LOG.info("Cluster "+cluster.getName()+" found");
					com.amazonaws.services.elasticloadbalancing.model.Instance instance = new com.amazonaws.services.elasticloadbalancing.model.Instance();
					ArrayList<com.amazonaws.services.elasticloadbalancing.model.Instance> instanceList = new ArrayList<com.amazonaws.services.elasticloadbalancing.model.Instance>();
					instance.setInstanceId(machine.getInstanceId());
					instanceList.add(instance);
					ec2.deregisterInstancesToLoadBalancer(instanceList, cluster.getLbName());
					cluster.setNumberOfMachines(cluster.getNumberOfMachines()-1);
					clusterService.updateCluster(cluster);
				} else {
					LOG.info("Cluster information not found, proceeding directly to termination");
				}
				ec2.terminateInstance(machine.getInstanceId());
				LOG.info("Machine "+machine.getInstanceId()+" terminated.");
			}
		}
	}
	
	private void updateMachines(EC2Wrapper ec2, Collection<Machine> machines) throws AdminException {
		ArrayList<String> instanceList = new ArrayList<String>();
		Iterator<Machine> i = machines.iterator();
		while(i.hasNext()) {
			Machine m = i.next();
			instanceList.add(m.getInstanceId());
		}
		Collection<Reservation> resList = ec2.describeInstances(instanceList);
		if (resList != null) {
			LOG.info("Describe returned "+resList.size()+" reservations");
			Iterator<Reservation> r = resList.iterator();
			while (r.hasNext()) {
				Reservation tempR = r.next();
				
				List<Instance> iList = tempR.getInstances();
				LOG.info("This reservation has "+iList.size()+" instances");
				Iterator<Instance> ir = iList.iterator();
				while (ir.hasNext()) {
					Instance instance = ir.next();
					instanceList.remove(instance.getInstanceId());
					Machine machine = machineService.getMachine(instance.getInstanceId());
					if (machine != null) {
						machine.setDnsName(instance.getPublicDnsName());
						machine.setState(instance.getState().getName());
						LOG.info("Machine status: "+machine.getState());
						if(machine.getState().equals("terminated")) {
							LOG.info("Removing machine "+machine.getId());
							machineService.removeMachine(machine.getId());
						} else {
							LOG.info("Updating machine "+machine.getId());
							machineService.updateMachine(machine);
						}
					} else {
						LOG.info("Machine not found: "+instance.getInstanceId());
					}
				}
			}
		} else {
			LOG.info("Describe returnet null");
			i = machines.iterator();
			while(i.hasNext()) {
				Machine m = i.next();
				Instance juttuI = ec2.describeInstance(m.getInstanceId());
				if(juttuI == null) {
					machineService.removeMachine(m.getId());
				}
			}
		}
		if(instanceList.size() > 0) {
			Iterator<String> il = instanceList.iterator();
			while(il.hasNext()) {
				String iid = il.next();
				LOG.info("Deleting machine with instanceId: "+iid);
				machineService.removeMachine(iid);
			}
		}
	}
	
}
