/*
 * Copyright (c) 2011 the original author or authors.
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

package org.openinfinity.cloud.application.worker.service;

import java.util.Collection;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.openinfinity.cloud.application.worker.Configurer;
import org.openinfinity.cloud.application.worker.Processor;
import org.openinfinity.cloud.application.worker.Updater;
import org.openinfinity.cloud.application.worker.Worker;
import org.openinfinity.cloud.domain.Job;
import org.openinfinity.cloud.domain.Machine;
import org.openinfinity.cloud.service.administrator.InstanceService;
import org.openinfinity.cloud.service.administrator.JobService;
import org.openinfinity.cloud.service.administrator.MachineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * CloudAdmin processor Service
 * @author Ossi Hämäläinen
 * @version 1.0.0 Initial version
 * @since 1.0.0
 */

@Service
public class CloudAdminProcessor implements Processor {
	private static final Logger LOG = Logger.getLogger(CloudAdminProcessor.class.getName());
	
	@Autowired
	@Qualifier("jobService")
	private JobService jobService;
	
	@Autowired
	@Qualifier("machineService")
	private MachineService machineService;
	
	@Autowired
	@Qualifier("EC2Worker")
    private Worker worker;
	
	@Autowired
	@Qualifier("MachineConfigurer")
	private Configurer configurer;
	
	@Autowired
	@Qualifier("BigDataConfigurer")
	private Configurer bigDataConfigurer;
	
	@Autowired
	@Qualifier("MachineUpdater")
	private Updater updater;
	
	@Scheduled(fixedDelay = 10000)
	public void process() {
		String threadName = Thread.currentThread().getName();
		LOG.info(threadName+": Checking database for Jobs");
		Collection<Job> jobs = jobService.getJobs(JobService.CLOUD_JOB_CREATED, 5);
		if(jobs != null && jobs.size() > 0) {
			LOG.info(threadName+": Found "+jobs.size()+" jobs, starting workers");
			Iterator<Job> i = jobs.iterator();
			while(i.hasNext()) {
				Job job = i.next();
				if(job.getCloud() == InstanceService.CLOUD_TYPE_AMAZON || job.getCloud() == InstanceService.CLOUD_TYPE_EUCALYPTUS) {
					LOG.info(threadName+": Starting EC2 worker for job "+job.getJobId());
					jobService.updateStatus(job.getJobId(), JobService.CLOUD_JOB_STARTED);
					job.setJobStatus(JobService.CLOUD_JOB_STARTED);
					
					worker.work(job);
					
				}
			}
		}
		
		LOG.info(threadName+": Checking machines needing configure");
		Collection<Machine> machines = machineService.getMachinesNeedingConfigure();
		if(machines != null && machines.size() > 0) {
			LOG.info(threadName+": Found "+machines.size()+" machines, starting configure workers");
			Iterator<Machine> j = machines.iterator();
			while(j.hasNext()) {
				Machine m = j.next();
				LOG.info(threadName+": Starting configuration worker job for machine "+m.getId());
				machineService.updateMachineConfigure(m.getId(), MachineService.MACHINE_CONFIGURE_STARTED);
				m.setConfigured(MachineService.MACHINE_CONFIGURE_STARTED);
				configurer.configure(m);
			}
		}
		
		LOG.info(threadName+": Checking bigdata machines needing configure");
		Collection<Machine> bdMachines = machineService.getBigDataMachinesNeedingConfigure();
		if(bdMachines != null && bdMachines.size() > 0) {
			LOG.info(threadName+": Found "+bdMachines.size()+" machines, starting configure workers");
			Iterator<Machine> j = bdMachines.iterator();
			while(j.hasNext()) {
				Machine m = j.next();
				LOG.info(threadName+": Starting configuration worker job for machine "+m.getId());
				machineService.updateMachineConfigure(m.getId(), MachineService.DATA_MACHINE_CONFIGURE_STARTED);
				m.setConfigured(MachineService.DATA_MACHINE_CONFIGURE_STARTED);
				bigDataConfigurer.configure(m);
			}
		}
		
		LOG.info(threadName+": Checking machines needing status update from cloud");
		Collection<Machine> amazonList = machineService.getMachinesNeedingUpdate(InstanceService.CLOUD_TYPE_AMAZON);
		if(amazonList != null && amazonList.size() > 0) {
			LOG.info(threadName+": Found "+amazonList.size()+" machines, starting status update from amazon cloud");
			updater.update(amazonList, InstanceService.CLOUD_TYPE_AMAZON);
		}
		Collection<Machine> eucaList = machineService.getMachinesNeedingUpdate(InstanceService.CLOUD_TYPE_EUCALYPTUS);
		if(eucaList != null && eucaList.size() > 0) {
			LOG.info(threadName+": Found "+eucaList.size()+" machines, starting status update from eucalyptus cloud");
			updater.update(eucaList, InstanceService.CLOUD_TYPE_EUCALYPTUS);
		}
	}
	
}
