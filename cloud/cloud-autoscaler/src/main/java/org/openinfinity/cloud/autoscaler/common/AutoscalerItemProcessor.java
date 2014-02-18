package org.openinfinity.cloud.autoscaler.common;

import org.openinfinity.cloud.autoscaler.notifier.Notifier;
import org.openinfinity.cloud.autoscaler.periodicautoscaler.ClusterProcessingState;
import org.openinfinity.cloud.autoscaler.util.ScalingData;
import org.openinfinity.cloud.domain.Cluster;
import org.openinfinity.cloud.domain.Instance;
import org.openinfinity.cloud.domain.Job;
import org.openinfinity.cloud.domain.ScalingRule;
import org.openinfinity.cloud.service.administrator.ClusterService;
import org.openinfinity.cloud.service.administrator.InstanceService;
import org.openinfinity.cloud.service.administrator.JobService;
import org.openinfinity.cloud.service.scaling.ScalingRuleService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

public class AutoscalerItemProcessor {

    /**
     * Maps cluster id (group id) to ClusterProcessingState data
     */

    protected Map<Integer, ClusterProcessingState> processingStatusMap;

    protected int clusterId;

    @Autowired
    protected InstanceService instanceService;

    @Autowired
    protected ScalingRuleService scalingRuleService;

    @Autowired
    protected ClusterService clusterService;

    @Autowired
    protected Notifier notifier;

    protected AutoscalerItemProcessor(){
        processingStatusMap = new HashMap<Integer, ClusterProcessingState>();
    }

    public Map<Integer, ClusterProcessingState> getProcessingStatusMap() {
        return processingStatusMap;
    }

    public Job createJob(Cluster cluster, int newClusterSize) {
        Instance instance = instanceService.getInstance(cluster.getInstanceId());
        return new Job("scale_cluster",
                cluster.getInstanceId(),
                instance.getCloudType(),
                JobService.CLOUD_JOB_CREATED,
                instance.getZone(),
                Integer.toString(cluster.getId()),
                newClusterSize);
    }

    protected ClusterProcessingState initializeFailures(){
        if (!processingStatusMap.containsKey(clusterId)){
            processingStatusMap.put(clusterId, new ClusterProcessingState(0, false, false));
        }
        return processingStatusMap.get(clusterId);
    }

    protected void notifyPreviousScalingFailed(ClusterProcessingState state, Cluster cluster, ScalingRule rule){
        if (!state.isJobFailureDetected()) {
            state.setJobFailureDetected(true);
            notifier.notify(new ScalingData(0, cluster, rule), Notifier.NotificationType.PREVIOUS_SCALING_FAILED);
        }
    }
    protected void notifyMachineConfigurationError(ClusterProcessingState state, Cluster cluster, ScalingRule rule){
        if (!state.isClusterConfigurationErrorDetected()) {
            state.setClusterConfigurationErrorDetected(true);
            notifier.notify(new ScalingData(0, cluster, rule), Notifier.NotificationType.MACHINE_CONFIGURATION_ERROR);
        }
    }
}
