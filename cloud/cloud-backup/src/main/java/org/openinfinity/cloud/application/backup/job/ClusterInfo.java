package org.openinfinity.cloud.application.backup.job;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.log4j.Logger;

/**
 * Keeps track of ClusterJob objects and cluster backup initialization and finalization.
 * 
 * @author Timo Saarinen
 */
public class ClusterInfo implements ClusterSyncOperations {
	private Logger logger = Logger.getLogger(ClusterInfo.class);
	
	private int clusterId;
	
	private int toasInstanceId;
	
	/**
	 * List of instance jobs, that are making backup/restore
	 */
	private Set<InstanceJob> instanceJobsOnProgress = new HashSet<InstanceJob>();

	public ClusterInfo(int cluster_id, int toas_instance_id) {
		this.clusterId = cluster_id;
		this.toasInstanceId = toas_instance_id;
	}

	/**
	 * Returns cluster id.
	 */
	public int getClusterId() {
		return clusterId;
	}
	
	/**
	 * Called, when instance backup starts.
	 * @param job
	 * @throws Exception 
	 * @throws BackupException 
	 */
	synchronized void start(InstanceJob job) throws Exception {
		logger.trace("start: " + job + " size=" + instanceJobsOnProgress.size());
		assert job != null;
		
		if (instanceJobsOnProgress.isEmpty()) {
			// We need to run cluster-sync now to initialize the backup/restore
			if (job instanceof InstanceBackupJob) {
				new RemoteMachineClusterCommand(job, BEFORE_BACKUP).execute();
			} else if (job instanceof InstanceRestoreJob) {
				new RemoteMachineClusterCommand(job, BEFORE_RESTORE).execute();
			}
		}
		instanceJobsOnProgress.add(job);
	}

	/**
	 * Called, when instance backup starts.
	 * @param job
	 * @throws Exception 
	 * @throws BackupException 
	 */
	synchronized void finish(InstanceJob job) throws Exception {
		logger.trace("finish: " + job + " size=" + instanceJobsOnProgress.size());
		assert job != null;
		
		instanceJobsOnProgress.remove(job);
		if (instanceJobsOnProgress.isEmpty()) {
			// We need to run cluster-sync now to finalize the backup/restore
			if (job instanceof InstanceBackupJob) {
				new RemoteMachineClusterCommand(job, AFTER_BACKUP).execute();
			} else if (job instanceof InstanceRestoreJob) {
				new RemoteMachineClusterCommand(job, AFTER_RESTORE).execute();
			}
		}
	}

	public int getToasInstanceId() {
		return toasInstanceId;
	}
}
