package org.openinfinity.cloud.autoscaler.notifier;

public interface Notifier {
    void notifyClusterScalingFailed(int clusterId, int instanceId, float load);
}
