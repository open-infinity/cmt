package org.openinfinity.cloud.autoscaler.common;

import org.openinfinity.cloud.autoscaler.notifier.Notifier;
import org.openinfinity.cloud.autoscaler.notifier.Notifier.NotificationType;
import org.openinfinity.cloud.autoscaler.periodicautoscaler.ClusterProcessingState;
import org.openinfinity.cloud.autoscaler.util.ScalingData;
import org.openinfinity.cloud.domain.Cluster;
import org.openinfinity.cloud.domain.Instance;
import org.openinfinity.cloud.domain.Job;
import org.openinfinity.cloud.domain.ScalingRule;
import org.openinfinity.cloud.service.administrator.ClusterService;
import org.openinfinity.cloud.service.administrator.InstanceService;
import org.openinfinity.cloud.service.administrator.JobService;
import org.openinfinity.cloud.service.scaling.Enumerations;
import org.openinfinity.cloud.service.scaling.ScalingRuleService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

public abstract class AutoscalerItemProcessor {

    /**
     * Maps cluster id (group id) to ClusterProcessingState data
     */

    protected Map<Integer, ClusterProcessingState> processingStatusMap;

    protected Cluster cluster;

    protected ScalingRule rule;

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

    protected Job handleScalingStatus(ClusterProcessingState clusterState, ScalingRule rule, Enumerations.ScalingStatus scalingStatus){
        Job job = null;
        switch (scalingStatus) {
            case SCALING_OUT_REQUIRED:
                job = createJob(cluster, getScaleOutSize());
                clusterState.clearErrors();
                break;
            case SCALING_IN_REQUIRED:
                job = createJob(cluster, getScaleInSize());
                clusterState.clearErrors();
                break;
            case SCALING_IMPOSSIBLE_SCALING_JOB_ERROR:
                notifyScalingJobError(clusterState, cluster, rule);
                break;
            case SCALING_IMPOSSIBLE_MACHINE_CONFIGURATION_ERROR:
                notifyMachineConfigurationError(clusterState, cluster, rule);
                break;
            case SCALING_IMPOSSIBLE_INVALID_RULE:
                notifyInvalidRule(clusterState, cluster, rule);
                break;
            case SCALING_IMPOSSIBLE_SCALING_RULE_LIMIT:
                notifyScalingRuleLimit(clusterState, cluster, rule);
                break;
            case SCALING_IMPOSSIBLE_SCALING_ALREADY_ONGOING:
                break;
            case SCALING_NOT_REQUIRED:
                break;
            default:
                break;
        }
        return job;
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

    protected ClusterProcessingState getClusterState(){
        if (!processingStatusMap.containsKey(cluster.getId())){
            processingStatusMap.put(cluster.getId(), new ClusterProcessingState(0, false, false, false, false));
        }
        return processingStatusMap.get(cluster.getId());
    }

    protected void notifyScalingJobError(ClusterProcessingState state, Cluster cluster, ScalingRule rule){
        if (!state.isDetectedJobError()) {
            state.setDetectedJobError(true);
            notifier.notify(new ScalingData(0, cluster, rule), NotificationType.SCALING_JOB_ERROR);
        }
    }

    protected void notifyMachineConfigurationError(ClusterProcessingState state, Cluster cluster, ScalingRule rule){
        if (!state.isDetectedMachineConfigurationError()) {
            state.setDetectedMachineConfigurationError(true);
            notifier.notify(new ScalingData(0, cluster, rule), NotificationType.MACHINE_CONFIGURATION_ERROR);
        }
    }

    protected void notifyInvalidRule(ClusterProcessingState state, Cluster cluster, ScalingRule rule){
        if (!state.isDetectedInvalidScalingRule()) {
            state.setDetectedInvalidScalingRule(true);
            notifier.notify(new ScalingData(0, cluster, rule), NotificationType.SCALING_RULE_INVALID);
        }
    }

    protected void notifyScalingRuleLimit(ClusterProcessingState state, Cluster cluster, ScalingRule rule){
        if (!state.isDetectedScalingRuleLimit()) {
            state.setDetectedScalingRuleLimit(true);
            notifier.notify(new ScalingData(0, cluster, rule), NotificationType.SCALING_RULE_LIMIT);
        }
    }

    protected abstract int getScaleOutSize();

    protected abstract int getScaleInSize();

}
