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

package org.openinfinity.cloud.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.openinfinity.core.annotation.NotScript;

import lombok.Data;

/**
 * Domain class is responsible for storing instance specific information within cloud instance.
 * 
 * @author Ilkka Leinonen
 * @author Ossi Hämäläinen
 * @author Juha-Matti Sironen
 * @version 1.0.0 Initial version
 * @since 1.0.0
 */
@Data
public class Instance implements Serializable {
	
	@NotScript
	private int instanceId;
	@NotScript
	private int userId;
	@NotScript
	private Long organizationid;
	@NotScript
	private String name;
	@NotScript
	private int cloudType;
	@NotScript
	private String zone;
	@NotScript
	private String status;
	@NotScript
	private String userName;
	@NotScript
	private String organizationName;
	
	//Instance parameters
	private List<InstanceParameter> parameters;
		
	public void addParameter(InstanceParameter param) {
		if (parameters==null){
			parameters=new ArrayList<InstanceParameter>();
		}
		parameters.add(param);		
	}
	
	public String toCSV(String delimiter) {
		return instanceId + delimiter +
				userId + delimiter +
				organizationid + delimiter +
				name + delimiter +
				cloudType + delimiter +
				zone + delimiter +
				status + delimiter +
				userName + delimiter +
				organizationName;
	}
		
}