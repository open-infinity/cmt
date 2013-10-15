package org.openinfinity.cloud.application.backup;

import java.util.Date;

import org.apache.log4j.Logger;
import org.aspectj.weaver.NewConstructorTypeMunger;
import org.openinfinity.cloud.application.backup.job.InstanceBackupJob;
import org.quartz.SchedulerException;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Takes care of launching schedulers.
 * 
 * @author Timo Saarinen
 */
public class CloudBackup {
	private Logger logger = Logger.getLogger(CloudBackup.class);
	
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
			// Start the scheduler
			logger.info("Starting Quartz Scheduler");
			dynamicQuartzSchedulerManager.start();
			logger.info("Quartz Scheduler started");
			
			// Read instance information from the database
			// TODO
			
			// Local test
			InstanceBackupJob job = new InstanceBackupJob(context);
			job.setJobName("test");
			job.setToasInstanceId(981);
			job.setHostname("10.33.208.10");
			job.setUsername("root");
			Date d = new Date(System.currentTimeMillis() + 3000L);
			dynamicQuartzSchedulerManager.addJob(job.getJobName(), job, "" + d.getSeconds() + " " + d.getMinutes() + " * * * ?");
			
			//dynamicQuartzSchedulerManager.addJob("test", new TestJob(), "*/5 * * * * ?");

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