package org.openinfinity.cloud.application.backup;

import java.util.*;

import org.openinfinity.cloud.application.backup.job.*;
import org.openinfinity.cloud.domain.*;
import org.openinfinity.cloud.domain.repository.backup.*;
import org.openinfinity.cloud.service.administrator.MachineService;
import org.apache.log4j.Logger;
import org.aspectj.apache.bcel.generic.ReturnaddressType;
import org.aspectj.weaver.patterns.ThisOrTargetAnnotationPointcut;

/**
 * Polls repository layer to receive in-coming backup/restore requests.
 * 
 * @author Timo Saarinen
 */
public class BackupOperationPollerJob {
	private Logger logger = Logger.getLogger(BackupOperationPollerJob.class);

	private BackupWorkRepository backupWorkRepository = null;

	private DynamicQuartzSchedulerManager dynamicQuartzSchedulerManager = null;

	private CloudBackup backup = null;

	private int lastBackupOperationId = -1;

	private BackupOperationPollerJob(CloudBackup backup) {
		backupWorkRepository = backup.getBackupWorkRepository();
		dynamicQuartzSchedulerManager = (DynamicQuartzSchedulerManager) backup
				.getContext().getBean("dynamicQuartzSchedulerManager");
	}

	/**
	 * Singleton method
	 */
	public static BackupOperationPollerJob getInstance(CloudBackup backup) {
		if (instanceOfBackupOperationPollerJob == null) {
			instanceOfBackupOperationPollerJob = new BackupOperationPollerJob(backup);
		}
		return instanceOfBackupOperationPollerJob;
	}

	private static BackupOperationPollerJob instanceOfBackupOperationPollerJob = null;

	/**
	 * Needed by Quartz Scheduler.
	 */
	public final void run() throws Exception {
		logger.trace("Polling backup operations...");
		int last_id = lastBackupOperationId;
		for (BackupOperation op : backupWorkRepository
				.readBackupOperationsAfter(lastBackupOperationId)) {
			logger.info("Read backup operation '" + op + "'");

			// Handle different cases
			if ("backup".equals(op.getOperation())) {
				int cluster_id = op.getTargetClusterId();
				Cluster cluster = backup.getClusterService().getCluster(cluster_id);
				ClusterInfo cluster_info = new ClusterInfo(cluster_id, cluster.getInstanceId());
				for (Machine machine : backup.getMachineService().getMachinesInCluster(cluster_id)) {
					InstanceBackupJob job = new InstanceBackupJob(cluster_info, machine.getId());

					// Trigger job instantly
					dynamicQuartzSchedulerManager.runJob("job-" + op.getId() + "-" + machine.getInstanceId(), "cluster-" + cluster_id, job);
				}
			} else if ("full-restore".equals(op.getOperation())) {
				int source_cluster_id = op.getSourceClusterId();
				int target_cluster_id = op.getTargetClusterId();
				if (source_cluster_id == -1) source_cluster_id = target_cluster_id;
				Cluster cluster = backup.getClusterService().getCluster(target_cluster_id);
				ClusterInfo source_cluster_info = new ClusterInfo(source_cluster_id, cluster.getInstanceId());
				ClusterInfo target_cluster_info = new ClusterInfo(target_cluster_id, cluster.getInstanceId());
				for (Machine machine : backup.getMachineService().getMachinesInCluster(target_cluster_id)) {
					InstanceRestoreJob job = new InstanceRestoreJob(target_cluster_info, source_cluster_info, machine.getId());
					job.setLocalPackageDirectory("/var/tmp"); // TODO
					
					// Trigger job instantly
					dynamicQuartzSchedulerManager.runJob("job-" + op.getId() + "-" + machine.getInstanceId(), "cluster-" + target_cluster_id, job);
				}
			} else if ("partial-restore".equals(op.getOperation())) {
				int cluster_id = op.getTargetClusterId();
				Cluster cluster = backup.getClusterService().getCluster(cluster_id);
				ClusterInfo cluster_info = new ClusterInfo(cluster_id, cluster.getInstanceId());
				for (Machine machine : backup.getMachineService().getMachinesInCluster(cluster_id)) {
					InstanceRestoreJob job = new InstanceRestoreJob(cluster_info, machine.getId());
					job.setLocalPackageDirectory("/var/tmp"); // TODO

					// Trigger job instantly
					dynamicQuartzSchedulerManager.runJob("job-" + op.getId() + "-" + machine.getInstanceId(), "cluster-" + cluster_id, job);
				}
			} else {
				logger.error("Unexpected backup operation '"
						+ op.getOperation()
						+ "', marking it failed and skipping");
				op.setState("failed");
				backupWorkRepository.writeBackupOperation(op);
			}

			last_id = op.getId();
		}
		lastBackupOperationId = last_id;
	}
}
