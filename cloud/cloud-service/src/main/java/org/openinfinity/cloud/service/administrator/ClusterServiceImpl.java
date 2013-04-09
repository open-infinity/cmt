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

package org.openinfinity.cloud.service.administrator;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.openinfinity.cloud.domain.Cluster;
import org.openinfinity.cloud.domain.Machine;
import org.openinfinity.cloud.domain.repository.administrator.AuthorizedRoutingRepository;
import org.openinfinity.cloud.domain.repository.administrator.ClusterRepository;
import org.openinfinity.cloud.domain.repository.administrator.MachineRepository;
import org.openinfinity.cloud.domain.repository.administrator.MulticastAddressRepository;
import org.openinfinity.cloud.domain.repository.scaling.ScalingRuleRepository;
import org.openinfinity.core.annotation.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * Cluster service interface implementation for building clusters inside a cloud environment.
 * 
 * @author Ilkka Leinonen
 * @author Ossi Hämäläinen
 * @author Juha-Matti Sironen
 * @version 1.0.0 Initial version
 * @since 1.0.0
 */
@Service("clusterService")
public class ClusterServiceImpl implements ClusterService {
	
	@Autowired
	@Qualifier("clusterRepository")
	ClusterRepository clusterRepository;
	
	@Autowired
	@Qualifier("machineRepository")
	MachineRepository machineRepository;
	
	@Autowired
	private ScalingRuleRepository scalingRuleRepository;

	@Autowired
	private AuthorizedRoutingRepository authorizedRoutingRepository;
	
	@Autowired
	private MulticastAddressRepository multicastAddressRepository;
	
	
	@Log
	public void addCluster(Cluster cluster) {
		clusterRepository.addCluster(cluster);
	}
	
	@Log
	public void deleteCluster(Cluster cluster) {
		int clusterId = cluster.getId();
		clusterRepository.deleteCluster(cluster);
		scalingRuleRepository.deleteByClusterId(clusterId);
		authorizedRoutingRepository.deleteClusterIPs(clusterId);
		authorizedRoutingRepository.deleteAllUserAuthorizedIPsFromCluster(clusterId);
		multicastAddressRepository.deleteMulticastAddressForCluster(clusterId);
	}
	
	@Log
	public Cluster getCluster(int id) {
		return clusterRepository.getCluster(id);
	}

	@Log
	public Collection<Cluster> getClusters() {
		return Collections.unmodifiableCollection(clusterRepository.getClusters());
	}

	@Log
	public Collection<Cluster> getClusters(int instanceId) {
		return Collections.unmodifiableCollection(clusterRepository.getClusters(instanceId));
	}
	
	@Log
	public void updatePublished(int id, int published) {
		clusterRepository.updatePublished(id, published);
	}
	
	@Log
	public void updateCluster(Cluster cluster) {
		clusterRepository.updateCluster(cluster);
	}

	@Log
	public Machine getClustersLoadBalancer(int clusterId) {
		List<Machine> machinesInCluster = machineRepository.getMachinesInCluster(clusterId);
		if(machinesInCluster == null) {
			return null;
		}
		for(Machine m : machinesInCluster) {
			if(m.getType().equals("loadbalancer")) {
				return m;
			}
		}
		return null;
	}

	@Override
	public Cluster getClusterByLoadBalancerId(int loadBalancerId) {
		return clusterRepository.getClusterByLoadBalancerId(loadBalancerId);
	}

	@Override
	public Cluster getClusterByClusterId(int id) {
		return clusterRepository.getCluster(id);
	}
	
}