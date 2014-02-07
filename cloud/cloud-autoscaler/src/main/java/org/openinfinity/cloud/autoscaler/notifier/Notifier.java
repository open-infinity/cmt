package org.openinfinity.cloud.autoscaler.notifier;

import org.openinfinity.cloud.autoscaler.common.ScalingData;

public interface Notifier {
    //void notifyClusterScalingFailed(int clusterId, int instanceId, float load, float threshold);
    //void notifyGroupLoadFetchingFailed(int clusterId, int instanceId, int failures);
    void notify(ScalingData scalingData, NotificationType type);

    public enum NotificationType {
        SCALING_FAILED,
        LOAD_FETCHING_FAILED
    }
}
