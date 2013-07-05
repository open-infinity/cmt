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
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
			
			// get all properties and generate map where keys are distinct clusters and values collections of sharedproperties targeted for the same cluster
			
			Map<Integer, Collection<SharedProperty>> sharedPropertiesForDistinctClusters = new HashMap<Integer, Collection<SharedProperty>>();

			Collection<SharedProperty> distinctSharedProperties = centralizedPropertiesService.loadKnownSharedPropertyDeployments();

			
			
			for (SharedProperty sharedProperty : distinctSharedProperties) {
				Integer clusterId = new Integer(sharedProperty.getClusterId());
				
				if (sharedPropertiesForDistinctClusters.containsKey(clusterId)) {
					//Collection<SharedProperty> sharedProperties = sharedPropertiesForDistinctClusters.get(clusterId);
					//sharedProperties.add(sharerProperty);
					LOGGER.debug("Updating properties list for cluster. [" + clusterId + "]."+
					"Key: ["+sharedProperty.getKey()+"],  Value: ["+sharedProperty.getValue()+"].");
					sharedPropertiesForDistinctClusters.get(clusterId).add(sharedProperty);					
				} else {
					LOGGER.debug("New properties list for cluster. [" + clusterId + "]."+
					"Key: ["+sharedProperty.getKey()+"],  Value: ["+sharedProperty.getValue()+"].");
					Collection<SharedProperty> sharedProperties = new ArrayList<SharedProperty>();
					sharedProperties.add(sharedProperty);
					sharedPropertiesForDistinctClusters.put(clusterId, sharedProperties);
				}
			}
			// continue from here
			// now we have cluster specific list of sharedProperties. Then we need to 
			// verify timestamps and check if those are deployed
			for (Integer clusterId : sharedPropertiesForDistinctClusters.keySet()) {
				LOGGER.debug("Verifying timestamps of properties for cluster. [" + clusterId + "].");							
				addSharedPropertyIfNotDeployed(sharedPropertiesForDistinctClusters.get(clusterId));				
			}
			
			
			/* OLD stuff
			Collection<SharedProperty> distinctSharedProperties = centralizedPropertiesService.loadKnownSharedPropertyDeployments();
			for (SharedProperty sample : distinctSharedProperties) {
				Collection<SharedProperty> sharedPropertiesPerDistinctCluster = centralizedPropertiesService.loadAll(sample);
				addSharedPropertyIfNotDeployed(sharedPropertiesPerDistinctCluster);
			}
			*/
			LOGGER.debug("Initializing reader finished. [" + sharedProperties.size() + "] deployments loaded.");			
		}
		if (index < sharedProperties.size()) {
			LOGGER.debug("Processing deployment statuses, current index is [" + index + "].");
			Collection<SharedProperty> retValue = sharedProperties.get(index++);
			return retValue;
		} else {
			LOGGER.debug("Reader finished, all items handled. Index is [" + index + "]. Returning null");			
			sharedProperties.clear();
			sharedProperties = new ArrayList<Collection<SharedProperty>>();
			index = 0;
			return null;
		}	
	}

	private void addSharedPropertyIfNotDeployed(Collection<SharedProperty> sharedPropertiesPerDistinctCluster) {
		for (SharedProperty sharedProperty : sharedPropertiesPerDistinctCluster) {
			LOGGER.debug("Verifying cluster distinct shared property. ClusterId: ["+sharedProperty.getClusterId()+"], " +
					"Key: ["+sharedProperty.getKey()+"],  Value: ["+sharedProperty.getValue()+", state: ["+sharedProperty.getState()+"].");	
			if (sharedProperty.getState()==CentralizedPropertiesService.PROPERTIES_STATE_NEW || sharedProperty.getState()==CentralizedPropertiesService.PROPERTIES_STATE_DELETED) {
				LOGGER.info("Adding not deployed cluster distinct shared property. ClusterId: ["+sharedProperty.getClusterId()+"], " +
						"Key: ["+sharedProperty.getKey()+"],  Value: ["+sharedProperty.getValue()+"].");
				sharedProperties.add(sharedPropertiesPerDistinctCluster);
				// if any of properties is updated properties file for cluster needs to be updated
				return;
				// if this handles properties as one deployment it should be updated if even one property is newer
				//return;
			}
		}
	}
	
	
	
	private void addSharedPropertyIfNotDeployed2(Collection<SharedProperty> sharedPropertiesPerDistinctCluster) {
		for (SharedProperty sharedProperty : sharedPropertiesPerDistinctCluster) {
			boolean isNeedForNewDeployement = false;
			LOGGER.debug("Verifying cluster distict shared property. ClusterId: ["+sharedProperty.getClusterId()+"], " +
					"Key: ["+sharedProperty.getKey()+"],  Value: ["+sharedProperty.getValue()+"].");			
			Deployment deployment = populateDeployment(sharedProperty);
			// loads all deployment for cluster with name, state should be taken into account!
			//Collection<Deployment> deploymentsPerCluster = deployerService.loadDeploymentsForClusterWithName(deployment);
			//deployment.setDeploymentTimestamp(new Timestamp(sharedProperty.getTimestamp().getTime()));
			LOGGER.debug("Deployment populated from sharedproperty. OrgId: ["+deployment.getOrganizationId()+", InstId: ["+deployment.getInstanceId()+"], ClusterId: ["+deployment.getClusterId()+"], Name: ["+deployment.getName()+"], TimeStamp ["+deployment.getDeploymentTimestamp()+"].");						
			Collection<Deployment> newerDeployments = deployerService.loadNewerDeploymentsForClusterWithNameInDeployedState(deployment);
			LOGGER.debug("DeployerService retuned isEmpty() for loadNewerDeploymentsForClusterWithNameInDeployedState: ["+newerDeployments.isEmpty()+"].");			
			
//			if (deploymentsPerCluster.size() == 0) {
//				isNeedForNewDeployement = true;
//			}
//			for (Deployment existingDeployment : deploymentsPerCluster) {
//				isNeedForNewDeployement = verifyTimestampAndAddIfNeedsToBeDeployed(sharedPropertiesPerDistinctCluster, sharedProperty, existingDeployment, isNeedForNewDeployement);
//			}
//			if (isNeedForNewDeployement) {
//				sharedProperties.add(sharedPropertiesPerDistinctCluster);
//			}
			if (newerDeployments.isEmpty()) {
				LOGGER.info("Adding not deployed cluster distict shared property. ClusterId: ["+sharedProperty.getClusterId()+"], " +
						"Key: ["+sharedProperty.getKey()+"],  Value: ["+sharedProperty.getValue()+"].");
				sharedProperties.add(sharedPropertiesPerDistinctCluster);
				// if any of properties is updated properties file for cluster needs to be updated
				return;
				// if this handles properties as one deployment it should be updated if even one property is newer
				//return;
			}
		}
	}

	/*
	private boolean verifyTimestampAndAddIfNeedsToBeDeployed(Collection<SharedProperty> sharedPropertiesPerDistinctCluster, SharedProperty sharedProperty, Deployment existingDeployment, boolean isNeedForNewDeployement) {
		if (existingDeployment.getDeploymentTimestamp().before(sharedProperty.getTimestamp())) {
			sharedProperties.add(sharedPropertiesPerDistinctCluster);
			LOGGER.debug("ExistingDeployment Timestamp was before sharedProperty Timestamp. ClusterId: ["+sharedProperty.getClusterId()+"], " +
					"Key: ["+sharedProperty.getKey()+"],  Value: ["+sharedProperty.getValue()+"].");			
			//isNeedForNewDeployement = true;
			return true;
		}
		return false;
	}
	*/
	
	private Deployment populateDeployment(SharedProperty sharedProperty) {
		Deployment deployment = new Deployment();
		deployment.setOrganizationId(sharedProperty.getOrganizationId());
		deployment.setInstanceId(sharedProperty.getInstanceId()); 
		deployment.setClusterId(sharedProperty.getClusterId()); 
		deployment.setDeploymentTimestamp(sharedProperty.getPropertyTimestamp());
		deployment.setName("application");
		return deployment;
	}
		
}
