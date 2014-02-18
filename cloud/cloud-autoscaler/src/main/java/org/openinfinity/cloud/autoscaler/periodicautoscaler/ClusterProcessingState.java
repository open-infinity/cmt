package org.openinfinity.cloud.autoscaler.periodicautoscaler;

public class ClusterProcessingState {
    int httpFailures;
    boolean jobFailureDetected;

    public ClusterProcessingState(int httpFailures, boolean jobFailureDetected) {
        this.httpFailures = httpFailures;
        this.jobFailureDetected = jobFailureDetected;
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

    public void setJobFailureDetected(boolean newJobFailureDetected) {
        this.jobFailureDetected = newJobFailureDetected;
    }
}
