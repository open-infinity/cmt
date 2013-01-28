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

package org.openinfinity.cloud.autoscaler.periodicscaler;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.openinfinity.cloud.domain.Cluster;
import org.openinfinity.cloud.domain.HealthStatusResponse;
import org.openinfinity.cloud.domain.HealthStatusResponse.SingleHealthStatus;
import org.openinfinity.cloud.domain.Instance;
import org.openinfinity.cloud.domain.Job;
import org.openinfinity.cloud.domain.Machine;
import org.openinfinity.cloud.domain.RrdValue;
import org.openinfinity.cloud.service.administrator.ClusterService;
import org.openinfinity.cloud.service.administrator.InstanceService;
import org.openinfinity.cloud.service.administrator.JobService;
import org.openinfinity.cloud.service.administrator.MachineService;
import org.openinfinity.cloud.service.healthmonitoring.HealthMonitoringService;
import org.openinfinity.cloud.service.healthmonitoring.Request;
import org.openinfinity.cloud.service.scaling.Enumerations.ScalingBalance;
import org.openinfinity.cloud.service.scaling.ScalingRuleService;
import org.openinfinity.core.exception.SystemException;
import org.openinfinity.core.util.ExceptionUtil;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * Batch processor for verifying the scaling rules.
 * 
 * @author Ilkka Leinonen
 * @author Vedran Bartonicek
 * @version 1.0.0
 * @since 1.0.0
 */
@Component("periodicScalerItemProcessor")
public class PeriodicScalerItemProcessor implements ItemProcessor<Machine, Job> {
	private static final Logger LOG = Logger.getLogger(PeriodicScalerItemProcessor.class.getName());

	private static final String MSG_HM_NOT_CONFIGURED = "Health monitoring is not configured for the cluster";
	
	private static final String METRIC_CPU_IDLE = "cpu-idle.rrd";
			
	private final int ONE_SECOND = 1000;

	@Autowired
	MachineService machineService;
	
	@Autowired
	InstanceService instanceService;
	
	@Autowired 
	ClusterService clusterService;
	
	@Autowired
	ScalingRuleService scalingRuleService;
	
	@Autowired
	HealthMonitoringService healthMonitoringService;
	
	@Override
	public Job process(Machine machine) throws Exception {
        LOG.debug("-------------MACHINE ID " + machine.getDnsName());
		LOG.debug("-------------CLUSTER ID: " + machine.getClusterId());
		try {
			return applyScalingRulesOnCluster(machine, retrieveClusterCurrentCpuLoadPercentage(machine));
		}
		//catch HM not configured exceptions, don't catch others
		catch(SystemException e){
			//ExceptionUtil.throwSystemException(e);
			e.printStackTrace();
			return null;
		}			
	}

	private float retrieveClusterCurrentCpuLoadPercentage(Machine machine) throws IOException, JsonParseException, JsonMappingException, SystemException {
		Date now = new Date();
		Date past = new Date(now.getTime() - ONE_SECOND * 60);
		float cpuLoad = -1;
		
		String[] metricNames = {METRIC_CPU_IDLE};
       
		Map<Integer,String> rrdServers = healthMonitoringService.getClusterMasterMap(); 
        LOG.debug("------------cluster id = " + machine.getClusterId());
        LOG.debug("------------rrdServerdnsName = " + rrdServers.get(machine.getClusterId()));

		String rrdServerdnsName = rrdServers.get(machine.getClusterId()); 
		// TODO: group name will change into clusterId
		HealthStatusResponse status = healthMonitoringService.getHealthStatus(rrdServerdnsName, Request.SOURCE_GROUP,"cpu-0", 
        		metricNames, past, past);
	
        List<SingleHealthStatus> metrics =  status.getMetrics();
        LOG.debug("------------metrics size = " + metrics.size());

        if (metrics.size() > 0){
	        LOG.debug("------------metrics name = " + metrics.get(0).getName());
	        Map<String, List<RrdValue>> values = metrics.get(0).getValues();
	       
	        // for (Map.Entry<String, List<RrdValue>> entry : values.entrySet()) {
	        //	String entryName = entry.getKey();
		    //   LOG.debug("------------entryName = " + entryName);
			//	}	
				
	        List<RrdValue> rrds = values.get("value");
	        LOG.debug("------------rrds size = " + rrds.size());
	  
	        cpuLoad = (float) (100 - rrds.get(0).getValue());
	        LOG.debug("------------cpu load = " + cpuLoad);
        }
        else {
        	ExceptionUtil.throwSystemException(MSG_HM_NOT_CONFIGURED);
        	//LOG.warn(MSG_HM_NOT_CONFIGURED);
        }
		return cpuLoad;
	}
	
	private Job applyScalingRulesOnCluster(Machine machine, float currentCpuLoadPercentage) {      
        Job job = null;
		ScalingBalance scalingBalance = scalingRuleService.calculateScalingBalance(currentCpuLoadPercentage, machine.getClusterId());
		Cluster cluster = clusterService.getCluster(machine.getClusterId());
		if (cluster == null) {
			LOG.error("Cluster fetching failed.");
			return job;
		}
		switch (scalingBalance) {
			case SCALE_OUT: return createJob(machine, cluster, 1);
			case SCALE_IN: 	return createJob(machine, cluster, -1);
			case SYSTEM_DISASTER_PANIC: ExceptionUtil.throwSystemException
				("Cluster scaling rules failed. Current CPU [" + currentCpuLoadPercentage + "%] " +
				 "load for cluster [" + machine.getClusterId() + "] is remarkable and cluster maximum limit has been reached.");
		}
		return null;
	}
	
	private Job createJob(Machine machine, Cluster cluster, int machinesGrowth) {
		// TODO set scalingrule
		Instance instance = instanceService.getInstanceByMachineId(machine.getId());
		Job job = null;		
		job = new Job("scale_cluster",
			cluster.getInstanceId(),
			instance.getCloudType(),
			JobService.CLOUD_JOB_CREATED,
			instance.getZone());
		// TODO: use a new Job constructor
		job.addService(Integer.toString(cluster.getId()), cluster.getNumberOfMachines() + machinesGrowth);
		return job;
	}
	
}