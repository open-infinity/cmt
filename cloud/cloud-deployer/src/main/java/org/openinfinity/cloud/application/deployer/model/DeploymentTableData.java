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

import org.openinfinity.cloud.domain.Deployment;

/**
 * Extended deployment data
 *  
 * @author Vedran Bartonicek
 * 
 * @version 1.0.0 Initial version
 * @since 1.0.0
 */

// TODO use lombok
public class DeploymentTableData extends Deployment{
	private static final long serialVersionUID = 6080998463580546974L;
	private String organization;
	private String instance;
	private String cluster;
	private String formattedTime;
	
	public String getOrganization() {
		return organization;
	}

	public void setOrganization(String organization) {
		this.organization = organization;
	}

	public String getInstance() {
		return instance;
	}

	public void setInstance(String instance) {
		this.instance = instance;
	}

	public String getCluster() {
		return cluster;
	}

	public void setCluster(String cluster) {
		this.cluster = cluster;
	}

	public String getFormattedTime() {
		return formattedTime;
	}

	public void setFormattedTime(String time) {
		this.formattedTime = time;
	}
	
	public DeploymentTableData(Deployment aDeployment, String aOrganization, String aInstance, String aCluster){
		super(aDeployment);	
		this.organization = aOrganization;
		instance = aInstance;
		cluster = aCluster;
		formattedTime = aDeployment.getDeploymentTimestamp().toString();
	}
	
}
