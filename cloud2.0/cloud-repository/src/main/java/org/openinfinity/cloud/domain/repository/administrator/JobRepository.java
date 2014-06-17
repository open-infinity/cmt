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

import org.openinfinity.cloud.domain.Job;

import java.util.List;

/**
 * Interface for Job repository
 
 * @author Ossi Hämäläinen
 * @author Ilkka Leinonen
 * @author Juha-Matti Sironen
 * @version 1.0.0 Initial version
 * @since 1.0.0
 */

public interface JobRepository {

	List<Job> getJobs(int status, int limit);
	void updateStatus(int id, int status);
	void setStartTime(int id);
	void setEndTime(int id);
	Job getJob(int jobId);
	int addJob(Job job);
    Job getNewest();
	List<Job> getJobsForInstance(int instanceId);
    void deleteAll();

}
