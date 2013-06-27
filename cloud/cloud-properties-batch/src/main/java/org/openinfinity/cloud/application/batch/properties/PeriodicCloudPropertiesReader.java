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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.openinfinity.cloud.domain.Deployment;
import org.openinfinity.cloud.domain.SharedProperty;
import org.openinfinity.cloud.service.deployer.DeployerService;
import org.openinfinity.cloud.service.properties.CentralizedPropertiesService;
import org.openinfinity.core.annotation.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Periodic cloud properties reader reads all key value pairs to specified cluster.
 * 
 * @author Ilkka Leinonen
 * @version 1.0.0
 * @since 1.2.0
 */
@Component("periodicCloudPropertiesReader")
public class PeriodicCloudPropertiesReader implements ItemReader<Collection<SharedProperty>>{

	private static final Logger LOGGER = LoggerFactory.getLogger(PeriodicCloudPropertiesReader.class);

	private int index = 0;
	
	private List<Collection<SharedProperty>> sharedProperties = new ArrayList<Collection<SharedProperty>>();
	
	@Autowired
	private CentralizedPropertiesService centralizedPropertiesService;
	
	@Autowired
	private DeployerService deployerService;
	
	@Log
	@Override
	public Collection<SharedProperty> read() throws Exception {
		if (sharedProperties.isEmpty()) {
			Collection<SharedProperty> distinctSharedProperties = centralizedPropertiesService.loadKnownSharedPropertyDeployments();
			for (SharedProperty sample : distinctSharedProperties) {
				Collection<SharedProperty> sharedPropertiesPerDistinctCluster = centralizedPropertiesService.loadAll(sample);
				addSharedPropertyIfNotDeployed(sharedPropertiesPerDistinctCluster);
			}
			LOGGER.trace("Initializing reader finished. [" + sharedProperties.size() + "] deployments loaded.");			
		}
		if (index < sharedProperties.size()) {
			LOGGER.trace("Processing deployment statuses, current index is [" + index + "].");
			Collection<SharedProperty> retValue = sharedProperties.get(index++);
			return retValue;
		} else {
			LOGGER.trace("Reader finished, all items handled. Index is [" + index + "]. Returning null");			
			sharedProperties.clear();
			sharedProperties = new ArrayList<Collection<SharedProperty>>();
			index = 0;
			return null;
		}	
	}

	private void addSharedPropertyIfNotDeployed(Collection<SharedProperty> sharedPropertiesPerDistinctCluster) {
		for (SharedProperty sharedProperty : sharedPropertiesPerDistinctCluster) {
			Deployment deployment = populateDeployment(sharedProperty);
			Collection<Deployment> deploymentsPerCluster = deployerService.loadDeploymentsForClusterWithName(deployment);
			for (Deployment existingDeployment : deploymentsPerCluster) {
				verifyTimestampAndAddIfNeedsToBeDeployed(sharedPropertiesPerDistinctCluster, sharedProperty, existingDeployment);
			}
		}
	}

	private void verifyTimestampAndAddIfNeedsToBeDeployed(Collection<SharedProperty> sharedPropertiesPerDistinctCluster, SharedProperty sharedProperty, Deployment existingDeployment) {
		if (existingDeployment.getDeploymentTimestamp().before(sharedProperty.getTimestamp())) {
			sharedProperties.add(sharedPropertiesPerDistinctCluster);
		}
	}

	private Deployment populateDeployment(SharedProperty sharedProperty) {
		Deployment deployment = new Deployment();
		deployment.setOrganizationId(sharedProperty.getOrganizationId());
		deployment.setInstanceId(sharedProperty.getInstanceId()); 
		deployment.setClusterId(sharedProperty.getClusterId()); 
		deployment.setName("application");
		return deployment;
	}
		
}
