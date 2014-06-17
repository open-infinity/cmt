package org.openinfinity.cloud.autoscaler.notifier;

import org.openinfinity.cloud.autoscaler.util.ScalingData;

public interface Notifier {
    void notify(ScalingData scalingData, NotificationType type);

    public enum NotificationType {
        SCALING_RULE_LIMIT,
        SCALING_RULE_INVALID,
        SCALING_JOB_ERROR,
        LOAD_FETCHING_FAILED,
        MACHINE_CONFIGURATION_ERROR,
        INVALID_INTERNAL_STATE
    }
}
