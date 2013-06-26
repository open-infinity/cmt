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
			LOGGER.debug("Initializing reader finished. [" + deploymentStatuses.size() + "] deploymentstatuses loaded.");			
		}
		if (index < deploymentStatuses.size()) {
			LOGGER.debug("Processing deployment statuses, current index is [" + index + "].");
			DeploymentStatus retValue = deploymentStatuses.get(index++);
			return retValue;
			//return deploymentStatuses.get(index++);
		} else {
			LOGGER.debug("Reader finished, all items handled. Index is [" + index + "]. Returning null");			
			index = 0;
			deploymentStatuses.clear();	// this should not be necessary
			return null;
		}	
	}
	
	private List<DeploymentStatus> loadDeployments() {
		LOGGER.debug("Deployment of software artifacts started.");
		Collection<Instance> activeInstances = instanceService.getAllActiveInstances();
		LOGGER.debug("There are total of [" + activeInstances.size() + "] active instances in the infrastructure.");
		// Looping of instances not necessary, still ensures that deployments to same instance are handled at tthe same chunks 
		for (Instance instance : activeInstances) {
			Collection<Deployment> deployments = deployerService.loadDeploymentsForOrganization(instance.getOrganizationid());
			
		
			for (Deployment deployment : deployments) {
				
				// for supporting CI and staging area handling deployments with with same name and 
				// more current timestamps should replace current deployments
				if (deployment.getState()==DeployerService.DEPLOYMENT_STATE_DEPLOYED) {
					Collection<Deployment> newerDeployments = deployerService.loadNewerDeploymentsForClusterWithNameInDeployedState(deployment);
					
					// replace current deployment with new
					if (!newerDeployments.isEmpty()) {
						deployment.setState(DeployerService.DEPLOYMENT_STATE_UNDEPLOY);
					}
				}
				
				
				// check if deployment is targeted for this instance
				if (deployment.getInstanceId() != instance.getInstanceId()) {
					//LOGGER.debug("Deployment with id [" + deployment.getId() + "] not targeted for instance <"+instance.getInstanceId()+">. Skipping");					
					continue;
				}
				LOGGER.debug("Deployment with id [" + deployment.getId() + "] targeted for instance <"+instance.getInstanceId()+">. Comparing machines and DeploymentStatuses.");					
						
				// Verify machine
				// When scaling cluster also the configured state for ready machines will be tempory changed to not configured
				// Fech cannot be currenly used like this
				//Collection<Machine> machinesInCluster=machineService.getMachinesInClusterRunningAndReady(deployment.getClusterId());
				Collection<Machine> machinesInCluster=machineService.getMachinesInCluster(deployment.getClusterId());
				List<Machine> machinesToBeCompared = new ArrayList<Machine>();
				machinesToBeCompared.addAll(machinesInCluster);
				
				// loop machines
				//boolean exists= false;

				// TODO - new method to service for retrieving statuses by deployment, that should be used
				// returns DeploymentStatuses for all deployments with passed clusterId 
				//Collection<DeploymentStatus> deployedMachines = deployerService.loadDeploymentStatusesForCluster(deployment.getClusterId());
				//LOGGER.debug("There are total of [" + deployedMachines.size() + "] for deployment with clusterid [" + deployment.getClusterId() + "].");
				Collection<DeploymentStatus> deployedMachines = deployerService.loadDeploymentStatusesForDeployment(deployment.getId());				
				LOGGER.debug("There are total of [" + deployedMachines.size() + "] for deploymentStatuses with deploymentId [" + deployment.getId() + "].");
				

				// examine machines in the cluster
				// add new machines to deploymentStatuses (if not loadbalancers or for deployments in undeployed states)
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
					LOGGER.debug("Processing deployment for deploymentStatus id [" + deployment.getId() + "] and deploymentStatus state of [" + deploymentStatus.getDeploymentState() + "].");						
					deploymentStatus.setDeployment(deployment);
					
					// TODO - check if deployment is undeployed/to be undeployed
					// TODO - check if deployment is deleted/to be deleted
					if (deployment.getState()==DeployerService.DEPLOYMENT_STATE_UNDEPLOY) {
						// check if all deploymentstatuses are already handled and update deployment state if so
						// deployerService.

						LOGGER.debug("Deployment with UNDEPLOY status found. DeploymentId ["+deployment.getId()+"]. Processing instance <"+instance.getInstanceId()+">.");

						switch (deploymentStatus.getDeploymentState()) {
							case NOT_DEPLOYED: 
								deploymentStatus.setDeploymentState(DeploymentState.UNDEPLOYED);
								// add Undeployed status for writer to be persisted
								deploymentStatuses.add(deploymentStatus); 
								LOGGER.info("DeploymentStatus <"+deploymentStatus.getId()+"> state NOT_DEPLOYED set to UNDEPLOYED");								
							break;
							case UNDEPLOY: 
								//deploymentStatuses.add(deploymentStatus); 
								LOGGER.debug("DeploymentStatus <"+deploymentStatus.getId()+"> state already UNDEPLOY");								
							break;																	
							case DEPLOYED: 
								deploymentStatus.setDeploymentState(DeploymentState.UNDEPLOY);
								LOGGER.info("DeploymentStatus <"+deploymentStatus.getId()+"> state DEPLOYED set to UNDEPLOY");								
							break;
							// do nothing for terminated machines case TERMINATED and UNDEPLOYED. Not added for processing
						}						
					} 
					
					
					boolean deploymentStatusMachineMissingFromClusterMachines = true;  // machine does not exist in cluster anymore
				
					for (Machine machine: machinesInCluster) {
						LOGGER.debug("Processing machine [" + machine.getId() + "] of Deployment ["+deployment.getId()+"]");

						// check if DeploymentStatus is for this machine
						if(deploymentStatus.getMachineId()==machine.getId()) {
							// machine exists for this DeploymentStatus
							deploymentStatusMachineMissingFromClusterMachines=false;
							
							// If machines can have only one deploymentState it can be done with remove
							// otherwise separate collection needed							
							machinesToBeCompared.remove(machine);
							LOGGER.debug("Machine [" + machine.getId() + "] found in deploymentStauses of Deployment ["+deployment.getId()+"]");
							
							
							DeploymentState deploymentState = deploymentStatus.getDeploymentState();
							switch (deploymentState) {
								case NOT_DEPLOYED: 
									deploymentStatuses.add(deploymentStatus); 
									LOGGER.info("Not_deployed machine [" + machine.getId() + "] of Deployment ["+deployment.getId()+"]");								
								break;
								case UNDEPLOY: 
									deploymentStatuses.add(deploymentStatus); 
									LOGGER.info("Set for undeploy machine [" + machine.getId() + "] of Deployment ["+deployment.getId()+"]");								
								break;																	
								case DEPLOYED: 
									verifyTimeStampAndAddDeploymentInformation(deploymentStatuses, deploymentStatus, deployment); 
									LOGGER.debug("Deployed machine [" + machine.getId() + "] of Deployment ["+deployment.getId()+"]. Timestamp verified.");									
								break;
								case UNDEPLOYED:
									// handle redeployment
									// TODO: verify this does not mix something else
									if (deployment.getState()==DeployerService.DEPLOYMENT_STATE_DEPLOYED) {
										LOGGER.info("Set for redeploy machine [" + machine.getId() + "] of Deployment ["+deployment.getId()+"]");										
										deploymentStatus.setDeploymentState(DeploymentState.NOT_DEPLOYED);
										deploymentStatuses.add(deploymentStatus);
									}
								break;
								// do nothing for terminated machines case TERMINATED and UNDEPLOYED. Not added for processing
							}
							// continue; //can there be several statuses for the same machine?
						} 
					} // end of loop machines
					// handle deploymentStatuses with machines not existing anymore (TERMINATED) 
					if (deploymentStatusMachineMissingFromClusterMachines && deploymentStatus.getDeploymentState()!=DeploymentState.TERMINATED) {
						LOGGER.info("Terminated machine [" + deploymentStatus.getMachineId() + "] of Deployment ["+deployment.getId()+"]");															
						deploymentStatus.setDeploymentState(DeploymentState.TERMINATED);
						deploymentStatuses.add(deploymentStatus);
					}
											
				}
				
				// now machine collection contains only machines that did not exist in deploymentStatuses
				// add machines in cluster not found in DeploymentStatuses
				for (Machine machine: machinesToBeCompared) {						
					// if no deploymentStatuses found for machine it is probably new machine or new deployment
					// new deploymentstatus added
					// need to remove loadbalancers
					if (machine.getType().equals("loadbalancer")) {
						LOGGER.debug("Skipping (NEW) machine [" + machine.getId() + "] of type loadbalancer");							
						continue;
					}
					// take deployment state into account (no deployment to undeployed or other machines)
					if (deployment.getState()!=DeployerService.DEPLOYMENT_STATE_DEPLOYED && deployment.getState()!=DeployerService.DEPLOYMENT_STATE_NOT_DEPLOYED) {
						LOGGER.debug("Skipping (NEW) machine [" + machine.getId() + "] for deployment <"+deployment.getId()+"> in state <"+deployment.getState()+">.");							
						continue;
					}
					if(machine.getConfigured()==MachineService.MACHINE_CONFIGURE_READY) {
						LOGGER.info("Undeployed (NEW) machine [" + machine.getId() + "] of Deployment ["+deployment.getId()+"]. Setting deploymentStatus NOT_DEPLOYED.");
						DeploymentStatus newDeploymentStatus =  new DeploymentStatus();
						newDeploymentStatus.setDeployment(deployment);
						newDeploymentStatus.setDeploymentState(DeploymentState.NOT_DEPLOYED);
						newDeploymentStatus.setMachineId(machine.getId());
						deploymentStatuses.add(newDeploymentStatus); 					
					} else {
						LOGGER.debug("NEW machine [" + machine.getId() + "] of Deployment ["+deployment.getId()+"] still in configured_state <"+machine.getConfigured()+">. Skipping now.");						
					}
				}
				
				
				// update states deployment states UNDEPLOY, TO_BE_DEPLOYED, TO_BE_DELETED to final state 
				// if no deploymentstatuses found for deployment state update anymore it can be finished
				if (deploymentStatuses.isEmpty()) {
					switch(deployment.getState()) {
						case DeployerService.DEPLOYMENT_STATE_NOT_DEPLOYED:
							// if deploying is also done is stages update for deployed should be here
						break;
						case DeployerService.DEPLOYMENT_STATE_UNDEPLOY:
							// now all machines (deploymentStatuses) should be undeployed
							deployment.setState(DeployerService.DEPLOYMENT_STATE_UNDEPLOYED);
							deployerService.updateDeploymentState(deployment);
						break;
						case DeployerService.DEPLOYMENT_STATE_TO_BE_DELETED:
							deployment.setState(DeployerService.DEPLOYMENT_STATE_DELETED);
							deployerService.updateDeploymentState(deployment);
						break;
						case DeployerService.DEPLOYMENT_STATE_ERROR:
							// TODO: error state no used yet
						break;							
					}
				}
			}
		}
		return deploymentStatuses;
	}

	private void verifyTimeStampAndAddDeploymentInformation(Collection<DeploymentStatus> deploymentStatuses, DeploymentStatus deploymentStatus, Deployment deployment) {
		LOGGER.debug("Processing deployment state of machine [" + deploymentStatus.getMachineId() + "] for deployment status id [" + deployment.getId() + "] with allready existing deployment. Last deployment timestamp is [" + deployment.getDeploymentTimestamp().toString() + "] and last deployment status of the virtual machine is [" + deploymentStatus.getTimestamp().toString() + "].");
		if (deploymentStatus.getTimestamp().before(deployment.getDeploymentTimestamp())) {
			LOGGER.info("Processing deployment state of machine [" + deploymentStatus.getMachineId() + "] for deployment status id [" + deployment.getId() + "] with allready existing deployment. Redeployed, upgrading new deployment to machine.");			
			deploymentStatuses.add(deploymentStatus);
		}
	}	
	
	
	
}