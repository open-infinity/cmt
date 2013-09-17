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
import java.sql.Timestamp;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.NoArgsConstructor;

import org.openinfinity.core.annotation.NotScript;

/**
 * Domain object for cloud scaling rule.
 * 
 * @author Ilkka Leinonen
 * @author Vedran Bartonicek
 * @version 1.0.0
 * @since 1.0.0
 */

@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class ScalingRule implements Serializable {
	
	@NonNull 
	@NotScript
	protected int clusterId;
		
	@NonNull 
	@NotScript
	protected boolean periodicScalingOn;	
	
	@NonNull
	@NotScript
	protected boolean scheduledScalingOn;
	
	@NonNull
	@NotScript
	protected int scheduledScalingState;
	
	@NonNull 
	@NotScript
	protected int maxNumberOfMachinesPerCluster;
	
	@NonNull 
	@NotScript
	protected int minNumberOfMachinesPerCluster;
	
	@NonNull 
	@NotScript
	protected float maxLoad;
	
	@NonNull 
	@NotScript
	protected float minLoad;
		
	@NonNull
	@NotScript
	protected Timestamp periodFrom;
	
	@NonNull
	@NotScript
	protected Timestamp periodTo;
	
	@NonNull
	@NotScript
	private int clusterSizeNew; 

	@NonNull
	@NotScript
	protected int clusterSizeOriginal; 
	
	@NonNull 
	@NotScript
	protected int jobId;

}
