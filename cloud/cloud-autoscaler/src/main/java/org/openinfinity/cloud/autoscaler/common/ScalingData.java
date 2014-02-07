package org.openinfinity.cloud.autoscaler.common;

import org.openinfinity.cloud.domain.Cluster;
import org.openinfinity.cloud.domain.ScalingRule;

public class ScalingData {
    int failures;
    float load;
    float threshold;
    Cluster cluster;
    ScalingRule scalingRule;

    public ScalingData(int failures, float load, float threshold, Cluster cluster, ScalingRule scalingRule) {
        this.failures = failures;
        this.load = load;
        this.threshold = threshold;
        this.cluster = cluster;
        this.scalingRule = scalingRule;
    }

    public ScalingData(float load, Cluster cluster, ScalingRule scalingRule) {
        this.load = load;
        this.cluster = cluster;
        this.scalingRule = scalingRule;
    }

    public ScalingData(int failures, Cluster cluster) {
        this.failures = failures;
        this.cluster = cluster;
    }

    public int getFailures() {
        return failures;
    }

    public void setFailures(int failures) {
        this.failures = failures;
    }

    public float getLoad() {
        return load;
    }

    public void setLoad(float load) {
        this.load = load;
    }

    public float getThreshold() {
        return threshold;
    }

    public void setThreshold(float threshold) {
        this.threshold = threshold;
    }

    public Cluster getCluster() {
        return cluster;
    }

    public void setCluster(Cluster cluster) {
        this.cluster = cluster;
    }

    public ScalingRule getScalingRule() {
        return scalingRule;
    }

    public void setScalingRule(ScalingRule scalingRule) {
        this.scalingRule = scalingRule;
    }
}
