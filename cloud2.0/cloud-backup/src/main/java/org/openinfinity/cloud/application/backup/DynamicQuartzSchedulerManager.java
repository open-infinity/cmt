package org.openinfinity.cloud.application.backup;

import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.lang.mutable.Mutable;
import org.apache.log4j.Logger;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.JobKey;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.quartz.spi.MutableTrigger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.CronTriggerBean;
import org.springframework.scheduling.quartz.MethodInvokingJobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Service;

import static org.quartz.TriggerBuilder.*;
import static org.quartz.CronScheduleBuilder.*;
import static org.quartz.DateBuilder.*;
	
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
	
	private Set<String> jobNames = new TreeSet<String>();
	private TreeMap<String,String> jobGroupNames = new TreeMap<String,String>();
	
	// Quartz Factory
	@Autowired
	private SchedulerFactoryBean schedulerFactory; // (Scheduler) context.getBean("schedulerFactory");

	/**
	 * Start the scheduler.
	 */
	public void start() throws SchedulerException {
		assert schedulerFactory != null;
		logger.trace("starting scheduler");
		schedulerFactory.getScheduler().start();
	}
	
	/**
	 * Shutdown the scheduler.
	 */
	public void stop() throws SchedulerException {
		logger.trace("stopping scheduler");
		schedulerFactory.getScheduler().shutdown();
	}
	
	/**
	 * Add and scheduler a job.
	 * 
	 * @see http://quartz-scheduler.org/documentation/quartz-1.x/tutorials/crontrigger
	 * @param jobName Unique name of the job.
	 * @param groupName job group name for the scheduler
	 * @param job Job object with "run" method.
	 * @param cronExpression Crontab-like expression, like "0 0 22 * * ?". 
	 * @throws Exception
	 */
	public void addJob(String jobName, String groupName, Object job, String cronExpression) throws Exception {
		logger.debug("Adding job " + jobName + " (" + groupName + ")");
		
		// Because we want to dynamically create scheduled tasks, we can't rely on XML
		// configuration. Instead we use programmatic process to do a similar thing. 
		
		// Create job
		MethodInvokingJobDetailFactoryBean jdfb = new MethodInvokingJobDetailFactoryBean();
		jdfb.setTargetObject(job);
		jdfb.setTargetMethod("run");
		jdfb.setName(jobName);
		jdfb.setGroup(groupName);
		jdfb.setConcurrent(false); // TODO: what is this?
		jdfb.afterPropertiesSet();
		
		// Scheduling with cron trigger
		/* 1.8.5
		CronTriggerBean trigger = new CronTriggerBean();
		trigger.setBeanName(jobName + "-trigger");
		trigger.setGroup("common-trigger-group");
		trigger.setJobDetail((JobDetail)jdfb.getObject());
		trigger.setCronExpression(cronExpression);
		trigger.afterPropertiesSet();
		*/
		Trigger trigger = newTrigger()
				    .withIdentity(jobName + "-trigger", groupName + "-trigger")
				    .withSchedule(cronSchedule(cronExpression))
				    .forJob(jobName, groupName)
				    .build();
		  
		// Add to the scheduler
		schedulerFactory.getScheduler().scheduleJob((JobDetail)jdfb.getObject(), trigger);
		jobNames.add(jobName);
		jobGroupNames.put(jobName, groupName);
		
		logger.trace("Job " + jobName + " scheduled");
	}
	
	/**
	 * This behaves like addJob, but triggers the job immediately instead of
	 * scheduling it.
	 * 
	 * @see http://quartz-scheduler.org/documentation/quartz-1.x/tutorials/crontrigger
	 * @param jobName
	 *            Unique name of the job.
	 * @param groupName
	 *            job group name for the scheduler
	 * @param job
	 *            Job object with "run" method.
	 * @throws Exception
	 */
	public void runJob(String jobName, String groupName, Object job) throws Exception {
		logger.debug("Triggering job " + jobName + " (" + groupName + ")");
/*		
	    Trigger trigger = newTrigger()
                .withIdentity(jobName + "-trigger", groupName + "-trigger")
                .forJob(jobName, groupName)
                .startNow()
                .build();
*/
		// Create job detail
		MethodInvokingJobDetailFactoryBean jdfb = new MethodInvokingJobDetailFactoryBean();
		jdfb.setTargetObject(job);
		jdfb.setTargetMethod("run");
		jdfb.setName(jobName);
		jdfb.setGroup(groupName);
		jdfb.setConcurrent(false); // TODO: what is this?
		jdfb.afterPropertiesSet();
		
		//Register this job to the scheduler
		schedulerFactory.getScheduler().addJob((JobDetail)jdfb.getObject(), true);

		//Immediately fire the Job MyJob.class
		schedulerFactory.getScheduler().triggerJob(JobKey.jobKey(jobName, groupName));
		
		//jobNames.add(jobName);
		//jobGroupNames.put(jobName, groupName);
		
		// TODO: what about job deletion or durability?
		
		logger.trace("Job " + jobName + " triggered");
	}
	
	/**
	 * Unschedules and deletes job matching the jobName parameter.
	 * 
	 * @param jobName The job to be deleted.
	 * @throws SchedulerException
	 */
	public void deleteJob(String jobName) throws SchedulerException {
		logger.debug("Deleting job " + jobName);
/*	1.8.5	
		schedulerFactory.getScheduler().unscheduleJob(jobName + "-trigger", "common-trigger-group");
		schedulerFactory.getScheduler().deleteJob(jobName, "common-job-group");
		jobNames.remove(jobName);
*/
		schedulerFactory.getScheduler().unscheduleJob(TriggerKey.triggerKey(jobName + "-trigger", jobGroupNames.get(jobName) + "-trigger"));
		schedulerFactory.getScheduler().deleteJob(JobKey.jobKey(jobName, jobGroupNames.get(jobName)));
		jobNames.remove(jobName);
		jobGroupNames.remove(jobName);
	}

	/**
	 * Unschedule and delete all jobs.
	 */
	public void deleteAlljobs() {
		logger.trace("Deleting all jobs from scheduler");
		for (String job_name : new TreeSet<String>(jobNames)) {
			try {
				deleteJob(job_name);
			} catch (SchedulerException e) {
				logger.warn("Failed to delete job " + job_name, e);
			}
		}
		jobNames.clear();
		jobGroupNames.clear();
	}
}
