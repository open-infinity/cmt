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

import org.openinfinity.core.annotation.NotScript;

/**
 * Authorized route between network elements.
 * 
 * @author Ilkka Leinonen
 * @author Ossi Hämäläinen
 * @author Juha-Matti Sironen
 * @version 1.0.0 Initial version
 * @since 1.0.0
 */
@Data
public class AuthorizationRoute implements Serializable {	

	@NotScript
	private int id;
	@NotScript
	private int instanceId;
	@NotScript
	private int clusterId;
	@NotScript
	private String cidrIp;
	@NotScript
	private String protocol;
	@NotScript
	private String securityGroupName;
	@NotScript
	private int fromPort;
	@NotScript
	private int toPort;

	public AuthorizationRoute(){}
	
	public AuthorizationRoute(int aInstanceId, int aClusterId, String aCidrIp, String aProtocol, String aSgName, int aFromPort, int aToPort){
		instanceId =  aInstanceId;
		clusterId = aClusterId;
		cidrIp = aCidrIp;
		protocol = aProtocol;
		securityGroupName = aSgName;
		fromPort = aFromPort;
		toPort = aToPort;
	}
}