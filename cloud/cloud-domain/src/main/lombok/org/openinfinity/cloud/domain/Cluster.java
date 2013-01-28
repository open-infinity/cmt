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

import org.openinfinity.core.annotation.NotScript;

import lombok.Data;

/**
 * Domain class is responsible for storing cluster specific information within cloud instance.
 *
 * @author Ilkka Leinonen
 * @author Ossi Hämäläinen
 * @author Juha-Matti Sironen
 * @version 1.0.0 Initial version
 * @since 1.0.0
 */

@Data
public class Cluster implements Serializable {
	
	@NotScript
	private int id;
	@NotScript
	private String name;
	@NotScript
	private String lbName;
	@NotScript
	private String lbDns;
	@NotScript
	private String lbInstanceId;
	@NotScript
	private String securityGroupId;
	@NotScript
	private String securityGroupName;
	@NotScript
	private int numberOfMachines;
	@NotScript
	private int instanceId;
	@NotScript
	private int type;
	@NotScript
	private int published;
	@NotScript
	private int live;
	@NotScript
	private String multicastAddress;
	@NotScript
	private int machineType;
		
}