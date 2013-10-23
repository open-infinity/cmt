package org.openinfinity.cloud.application.backup;

import org.apache.log4j.Logger;
import org.quartz.SchedulerException;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Takes care of launching schedulers.
 * 
 * @author Timo Saarinen
 */
public class CloudBackup {
	private Logger logger = Logger.getLogger(CloudBackup.class);
	
	private DynamicQuartzSchedulerManager dynamicQuartzSchedulerManager;
	
	public CloudBackup(ClassPathXmlApplicationContext context) {
		dynamicQuartzSchedulerManager = new DynamicQuartzSchedulerManager(context);
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
		} catch (SchedulerException e) {
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
}
