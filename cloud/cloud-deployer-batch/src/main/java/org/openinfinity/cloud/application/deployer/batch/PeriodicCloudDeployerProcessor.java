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

import java.io.InputStream;

import org.openinfinity.cloud.domain.DeploymentStatus;
import org.openinfinity.cloud.domain.DeploymentStatus.DeploymentState;
import org.openinfinity.cloud.service.deployer.DeployerService;
import org.openinfinity.core.util.ExceptionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * Batch processor for fetching stored software assets and attasching byte stream through deployer service interface.
 * 
 * @author Ilkka Leinonen
 * @author Tommi Siitonen
 * @version 1.0.0
 * @since 1.2.0
 */
@Component("periodicCloudDeployerProcessor")
public class PeriodicCloudDeployerProcessor implements ItemProcessor<DeploymentStatus, DeploymentStatus> {

	private static final Logger LOGGER = LoggerFactory.getLogger(PeriodicCloudDeployerProcessor.class);
	
	@Autowired
	DeployerService deployerService;	
	
	public DeploymentStatus process(DeploymentStatus deploymentStatus) throws Exception {
		LOGGER.info("Processing of deployment <"+deploymentStatus.getDeployment().getId()+"> deploymentStatus [" + deploymentStatus.getId() + "] in state <"+deploymentStatus.getDeploymentState()+"> and machineId ["+deploymentStatus.getMachineId()+"] started. Setting inputstream for deploymentStatus.");
		
		switch (deploymentStatus.getDeploymentState()) {
		case CLUSTER_TERMINATED:
			// skip deployment to terminated machines, just leave for writer for walrus cleaning and updating state to db
			LOGGER.info("Processing of deployment with deployment status [" + deploymentStatus.getId() + "] and machineId ["+deploymentStatus.getMachineId()+"] started. State is CLUSTER_TERMINATED, passing to writer without modifications.");						
			break;
		case TERMINATED:
			// skip deployment to terminated machines, just leave for writer for updating state to db
			LOGGER.info("Processing of deployment with deployment status [" + deploymentStatus.getId() + "] and machineId ["+deploymentStatus.getMachineId()+"] started. State is TERMINATED, passing to writer without modifications.");						
			break;
		case UNDEPLOYED:
			// skip undeployed machines, just leave for writer for updating state to db
			LOGGER.info("Processing of deployment with deployment status [" + deploymentStatus.getId() + "] and machineId ["+deploymentStatus.getMachineId()+"] started. State is UNDEPLOYED, passing to writer without modifications.");			
			break;
		case UNDEPLOY:
			// skip setting of inputstream for deployment statuses for undeployment
			LOGGER.info("Processing of deployment with deployment status [" + deploymentStatus.getId() + "] and machineId ["+deploymentStatus.getMachineId()+"] started. State is UNDEPLOY, passing to writer without modifications.");			
			break;

		default:
			// set inputstream for deployments
			LOGGER.info("Processing of deploymentStatus [" + deploymentStatus.getId() + "] in state <"+deploymentStatus.getDeploymentState()+">. Setting inputstream for deploymentStatus.");
			if (deploymentStatus.getDeployment().getInputStream()!=null) {
				LOGGER.info("Beware! Stream is not null for deployment status [" + deploymentStatus.getId() + "] and machineId ["+deploymentStatus.getMachineId()+"] started. Skipping opening.");
				return deploymentStatus;
			}
			String bucketName = "" + deploymentStatus.getDeployment().getClusterId();
			// location contains deployment id specific name
			String key = deploymentStatus.getDeployment().getLocation();
			LOGGER.debug("Deployment status of [" + deploymentStatus.getId() + "] and machineId ["+deploymentStatus.getMachineId()+"] continued with reading deployment key (S3 key) ["+ deploymentStatus.getDeployment().getName() +"] and organization id (S3 bucket name) [" + deploymentStatus.getDeployment().getClusterId() + "].");
			// TODO - setting deploymentstates to error if artifact not found. Deployment state need to be updated also
			InputStream inputStream = deployerService.load(bucketName, key);
			//deploymentStatus.getDeployment().setInputStream(inputStream);
			deploymentStatus.setInputStream(inputStream);
			LOGGER.trace("Deployment status of [" + deploymentStatus.getId() + "] and machineId ["+deploymentStatus.getMachineId()+"] continued with reading deployment key (S3 key) ["+ deploymentStatus.getDeployment().getName() +"] and organization id (S3 bucket name) [" + deploymentStatus.getDeployment().getClusterId() + "]. Connection to S3 has been established and inputstream found for software asset.");
			
			break;
		}
		return deploymentStatus;		
		
		
	}	
	
}