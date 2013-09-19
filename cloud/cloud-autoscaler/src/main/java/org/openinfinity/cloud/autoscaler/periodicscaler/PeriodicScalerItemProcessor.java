/*
 * Copyright (c) 2013 the original author or authors.
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
import org.openinfinity.cloud.service.scaling.Enumerations.ScalingState;
import org.openinfinity.cloud.service.scaling.ScalingRuleService;
import org.openinfinity.cloud.domain.ScalingRule;
import org.openinfinity.core.exception.SystemException;
import org.openinfinity.core.util.ExceptionUtil;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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

	private static final String MSG_HM_METRIC_NOT_AVAILABLE = "Requested metric is not available";
	
	private static final String METRIC_RRD_FILE_LOAD = "load_relative.rrd";
	
	private static final String METRIC_TYPE_LOAD = "load";
	
	private static final String METRIC_PERIOD = "shortterm";
			
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
		try {
			return applyScalingRule(machine);
		}
		catch(SystemException e){
		    ExceptionUtil.throwBusinessViolationException(e.getMessage(), e);
			return null;
		}			
	}
	
	private Job applyScalingRule(Machine machine) throws IOException{
        int clusterId = machine.getClusterId();
	    ScalingRule rule = null; 
        Cluster cluster = null;
	    try{
            rule = scalingRuleService.getRule(clusterId);
            if (rule == null) return null;                
            cluster = clusterService.getCluster(clusterId);
        }
        catch(Exception e){
            if (rule == null) LOG.info("Rule not defined for cluster " + clusterId);
            else if (cluster == null) LOG.error("Cluster " + clusterId + " fetching failed.");
            return null;
        }
        float load = getClusterLoad(machine);
        LOG.debug("load = " + load);

        if (load == -1) return null;
        ScalingState state = scalingRuleService.calculateScalingState(rule, load, clusterId);
        switch (state) {
            case SCALING_OUT: 
                return createJob(machine, cluster, 1);
            case SCALING_IN:  
                return createJob(machine, cluster, -1);
            case SCALING_NEEDED_BUT_IMPOSSIBLE: 
                ExceptionUtil.throwSystemException(
                     "Cluster scaling failed. System load [" + load + "%] " +
                     "for cluster [" + clusterId + "] is + too high, but " +
                     "cluster maximum limit has been reached.");
            case SCALING_DISABLED: 
            case SCALING_SKIPPED:
            case SCALING_NOT_NEEDED:
        default:
            break;
        }
        return null;
    }
	
	private float getClusterLoad(Machine machine) throws IOException, IndexOutOfBoundsException,  
	    JsonParseException, JsonMappingException, SystemException {  
		String[] metricName = {METRIC_RRD_FILE_LOAD};
		
		HealthStatusResponse status = 
		    healthMonitoringService.getClusterHealthStatusLast(machine, METRIC_TYPE_LOAD, metricName, new Date());		
		List<SingleHealthStatus> metrics = status.getMetrics();
		LOG.debug("metrics len " + metrics.size());
        if (metrics.size() > 0){
	        Map<String, List<RrdValue>> values = metrics.get(0).getValues();
	        List<RrdValue> loadRrd = values.get(METRIC_PERIOD);
	        if (loadRrd != null){        
    	        return loadRrd.get(0).getValue().floatValue();
	        }
        }
        LOG.info(MSG_HM_METRIC_NOT_AVAILABLE);
        return -1;
	}
	
	private Job createJob(Machine machine, Cluster cluster, int machinesGrowth) {
        Instance instance = instanceService.getInstance(cluster.getInstanceId());
		return new Job("scale_cluster",
			cluster.getInstanceId(),
			instance.getCloudType(),
			JobService.CLOUD_JOB_CREATED,
			instance.getZone(),
			Integer.toString(cluster.getId()),
			cluster.getNumberOfMachines() + machinesGrowth);	
	}
	
}