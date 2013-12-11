package org.openinfinity.cloud.service.backup;

import java.util.List;

/**
 * Information about restore approach of a cluster. This object is returned 
 * by CloudBackup.analyze.
 * 
 * @author Timo Saarinen
 */
public class RestoreInfo {
	public enum ClusterApproach {
		/**
		 * The whole cluster have to be rebuilt, and its data restored 
		 * from backup.
		 */
		FULL_RESTORE,
		
		/**
		 * Part of the cluster have to be rebuilt. The machines replacing the
		 * old ones are define in MachineInfo objects (setNewMachineId). 
		 */
		PARTIAL_RESTORE,
		
		/**
		 * No operations needed. The cluster is valid as it is.
		 */
		NO_OPERATION
	}
	
	private ClusterApproach approach = ClusterApproach.NO_OPERATION;
	
	private List<MachineInfo> machines;

	/**
	 * Constructor.
	 * 
	 * @param clusterId The cluster to be analyzed
	 * @param machines  List of machines, that have been lost or damaged. 
	 */
	public RestoreInfo(int clusterId, List<MachineInfo> machines) {
		if (machines != null) {
			this.machines = machines;
		} else {
			
		}
	}
	
	/**
	 * Information to cloud backup about restore.
	 */
	public static class MachineInfo {
		public enum MachineApproach {
			/**
			 * Cloud Backup restores data. 
			 */
			RESTORE,
			
			/**
			 * Cloud Backup either lets replication take care of data restore. 
			 */
			JOIN,
			
			/**
			 * The machine won't be modified by Cloud Backup.
			 */
			NONE
		}

		/**
		 * Current machine id.
		 */
		private int machineId;
		
		/**
		 * New machine id.
		 */
		private int newMachineId;
		
		public MachineInfo(int machineId) {
			this.machineId = machineId;
		}

		public int getMachineId() {
			return machineId;
		}

		public void setMachineId(int machineId) {
			this.machineId = machineId;
		}

		public int getNewMachineId() {
			return newMachineId;
		}

		public void setNewMachineId(int newMachineId) {
			this.newMachineId = newMachineId;
		}
	}

	public ClusterApproach getApproach() {
		return approach;
	}

	public void setApproach(ClusterApproach approach) {
		this.approach = approach;
	}

	public List<MachineInfo> getMachines() {
		return machines;
	}

	public void setMachines(List<MachineInfo> machines) {
		this.machines = machines;
	}
}
