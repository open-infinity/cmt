package org.openinfinity.cloud.domain;

import lombok.Getter;
import lombok.ToString;

/**
 * Immutable class to hold usage information for machine and it's uptime. 
 * 
 * @author Esa Kytölä
 */
@ToString
public final class MachineUsage {
	
	public enum State {
		VALID, INVALID;
	}
	
	@Getter private final State state;
	@Getter private final int machineId;
	@Getter private final int instanceId;
	@Getter private final long uptime; // Milliseconds
	
	@Getter private final int errorCount;
	@Getter private final String errorMessage;
	
	public MachineUsage(int machineId, int instanceId, long uptime,
			int errorCount, String errorMessage) {
		super();
		
		if (errorCount == 0) {
			state = MachineUsage.State.VALID;
		} else {
			state = MachineUsage.State.INVALID;
		}
		
		this.machineId = machineId;
		this.instanceId = instanceId;
		this.uptime = uptime;
		this.errorCount = errorCount;
		this.errorMessage = errorMessage;
	}

	public long getUptimeInSeconds() {
		return uptime / 1000;
	}
	
	public long getUptimeInMinutes() {
		return uptime / 1000 / 60;
	}
}
