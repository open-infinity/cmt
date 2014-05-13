package org.openinfinity.cloud.application.backup;

import java.util.Date;
import java.util.List;
import java.util.Set;

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
	 * Returns the BackupProperties class from spring application context. 
	 * This is a convenience method.
	 * @return CloudProperties object or null
	 */
	public static BackupProperties getBackupProperties() {
		return (BackupProperties) getInstance().getContext().getBean("backupProperties");
	}
	
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
				if (instanceService == null)
					throw new BackupException("instanceService is null!");
			}
			if (clusterService == null) {
				clusterService = (ClusterService) context
						.getBean("clusterService");
				if (clusterService == null)
					throw new BackupException("clusterService is null!");
			}
			if (machineService == null) {
				machineService = (MachineService) context
						.getBean("machineService");
				if (machineService == null)
					throw new BackupException("machineService is null!");
			}
			if (backupService == null) {
				backupService = (BackupService) context
						.getBean("backupService");
				if (backupService == null)
					throw new BackupException("backupService is null!");
			}
			if (backupWorkRepository == null) {
				backupWorkRepository = (BackupWorkRepository) context
						.getBean("backupWorkRepository");
				if (backupWorkRepository == null)
					throw new BackupException("backupWorkRepository is null!");
			}

			// Start the scheduler
			logger.info("Starting Quartz Scheduler");
			dynamicQuartzSchedulerManager.start();
			logger.info("Quartz Scheduler started");

			// Read backup rules from the database and add needed jobs
			updateBackupClusters(-1);

			logger.debug("Intialize completed and the scheduler started successfully.");
		} catch (SchedulerException e) {
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Adds, updates or deletes backup jobs based on backup rules found in the database.
	 * 
	 * @param target_cluster_id The cluster backup rules to be refreshed, or -1 for all. 
	 */
	public void updateBackupClusters(int target_cluster_id) throws Exception {
		// First, clear all jobs from the scheduler
		// TODO: more advanced way based on the target_cluster_id parameter (if not -1)
		dynamicQuartzSchedulerManager.deleteAlljobs();
		
		// Read instance information from the database
		Set<Integer> cluster_ids = backupService.getBackupClusters();
		logger.debug("Backup cluster count is " + cluster_ids.size() + " (active and passive)");
		for (int cluster_id : cluster_ids) {
			Cluster cluster = clusterService.getCluster(cluster_id);
			List<BackupRule> rules = backupService
					.getClusterBackupRules(cluster_id);

			// Iterate all the cluster in instance
			ClusterInfo clusterInfo = new ClusterInfo(cluster);

			// Iterate all the machines in cluster
			for (Machine machine : machineService.getMachinesInCluster(cluster.getId())) {
				InstanceJob job = new InstanceBackupJob(clusterInfo,
						machine.getId(), null);
				job.setLocalPackageDirectory(getBackupProperties().getTemporaryDirectory());

				for (BackupRule rule : rules) {
					if (rule.isActive()) {
						// Schedule backup job
						String cron_String = "0 " 
								+ rule.getCronMinutes() + " "
								+ rule.getCronHours() + " "
								+ rule.getCronDayOfMonth() + " "
								+ rule.getCronMonth() + " "
								+ rule.getCronDayOfWeek() + " " 
								+ rule.getCronYear();
						dynamicQuartzSchedulerManager.addJob(job.getJobName(),
								"cluster-" + cluster_id, job, cron_String);
						logger.info("Scheduled job " + job.getJobName() + " with schedule " + cron_String);
					}
				}
			}
		}
		
		// Schedule database poller
		dynamicQuartzSchedulerManager.addJob("backup-operation-poller",
				"common", BackupOperationPollerJob.getInstance(this), "*/20 * * * * ?");
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
		if (clusterService == null)
			logger.warn("Returning clusterService == null");
		return clusterService;
	}

	public MachineService getMachineService() {
		return machineService;
	}
	
	public BackupWorkRepository getBackupWorkRepository() {
		return backupWorkRepository;
	}
}
