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

import org.apache.log4j.Logger;
import org.openinfinity.cloud.autoscaler.notifier.Notifier;
import org.openinfinity.cloud.domain.*;
import org.openinfinity.cloud.service.administrator.ClusterService;
import org.openinfinity.cloud.service.administrator.InstanceService;
import org.openinfinity.cloud.service.administrator.JobService;
import org.openinfinity.cloud.service.healthmonitoring.HealthMonitoringService;
import org.openinfinity.cloud.service.scaling.Enumerations.ClusterScalingState;
import org.openinfinity.cloud.service.scaling.ScalingRuleService;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Batch processor for verifying the scaling rules.
 * 
 * @author Ilkka Leinonen
 * @author Vedran Bartonicek
 * @version 1.2.2
 * @since 1.2.0
 */
@Component("periodicScalerItemProcessor")
public class PeriodicScalerItemProcessor implements ItemProcessor<Machine, Job> {
	private static final Logger LOG = Logger.getLogger(PeriodicScalerItemProcessor.class.getName());

	private static final String MSG_HM_METRIC_NOT_AVAILABLE = "Requested metric is not available";
	
	private static final String METRIC_RRD_FILE_LOAD = "load-relative.rrd";
	
	private static final String METRIC_TYPE_LOAD = "load";
	
	private static final String METRIC_PERIOD = "shortterm";
			
	@Autowired
	InstanceService instanceService;
	
	@Autowired 
	ClusterService clusterService;
	
	@Autowired
	ScalingRuleService scalingRuleService;
		
	@Autowired
	HealthMonitoringService healthMonitoringService;

    @Autowired
    Notifier notifier;
	
	@Override
	public Job process(Machine machine){
        Job job = null;
        int clusterId = machine.getClusterId();
        ScalingRule rule = scalingRuleService.getRule(clusterId);
        if (rule.isPeriodicScalingOn() == false){
            return null;
        }
        Cluster cluster = clusterService.getCluster(clusterId);
        String[] metricName = {METRIC_RRD_FILE_LOAD};
        float load = healthMonitoringService.getClusterLoad(machine, metricName, METRIC_TYPE_LOAD, METRIC_PERIOD);
        LOG.debug("load = " + load);
        if (load == -1){
            return null;
        }

        ClusterScalingState state = scalingRuleService.calculateScalingState(rule, load, clusterId);
        switch (state) {
            case REQUIRES_SCALING_OUT:
                return createJob(machine, cluster, 1);
            case REQUIRES_SCALING_IN:
                return createJob(machine, cluster, -1);
            case REQUIRED_SCALING_IS_NOT_POSSIBLE:
                LOG.info("Cluster scaling failed. System load [" + load + "%] for cluster [" + clusterId + "] is too high, but cluster maximum size limit has been reached.");
                if (notifier == null){
                    LOG.info("Null notifier");
                }
                if (cluster == null){
                    LOG.info("Null cluster");
                }
                LOG.info("notifier" + notifier.toString());
                LOG.info("args:" + clusterId);
                LOG.info("args:" + cluster.getInstanceId());
                LOG.info("args:" + load);
                notifier.notifyClusterScalingFailed(clusterId, cluster.getInstanceId(), load);
            case SCALING_DISABLED:
            case SCALING_SKIPPED:
            case REQUIRES_NO_SCALING:
            default:
                break;
        }
        //return null;

        /*
        catch(EmptyResultDataAccessException e){
            ExceptionUtil.throwSystemException(e.getMessage(), e);
        }

        catch (RuntimeException e) {
            ExceptionUtil.throwBusinessViolationException(e.getMessage(), e);
        }
        */

        return job;
    }

    /*
	private Job applyScalingRule(Machine machine) throws IOException{
        int clusterId = machine.getClusterId();
	    ScalingRule rule = null; 
        Cluster cluster = null;
	    try{
            rule = scalingRuleService.getRule(clusterId);
            if (rule == null) return null;
            else if (rule.isPeriodicScalingOn() == false) return null;
            else cluster = clusterService.getCluster(clusterId);
        }
        catch(Exception e){
            if (rule == null){ 
            	LOG.info("Rule not defined for cluster " + clusterId);
            }
            else if (cluster == null) {
            	LOG.error("Cluster " + clusterId + " fetching failed.");
            }
            return null;
        }
        float load = getClusterLoad(machine);
        LOG.debug("load = " + load);
        if (load == -1) return null;

        ClusterScalingState state = scalingRuleService.calculateScalingState(rule, load, clusterId);
        switch (state) {
            case REQUIRES_SCALING_OUT: 
                return createJob(machine, cluster, 1);
            case REQUIRES_SCALING_IN:  
                return createJob(machine, cluster, -1);
            case REQUIRED_SCALING_IS_NOT_POSSIBLE: 
                ExceptionUtil.throwSystemException(
                     "Cluster scaling failed. System load [" + load + "%] " +
                     "for cluster [" + clusterId + "] is + too high, but " +
                     "cluster maximum limit has been reached.");
            case SCALING_DISABLED: 
            case SCALING_SKIPPED:
            case REQUIRES_NO_SCALING:
        default:
            break;
        }
        return null;
    }
	*/

    /*
	private float getClusterLoad(Machine machine){
		String[] metricName = {METRIC_RRD_FILE_LOAD};
		HealthStatusResponse status = healthMonitoringService.getClusterHealthStatusLast(machine, METRIC_TYPE_LOAD, metricName, new Date());
		List<SingleHealthStatus> metrics = status.getMetrics();
        if (metrics.size() > 0){
	        Map<String, List<RrdValue>> values = metrics.get(0).getValues();
	        List<RrdValue> loadRrd = values.get(METRIC_PERIOD);
	        if (loadRrd != null){        
    	        return loadRrd.get(0).getValue().floatValue();
	        }
        }
        return -1;
	}
	*/
	
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