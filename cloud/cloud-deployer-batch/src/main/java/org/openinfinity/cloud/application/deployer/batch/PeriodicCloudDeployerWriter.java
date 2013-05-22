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
import java.util.Map;

import org.openinfinity.cloud.domain.DeploymentStatus;
import org.openinfinity.cloud.domain.DeploymentStatus.DeploymentState;
import org.openinfinity.cloud.domain.Cluster;
import org.openinfinity.cloud.domain.Key;
import org.openinfinity.cloud.domain.Machine;
import org.openinfinity.cloud.service.administrator.ClusterService;
import org.openinfinity.cloud.service.administrator.InstanceService;
import org.openinfinity.cloud.service.administrator.KeyService;
import org.openinfinity.cloud.service.administrator.MachineService;
import org.openinfinity.cloud.service.administrator.MachineTypeService;
import org.openinfinity.cloud.service.deployer.DeployerService;
import org.openinfinity.cloud.util.ssh.SSHGateway;
import org.openinfinity.core.exception.SystemException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;

/**
 * Writer interface implementation for deploying software assets to virtual machines.
 * 
 * @author Ilkka Leinonen
 * @author Tommi Siitonen
 * @version 1.0.0
 * @since 1.2.0
 */
//@Component("periodicCloudDeployerWriter")
public class PeriodicCloudDeployerWriter implements ItemWriter<DeploymentStatus> {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(PeriodicCloudDeployerWriter.class);
	
	//@Value("${pathToDeploymentDirectoryMap}")
	//@Autowired(required=true)
	Map<Integer, String> pathToDeploymentDirectoryMap;
	
	@Value("${deploymentHostPort}")
	int deploymentHostPort;
	
	@Value("${technicalUsername}")
	String username;	
	
	@Value("${filesystem.user}")
	String fileSystemUser;
	
	@Value("${filesystem.group}")
	String fileSystemGroup;
	
	@Autowired
	MachineService machineService;
	
	@Autowired
	@Qualifier("keyService")
	private KeyService keyService;
	
	@Autowired
	MachineTypeService machineTypeService;
	
	@Autowired
	ClusterService clusterService;
	
	@Autowired
	InstanceService instanceService;
	
	@Autowired
	DeployerService deployerService;
	
	public void setFileSystemUser(String fileSystemUser) {
		this.fileSystemUser = fileSystemUser;
	}

	public void setFileSystemGroup(String fileSystemGroup) {
		this.fileSystemGroup = fileSystemGroup;
	}

	public void setPathToDeploymentDirectoryMap(Map<Integer, String> pathToDeploymentDirectoryMap) {
		this.pathToDeploymentDirectoryMap = pathToDeploymentDirectoryMap;
	}

	public void setDeploymentHostPort(int deploymentHostPort) {
		this.deploymentHostPort = deploymentHostPort;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}

	public void setMachineService(MachineService machineService) {
		this.machineService = machineService;
	}

	public void setKeyService(KeyService keyService) {
		this.keyService = keyService;
	}

	public void setMachineTypeService(MachineTypeService machineTypeService) {
		this.machineTypeService = machineTypeService;
	}

	public void setClusterService(ClusterService clusterService) {
		this.clusterService = clusterService;
	}

	public void setInstanceService(InstanceService instanceService) {
		this.instanceService = instanceService;
	}

	@Override
	public void write(List<? extends DeploymentStatus> deploymentStatuses) throws Exception {
		LOGGER.info("Processing total of [" + deploymentStatuses.size() + "] deployments in writer.");
		
		for (DeploymentStatus deploymentStatus : deploymentStatuses) {
			LOGGER.info("Processing write of deployment <"+deploymentStatus.getDeployment().getId()+"> deploymentStatus [" + deploymentStatus.getId() + "] in state <"+deploymentStatus.getDeploymentState()+"> and machineId ["+deploymentStatus.getMachineId()+"] started.");
			
			// just update TERMINATED deploymentStatus states to db
			if (deploymentStatus.getDeploymentState()==DeploymentState.TERMINATED) {
				LOGGER.debug("Processing deploymentStatus with [" + deploymentStatus.getId() + "] and machineId ["+deploymentStatus.getMachineId()+"]. State TERMINATED, storing status.");				
				try {
					deployerService.storeDeploymentStatus(deploymentStatus);					
				} catch(SystemException se) {
					LOGGER.error(se.getMessage(),se);
				}
				continue;				
			}
						
			Key key = keyService.getKeyByInstanceId(deploymentStatus.getDeployment().getInstanceId());
			Machine machine = machineService.getMachine(deploymentStatus.getMachineId());
			LOGGER.debug("Processing machine with id [" + machine.getId() + "] with instance id [" + machine.getInstanceId() + "].");
			Cluster cluster = clusterService.getClusterByClusterId(deploymentStatus.getDeployment().getClusterId());
			LOGGER.debug("Processing deploymentStatus with [" + deploymentStatus.getId() + "] and machineId ["+deploymentStatus.getMachineId()+"] with deployment id [" + deploymentStatus.getDeployment().getId() + "] for cluster <"+cluster.getId()+">.");
			
			int type = cluster.getType();
			String deploymentDirectory = pathToDeploymentDirectoryMap.get(new Integer(type));
			

			// Remove applications in UNDEPLOY state
			if (deploymentStatus.getDeploymentState()==DeploymentState.UNDEPLOY) {
				Collection<String> commands = new ArrayList<String>();
				// Application updeployment  is platform specific
				switch (type) {
				case 5:	// BAS platform					
					commands.add("su toas -c 'rm " + deploymentDirectory + deploymentStatus.getDeployment().getName() + ".war'");				
					LOGGER.debug("Executing remote commands in machine with id [" + machine.getId() + "] .");
					SSHGateway.executeRemoteCommands(
							key.getSecret_key().getBytes(), 
							null,
							machine.getDnsName(), 
							deploymentHostPort, 
							username, 
							"",
							commands);					
					deploymentStatus.setDeploymentState(DeploymentState.UNDEPLOYED);
					break;					
				case 0:	// Portal platform										
					// Portal applications are deployed to tomcat webapps from portal hot deployment directory
					commands.add("su toas -c 'rm " + pathToDeploymentDirectoryMap.get(5) + deploymentStatus.getDeployment().getName() + ".war'");				
					commands.add("su toas -c 'rm -r " + pathToDeploymentDirectoryMap.get(5) + deploymentStatus.getDeployment().getName() + "'");				
					LOGGER.debug("Executing remote commands in machine with id [" + machine.getId() + "] .");
					SSHGateway.executeRemoteCommands(
							key.getSecret_key().getBytes(), 
							null,
							machine.getDnsName(), 
							deploymentHostPort, 
							username, 
							"",
							commands);					
					deploymentStatus.setDeploymentState(DeploymentState.UNDEPLOYED);
					break;
				default:
					LOGGER.debug("WARNING! Undeployment not supported for platform type ["+type+"]. Deployment status with id [" + deploymentStatus.getId() + "]  set to ERROR.");					
					deploymentStatus.setDeploymentState(DeploymentState.ERROR);
					break;
				}
				LOGGER.debug("Updating deployment status with id [" + deploymentStatus.getId() + "] as UNDEPLOYED.");
				deployerService.storeDeploymentStatus(deploymentStatus);				
				continue;
			} else if (deploymentStatus.getDeploymentState()==DeploymentState.UNDEPLOYED) {
				LOGGER.debug("Already UNDEPLOYED status in writer. Storing deployment status with id [" + deploymentStatus.getId() + "] as UNDEPLOYED.");
				deployerService.storeDeploymentStatus(deploymentStatus);								
			}
			
			
			LOGGER.debug("Pushing deployment to machine with id [" + machine.getId() + "] for deployment directory [" + deploymentDirectory + "] with artifact named [" + deploymentStatus.getDeployment().getName() + "]. to cluster type<"+type+">");	
//			SSHGateway.pushToServer(
//					key.getSecret_key().getBytes(), 
//					null,
//					deploymentStatus.getDeployment().getInputStream(), 
//					deploymentStatus.getDeployment().getName(), 
//					machine.getDnsName(), 
//					deploymentHostPort, 
//					username, 
//					"",
//					deploymentDirectory+deploymentStatus.getDeployment().getName()+".war");
			SSHGateway.pushToServer(
					key.getSecret_key().getBytes(), 
					null,
					deploymentStatus.getInputStream(), 
					deploymentStatus.getDeployment().getName(), 
					machine.getDnsName(), 
					deploymentHostPort, 
					username, 
					"",
					deploymentDirectory+deploymentStatus.getDeployment().getName()+".war");			
			Collection<String> commands = new ArrayList<String>();
			commands.add("chown "+ fileSystemUser + "." + fileSystemGroup + " " + deploymentDirectory + deploymentStatus.getDeployment().getName() + ".*");
			LOGGER.debug("Executing remote commands in machine with id [" + machine.getId() + "] .");
			SSHGateway.executeRemoteCommands(
					key.getSecret_key().getBytes(), 
					null,
					machine.getDnsName(), 
					deploymentHostPort, 
					username, 
					"",
					commands);
			LOGGER.debug("Updating deployment status with id [" + deploymentStatus.getId() + "] as DEPLOYED.");
			deploymentStatus.setDeploymentState(DeploymentState.DEPLOYED);
			deployerService.storeDeploymentStatus(deploymentStatus);
		}
	}	
	
}