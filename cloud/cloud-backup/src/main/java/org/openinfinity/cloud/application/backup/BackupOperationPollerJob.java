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
		this.backup = backup;
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
	public final void run() throws BackupException {
		logger.trace("Polling backup operations...");
		int last_id = lastBackupOperationId;
		for (BackupOperation op : backupWorkRepository.readBackupOperationsAfter(lastBackupOperationId)) {
			if (BackupOperation.REQUESTED.equals(op.getState())) {
				try {
					logger.info("Read backup operation '" + op + "'");
					
					// Handle different cases
					if (BackupOperation.BACKUP.equals(op.getOperation())) {
						logger.trace("Backup operation recognized, backup=" + backup);
						//
						// Instant backup
						//
						int cluster_id = op.getTargetClusterId();
						logger.trace("Getting cluster " + cluster_id);
						Cluster cluster = backup.getClusterService().getCluster(cluster_id);
						if (cluster != null) {
							logger.trace("New cluster info");
							ClusterInfo cluster_info = new ClusterInfo(cluster_id, cluster.getInstanceId());
							int count = 0;
							logger.trace("Getting machine list");
							Collection<Machine> machines = backup.getMachineService().getMachinesInCluster(cluster_id);
							JobResultSaver jrs = new JobResultSaver(op, machines.size());
							for (Machine machine : machines) {
								InstanceBackupJob job = new InstanceBackupJob(cluster_info, machine.getId(), jrs);
		
								logger.trace("Scheduling job " + job);
		
								// Trigger job instantly
								dynamicQuartzSchedulerManager.runJob("job-" + op.getId() + "-" 
										+ machine.getInstanceId(), "cluster-" + cluster_id, job);
								count++;
							}
							logger.trace("Saving op state");
							op.setState(BackupOperation.IN_PROGRESS);
							backupWorkRepository.writeBackupOperation(op);
							logger.debug("" + count + " backup jobs launched");
						} else {
							logger.error("Cluster " + cluster_id + " not found");
							op.setState(BackupOperation.FAILED);
							op.setDescription("Cluster " + cluster_id + " not found");
							backupWorkRepository.writeBackupOperation(op);
						}
					} else if (BackupOperation.FULL_RESTORE.equals(op.getOperation())) {
						logger.trace("Full-restore operation recognized");
						//
						// Full restore
						//
						int source_cluster_id = op.getSourceClusterId();
						int target_cluster_id = op.getTargetClusterId();
						if (source_cluster_id == -1) source_cluster_id = target_cluster_id;
						Cluster cluster = backup.getClusterService().getCluster(target_cluster_id);
						if (cluster != null) {
							ClusterInfo source_cluster_info = new ClusterInfo(source_cluster_id, cluster.getInstanceId());
							ClusterInfo target_cluster_info = new ClusterInfo(target_cluster_id, cluster.getInstanceId());
							Collection<Machine> machines = backup.getMachineService().getMachinesInCluster(target_cluster_id);
							JobResultSaver jrs = new JobResultSaver(op, machines.size());
							for (Machine machine : machines) {
								InstanceRestoreJob job = new InstanceRestoreJob(target_cluster_info, source_cluster_info, machine.getId(), jrs);
								job.setLocalPackageDirectory("/var/tmp"); // TODO
								
								// Trigger job instantly
								dynamicQuartzSchedulerManager.runJob("job-" + op.getId() + "-" 
										+ machine.getInstanceId(), "cluster-" + target_cluster_id, job);
							}
							op.setState(BackupOperation.IN_PROGRESS);
							backupWorkRepository.writeBackupOperation(op);
						} else {
							logger.error("Target cluster " +target_cluster_id + " not found");
							op.setState(BackupOperation.FAILED);
							op.setDescription("Target cluster " + target_cluster_id + " not found");
							backupWorkRepository.writeBackupOperation(op);
						}
					} else if (BackupOperation.PARTIAL_RESTORE.equals(op.getOperation())) {
						logger.trace("Partial-restore operation recognized");
						//
						// Partial restore
						//
						int cluster_id = op.getTargetClusterId();
						Cluster cluster = backup.getClusterService().getCluster(cluster_id);
						if (cluster != null) {
							ClusterInfo cluster_info = new ClusterInfo(cluster_id, cluster.getInstanceId());
							Collection<Machine> machines = backup.getMachineService().getMachinesInCluster(cluster_id);
							JobResultSaver jrs = new JobResultSaver(op, machines.size());
							for (Machine machine : machines) {
								InstanceRestoreJob job = new InstanceRestoreJob(cluster_info, machine.getId(), jrs);
								job.setLocalPackageDirectory("/var/tmp"); // TODO
			
								// Trigger job instantly
								dynamicQuartzSchedulerManager.runJob("job-" + op.getId() 
										+ "-" + machine.getInstanceId(), "cluster-" + cluster_id, job);
							}
							op.setState(BackupOperation.IN_PROGRESS);
							backupWorkRepository.writeBackupOperation(op);
						} else {
							logger.error("Cluster " + cluster_id + " not found");
							op.setState(BackupOperation.FAILED);
							op.setDescription("Cluster " + cluster_id + " not found");
							backupWorkRepository.writeBackupOperation(op);
						}
					} else if (BackupOperation.REFRESH_SCHEDULES.equals(op.getOperation())) {
						logger.trace("Refresh operation recognized");
						//
						// Refresh backup schedules
						//
						CloudBackup.getInstance().updateBackupClusters(op.getTargetClusterId());
						op.setState(BackupOperation.SUCCEEDED);
						op.setDescription(null);
						backupWorkRepository.writeBackupOperation(op);
					} else {
						logger.trace("Unknown backup operation");
						//
						// Unknown operation
						//
						logger.error("Unexpected backup operation '"
								+ op.getOperation()
								+ "', marking it failed and skipping");
						op.setState(BackupOperation.FAILED);
						op.setDescription("Unrecognized backup operation: " + op.getOperation());
						backupWorkRepository.writeBackupOperation(op);
					}
		
				} catch (Exception e) {
					logger.error("Backup operation handling failed!", e);
					op.setState(BackupOperation.FAILED);
					op.setDescription("Handling failed: " + e.getMessage());
					backupWorkRepository.writeBackupOperation(op);
					throw new BackupException("Backup operation handling failed!", e);
				}
				last_id = op.getId();
			}
		}
		lastBackupOperationId = last_id;
	}

	/**
	 * Waits for job completion and writes the result to database.
	 */
	private class JobResultSaver implements InstanceJob.ResultListener {
		public BackupOperation op;
		public int failed = 0;
		public int succeeded = 0;
		public int count = 0;
		
		public JobResultSaver(BackupOperation op, int num) {
			this.op = op;
			count = num;
		}
		
		public synchronized void report(boolean success, String description) {
			try {
				logger.trace("JobResultSaver.report(" + success + ", " + description + ")");
				
				if (success) {
					succeeded++;
					if (succeeded == count) {
						op.setState(BackupOperation.SUCCEEDED);
						op.setDescription(null);
						backup.getBackupWorkRepository().writeBackupOperation(op);
						logger.trace("JobResultSaver.report: all succeeded, saved (count=" + count + ", succeeded=" + succeeded + " failed=" + failed + ")");
					} else {
						logger.trace("JobResultSaver.report: this succeeeded, waiting for others (count=" + count + ", succeeded=" + succeeded + " failed=" + failed + ")");
					}
				} else {
					failed++;
					op.setState(BackupOperation.FAILED);
					op.setDescription(description);
					backup.getBackupWorkRepository().writeBackupOperation(op);
					logger.trace("JobResultSaver.report: failed saved (count=" + count + ", succeeded=" + succeeded + " failed=" + failed + ")");
				}
			} catch (Exception e) {
				logger.warn(e);
			}
		}
	}
}
