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

package org.openinfinity.cloud.autoscaler.periodicscaler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.openinfinity.cloud.domain.Cluster;
import org.openinfinity.cloud.domain.Machine;
import org.openinfinity.cloud.service.administrator.ClusterService;
import org.openinfinity.cloud.service.administrator.MachineService;
import org.openinfinity.core.exception.SystemException;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Reader interface for reading cluster's capacity state.
 * 
 * @author Ilkka Leinonen
 * @author Vedran Bartonicek
 * @version 1.0.0
 * @since 1.0.0
 */
@Component("periodicScalerItemReader-not-in-use")
public class PeriodicScalerItemReader implements ItemReader<Machine> {
	private static final Logger LOG = Logger.getLogger(PeriodicScalerItemReader.class.getName());
	
	@Autowired
	ClusterService clusterService;
	
	@Autowired 
	MachineService machineService;
	
	private int index = 0;
	
	private List<Machine> loadBalancerMachines;

	/**
	 * Reads next record from input
	 */
	public Machine read() throws Exception {
		loadBalancerMachines = loadLoadBalancerMachines();
		LOG.error("read() enter");
		if (loadBalancerMachines.isEmpty())
			loadBalancerMachines = loadLoadBalancerMachines();
		if (index < loadBalancerMachines.size()) {
			LOG.error("index:  " + Integer.toString(index));
			LOG.error("returning:  " + loadBalancerMachines.get(index).getInstanceId());
			return loadBalancerMachines.get(index++);
		}		
		else {
			index = 0;
			return null;
		}	
	}
	
	private List<Machine> loadLoadBalancerMachines() {
		LOG.debug("loadLoadBalancerMachines: enter");
		List<Machine> loadBalancers = new ArrayList<Machine>();
		Collection<Cluster> clusters = clusterService.getClusters();
		for (Cluster cluster : clusters) {
			int clusterType = cluster.getType();
			LOG.debug("entering loop for cluster name = " + cluster.getName() + " type = " + Integer.toString(clusterType));
			if (clusterType == ClusterService.CLUSTER_TYPE_BAS||
				clusterType == ClusterService.CLUSTER_TYPE_MULE_MQ ||
				clusterType == ClusterService.CLUSTER_TYPE_PORTAL){
				String lbInstanceId = cluster.getLbInstanceId();
				Machine machine = machineService.getMachine(lbInstanceId);
				if (machine == null) 
					throw new SystemException("Machine fetching failed.");
				LOG.debug("Got machine with lbInstanceId =  " + machine.getName());
				loadBalancers.add(machine);		
			}
		}
		return loadBalancers;
	}	
}
