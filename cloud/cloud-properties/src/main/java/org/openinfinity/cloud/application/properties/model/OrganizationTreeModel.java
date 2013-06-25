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
package org.openinfinity.cloud.application.properties.model;

import org.openinfinity.cloud.domain.SharedProperty;

/**
 * 
 * 
 * @author Ilkka Leinonen
 * @version 1.0.0
 * @since 1.0.0
 */
public class OrganizationTreeModel implements Comparable<OrganizationTreeModel>{
	
	private long id = 0;
	private String name;
	
	private SharedProperty sharedProperty;
	
	public SharedProperty getSharedProperty() {
		return sharedProperty;
	}
	public void setSharedProperty(SharedProperty sharedProperty) {
		this.sharedProperty = sharedProperty;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	@Override
	public int compareTo(OrganizationTreeModel organizationTreeModel) {
		if(id == 0) return -1;
		if(id > getId()) return 1;
		else return 0;
	}
	@Override
	public String toString() {
		return "OrganizationTreeModel [id=" + id + ", name=" + name
				+ ", deployment=" + sharedProperty + "]";
	}
	
	

}
