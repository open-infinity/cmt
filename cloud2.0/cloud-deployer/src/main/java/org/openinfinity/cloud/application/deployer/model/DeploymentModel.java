/*
 * Copyright (c) 2012 the original author or authors.
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
package org.openinfinity.cloud.application.deployer.model;

import java.io.IOException;
import java.io.InputStream;

import org.openinfinity.cloud.domain.Deployment;
import org.springframework.web.multipart.commons.CommonsMultipartFile;


/**
 * 
 * 
 * @author Ilkka Leinonen
 * @version 1.0.0
 * @since 1.0.0
 */
public class DeploymentModel extends Deployment {

	private CommonsMultipartFile fileData;
	
	public CommonsMultipartFile getFileData() {
		return fileData;
	}
	
	public InputStream getInputStream() {
		InputStream inputStream = null;
		try {
			inputStream = fileData.getInputStream();
		} catch (IOException e) {
			//SystemException systemException = new SystemException();
		}
		return inputStream;
	}
	
	public void setFileData(CommonsMultipartFile fileData) {
		this.fileData = fileData;
	}
	
}