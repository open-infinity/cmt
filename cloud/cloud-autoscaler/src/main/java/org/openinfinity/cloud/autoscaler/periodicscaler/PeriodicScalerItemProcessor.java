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
import org.openinfinity.cloud.autoscaler.common.ScalingData;
import org.openinfinity.cloud.autoscaler.notifier.Notifier;
import org.openinfinity.cloud.autoscaler.notifier.Notifier.NotificationType;
import org.openinfinity.cloud.domain.*;
import org.openinfinity.cloud.service.administrator.ClusterService;
import org.openinfinity.cloud.service.administrator.InstanceService;
import org.openinfinity.cloud.service.administrator.JobService;
import org.openinfinity.cloud.service.healthmonitoring.HealthMonitoringService;
import org.openinfinity.cloud.service.scaling.Enumerations.ClusterScalingState;
import org.openinfinity.cloud.service.scaling.ScalingRuleService;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Batch processor for periodic autoscaler
 * 
 * @author Ilkka Leinonen
 * @author Vedran Bartonicek
 * @version 1.2.2
 * @since 1.2.0
 */
@Component("periodicScalerItemProcessor")
public class PeriodicScalerItemProcessor implements ItemProcessor<Machine, Job> {

	private static final Logger LOG = Logger.getLogger(PeriodicScalerItemProcessor.class.getName());

	public static final String METRIC_RRD_FILE_LOAD = "load-relative.rrd";

    public static final String METRIC_TYPE_LOAD = "load";

    public static final String METRIC_PERIOD = "shortterm";

    /**
     * Maps cluster id (group id) to number of successive failures to get group load from rrd-http server
     */
    Map<Integer, Integer> failureMap;

    /**
     * Used to make unit testing easier
     */
    private int clusterId;

    /**
     * Specifies number of tolerated successive failures to get group load.
     *
     * If  number of failures is bigger than threshold, a notification is sent.
     */
    @Value("${http.attempts.threshold}")
    int httpAttemptsThreshold;

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

    PeriodicScalerItemProcessor(){
        LOG.info("Constructing PeriodicScalerItemProcessor");
        failureMap = new HashMap<Integer, Integer>();
    }

    public int getClusterId() {
        return clusterId;
    }

    public Map<Integer, Integer> getFailureMap() {
        return failureMap;
    }

    public void setFailureMap(Map<Integer, Integer> failureMap) {
        this.failureMap = failureMap;
    }

    @Override
	public Job process(Machine machine){
        Job job = null;

        // Get cluster for machine
        clusterId = machine.getClusterId();
        ScalingRule rule = scalingRuleService.getRule(clusterId);
        if (rule.isPeriodicScalingOn() == false){
            return null;
        }
        Cluster cluster = clusterService.getCluster(clusterId);

        // Get number of successive failed attempts to get a group load
        boolean keyExists = failureMap.containsKey(clusterId);
        int failures = 0;
        if (keyExists == true){
            failures = failureMap.get(clusterId);
        }


        // Get group load and handle result

        // Get group load from http-rrd server
        String[] metricNames = {METRIC_RRD_FILE_LOAD};
        float load = healthMonitoringService.getClusterLoad(machine, metricNames, METRIC_TYPE_LOAD, METRIC_PERIOD);

        // Load not received. Return null job and send notification if necessary
        if (load == -1){
            failureMap.put(clusterId, ++failures);
            if (failures == httpAttemptsThreshold){
                notifier.notify(new ScalingData(failures, cluster), NotificationType.LOAD_FETCHING_FAILED);
            }
            return null;
        }

        // Load received. Clear entry in failureMap for clusterId
        else if (failures > 0){
            failureMap.put(clusterId, 0);
        }

        // Apply scaling rule on cluster with given load.
        // Autoscaler takes actions depending on returned ClusterScalingState.
        ClusterScalingState state = scalingRuleService.applyScalingRule(load, clusterId, rule);
        switch (state) {
            case REQUIRES_SCALING_OUT:
                return createJob(machine, cluster, 1);
            case REQUIRES_SCALING_IN:
                return createJob(machine, cluster, -1);
            case REQUIRED_SCALING_IS_NOT_POSSIBLE:
               /*
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
                */
                notifier.notify(new ScalingData(load, cluster, rule), NotificationType.SCALING_FAILED);
            case SCALING_SKIPPED:
            case REQUIRES_NO_SCALING:
            default:
                break;
        }
        return job;
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