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

    public static enum ScheduledScalingState{
        READY_FOR_SCALE_IN(0),
        READY_FOR_SCALE_OUT(1);

        private final int value;
        private ScheduledScalingState(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

	@NonNull 
	@NotScript
	private int clusterId;
		
	@NonNull 
	@NotScript
	private boolean periodicScalingOn;	
	
	@NonNull
	@NotScript
	private boolean scheduledScalingOn;
	
	@NonNull
	@NotScript
	private int scheduledScalingState;
	
	@NonNull 
	@NotScript
	private int maxNumberOfMachinesPerCluster;
	
	@NonNull 
	@NotScript
	private int minNumberOfMachinesPerCluster;
	
	@NonNull 
	@NotScript
	private float maxLoad;
	
	@NonNull 
	@NotScript
	private float minLoad;
		
	@NonNull
	@NotScript
	private Timestamp periodFrom;
	
	@NonNull
	@NotScript
	private Timestamp periodTo;
	
	@NonNull
	@NotScript
	private int clusterSizeNew; 

	@NonNull
	@NotScript
	private int clusterSizeOriginal; 
	
	@NonNull 
	@NotScript
	private int jobId;
}