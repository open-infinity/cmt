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
import org.openinfinity.cloud.service.deployer.DeployerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Batch processor for fetching stored software assets and attasching byte stream through deployer service interface.
 * 
 * @author Ilkka Leinonen
 * @version 1.0.0
 * @since 1.2.0
 */
@Component("periodicCloudDeployerProcessor")
public class PeriodicCloudDeployerProcessor implements ItemProcessor<DeploymentStatus, DeploymentStatus> {

	private static final Logger LOGGER = LoggerFactory.getLogger(PeriodicCloudDeployerProcessor.class);
	
	@Autowired
	DeployerService deployerService;
	
	public DeploymentStatus process(DeploymentStatus deploymentStatus) throws Exception {
		LOGGER.info("Processing of deployment with deployment status [" + deploymentStatus.getId() + "] started.");
		String bucketName = "" + deploymentStatus.getDeployment().getClusterId();
		String key = deploymentStatus.getDeployment().getName();
		LOGGER.debug("Deployment status of [" + deploymentStatus.getId() + "] continued with reading deployment key (S3 key) ["+ deploymentStatus.getDeployment().getName() +"] and organization id (S3 bucket name) [" + deploymentStatus.getDeployment().getClusterId() + "].");
		InputStream inputStream = deployerService.load(bucketName, key);
		deploymentStatus.getDeployment().setInputStream(inputStream);
		LOGGER.trace("Deployment status of [" + deploymentStatus.getId() + "] continued with reading deployment key (S3 key) ["+ deploymentStatus.getDeployment().getName() +"] and organization id (S3 bucket name) [" + deploymentStatus.getDeployment().getClusterId() + "]. Connection to S3 has been established and inputstream found for software asset.");
		return deploymentStatus;		
	}	
	
}