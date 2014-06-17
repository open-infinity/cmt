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

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;

import org.openinfinity.cloud.domain.Deployment;
import org.openinfinity.core.annotation.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Periodic processor for deployed artifacts in the staging area to create <code>Deployment</code> in the staging area.
 * 
 * @author Ilkka Leinonen
 * @author Tommi Siitonen
 * @version 1.0.0
 * @since 1.2.0
 */
@Component("periodicStagingAreaProcessor")
public class PeriodicStagingAreaProcessor implements ItemProcessor<File, Map<Deployment, File>> {

	private static final Logger LOGGER = LoggerFactory.getLogger(PeriodicStagingAreaProcessor.class);
	
	@Value("${stagingArea}")
	private String stagingArea;
	
	@Log
	@Override
	public Map<Deployment, File> process(File file) throws Exception {
		return parseMetadataFromDirectoriesAndCreateDeployments(file);
	}
	
	public Map<Deployment, File> parseMetadataFromDirectoriesAndCreateDeployments(File file) throws Exception {
		// <platform-version>/? , <action> or .delete file?
		// <stagingarea>/<availability-zone>/<orgnization>/<instance>/<cluster>/<type>/<name>/<artifact>
		int directoryIndex = 1;
		String path = file.getAbsolutePath();
		LOGGER.debug("Original absolute directory path: " + path);
		path = path.replace(stagingArea, "");
		LOGGER.debug("Relative directory path: " + path);
		String[] metadata = path.split(File.separator);
		
		LOGGER.debug("AvailabilityZone: " + metadata[directoryIndex]);		
		LOGGER.debug("OrganizationId: " + metadata[directoryIndex+1]);		
		LOGGER.debug("InstanceId: " + metadata[directoryIndex+2]);
		LOGGER.debug("ClusterId: " + metadata[directoryIndex+3]);
		LOGGER.debug("Type: " + metadata[directoryIndex+4]);
		LOGGER.debug("Name: " + metadata[directoryIndex+5]);
		
		Deployment deployment = new Deployment();
		deployment.setAvailabilityZone(Integer.parseInt(metadata[directoryIndex]));
		deployment.setOrganizationId(Integer.parseInt(metadata[++directoryIndex]));
		deployment.setInstanceId(Integer.parseInt(metadata[++directoryIndex]));
		deployment.setClusterId(Integer.parseInt(metadata[++directoryIndex]));
		deployment.setType(metadata[++directoryIndex]);
		deployment.setName(metadata[++directoryIndex]);
		deployment.setLocalTimeStamp(file.lastModified());
		deployment.setInputStream(new FileInputStream(file));
		Map<Deployment, File> map = new HashMap<Deployment, File>();
		map.put(deployment, file);
		return map;
	}
	
}
