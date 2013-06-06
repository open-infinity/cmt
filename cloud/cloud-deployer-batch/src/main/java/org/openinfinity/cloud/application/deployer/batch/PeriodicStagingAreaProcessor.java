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
 * @version 1.0.0
 * @since 1.2.0
 */
@Component("periodicStagingAreaProcessor")
public class PeriodicStagingAreaProcessor implements ItemProcessor<File, Deployment> {

	private static final Logger LOGGER = LoggerFactory.getLogger(PeriodicStagingAreaProcessor.class);
	
	@Value("${awsaccesskeyid}")
	private String stagingArea;
	
	@Log
	@Override
	public Deployment process(File file) throws Exception {
		return parseMetadataFromDirectoriesAndCreateDeployments(file);
	}
	
	public Deployment parseMetadataFromDirectoriesAndCreateDeployments(File file) throws Exception {
		//<stagingarea>/<cloud>/<orgnization>/<instance>/<cluster>/<type>/<name>/<artifact>
		int directoryIndex = 0;
		String path = file.getAbsolutePath();
		LOGGER.debug("Original absolute directory path: " + path);
		path = path.replace(stagingArea, "");
		LOGGER.debug("Relative directory path: " + path);
		String[] metadata = path.split(File.separator);
		Deployment deployment = new Deployment();
		deployment.setCloudInstance(metadata[directoryIndex]);
		deployment.setInstanceId(Integer.parseInt(metadata[directoryIndex++]));
		deployment.setClusterId(Integer.parseInt(metadata[directoryIndex++]));
		deployment.setType(metadata[directoryIndex++]);
		deployment.setName(metadata[directoryIndex++]);
		deployment.setLocalTimeStamp(file.lastModified());
		deployment.setInputStream(new FileInputStream(file));
		return deployment;
	}
	
}
