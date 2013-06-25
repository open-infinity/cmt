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

package org.openinfinity.cloud.application.properties.model;

import org.openinfinity.cloud.domain.SharedProperty;

/**
 * Extended deployment data
 *  
 * @author Ilkka Leinonen
 * 
 * @version 1.0.0 Initial version
 * @since 1.2.0
 */

// TODO use lombok
public class SharedPropertyTableData extends SharedProperty {
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
	
	public SharedPropertyTableData(SharedProperty sharedProperty, String organization, String instance, String cluster){
		this.setKey(sharedProperty.getKey());
		this.setValue(sharedProperty.getValue());
		this.setClusterId(sharedProperty.getClusterId());
		this.setInstanceId(sharedProperty.getInstanceId());
		this.setOrganizationId(sharedProperty.getOrganizationId()); 
		this.organization = organization;
		this.instance = instance;
		this.cluster = cluster;
		formattedTime = sharedProperty.getTimestamp().toString();
	}
	
}
