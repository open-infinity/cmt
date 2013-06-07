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
import java.util.List;
import java.util.Map;

import org.openinfinity.cloud.domain.Deployment;
import org.openinfinity.cloud.service.deployer.DeployerService;
import org.openinfinity.core.annotation.Log;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Periodic writer to transfer deployments to backend service.
 * 
 * @author Ilkka Leinonen
 * @version 1.0.0
 * @since 1.2.0
 */
@Component("periodicStagingAreaWriter")
public class PeriodicStagingAreaWriter implements ItemWriter<Map<Deployment, File>> {

	@Autowired
	private DeployerService deployerService;
	
	@Log
	@Override
	public void write(List<? extends Map<Deployment, File>> deploymentsAndFiles) throws Exception {
		for (Map<Deployment, File> deploymentAndFile : deploymentsAndFiles) {
			for (Map.Entry<Deployment, File> entry : deploymentAndFile.entrySet()) {
				Deployment deployment = entry.getKey();
				File file = entry.getValue();
				deployerService.deploy(deployment);
				if (file.exists()) {
					file.delete();
				}
			}	
		}
	}

}
