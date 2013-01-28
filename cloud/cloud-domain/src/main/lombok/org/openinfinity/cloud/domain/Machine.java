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

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.NoArgsConstructor;

import org.openinfinity.core.annotation.NotScript;

/**
 * Machine domain object
 *  
 * @author Ilkka Leinonen
 * @author Ossi Hämäläinen
 * @author Juha-Matti Sironen
 * 
 * @version 1.0.0 Initial version
 * @since 1.0.0
 */

@Data
@RequiredArgsConstructor
@NoArgsConstructor
public class Machine implements Serializable {
	
	@NotScript	
	private int id;
	
	@NotScript
	private int projectId;
	
	@NotScript
	private int clusterId;
	
	@NotScript @NonNull
	private String instanceId;
	
	@NotScript @NonNull
	private String name;
	
	@NotScript @NonNull
	private int key;
	
	@NotScript @NonNull
	private String dnsName;
	
	@NotScript
	private String privateDnsName;
	
	@NotScript
	private String userName;
	
	@NotScript
	private int running;
	
	@NotScript
	private String state;
	
	@NotScript
	private String type;
	
	@NotScript
	private int configured;
	
	@NotScript
	private int cloud;

}