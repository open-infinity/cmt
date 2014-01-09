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

	@Autowired
	private InstanceService instanceService;

	@Autowired
	private ClusterService clusterService;

	@Autowired
	private MachineService machineService;

	@Autowired
	private BackupService backupService;

	@Autowired
	private BackupWorkRepository backupWorkRepository;

	private ClassPathXmlApplicationContext context;
	private DynamicQuartzSchedulerManager dynamicQuartzSchedulerManager;

	public CloudBackup(ClassPathXmlApplicationContext context) {
		this.context = context;
		dynamicQuartzSchedulerManager = (DynamicQuartzSchedulerManager) context
				.getBean("dynamicQuartzSchedulerManager");
	}

	/**
	 * Analyze failed cluster.
	 * 
	 * @return Object for Worker about the approach.
	 */
	public RestoreInfo analyze() {
		throw new RuntimeException("UNIMPLEMENTED"); // TODO
	}

	/**
	 * Reads initial instance data from the database and creates schedulable
	 * jobs based on that.
	 */
	public void initialize() {
		try {
			// FIXME: make autowiring work
			if (instanceService == null) {
				logger.debug("Instance service not autowired");
				instanceService = (InstanceService) context
						.getBean("instanceService");
			}
			if (clusterService == null) {
				logger.debug("Cluster service not autowired");
				clusterService = (ClusterService) context
						.getBean("clusterService");
			}
			if (machineService == null) {
				logger.debug("Machine service not autowired");
				machineService = (MachineService) context
						.getBean("machineService");
			}
			if (backupService == null) {
				logger.debug("Backup service not autowired");
				backupService = (BackupService) context
						.getBean("backupService");
			}
			if (backupWorkRepository == null) {
				logger.debug("Backup repostiory not autowired");
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
					List<BackupRule> rules = backupService
							.getClusterBackupRules(cluster_id);

					// Iterate all the cluster in instance
					ClusterInfo clusterInfo = new ClusterInfo(
							cluster.getInstanceId());

					// Iterate all the machines in cluster
					for (Machine machine : machineService
							.getMachinesInCluster(cluster.getId())) {
						InstanceJob job = new InstanceBackupJob(clusterInfo,
								context);
						job.setJobName(("" + machine.getType()).toUpperCase()
								+ ":" + machine.getDnsName());
						job.setToasInstanceId(instance.getInstanceId());
						job.setHostname(machine.getDnsName());
						job.setVirtualMachineInstanceId("" + machine.getId());
						job.setUsername(machine.getUserName());
						job.setLocalPackageDirectory("/var/tmp"); // TODO
						Date d = new Date(System.currentTimeMillis() + 3000L);

						// FIXME: real schedule
						dynamicQuartzSchedulerManager.addJob(job.getJobName(),
								job, "" + d.getSeconds() + " " + d.getMinutes()
										+ " * * * ?");
					}
				} else {
					throw new BackupException("No instance to backup!");
				}
			}

			// Start database poller
			dynamicQuartzSchedulerManager.addJob("backup-operation-poller",
					BackupOperationPollerJob.getInstance(backupWorkRepository, this),
					"0 * * * * ?");

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
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
	}

	private class TestJob {
		public TestJob() {
		}

		public void run() {
			logger.info("Test job started");
		}
	}
}
