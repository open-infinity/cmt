package org.openinfinity.cloud.application.backup;

import java.util.Date;

import org.apache.log4j.Logger;
import org.aspectj.weaver.NewConstructorTypeMunger;
import org.openinfinity.cloud.application.backup.job.*;
import org.openinfinity.cloud.domain.Cluster;
import org.openinfinity.cloud.domain.Instance;
import org.openinfinity.cloud.domain.Machine;
import org.openinfinity.cloud.service.administrator.ClusterService;
import org.openinfinity.cloud.service.administrator.InstanceService;
import org.openinfinity.cloud.service.administrator.MachineService;
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

	private ClassPathXmlApplicationContext context;
	private DynamicQuartzSchedulerManager dynamicQuartzSchedulerManager;
	
	public CloudBackup(ClassPathXmlApplicationContext context) {
		this.context = context;
		dynamicQuartzSchedulerManager = (DynamicQuartzSchedulerManager) context.getBean("dynamicQuartzSchedulerManager");
	}

	/**
	 * Reads initial instance data from the database and creates schedulable jobs based on that. 
	 */
	public void initialize() {
		try {
			// FIXME: make autowiring work
			if (instanceService == null) {
				logger.debug("Instance service not autowired");
				instanceService = (InstanceService) context.getBean("instanceService");
			}
			if (clusterService == null) {
				logger.debug("Cluster service not autowired");
				clusterService = (ClusterService) context.getBean("clusterService");
			}
			if (machineService == null) {
				logger.debug("Machine service not autowired");
				machineService = (MachineService) context.getBean("machineService");
			}
			
			// Start the scheduler
			logger.info("Starting Quartz Scheduler");
			dynamicQuartzSchedulerManager.start();
			logger.info("Quartz Scheduler started");
			
			// Read instance information from the database
			Instance instance = instanceService.getInstance(1001); // FIXME: real criterion for backup
			if (instance != null) { 
				// Iterate all the cluster in instance
				for (Cluster cluster : clusterService.getClusters(instance.getInstanceId())) {
					ClusterInfo clusterInfo = new ClusterInfo(cluster.getInstanceId());
					
					// Iterate all the machines in cluster
					for (Machine machine : machineService.getMachinesInCluster(cluster.getId())) {
						InstanceJob job = new InstanceBackupJob(clusterInfo, context);
						job.setJobName(("" + machine.getType()).toUpperCase() + ":" + machine.getDnsName());
						job.setToasInstanceId(instance.getInstanceId());
						job.setHostname(machine.getDnsName());
						job.setVirtualMachineInstanceId("" + machine.getId());
						job.setUsername(machine.getUserName());
						job.setLocalPackageDirectory("/var/tmp"); // TODO
						Date d = new Date(System.currentTimeMillis() + 3000L);
						
						// FIXME: real schedule
						dynamicQuartzSchedulerManager.addJob(job.getJobName(), job, "" + d.getSeconds() + " " + d.getMinutes() + " * * * ?");
					}
				}
			} else {
				throw new BackupException("No instance to backup!");
			}
			
			// TODO: restore (but not here)

/*			
			Cluster test_cluster = new Cluster("1524"); 
			
			// Local backup test
			{
				InstanceJob job = new InstanceBackupJob(test_cluster, context);
				job.setJobName("backup-test");
				job.setToasInstanceId(993);
				job.setHostname("10.33.208.10");
				job.setVirtualMachineInstanceId("1234"); // FIXME
				job.setUsername("root");
				job.setLocalPackageDirectory("/var/tmp");
				Date d = new Date(System.currentTimeMillis() + 3000L);
				dynamicQuartzSchedulerManager.addJob(job.getJobName(), job, "" + d.getSeconds() + " " + d.getMinutes() + " * * * ?");
			}
			
			// Local restore test
			{
				InstanceJob job = new InstanceRestoreJob(test_cluster, context);
				job.setJobName("restore-test");
				job.setToasInstanceId(981);
				job.setHostname("10.33.208.10");
				job.setVirtualMachineInstanceId("1234"); // FIXME
				job.setUsername("root");
				job.setLocalPackageDirectory("/var/tmp");
				Date d = new Date(System.currentTimeMillis() + 3000L + 30000L);
				dynamicQuartzSchedulerManager.addJob(job.getJobName(), job, "" + d.getSeconds() + " " + d.getMinutes() + " * * * ?");
			}
*/			
			
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
