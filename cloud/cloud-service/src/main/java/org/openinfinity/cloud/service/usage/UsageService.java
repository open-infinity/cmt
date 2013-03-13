package org.openinfinity.cloud.service.usage;

import java.util.Date;

import org.openinfinity.cloud.domain.UsagePeriod;

/**
 * Interface for managing usage of the virtual machines.
 * 
 * @author Ilkka Leinonen
 * @version 1.0.0
 * @since 1.2.0
 */
public interface UsageService {
	
	/**
	 * Stars a virtual machine usage hour trend gathering.
	 * 
	 * @param organizationId
	 * @param platformId
	 * @param clusterId
	 * @param machineId
	 */
	void startVirtualMachineUsageMonitoring(long organizationId, String platformId, long clusterId, String machineId);
	
	/**
	 * Resumes a virtual machine usage hour trend gathering.
	 * 
	 * @param organizationId
	 * @param platformId
	 * @param clusterId
	 * @param machineId
	 */
	void resumeVirtualMachineUsageMonitoring(long organizationId, int platformId, long clusterId, String machineId);
	
	/**
	 * Stops a virtual machine usage hour trend gathering.
	 * 
	 * @param organizationId
	 * @param platformId
	 * @param clusterId
	 * @param machineId
	 */
	void stopVirtualMachineUsageMonitoring(long organizationId, int platformId, long clusterId, String machineId);
	
	/**
	 * Pauses a virtual machine usage hour trend gathering.
	 * 
	 * @param organizationId
	 * @param platformId
	 * @param clusterId
	 * @param machineId
	 */
	void pauseVirtualMachineUsageMonitoring(String machineId);
	
	/**
	 * Terminates a virtual machine usage hour trend gathering.
	 * 
	 * @param organizationId
	 * @param platformId
	 * @param clusterId
	 * @param machineId
	 */
	void terminateVirtualMachineUsageMonitoring(String machineId);

	/**
	 * Loads usage period based on organization id, start time of the period, and end time of the period.
	 * 
	 * @param organizationId
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	UsagePeriod loadUsagePeriod(long organizationId, Date startTime, Date endTime);

}
