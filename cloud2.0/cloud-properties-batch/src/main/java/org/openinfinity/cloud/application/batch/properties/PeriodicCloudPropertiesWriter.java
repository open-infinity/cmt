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
package org.openinfinity.cloud.application.batch.properties;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.openinfinity.cloud.domain.Deployment;
import org.openinfinity.cloud.service.deployer.DeployerService;
import org.openinfinity.cloud.service.properties.CentralizedPropertiesService;
import org.openinfinity.cloud.util.filesystem.FileUtil;
import org.openinfinity.core.annotation.Log;
import org.openinfinity.core.util.IOUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Periodic cloud properties reader reads all key value pairs to specified cluster.
 * 
 * @author Ilkka Leinonen
 * @author Tommi Siitonen
 * @version 1.0.0
 * @since 1.2.0
 */
@Component("periodicCloudPropertiesWriter")
public class PeriodicCloudPropertiesWriter implements ItemWriter<Map<Deployment, File>> {
	private static final Logger LOGGER = LoggerFactory.getLogger(PeriodicCloudPropertiesWriter.class);

	@Autowired 
	private DeployerService deployerService;

	@Autowired
	private CentralizedPropertiesService centralizedPropertiesService;
	
	@Log
	@Override
	public void write(List<? extends Map<Deployment, File>> items) {
		for (Map<Deployment, File> fileAndDeploymentMap : items) {
			for (Map.Entry<Deployment, File> entry : fileAndDeploymentMap.entrySet()) {
				Deployment deployment = entry.getKey();
				File file = entry.getValue();
				LOGGER.info("Deploying properties file for cluster ["+deployment.getClusterId()+"]. Filename: ["+file+"].");
				LOGGER.debug("Deploying properties. Deployment is - OrganizationId:["+deployment.getOrganizationId()
						+"], InstanceId:["+deployment.getInstanceId()+"], ClusterId:["+deployment.getClusterId()
						+"], Name:["+deployment.getName()+"], Type:["+deployment.getType()+"], State:["+deployment.getState()+"].");
				
				// Handle undeployment if all properties have been deleted
				if (deployment.getState()==DeployerService.DEPLOYMENT_STATE_UNDEPLOY) {
					LOGGER.info("Deployment state was ["+deployment.getState()+"]. No properties left for cluster ["+deployment.getClusterId()+"]. Just updating deployment state and removing property.");
					deployerService.updateExistingDeployedDeploymentState(deployment, DeployerService.DEPLOYMENT_STATE_UNDEPLOY);
					// property need to be deleted as well
					centralizedPropertiesService.deleteByStateOrgInstClusName(deployment.getOrganizationId(), deployment.getInstanceId(), deployment.getClusterId());
					continue;
				}
				
				deployerService.deploy(deployment);
				IOUtil.closeStream(deployment.getInputStream());
				FileUtil.remove(file.getAbsolutePath());
				// properties states need to be updated
				// remove if there were any deleted properties
				centralizedPropertiesService.deleteByStateOrgInstClusName(deployment.getOrganizationId(), deployment.getInstanceId(), deployment.getClusterId());
				// set states to PROCESSED for added
				centralizedPropertiesService.updateStatesNewToFinalizedByOrgInstClusName(deployment.getOrganizationId(), deployment.getInstanceId(), deployment.getClusterId());
			}	
		}
	}

}