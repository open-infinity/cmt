package org.openinfinity.cloud.domain;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.validation.constraints.NotNull;

import org.openinfinity.core.annotation.NotScript;

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
		USAGE_DATA_VALID, USAGE_DATA_INVALID;
	}
	private static final SimpleDateFormat DF = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
	
	@Getter private final State state;
	@Getter private final int machineId;
	@Getter private final int instanceId;
	
	@Getter private final String clusterTypeTitle;
	@Getter private final int clusterId;
	@Getter private final int machineTypeId;
	@Getter private final String machineTypeName;
	@Getter private final String machineTypeSpec;
	@Getter private final String machineMachineType;
	@Getter private final int clusterEbsImageUsed;
	@Getter private final int clusterEbsVolumesUsed;
	
	@Getter private final Date started;
	@Getter private final Date stopped;
	@Getter private final Date periodStart;
	@Getter private final Date periodEnd;
	
	@Getter private final long uptime; // Milliseconds
	
	@Getter private final int errorCount;
	@Getter private final String errorMessage;
	
	public MachineUsage(
			int machineId,
			int instanceId,
			
			String clusterTypeTitle,  
			int clusterId,
			int machineTypeId,
			String machineTypeName,
			String machineTypeSpec,
			String machineMachineType,
			int clusterEbsImageUsed,
			int clusterEbsVolumesUsed,
			
			Date started,
			Date stopped,
			Date periodStart,
			Date periodEnd,
			long uptime,
			int errorCount,
			String errorMessage
			) {
		
		super();
		
		if (errorCount == 0) {
			state = MachineUsage.State.USAGE_DATA_VALID;
		} else {
			state = MachineUsage.State.USAGE_DATA_INVALID;
		}
		
		this.machineId = machineId;
		this.instanceId = instanceId;
		
		this.clusterTypeTitle = clusterTypeTitle;
		this.clusterId = clusterId;
		this.machineTypeId = machineTypeId;
		this.machineTypeName = machineTypeName;
		this.machineTypeSpec = machineTypeSpec;
		this.machineMachineType = machineMachineType;
		this.clusterEbsImageUsed = clusterEbsImageUsed;
		this.clusterEbsVolumesUsed = clusterEbsVolumesUsed;
		
		this.started = started;
		this.stopped = stopped;
		this.periodStart = periodStart;
		this.periodEnd = periodEnd;
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
	
	public String toCSV(String delimiter) {
		
		return state + delimiter +
				machineId + delimiter +
				//instanceId + delimiter +
				clusterTypeTitle + delimiter +
				clusterId + delimiter +
				machineTypeId + delimiter +
				machineTypeName + delimiter +
				machineTypeSpec + delimiter +
				machineMachineType + delimiter +
				clusterEbsImageUsed + delimiter +
				clusterEbsVolumesUsed + delimiter +
				formatDate(started) + delimiter +
				formatDate(stopped) + delimiter +
				formatDate(periodStart) + delimiter +
				formatDate(periodEnd) + delimiter +
				getUptimeInMinutes() + delimiter +
				errorCount + delimiter +
				errorMessage;
	}
	
	private String formatDate(Date d) {
		String ret = "";
		if (d != null) {
			ret = DF.format(d);
		}
		return ret;
	}
}
