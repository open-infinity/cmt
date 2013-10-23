package org.openinfinity.cloud.application.backup;

import java.util.Set;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.scheduling.quartz.CronTriggerBean;
import org.springframework.scheduling.quartz.MethodInvokingJobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Service;

/**
 * Class for scheduling jobs in Spring Quartz schduler dynamically, as opposite to static
 * XML configuration. 
 * 
 * @see http://darthanthony.wordpress.com/2009/07/07/dynamic-scheduling-with-javaspring/
 * @author Timo Saarinen
 */
@Service
public class DynamicQuartzSchedulerManager {

	private Logger logger = Logger.getLogger(DynamicQuartzSchedulerManager.class);
	
	ClassPathXmlApplicationContext context;
	
	private Set<String> jobNames = new TreeSet<String>();
	
	// Quartz Factory
	@Autowired
	private SchedulerFactoryBean schedulerFactory; // (Scheduler) context.getBean("schedulerFactory");

	/**
	 * Start the scheduler.
	 */
	public void start() throws SchedulerException {
		assert schedulerFactory != null;
		schedulerFactory.getScheduler().start();
	}
	
	/**
	 * Shutdown the scheduler.
	 */
	public void stop() throws SchedulerException {
		schedulerFactory.getScheduler().shutdown();
	}
	
	/**
	 * Add and scheduler a job.
	 * 
	 * @see http://quartz-scheduler.org/documentation/quartz-1.x/tutorials/crontrigger
	 * @param jobName Unique name of the job.
	 * @param job Job object with "run" method.
	 * @param cronExpression Crontab-like expression, like "0 0 22 * * ?". 
	 * @throws Exception
	 */
	public void addJob(String jobName, Object job, String cronExpression) throws Exception {
		logger.debug("Adding job " + jobName);
		
		// Because we want to dynamically create scheduled tasks, we can't rely on XML
		// configuration. Instead we use programmatic process to do a similar thing. 
		
		// Create job
		MethodInvokingJobDetailFactoryBean jdfb = new MethodInvokingJobDetailFactoryBean();
		jdfb.setTargetObject(job);
		jdfb.setTargetMethod("run");
		jdfb.setName(jobName);
		jdfb.setGroup("common-job-group");
		jdfb.setConcurrent(false); // TODO: what is this?
		jdfb.afterPropertiesSet();
		
		// Scheduling with cron trigger
		CronTriggerBean trigger = new CronTriggerBean();
		trigger.setBeanName(jobName + "-trigger");
		trigger.setGroup("common-trigger-group");
		trigger.setJobDetail((JobDetail)jdfb.getObject());
		trigger.setCronExpression(cronExpression);
		trigger.afterPropertiesSet();

		// Add to the schedule
		schedulerFactory.getScheduler().scheduleJob((JobDetail)jdfb.getObject(), trigger);
		jobNames.add(jobName);
	}

	/**
	 * Unschedules and deletes job matching the jobName parameter.
	 * 
	 * @param jobName The job to be deleted.
	 * @throws SchedulerException
	 */
	public void deleteJob(String jobName) throws SchedulerException {
		logger.debug("Deleting job " + jobName);
		schedulerFactory.getScheduler().unscheduleJob(jobName + "-trigger", "common-trigger-group");
		schedulerFactory.getScheduler().deleteJob(jobName, "common-job-group");
		jobNames.remove(jobName);
	}

	/**
	 * Unschedule and delete all jobs.
	 */
	public void delteAlljobs() {
		logger.debug("Deleting all jobs from scheduler");
		for (String job_name : new TreeSet<String>(jobNames)) {
			try {
				deleteJob(job_name);
			} catch (SchedulerException e) {
				logger.warn("Failed to delete job " + job_name, e);
			}
		}
	}
}
