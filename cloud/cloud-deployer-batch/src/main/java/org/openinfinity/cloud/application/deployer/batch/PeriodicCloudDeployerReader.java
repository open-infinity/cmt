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
import org.openinfinity.cloud.service.administrator.InstanceService;
import org.openinfinity.cloud.service.deployer.DeployerService;
import org.openinfinity.core.annotation.Log;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Reader interface implementation for reading deployed software assets.
 * 
 * @author Ilkka Leinonen
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
	
	private int index = 0;
	
	private List<DeploymentStatus> deploymentStatuses = new ArrayList<DeploymentStatus>();

	/**
	 * Reads next record from deployment list.
	 */
	@Log
	public DeploymentStatus read() throws Exception {
		if (deploymentStatuses.isEmpty())
			deploymentStatuses = loadDeployments();
		if (index < deploymentStatuses.size()) {
			LOGGER.trace("Processing deployment statuses, current index is [" + index + "].");
			return deploymentStatuses.get(index++);
		} else {
			index = 0;
			return null;
		}	
	}
	
	private List<DeploymentStatus> loadDeployments() {
		LOGGER.info("Deployment of software artifacts started.");
		Collection<Instance> activeInstances = instanceService.getAllActiveInstances();
		LOGGER.info("There are total of [" + activeInstances.size() + "] active instances in the infrastructure.");
		for (Instance instance : activeInstances) {
			Collection<Deployment> deployments = deployerService.loadDeploymentsForOrganization(instance.getOrganizationid());
			LOGGER.debug("There are [" + deployments.size() + "] deployment  for instance ["+instance.getInstanceId()+"].");
			for (Deployment deployment : deployments) {
				Collection<DeploymentStatus> deployedMachines = deployerService.loadDeploymentStatusesForCluster(deployment.getClusterId());
				LOGGER.debug("There are total of [" + deployedMachines.size() + "] for deployment with id [" + deployment.getId() + "].");
				for (DeploymentStatus deploymentStatus : deployedMachines) {
					LOGGER.info("Processing deployment state of machine [" + deploymentStatus.getMachineId() + "] for deployment status id [" + deployment.getId() + "] and deployment state of [" + deploymentStatus.getDeploymentState() + "].");
					deploymentStatus.setDeployment(deployment);
					DeploymentState deploymentState = deploymentStatus.getDeploymentState();
					switch (deploymentState) {
						case NOT_DEPLOYED: deploymentStatuses.add(deploymentStatus); break;
						case DEPLOYED: verifyTimeStampAndAddDeploymentInformation(deploymentStatuses, deploymentStatus, deployment); break;
					}
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