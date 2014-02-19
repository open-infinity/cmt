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

import org.openinfinity.cloud.autoscaler.common.AutoscalerItemProcessor;
import org.openinfinity.cloud.autoscaler.notifier.Notifier.NotificationType;
import org.openinfinity.cloud.autoscaler.util.ScalingData;
import org.openinfinity.cloud.domain.Job;
import org.openinfinity.cloud.domain.Machine;
import org.openinfinity.cloud.service.healthmonitoring.HealthMonitoringService;
import org.openinfinity.cloud.service.scaling.Enumerations.ScalingStatus;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Component;

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

    public static final String[] METRIC_NAMES = {"load-relative.rrd"};

    public static final String METRIC_TYPE_LOAD = "load";

    public static final String METRIC_PERIOD = "shortterm";

    /**
     * Specifies number of tolerated successive failures to get group load.
     *
     * If  number of failures is bigger than threshold, a notification is sent.
     */
    @Value("${http.attempts.threshold}")
    private int httpAttemptsThreshold;

	@Autowired
	HealthMonitoringService healthMonitoringService;

    PeriodicAutoscalerItemProcessor(){
    }

    @Override
	public Job process(Machine machine){
        try{
            rule = scalingRuleService.getRule(machine.getClusterId());
            if (!rule.isPeriodicScalingOn()){
                return null;
            }
        }
        catch (EmptyResultDataAccessException e){
            return null;
        }

        cluster = clusterService.getCluster(machine.getClusterId());
        ClusterProcessingState clusterState = getClusterState();
        int httpFailures = clusterState.getHttpFailures();

        float load = healthMonitoringService.getClusterLoad(machine, METRIC_NAMES, METRIC_TYPE_LOAD, METRIC_PERIOD);
        if (load == -1){
            clusterState.setHttpFailures(++httpFailures);
            if (httpFailures == httpAttemptsThreshold){
                notifier.notify(new ScalingData(httpFailures, cluster), NotificationType.LOAD_FETCHING_FAILED);
            }
            return null;
        }
        else {
            clusterState.setLoad(load);
            clusterState.setHttpFailures(0);
        }

        ScalingStatus scalingStatus = scalingRuleService.applyScalingRule(load, cluster.getId(), rule);
        Job job = handleScalingStatus(clusterState, rule, scalingStatus);
        processingStatusMap.put(cluster.getId(), clusterState);

        return job;
    }

    protected int getScaleOutSize(){
        return cluster.getNumberOfMachines() + 1;
    }

    protected int getScaleInSize(){
        return cluster.getNumberOfMachines() - 1;
    }

}