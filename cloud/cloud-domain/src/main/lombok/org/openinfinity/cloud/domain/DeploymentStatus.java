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
package org.openinfinity.cloud.domain;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

/**
 * Deployment status defines the state of the deployed clustered machines.
 * 
 * @author Ilkka Leinonen
 * @version 1.0.0
 * @since 1.2.0
 */
@Data
public class DeploymentStatus implements Serializable {
	
	private static final int DEPLOYMENT_STATE_NOT_DEPLOYED = 0;
	
	private static final int DEPLOYMENT_STATE_DEPLOYED = 1;
	
	public enum DeploymentState {
		
		NOT_DEPLOYED (DEPLOYMENT_STATE_NOT_DEPLOYED), 
		
		DEPLOYED (DEPLOYMENT_STATE_DEPLOYED); 
		
		private int state;
		
		DeploymentState(int state) {
			this.state = state;
		}
		
		public int getValue() {
			return state;
		}
		
	}
	
	public static DeploymentState getDeploymentStateWithNumericValue(int deploymentStateNumericValue) {
		switch (deploymentStateNumericValue) {
			case DEPLOYMENT_STATE_NOT_DEPLOYED : return DeploymentState.NOT_DEPLOYED;
			case DEPLOYMENT_STATE_DEPLOYED : return DeploymentState.DEPLOYED;
			default: return DeploymentState.NOT_DEPLOYED;
		}
	}
	
	int id;
	
	int machineId;
	
	DeploymentState deploymentState;

	Date timestamp;
	
	Deployment deployment;
	
}