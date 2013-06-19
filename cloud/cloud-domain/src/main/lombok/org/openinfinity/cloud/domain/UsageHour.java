/*
 * Copyright (c) 2011-2013 the original author or authors.
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

import java.util.Date;

import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.NoArgsConstructor;

import org.openinfinity.core.annotation.NotScript;

/**
 * Usage hour metadata represents a state in certain time.
 * 
 * @author Ilkka Leinonen
 * @version 1.0.0
 * @since 1.2.0
 */
@Data
@NoArgsConstructor
public class UsageHour {

	private long id;
	
	@NotNull
	private long organizationId;
	
	@NotScript
	@NotNull
	private int platformId;
	
	/*
	 * Duplicated from cluster_type_tbl.title.
	 */
	@NotScript
	@NotNull
	private String clusterTypeTitle;
	
	@NotNull
	private int clusterId;
	
	@NotScript
	@NotNull
	private int machineId;
	
	/*
	 * Duplicated from cluster_tbl.cluster_machine_type.
	 */
	@NotScript
	@NotNull
	private int machineTypeId;
	
	/*
	 * Duplicated from cluster_tbl.machine_type_tbl.name.
	 */
	@NotScript
	@NotNull
	private String machineTypeName;
	
	/*
	 * Duplicated from cluster_tbl.machine_type_tbl.spec.
	 */
	@NotScript
	@NotNull
	private String machineTypeSpec;
	
	@NotNull
	private Date timeStamp;
	
	private VirtualMachineState virtualMachineState;
	
	public enum VirtualMachineState {
		
		STARTED(VIRTUAL_MACHINE_STATE_STARTED), 
		
		PAUSED(VIRTUAL_MACHINE_STATE_PAUSED), 
		
		STOPPED(VIRTUAL_MACHINE_STATE_STOPPED), 
		
		TERMINATED(VIRTUAL_MACHINE_STATE_TERMINATED), 
		
		RESUMED(VIRTUAL_MACHINE_STATE_RESUMED);
		
		private int state;
		
		VirtualMachineState(int state) {
			this.state = state;
		}
		
		public int getValue() {
			return state;
		}
		
	}
	
	public static VirtualMachineState getVirtualMachineStateWithNumericValue(int virtualMachineStateNumericValue) {
		switch (virtualMachineStateNumericValue) {
			case VIRTUAL_MACHINE_STATE_STARTED : return VirtualMachineState.STARTED;
			case VIRTUAL_MACHINE_STATE_PAUSED : return VirtualMachineState.PAUSED;
			case VIRTUAL_MACHINE_STATE_STOPPED : return VirtualMachineState.STOPPED;
			case VIRTUAL_MACHINE_STATE_TERMINATED : return VirtualMachineState.TERMINATED;
			case VIRTUAL_MACHINE_STATE_RESUMED : return VirtualMachineState.RESUMED;
			default: return VirtualMachineState.STOPPED;
		}
	}
	
	public static final int VIRTUAL_MACHINE_STATE_STARTED = 1;
	public static final int VIRTUAL_MACHINE_STATE_PAUSED = 2;
	public static final int VIRTUAL_MACHINE_STATE_STOPPED = 3;
	public static final int VIRTUAL_MACHINE_STATE_TERMINATED = 4;
	public static final int VIRTUAL_MACHINE_STATE_RESUMED = 5;
	
}
