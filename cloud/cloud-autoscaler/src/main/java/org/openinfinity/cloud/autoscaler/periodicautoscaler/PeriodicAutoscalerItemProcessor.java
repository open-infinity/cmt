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

package org.openinfinity.cloud.autoscaler.periodicautoscaler;

import org.apache.log4j.Logger;
import org.openinfinity.cloud.autoscaler.common.AutoscalerItemProcessor;
import org.openinfinity.cloud.autoscaler.notifier.Notifier;
import org.openinfinity.cloud.autoscaler.notifier.Notifier.NotificationType;
import org.openinfinity.cloud.autoscaler.util.ScalingData;
import org.openinfinity.cloud.domain.Cluster;
import org.openinfinity.cloud.domain.Job;
import org.openinfinity.cloud.domain.Machine;
import org.openinfinity.cloud.domain.ScalingRule;
import org.openinfinity.cloud.service.healthmonitoring.HealthMonitoringService;
import org.openinfinity.cloud.service.scaling.Enumerations.ScalingState;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Batch processor for Periodic Autoscaler
 * 
 * @author Ilkka Leinonen
 * @author Vedran Bartonicek
 * @version 1.2.2
 * @since 1.2.0
 */
@Component("periodicAutoscalerItemProcessor")
public class PeriodicAutoscalerItemProcessor extends AutoscalerItemProcessor implements ItemProcessor<Machine, Job> {

    private static final Logger LOG = Logger.getLogger(PeriodicAutoscalerItemProcessor.class.getName());


    public static final String[] METRIC_NAMES = {"load-relative.rrd"};

    public static final String METRIC_TYPE_LOAD = "load";

    public static final String METRIC_PERIOD = "shortterm";

    /**
     * Maps cluster id (group id) to number of successive failures to get group load from rrd-http server
     */
    private Map<Integer, Integer> failureMap;

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
    private int httpAttemptsThreshold;

	@Autowired
	HealthMonitoringService healthMonitoringService;

    @Autowired
    Notifier notifier;

    PeriodicAutoscalerItemProcessor(){
        failureMap = new HashMap<Integer, Integer>();
    }

    public Map<Integer, Integer> getFailureMap() {
        return failureMap;
    }

    @Override
	public Job process(Machine machine){
        Job job = null;
        clusterId = machine.getClusterId();
        ScalingRule rule;
        try{
            rule = scalingRuleService.getRule(clusterId);
            if (!rule.isPeriodicScalingOn()){
                return null;
            }
        }
        catch (EmptyResultDataAccessException e){
            LOG.debug("Scaling rule does not exist for clusterId [" + clusterId + "]");
            return null;
        }

        Cluster cluster = clusterService.getCluster(clusterId);
        boolean keyExists = failureMap.containsKey(clusterId);
        int failures = 0;
        if (keyExists){
            failures = failureMap.get(clusterId);
        }
        float load = healthMonitoringService.getClusterLoad(machine, METRIC_NAMES, METRIC_TYPE_LOAD, METRIC_PERIOD);

        if (load == -1){
            failureMap.put(clusterId, ++failures);
            if (failures == httpAttemptsThreshold){
                notifier.notify(new ScalingData(failures, cluster), NotificationType.LOAD_FETCHING_FAILED);
            }
            return null;
        }
        else if (failures > 0){
            failureMap.put(clusterId, 0);
        }

        ScalingState state = scalingRuleService.applyScalingRule(load, clusterId, rule);
        switch (state) {
            case SCALE_OUT:
                return createJob(cluster, cluster.getNumberOfMachines() + 1);
            case SCALE_IN:
                return createJob(cluster, cluster.getNumberOfMachines() - 1);
            case SCALING_OUT_IMPOSSIBLE:
                notifier.notify(new ScalingData(load, cluster, rule), NotificationType.SCALING_FAILED);
            case SCALING_ONGOING:
            case SCALING_NOT_REQUIRED:
            default:
                break;
        }
        return job;
    }

}