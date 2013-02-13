package org.openinfinity.cloud.domain.repository.administrator;

import static org.junit.Assert.*;

import java.util.List;

import javax.sql.DataSource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.openinfinity.cloud.domain.Job;
import org.openinfinity.cloud.domain.JobPlatformParameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration(locations={"classpath*:META-INF/spring/repository-context.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class JobRepositoryJdbcImplTest {

	@Autowired
	@Qualifier("cloudDataSource")
	DataSource ds;
	
	@Autowired
	private JobRepository jobRepository;
	
	@Test
	public void testJobRepository() {
		
		int aJobStatus=1;
		int aCloud=0;
		int aInstanceId=0;
		String aJobType="create";

		Job job=new Job(aJobType, aInstanceId, aCloud, aJobStatus);
		JobPlatformParameter param=new JobPlatformParameter();
		param.setKey("jdbc.url");
		param.setValue("http://localhost:3306/lportal");
		job.addParameter(param);
		int jobId = jobRepository.addJob(job);
		
		Job job2 = jobRepository.getJob(jobId);
		assertEquals(job.getParameters().get(0).getKey(),job2.getParameters().get(0).getKey());
		assertEquals(job.getParameters().get(0).getValue(),job2.getParameters().get(0).getValue());
		
		for (int i=0;i<10;i++){
			job.setJobStatus(3);
			job.setParameters(null);
			jobRepository.addJob(job);
		}
		List<Job> jobs = jobRepository.getJobs(3, 100);
		assertEquals(jobs.size(),10);
		assertEquals(jobs.get(0).getParameters().size(),0);

	}

}
