package org.openinfinity.cloud.autoscaler.periodicautoscaler;

public class ClusterProcessingState {
    int httpFailures;
    boolean jobFailureDetected;
    boolean clusterConfigurationErrorDetected;

    public ClusterProcessingState(int httpFailures, boolean jobFailureDetected, boolean clusterConfigurationErrorDetected) {
        this.httpFailures = httpFailures;
        this.jobFailureDetected = jobFailureDetected;
        this.clusterConfigurationErrorDetected = clusterConfigurationErrorDetected;
    }

    public int getHttpFailures() {
        return httpFailures;
    }

    public void setHttpFailures(int httpFailures) {
        this.httpFailures = httpFailures;
    }

    public boolean isJobFailureDetected() {
        return jobFailureDetected;
    }

    public void setJobFailureDetected(boolean jobFailureDetected) {
        this.jobFailureDetected = jobFailureDetected;
    }

    public boolean isClusterConfigurationErrorDetected() {
        return clusterConfigurationErrorDetected;
    }

    public void setClusterConfigurationErrorDetected(boolean clusterConfigurationErrorDetected) {
        this.clusterConfigurationErrorDetected = clusterConfigurationErrorDetected;
    }
}
