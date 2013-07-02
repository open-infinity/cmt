package org.openinfinity.cloud.service.usage;

import java.util.Collection;
import java.util.Date;

import org.openinfinity.cloud.domain.Cluster;
import org.openinfinity.cloud.domain.Machine;
import org.openinfinity.cloud.domain.MachineType;
import org.openinfinity.cloud.domain.UsageHour;
import org.openinfinity.cloud.domain.UsageHour.VirtualMachineState;
import org.openinfinity.cloud.domain.UsagePeriod;
import org.openinfinity.cloud.domain.repository.administrator.ClusterRepository;
import org.openinfinity.cloud.domain.repository.administrator.MachineRepository;
import org.openinfinity.cloud.domain.repository.administrator.MachineTypeRepository;
import org.openinfinity.cloud.domain.repository.usage.UsageHourRepository;
import org.openinfinity.core.annotation.AuditTrail;
import org.openinfinity.core.annotation.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Implementation of the the <code>org.openinfinity.cloud.service.usage.UsageService</code> inteface for managing usage of the virtual machines.
 * 
 * @author Ilkka Leinonen
 * @version 1.0.0
 * @since 1.2.0
 */ 
@Service("usageService")
public class UsageServiceImpl implements UsageService {

	@Autowired
	private UsageHourRepository usageRepository;
	
	@Autowired
	private ClusterRepository clusterRepository;
	
	@Autowired
	private MachineTypeRepository machineTypeRepository;
	
	@Autowired
	private MachineRepository machineRepository;
	
	@Log
	@AuditTrail
	public void startVirtualMachineUsageMonitoring(long organizationId, int platformId, int clusterId, int machineId) {
		Cluster cluster = clusterRepository.getCluster(clusterId);
		MachineType machineType = machineTypeRepository.getMachineTypeById(cluster.getMachineType());
		Machine machine = machineRepository.getMachine(machineId);
		
		UsageHour usageHour = new UsageHour();
		usageHour.setInstanceId(cluster.getInstanceId());
		usageHour.setClusterId(clusterId);
		usageHour.setMachineId(machineId);
		usageHour.setOrganizationId(organizationId);
		usageHour.setPlatformId(platformId);
		usageHour.setVirtualMachineState(VirtualMachineState.STARTED);
		// The cluster type title is copied also to cluster_tbl.cluster_name so take advantage of that
		usageHour.setClusterTypeTitle(cluster.getName());
		// Sizes of the machines in the cluster (see machine_type_tbl)
		usageHour.setMachineTypeId(cluster.getMachineType());
		usageHour.setMachineTypeName(machineType.getName()); // (see machine_type_tbl)
		usageHour.setMachineTypeSpec(machineType.getSpecification()); // (see machine_type_tbl)
		usageHour.setMachineMachineType(machine.getType());
		usageHour.setClusterEbsImageUsed(cluster.getEbsImageUsed());
		usageHour.setClusterEbsVolumesUsed(cluster.getEbsVolumesUsed());
		usageRepository.store(usageHour);
	}
	
	@Log
	@AuditTrail
	public void resumeVirtualMachineUsageMonitoring(long organizationId, int platformId, int clusterId, int machineId) {
		UsageHour usageHours = usageRepository.loadByMachineId(machineId);
		usageHours.setVirtualMachineState(VirtualMachineState.RESUMED);
		usageRepository.store(usageHours);
	}

	@Log
	@AuditTrail
	public void stopVirtualMachineUsageMonitoring(long organizationId, int platformId, int clusterId, int machineId) {
		UsageHour usageHours = usageRepository.loadByMachineId(machineId);
		usageHours.setVirtualMachineState(VirtualMachineState.STOPPED);
		usageRepository.store(usageHours);
	}

	@Log
	@AuditTrail
	public void pauseVirtualMachineUsageMonitoring(int machineId) {
		UsageHour usageHours = usageRepository.loadByMachineId(machineId);
		usageHours.setVirtualMachineState(VirtualMachineState.PAUSED);
		usageRepository.store(usageHours);
	}

	@Log
	@AuditTrail
	public void terminateVirtualMachineUsageMonitoring(int machineId) {
		UsageHour usageHours = usageRepository.loadByMachineId(machineId);
		usageHours.setVirtualMachineState(VirtualMachineState.TERMINATED);
		usageRepository.store(usageHours);
	}

	@Log
	@AuditTrail
	public UsagePeriod loadUsagePeriod(long organizationId, Date startTime, Date endTime) {
		Collection<UsageHour> usageHours = usageRepository.loadUsageHoursByOrganizationIdAndUsagePeriod(organizationId, startTime, endTime);
		UsagePeriod usagePeriod = new UsagePeriod();
		usagePeriod.setOrganizationId(organizationId);
		usagePeriod.setStartTime(startTime);
		usagePeriod.setEndTime(endTime);
		usagePeriod.setUsageHours(usageHours);
		usagePeriod.loadUptimeHours();
		return usagePeriod;
	}

}
