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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

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
	
	Map<String, String> deploymentDirectoryMap;
	Map<String, String> unDeploymentDirectoryMap;
	Map<String, String> deploymentCommandsMap;
	Map<String, String> unDeploymentCommandsMap;
	
	
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

	
	public void setDeploymentDirectoryMap(Map<String, String> deploymentDirectoryMap) {
		this.deploymentDirectoryMap = deploymentDirectoryMap;
	}
	public void setUnDeploymentDirectoryMap(Map<String, String> unDeploymentDirectoryMap) {
		this.unDeploymentDirectoryMap = unDeploymentDirectoryMap;
	}
	public void setDeploymentCommandsMap(Map<String, String> deploymentCommandsMap) {
		this.deploymentCommandsMap = deploymentCommandsMap;
	}
	public void setUnDeploymentCommandsMap(Map<String, String> unDeploymentCommandsMap) {
		this.unDeploymentCommandsMap = unDeploymentCommandsMap;
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
			// TODO - to get clusterTypeName ClusterType repository and service need to be updated
			// String clusterType = clusterTypeService.getClusterTypeById(int type).getName();
			
			
			String deploymentDirectory = deploymentDirectoryMap.get(type+"-"+deploymentStatus.getDeployment().getType());
			String unDeploymentDirectory = unDeploymentDirectoryMap.get(type+"-"+deploymentStatus.getDeployment().getType());

			//LOGGER.debug("DeploymentDirectory: "+ deploymentDirectory);
			//LOGGER.debug("UndeploymentDirectory: "+ unDeploymentDirectory);
			
			
//			Replace command configuration with predefined set of dynamic parameters provided to be used in commands:
//			${deploymentDirectory}
//			${unDeploymentDirectory}
//			${deploymentName}
//			${deploymentType} 
//			${targetIp}
//			${targetClusterLBIp}
//			${fileSystemUser}
//			${fileSystemGroup}
			Map <String, String> replacePatterns = new HashMap<String, String>();
			replacePatterns.put("#%deploymentDirectory%#", deploymentDirectory);
			replacePatterns.put("#%unDeploymentDirectory%#", unDeploymentDirectory);
			replacePatterns.put("#%deploymentDirectory%#", deploymentDirectory);
			replacePatterns.put("#%deploymentName%#", deploymentStatus.getDeployment().getName());
			replacePatterns.put("#%deploymentType%#", deploymentStatus.getDeployment().getType()+"");
			replacePatterns.put("#%targetIp%#", machine.getDnsName());
			replacePatterns.put("#%targetClusterLBIp%#", cluster.getLbDns());
			replacePatterns.put("#%fileSystemUser%#", fileSystemUser);
			replacePatterns.put("#%fileSystemGroup%#", fileSystemGroup);
			//LOGGER.debug("ReplacePatternsMap is:");
			//listMap(replacePatterns);

			
			// Handle applications in UNDEPLOY state			
			if (deploymentStatus.getDeploymentState()==DeploymentState.UNDEPLOY) {
				
				//Collection<String> commands = new ArrayList<String>();
				// Application updeployment  is platform specific
				//listMap(unDeploymentCommandsMap);
				
				Collection<String> commands = getCommands(unDeploymentCommandsMap, deploymentStatus.getDeployment().getType(), cluster.getType()+"", replacePatterns);
				
				LOGGER.debug("Executing undeployment remote commands in machine with id [" + machine.getId() + "] .");
				//listCollection(commands);
				
				SSHGateway.executeRemoteCommands(
						key.getSecret_key().getBytes(), 
						null,
						machine.getDnsName(), 
						deploymentHostPort, 
						username, 
						"",
						commands);					
				
				deploymentStatus.setDeploymentState(DeploymentState.UNDEPLOYED);
				LOGGER.debug("Updating deployment status with id [" + deploymentStatus.getId() + "] as UNDEPLOYED.");
				deployerService.storeDeploymentStatus(deploymentStatus);				
				continue;
			} else if (deploymentStatus.getDeploymentState()==DeploymentState.UNDEPLOYED) {
				LOGGER.debug("Already UNDEPLOYED status in writer. Storing deployment status with id [" + deploymentStatus.getId() + "] as UNDEPLOYED.");
				deployerService.storeDeploymentStatus(deploymentStatus);								
			}
			
			// Handle DEPLOYMENTS
			
			// Pushing deployment
			LOGGER.debug("Pushing deployment to machine with id [" + machine.getId() + "] for deployment directory [" + deploymentDirectory + "] with artifact named [" + deploymentStatus.getDeployment().getName() + "]. to cluster type<"+type+">");	
						
			// deploymentDirectory already platform specific
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
			
			// Executing PostPush commands
			//listMap(deploymentCommandsMap);			
			Collection<String> commands = getCommands(deploymentCommandsMap, deploymentStatus.getDeployment().getType(), cluster.getType()+"", replacePatterns);
			
			LOGGER.debug("Executing deployment remote commands in machine with id [" + machine.getId() + "] .");
			
			listCollection(commands);
			
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
		
	private Collection<String> getCommands(Map <String, String> commandMap, String applicationType, String clusterType, Map<String, String> replacePatterns) {
		Collection<String> commands = new ArrayList<String>();
		int index=1;
		String key = clusterType+"-"+applicationType+"."+index;
		
		while (deploymentCommandsMap.containsKey(key)) {
			index++;
			String command = commandMap.get(key);
			LOGGER.debug("Fetched command with key:<"+key+">. Command: <"+command+">.");
			commands.add(formatCommandParameters(command, replacePatterns));
			key = clusterType+"-"+applicationType+"."+index;			
		}
		
		return commands;
	}
	
//	Replace command configuration with predefined set of dynamic parameters provided to be used in commands:
//		${deploymentDirectory}
//		${unDeploymentDirectory}
//		${deploymentName}
//		${deploymentType} 
//		${targetIp}
//		${targetClusterLBIp}
//		${fileSystemUser}
//		${fileSystemGroup}
	private String formatCommandParameters(String command, Map<String, String> replacePatterns) {
		LOGGER.debug("Unformatted command: <"+command+">.");
		for (String key : replacePatterns.keySet()) {
			command=command.replaceAll(Pattern.quote(key), replacePatterns.get(key));			
		}  
		LOGGER.debug("Formatted command: <"+command+">.");
		return command;
	}
	
	private void listMap(Map<String, String> map) {
		for (String key : map.keySet()) {
			LOGGER.debug("KEY: <"+key+">.");
			LOGGER.debug("VALUE: <"+map.get(key)+">.");
		}  		
	}
	
	private void listCollection(Collection c) {
		int i=0;
		for (Object item : c) {
			LOGGER.debug(i+++": < "+item+" >");					
		}
	}
	
}