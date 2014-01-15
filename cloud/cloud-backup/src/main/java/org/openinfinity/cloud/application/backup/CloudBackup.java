package org.openinfinity.cloud.application.backup;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.openinfinity.cloud.application.backup.job.*;
import org.openinfinity.cloud.domain.BackupRule;
import org.openinfinity.cloud.domain.Cluster;
import org.openinfinity.cloud.domain.Instance;
import org.openinfinity.cloud.domain.Machine;
import org.openinfinity.cloud.service.administrator.ClusterService;
import org.openinfinity.cloud.service.administrator.InstanceService;
import org.openinfinity.cloud.service.administrator.MachineService;
import org.openinfinity.cloud.service.backup.BackupService;
import org.openinfinity.cloud.service.backup.RestoreInfo;
import org.openinfinity.cloud.domain.repository.backup.*;
import org.springframework.stereotype.Component;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Takes care of launching schedulers.
 * 
 * @author Timo Saarinen
 */
public class CloudBackup {
	private Logger logger = Logger.getLogger(CloudBackup.class);

	private InstanceService instanceService;
	private ClusterService clusterService;
	private MachineService machineService;
	private BackupService backupService;
	private BackupWorkRepository backupWorkRepository;

	private ClassPathXmlApplicationContext context;
	private DynamicQuartzSchedulerManager dynamicQuartzSchedulerManager;

	private CloudBackup(ClassPathXmlApplicationContext context) {
		this.context = context;
		dynamicQuartzSchedulerManager = (DynamicQuartzSchedulerManager) context
				.getBean("dynamicQuartzSchedulerManager");
	}

	/**
	 * Returns backup instance.
	 */
	public static CloudBackup getInstance() {
		if (cloudBackupInstance == null) {
			cloudBackupInstance = new CloudBackup(
					new ClassPathXmlApplicationContext(
							"/cloud-backup-context.xml"));
		}
		return cloudBackupInstance;
	}

	private static CloudBackup cloudBackupInstance = null;

	/**
	 * Reads initial instance data from the database and creates schedulable
	 * jobs based on that.
	 */
	public void initialize() {
		context.start();

		try {
			if (instanceService == null) {
				instanceService = (InstanceService) context
						.getBean("instanceService");
			}
			if (clusterService == null) {
				clusterService = (ClusterService) context
						.getBean("clusterService");
			}
			if (machineService == null) {
				machineService = (MachineService) context
						.getBean("machineService");
			}
			if (backupService == null) {
				backupService = (BackupService) context
						.getBean("backupService");
			}
			if (backupWorkRepository == null) {
				backupWorkRepository = (BackupWorkRepository) context
						.getBean("backupWorkRepository");
			}

			// Start the scheduler
			logger.info("Starting Quartz Scheduler");
			dynamicQuartzSchedulerManager.start();
			logger.info("Quartz Scheduler started");

			// Read instance information from the database
			List<Integer> cluster_ids = backupService.getBackupClusters();
			for (int cluster_id : cluster_ids) {
				Instance instance = instanceService.getInstance(cluster_id);
				if (instance != null) {
					Cluster cluster = clusterService.getCluster(cluster_id);
					List<BackupRule> rules = backupService.getClusterBackupRules(cluster_id);

					// Iterate all the cluster in instance
					ClusterInfo clusterInfo = new ClusterInfo(cluster_id, cluster.getInstanceId());

					// Iterate all the machines in cluster
					for (Machine machine : machineService
							.getMachinesInCluster(cluster.getId())) {
						InstanceJob job = new InstanceBackupJob(clusterInfo, machine.getId());
						job.setLocalPackageDirectory("/var/tmp"); // TODO
						Date d = new Date(System.currentTimeMillis() + 3000L);

						for (BackupRule rule : backupService.getClusterBackupRules(cluster_id)) {
							// Schedule backup
							String cron_String = "0" + rule.getCronMinutes() + " " 
									+ rule.getCronHours() + " "
									+ rule.getCronDayOfMonth() + " "
									+ rule.getCronMonth() + " "
									+ rule.getCronYear() + " "
									+ rule.getCronDayOfWeek();
							dynamicQuartzSchedulerManager.addJob(
									job.getJobName(), 
									"cluster-" + cluster_id,
									job, cron_String);
						}
					}
				} else {
					throw new BackupException("No instance to backup!");
				}
			}

			// Schedule database poller
			dynamicQuartzSchedulerManager.addJob("backup-operation-poller",
					"common", BackupOperationPollerJob.getInstance(this), "*/20 * * * * ?");

			// TODO: restore (but not here)

			logger.debug("Intialize completed and the scheduler started successfully.");
		} catch (SchedulerException e) {
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Unschedule and remove all jobs.
	 */
	public void cleanup() {
		try {
			logger.info("Stopping Quartz Scheduler");
			dynamicQuartzSchedulerManager.stop();
			logger.info("Quartz Scheduler stopped");

			context.close();
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Returns application context.
	 */
	public ClassPathXmlApplicationContext getContext() {
		return context;
	}

	public InstanceService getInstanceService() {
		return instanceService;
	}

	public ClusterService getClusterService() {
		return clusterService;
	}

	public MachineService getMachineService() {
		return machineService;
	}
	
	public BackupWorkRepository getBackupWorkRepository() {
		return backupWorkRepository;
	}
}
