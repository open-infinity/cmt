package org.openinfinity.cloud.application.invoicing.batch;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openinfinity.cloud.domain.Instance;
import org.openinfinity.cloud.domain.InstanceShare;
import org.openinfinity.cloud.domain.InstanceShareDetail;
import org.openinfinity.cloud.domain.MachineUsage;

public class InstanceInvoice {
	
	private Date periodStart;
	private Date periodEnd;
	
	private Instance instance;
	private InstanceShare instanceShare;
	private Map<Integer, MachineUsage> machineUsages;
	
	public InstanceInvoice() {
		machineUsages = new HashMap<Integer, MachineUsage>();
	}
	
	public Date getPeriodStart() {
		return periodStart;
	}

	public void setPeriodStart(Date periodStart) {
		this.periodStart = periodStart;
	}

	public Date getPeriodEnd() {
		return periodEnd;
	}

	public void setPeriodEnd(Date periodEnd) {
		this.periodEnd = periodEnd;
	}
	
	/**
	 * Get the instance.
	 * 
	 * @return
	 */
	public Instance getInstance() {
		return instance;
	}
	
	/**
	 * Set the instance.
	 * 
	 * @param instance
	 */
	public void setInstance(Instance instance) {
		this.instance = instance;
	}
	
	/**
	 * Get current instance share information. Returns null if there is no share information for the instance.
	 * 
	 * @return
	 */
	public InstanceShare getInstanceShare() {
		return instanceShare;
	}
	
	/**
	 * Set current instance share information. Set to null if there is no share information for the instance.
	 * 
	 * @param instanceShare
	 */
	public void setInstanceShare(InstanceShare instanceShare) {
		this.instanceShare = instanceShare;
	}
	
	/**
	 * Get machine usages for the instance.
	 * 
	 * @return a Map that has a machine id as a key
	 */
	public Map<Integer, MachineUsage> getMachineUsages() {
		return machineUsages;
	}
	
	/**
	 * Set machine usage for the instance. Saves to a Map using machine id as a key.
	 * 
	 * @param machineUsage
	 */
	public void setMachineUsage(MachineUsage machineUsage) {
		this.machineUsages.put(machineUsage.getMachineId(), machineUsage);
	}
	
	/**
	 * Count the sum of the MachineUsage's uptime in minutes.
	 * 
	 * @return
	 */
	public int sumUptimeInMinutes() {
		int sum = 0;
		for (MachineUsage machineUsage : machineUsages.values()) {
			sum += machineUsage.getUptimeInMinutes();
		}
		return sum;
	}
	
	/**
	 * Overall format of the CSV file:
	 * 
	 * Objects print their data to CSV format in this order:
	 * 1. instance data
	 * 2. machine usage data
	 * 3. share data
	 * 
	 * @return
	 */
	public String toCSV(String lineFeed, String delimiter) {
		StringBuffer strBuffer = new StringBuffer();
		
		for (MachineUsage machineUsage : machineUsages.values()) {
			if (instanceShare == null) {				
				strBuffer.append(instance.toCSV(delimiter) + delimiter + machineUsage.toCSV(delimiter) + delimiter + "NO SHARE DATA" + lineFeed);
			} else {
				// Share data was found. Iterate through share data and create row for each share row.
				List<InstanceShareDetail> shareDetails = instanceShare.getInstanceShareDetails();
				for (InstanceShareDetail shareDetail : shareDetails) {
					strBuffer.append(
							instance.toCSV(delimiter) + delimiter +
							machineUsage.toCSV(delimiter) + delimiter +
							instanceShare.toCSV(delimiter) + delimiter +
							shareDetail.toCSV(delimiter) + lineFeed
							);
				}		
			}
		}
		
		return strBuffer.toString();
	}
	
	@Override
	public String toString() {
		return "TODO: InstanceInvoice [instanceShare=" + instanceShare
				+ ", machineUsages=" + machineUsages + "]";
	}
	
	
}
