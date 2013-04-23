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

package org.openinfinity.cloud.application.deployer.batch;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.openinfinity.cloud.domain.Deployment;
import org.openinfinity.cloud.domain.DeploymentStatus;
import org.openinfinity.cloud.domain.DeploymentStatus.DeploymentState;
import org.openinfinity.cloud.domain.Instance;
import org.openinfinity.cloud.domain.Machine;
import org.openinfinity.cloud.domain.MachineType;
import org.openinfinity.cloud.service.administrator.InstanceService;
import org.openinfinity.cloud.service.administrator.MachineService;
import org.openinfinity.cloud.service.deployer.DeployerService;
import org.openinfinity.core.annotation.Log;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Reader interface implementation for reading deployed software assets.
 * 
 * @author Ilkka Leinonen
 * @author Tommi Siitonen
 * @version 1.0.0
 * @since 1.2.0
 */
@Component("periodicCloudDeployerReader")
public class PeriodicCloudDeployerReader implements ItemReader<DeploymentStatus> {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(PeriodicCloudDeployerReader.class);
	
	@Autowired
	DeployerService deployerService;
	
	@Autowired 
	InstanceService instanceService;
	
	@Autowired 	
	MachineService machineService;
	
	private int index = 0;
	
	private List<DeploymentStatus> deploymentStatuses = new ArrayList<DeploymentStatus>();

	/**
	 * Reads next record from deployment list.
	 */
	@Log
	public DeploymentStatus read() throws Exception {
		if (deploymentStatuses.isEmpty()) {
			deploymentStatuses = loadDeployments();
			LOGGER.trace("Initializing reader finished. [" + deploymentStatuses.size() + "] deploymentstatuses loaded.");			
		}
		if (index < deploymentStatuses.size()) {
			LOGGER.trace("Processing deployment statuses, current index is [" + index + "].");
			DeploymentStatus retValue = deploymentStatuses.get(index++);
			return retValue;
			//return deploymentStatuses.get(index++);
		} else {
			LOGGER.trace("Reader finished, all items handled. Index is [" + index + "]. Returning null");			
			index = 0;
			deploymentStatuses.clear();
			return null;
		}	
	}
	
	private List<DeploymentStatus> loadDeployments() {
		LOGGER.info("Deployment of software artifacts started.");
		Collection<Instance> activeInstances = instanceService.getAllActiveInstances();
		LOGGER.info("There are total of [" + activeInstances.size() + "] active instances in the infrastructure.");
		// Looping of instances not necessary?
		for (Instance instance : activeInstances) {
			Collection<Deployment> deployments = deployerService.loadDeploymentsForOrganization(instance.getOrganizationid());
			
			LOGGER.debug("There are [" + deployments.size() + "] deployment  for organization ["+instance.getOrganizationid()+"]. Processing instance <"+instance.getInstanceId()+">.");
			
			for (Deployment deployment : deployments) {
				// check if deployment is targeted for this instance
				if (deployment.getInstanceId() != instance.getInstanceId()) {
					LOGGER.debug("Deployment with id [" + deployment.getId() + "] not targeted for instance <"+instance.getInstanceId()+">. Skipping");					
					continue;
				}
				
				// Verify machine
				Collection<Machine> machinesInCluster=machineService.getMachinesInCluster(deployment.getClusterId());
				List<Machine> machinesToBeCompared = new ArrayList<Machine>();
				machinesToBeCompared.addAll(machinesInCluster);
				
				// loop machines
				//boolean exists= false;

				// returns DeploymentStatuses for all deployments with passed clusterId 
				// TODO - new method to service for retrieving statuses by deployment, that should be used
				Collection<DeploymentStatus> deployedMachines = deployerService.loadDeploymentStatusesForCluster(deployment.getClusterId());
				LOGGER.debug("There are total of [" + deployedMachines.size() + "] for deployment with clusterid [" + deployment.getClusterId() + "].");
				

				// examine machines in the cluster
				// add new machines to deploymentStatuses
				// add machines with NOT_DEPLOYED status to deploymentStatuses
				// check machines with DEPLOYED status
				// 		add machines with old deployment timestamp to deploymentStatuses
				// 		skip DEPLOYED machines with valid timestamp
				// add terminated machines for updating state to deploymentStatuses
				// skip machines with TERMINATED status
				
				// check all machines in cluster defined for deployment
				
				//boolean deploymentStatusMachineMissingFromClusterMachines = true;  // machine does not exist in cluster anymore
				// this probably need to handled as an list
				
				// loopingOtherWayAround------------------
				
				
				for (DeploymentStatus deploymentStatus : deployedMachines) {												
					LOGGER.info("Processing deployment for deployment status id [" + deployment.getId() + "] and deployment state of [" + deploymentStatus.getDeploymentState() + "].");						
					deploymentStatus.setDeployment(deployment);
				
					boolean deploymentStatusMachineMissingFromClusterMachines = true;  // machine does not exist in cluster anymore
				
					for (Machine machine: machinesInCluster) {
						LOGGER.info("Processing machine [" + machine.getId() + "] of Deployment ["+deployment.getId()+"]");

						// check if DeploymentStatus is for this machine
						if(deploymentStatus.getMachineId()==machine.getId()) {
							// machine exists for this DeploymentStatus
							deploymentStatusMachineMissingFromClusterMachines=false;
							
							// If machines can have only one deploymentState it can be done with remove
							// otherwise separate collection needed							
							//machinesInCluster.remove(machine);
							machinesToBeCompared.remove(machine);
							LOGGER.info("Machine [" + machine.getId() + "] found in deploymentStauses of Deployment ["+deployment.getId()+"]");
							
							
							DeploymentState deploymentState = deploymentStatus.getDeploymentState();
							switch (deploymentState) {
								case NOT_DEPLOYED: deploymentStatuses.add(deploymentStatus); 
								LOGGER.info("Not_deployed machine [" + machine.getId() + "] of Deployment ["+deployment.getId()+"]");								
								break;									
								case DEPLOYED: verifyTimeStampAndAddDeploymentInformation(deploymentStatuses, deploymentStatus, deployment); 
								LOGGER.info("Redeployed machine [" + machine.getId() + "] of Deployment ["+deployment.getId()+"]");									
								break;
								// do nothing for terminated machines case TERMINATED
							}
							// continue; //can there be several statuses for the same machine?
						} 
					}
					// handle deploymentStatuses with machines not existing anymore (TERMINATED) 
					if (deploymentStatusMachineMissingFromClusterMachines) {
						LOGGER.info("Terminated machine [" + deploymentStatus.getMachineId() + "] of Deployment ["+deployment.getId()+"]");															
						deploymentStatus.setDeploymentState(DeploymentState.TERMINATED);
						deploymentStatuses.add(deploymentStatus);
					}
											
				}
				
				// now machine collection contains only machines that did not exist in deploymentStatuses
				// add machines in cluster not found in DeploymentStatuses
				//for (Machine machine: machinesInCluster) {						
				for (Machine machine: machinesToBeCompared) {						
					// if no deploymentStatuses found for machine it is propably new machine or new deployment
					// new deploymentstatus added
					// TODO: need to remove loadbalancers
					if (machine.getType().equals("loadbalancer")) {
						LOGGER.info("Skipping (NEW) machine [" + machine.getId() + "] of type loadbalancer");							
						continue;
					}
					LOGGER.info("Undeployed (NEW) machine [" + machine.getId() + "] of Deployment ["+deployment.getId()+"]");
					//if (machineMissingFromDeploymentStatuses2) {
						DeploymentStatus newDeploymentStatus =  new DeploymentStatus();
						newDeploymentStatus.setDeployment(deployment);
						newDeploymentStatus.setDeploymentState(DeploymentState.NOT_DEPLOYED);
						newDeploymentStatus.setMachineId(machine.getId());
						deploymentStatuses.add(newDeploymentStatus); 
					//}
					
				}
								
			}
		}
		return deploymentStatuses;
	}

	private void verifyTimeStampAndAddDeploymentInformation(Collection<DeploymentStatus> deploymentStatuses, DeploymentStatus deploymentStatus, Deployment deployment) {
		LOGGER.info("Processing deployment state of machine [" + deploymentStatus.getMachineId() + "] for deployment status id [" + deployment.getId() + "] with allready existing deployment. Last deployment timestamp is [" + deployment.getDeploymentTimestamp().toString() + "] and last deployment status of the virtual machine is [" + deploymentStatus.getTimestamp().toString() + "].");
		if (deploymentStatus.getTimestamp().before(deployment.getDeploymentTimestamp())) {
			LOGGER.debug("Processing deployment state of machine [" + deploymentStatus.getMachineId() + "] for deployment status id [" + deployment.getId() + "] with allready existing deployment. Upgrading new deployment to machine.");			
			deploymentStatuses.add(deploymentStatus);
		}
	}	
	
	
	
}