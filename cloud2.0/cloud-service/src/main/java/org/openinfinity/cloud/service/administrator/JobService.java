package org.openinfinity.cloud.service.administrator;

import org.openinfinity.cloud.domain.Job;

import java.util.Collection;

/**
 * Job Service interface implementation for handling cloud configuration management.
 * 
 * @author Ilkka Leinonen
 * @author Ossi Hämäläinen
 * @author Juha-Matti Sironen
 */
 public interface JobService {
	
	static final int CLOUD_JOB_CREATED = 1;
	static final int CLOUD_JOB_STARTED = 2;
	static final int CLOUD_JOB_READY = 10;
	static final int CLOUD_JOB_ERROR = 15;
	
	static final String EXTRA_DATA_PORTAL = "1";
	static final String EXTRA_DATA_PORTAL_IG = "2";
	static final String EXTRA_DATA_PORTAL_IG_ECM = "3";
	

	Job getJob(int jobId);
	
	Collection<Job> getJobs(int status, int limit);
	
	int addJob(Job job);
	
	Collection<Job> getJobsForInstance(int instanceId);
	
	void setStartTime(int id);
	
	void setEndTime(int id);
	
	void updateStatus(int id, int status);

    void deleteAll();

    Job getNewest();

}
