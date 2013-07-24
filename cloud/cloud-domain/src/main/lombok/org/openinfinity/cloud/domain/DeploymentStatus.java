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

import java.io.InputStream;
import java.io.Serializable;
import java.util.Date;

import org.openinfinity.core.annotation.NotScript;

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
	
	private static final int DEPLOYMENT_STATE_UNDEPLOY = 10;	
	private static final int DEPLOYMENT_STATE_UNDEPLOYED = 11;	
	
	private static final int DEPLOYMENT_STATE_ERROR = 15;	
	
	private static final int DEPLOYMENT_STATE_TERMINATED = -1;

	private static final int DEPLOYMENT_STATE_CLUSTER_TERMINATED = -10;
	
	public enum DeploymentState {
		
		NOT_DEPLOYED (DEPLOYMENT_STATE_NOT_DEPLOYED), 
		
		DEPLOYED (DEPLOYMENT_STATE_DEPLOYED),
		
		UNDEPLOY (DEPLOYMENT_STATE_UNDEPLOY),		
		
		UNDEPLOYED (DEPLOYMENT_STATE_UNDEPLOYED),		
		
		ERROR (DEPLOYMENT_STATE_ERROR),		

		TERMINATED (DEPLOYMENT_STATE_TERMINATED), 
		
		CLUSTER_TERMINATED (DEPLOYMENT_STATE_CLUSTER_TERMINATED); 
		
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
			case DEPLOYMENT_STATE_UNDEPLOY : return DeploymentState.UNDEPLOY;
			case DEPLOYMENT_STATE_UNDEPLOYED : return DeploymentState.UNDEPLOYED;
			case DEPLOYMENT_STATE_ERROR : return DeploymentState.ERROR;
			case DEPLOYMENT_STATE_TERMINATED : return DeploymentState.TERMINATED;
			case DEPLOYMENT_STATE_CLUSTER_TERMINATED: return DeploymentState.CLUSTER_TERMINATED;
			default: return DeploymentState.NOT_DEPLOYED;
		}
	}
	
	int id;
	
	int machineId;
	
	DeploymentState deploymentState;

	Date timestamp;
	
	@NotScript
	private InputStream inputStream;
	
	Deployment deployment;
	
}
