package org.openinfinity.cloud.autoscaler.notifier;

import org.openinfinity.cloud.autoscaler.util.ScalingData;

public interface Notifier {
    void notify(ScalingData scalingData, NotificationType type);

    public enum NotificationType {
        SCALING_FAILED_RULE_LIMIT,
        LOAD_FETCHING_FAILED,
        PREVIOUS_SCALING_FAILED,
        MACHINE_CONFIGURATION_ERROR
    }
}
