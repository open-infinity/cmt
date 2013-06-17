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
import java.util.ArrayList;
import java.util.List;

import org.openinfinity.cloud.util.filesystem.FileUtil;
import org.openinfinity.core.annotation.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Periodic reader for deployed artifacts in the staging area.
 * 
 * @author Ilkka Leinonen
 * @version 1.0.0
 * @since 1.2.0
 */
@Component("periodicStagingAreaReader")
public class PeriodicStagingAreaReader implements ItemReader<File> {

	private static final Logger LOGGER = LoggerFactory.getLogger(PeriodicStagingAreaReader.class);
	
	@Value("${stagingArea}")
	private String stagingArea;
	
	private int index = 0;
	
	private List<File> stagingAreaFiles = new ArrayList<File>();
	
	@Log
	@Override
	public File read() throws Exception {
		LOGGER.debug("Reading files in staging area: "+stagingArea);
		File stagingAreaDirectory = new File(stagingArea);
		if (stagingAreaDirectory.isDirectory() && stagingAreaFiles.isEmpty()) {
			FileUtil.findFilesRecursively(stagingAreaDirectory, stagingAreaFiles);
			stagingAreaFiles = FileUtil.sortByLastModifiedTimestamp(stagingAreaFiles);
			LOGGER.trace("Initializing reader finished. [" + stagingAreaFiles.size() + "] deployments loaded.");			
		}
		if (index < stagingAreaFiles.size()) {
			LOGGER.trace("Processing deployment statuses, current index is [" + index + "].");
			File retValue = stagingAreaFiles.get(index++);
			return retValue;
		} else {
			LOGGER.trace("Reader finished, all items handled. Index is [" + index + "]. Returning null");			
			//stagingAreaFiles.clear();
			index = 0;
			return null;
		}	
	}

	

}