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

package org.openinfinity.cloud.application.worker.component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.openinfinity.cloud.application.worker.Updater;
import org.openinfinity.cloud.domain.Cluster;
import org.openinfinity.cloud.domain.Machine;
import org.openinfinity.cloud.service.administrator.ClusterService;
import org.openinfinity.cloud.service.administrator.EC2Wrapper;
import org.openinfinity.cloud.service.administrator.InstanceService;
import org.openinfinity.cloud.service.administrator.MachineService;
import org.openinfinity.cloud.util.PropertyManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.Reservation;

/**
 * Machine updater Component
 * @author Ossi Hämäläinen
 * @version 1.0.0 Initial version
 * @since 1.0.0
 */

@Component("MachineUpdater")
public class MachineUpdater implements Updater {
	private static final Logger LOG = Logger.getLogger(MachineUpdater.class.getName());
	
	@Autowired
	@Qualifier("machineService")
	private MachineService machineService;
	
	@Autowired
	@Qualifier("clusterService")
	private ClusterService clusterService;
	
	@Autowired
	@Qualifier("cloudCredentials")
	AWSCredentials eucaCredentials;
	
	@Autowired
	@Qualifier("amazonCredentials")
	private AWSCredentials amazonCredentials;
	
	@Async
	public void update(Collection<Machine> mList, int cloud) {
		String threadName = Thread.currentThread().getName();
		LOG.info("Machine updater job starting on thread "+threadName);
		ArrayList<String> instanceList = new ArrayList<String>();
		Iterator<Machine> i = mList.iterator();
		while(i.hasNext()) {
			Machine m = i.next();
			instanceList.add(m.getInstanceId());
		}
		
		EC2Wrapper ec2 = new EC2Wrapper();
		LOG.debug(threadName + ": Got credentials, initing ec2 connection");
		try {
			if (cloud == InstanceService.CLOUD_TYPE_AMAZON) {
				String endPoint = PropertyManager.getProperty("cloudadmin.worker.amazon.endpoint");
				ec2.setEndpoint(endPoint);
				ec2.init(amazonCredentials, cloud);
			} else if (cloud == InstanceService.CLOUD_TYPE_EUCALYPTUS) {
				String endPoint = PropertyManager.getProperty("cloudadmin.worker.eucalyptus.endpoint");
				ec2.setEndpoint(endPoint);
				ec2.init(eucaCredentials, cloud);
			}

		} catch (Exception e) {
			LOG.error(threadName + ": Error initializin ec2 connection");
			return;
		}
		try {
			Collection<Reservation> resList = ec2.describeInstances(instanceList);
			if (resList != null) {
				LOG.info(threadName+": Describe returnet "+resList.size()+" reservations");
				Iterator<Reservation> r = resList.iterator();
				while (r.hasNext()) {
					Reservation tempR = r.next();
					
					List<Instance> iList = tempR.getInstances();
					LOG.info(threadName+": This reservation has "+iList.size()+" instances");
					Iterator<Instance> ir = iList.iterator();
					while (ir.hasNext()) {
						Instance instance = ir.next();
						instanceList.remove(instance.getInstanceId());
						Machine machine = machineService.getMachine(instance.getInstanceId());
						if (machine != null) {
							machine.setDnsName(instance.getPublicDnsName());
							machine.setPrivateDnsName(instance.getPrivateDnsName());
							machine.setState(instance.getState().getName());
							if(machine.getType() != null && machine.getType().equalsIgnoreCase("loadbalancer")) {
								Cluster cluster = clusterService.getCluster(machine.getClusterId());
								if(cluster != null) {
									cluster.setLbDns(instance.getPublicDnsName());
									clusterService.updateCluster(cluster);
								}
								
							}
							LOG.info(threadName+": Machine status: "+machine.getState());
							if(machine.getState().equals("terminated")) {
								LOG.info(threadName+": Removing machine "+machine.getId());
								machineService.removeMachine(machine.getId());
							} else {
								LOG.info(threadName+": Updating machine "+machine.getId());
								machineService.updateMachine(machine);
							}
						} else {
							LOG.info(threadName+": Machine not found: "+instance.getInstanceId());
						}
					}
				}
			} else {
				LOG.debug(threadName+": Describe returnet null");	
				i = mList.iterator();
				while(i.hasNext()) {
					Machine m = i.next();
					Instance juttuI = ec2.describeInstance(m.getInstanceId());
					if(juttuI == null) {
						LOG.info(threadName+": Deleting machine "+m.getId()+" ("+m.getInstanceId()+")");
						machineService.removeMachine(m.getId());
					}
				} 
			}
			if(instanceList.size() > 0) {
				Iterator<String> il = instanceList.iterator();
				while(il.hasNext()) {
					String iid = il.next();
					LOG.info(threadName+": Deleting machine with instanceId: "+iid);
					machineService.removeMachine(iid);
				}
			}
			
		} catch (Exception e) {
			LOG.error(threadName+": Error updating machines: "+e.getMessage());
		}
	}

}
