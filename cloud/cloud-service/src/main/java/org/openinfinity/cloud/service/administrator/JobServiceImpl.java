package org.openinfinity.cloud.service.administrator;

import java.util.Collection;
import java.util.List;

import org.openinfinity.cloud.domain.Job;
import org.openinfinity.cloud.domain.repository.administrator.JobRepository;
import org.openinfinity.core.annotation.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Job Service interface for handling cloud configuration management.
 * 
 * @author Ilkka Leinonen
 * @author Ossi Hämäläinen
 * @author Juha-Matti Sironen
 */
@Service("jobService")
public class JobServiceImpl implements JobService {

	@Autowired
	private JobRepository jobRepository;
	
	@Log
	public Job getJob(int jobId) {
		return jobRepository.getJob(jobId);
	}

	@Log
	public int addJob(Job job) {
		return jobRepository.addJob(job);
	}
	
	@Log
	public List<Job> getJobsForInstance(int instanceId) {
		return jobRepository.getJobsForInstance(instanceId);
	}

	@Log
	public void setStartTime(int id) {
		jobRepository.setStartTime(id);
	}

	@Log
	public void setEndTime(int id) {
		jobRepository.setEndTime(id);
	}

	@Log
	public void updateStatus(int id, int status) {
		jobRepository.updateStatus(id, status);
	}

	@Log
	public Collection<Job> getJobs(int status, int limit) {
		return jobRepository.getJobs(status, limit);
	}

    @Log
    public void deleteAll() {
        jobRepository.deleteAll();
    }
}