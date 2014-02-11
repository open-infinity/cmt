/*
 * Copyright (c) 2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.openinfinity.cloud.domain.repository.administrator;

import org.apache.log4j.Logger;
import org.openinfinity.cloud.domain.Job;
import org.openinfinity.core.annotation.AuditTrail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Jdbc implementation of Job repository interface
 
 * @author Ossi Hämäläinen
 * @author Ilkka Leinonen
 * @author Juha-Matti Sironen
 * @version 1.0.0 Initial version
 * @since 1.0.0
 */

@Repository("jobRepositoryJdbcImpl")
public class JobRepositoryJdbcImpl implements JobRepository {
	
	private static final Logger LOG = Logger.getLogger(JobRepositoryJdbcImpl.class.getName());
	
	private JdbcTemplate jdbcTemplate;
	private DataSource dataSource;
	
	@Autowired
	public JobRepositoryJdbcImpl(@Qualifier("cloudDataSource") DataSource ds) {
		Assert.notNull(ds, "Please define datasource for job repository.");
		this.jdbcTemplate = new JdbcTemplate(ds);
		this.dataSource = ds;
	}

	@AuditTrail
	public List<Job> getJobs(int status, int limit) {
		List<Job> jobs = this.jdbcTemplate.query("select * from job_tbl where job_status = ? order by job_create_time limit ?", new Object[] { status, limit }, new JobMapper());
		return jobs;
	}

	@AuditTrail
	public void updateStatus(final int id, final int status) {
		this.jdbcTemplate.update("update job_tbl set job_status = ? where job_id = ?", 
				new PreparedStatementSetter() {
					public void setValues(PreparedStatement ps) throws SQLException {
						ps.setInt(1, status);
						ps.setInt(2, id);
					}
				}
			);
	}

	@AuditTrail
	public void setStartTime(final int id) {
		final Timestamp now = new Timestamp(System.currentTimeMillis());
		this.jdbcTemplate.update("update job_tbl set job_start_time = ? where job_id = ?",
				new PreparedStatementSetter() {
					public void setValues(PreparedStatement ps) throws SQLException {
						ps.setTimestamp(1, now);
						ps.setInt(2, id);
					}
				}
			);

	}

	@AuditTrail
	public void setEndTime(final int id) {
		final Timestamp now = new Timestamp(System.currentTimeMillis());
		this.jdbcTemplate.update("update job_tbl set job_end_time = ? where job_id = ?",
				new PreparedStatementSetter() {
					public void setValues(PreparedStatement ps) throws SQLException {
						ps.setTimestamp(1, now);
						ps.setInt(2, id);
					}
				}
			);

	}
	
	@AuditTrail
	public List<Job> getJobsForInstance(int instanceId) {
		List<Job> jobs = this.jdbcTemplate.query("select * from job_tbl where job_instance_id = ?", new Object[] {instanceId}, new JobMapper());
		return jobs;
	}

	@AuditTrail
	public Job getJob(int jobId) {
		List<Job> jobs = this.jdbcTemplate.query("select * from job_tbl where job_id = ?", new Object[] {jobId}, new JobMapper());
		Job job = DataAccessUtils.singleResult(jobs);		
		return job;
	}

    @AuditTrail
    public void deleteAll() {
        jdbcTemplate.update("delete from job_tbl");
    }

	@AuditTrail
	public int addJob(Job job) {
		SimpleJdbcInsert insert = new SimpleJdbcInsert(dataSource).withTableName("job_tbl").usingGeneratedKeyColumns("job_id");
		Map<String,Object> parameters = new HashMap<String,Object>();
		parameters.put("job_type", job.getJobType());
		parameters.put("job_status", job.getJobStatus());
		parameters.put("job_instance_id", job.getInstanceId());
		parameters.put("job_services", job.getServices());
		parameters.put("job_extra_data", job.getExtraData());
		parameters.put("job_cloud", job.getCloud());
		parameters.put("job_zone", job.getZone());
		parameters.put("job_create_time", job.getCreateTime());
		parameters.put("job_start_time", job.getStartTime());
		parameters.put("job_end_time", job.getEndTime());
		Number newId = insert.executeAndReturnKey(parameters);
		LOG.info("Job id: "+newId);
		int jobId = newId.intValue();
		job.setJobId(jobId);
		return jobId;
	}

    @AuditTrail
    public Job getNewest(){
        List<Job> jobs = jdbcTemplate.query("select * from job_tbl order by job_id desc limit 1", new JobMapper());
        return DataAccessUtils.singleResult(jobs);
    }

	private static final class JobMapper implements RowMapper<Job> {

		public Job mapRow(ResultSet rs, int rowNumber) throws SQLException {
			Job job = new Job();
			job.setJobId(rs.getInt("job_id"));
			job.setJobType(rs.getString("job_type"));
			job.setJobStatus(rs.getInt("job_status"));
			job.setInstanceId(rs.getInt("job_instance_id"));
			job.setServices(rs.getString("job_services"));
			job.setExtraData(rs.getString("job_extra_data"));
			job.setCloud(rs.getInt("job_cloud"));
			job.setZone(rs.getString("job_zone"));
			job.setCreateTime(rs.getTimestamp("job_create_time"));
			job.setStartTime(rs.getTimestamp("job_start_time"));
			job.setEndTime(rs.getTimestamp("job_end_time"));
			return job;
		}
	}
}
