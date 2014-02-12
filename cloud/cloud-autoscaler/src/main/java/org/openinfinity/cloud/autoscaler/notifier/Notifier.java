package org.openinfinity.cloud.autoscaler.notifier;

import org.openinfinity.cloud.autoscaler.util.ScalingData;

public interface Notifier {
    void notify(ScalingData scalingData, NotificationType type);

    public enum NotificationType {
        SCALING_FAILED,
        LOAD_FETCHING_FAILED
    }
}
