package org.openinfinity.cloud.autoscaler.periodicautoscaler;

public class ClusterProcessingState {
    int httpFailures;
    boolean detectedJobError;
    boolean detectedMachineConfigurationError;
    boolean detectedInvalidScalingRule;
    boolean detectedScalingRuleLimit;

   public ClusterProcessingState(int httpFailures, boolean detectedJobError, boolean detectedMachineConfigurationError, boolean detectedInvalidScalingRule, boolean scalingImpossibleRuleLimit) {
        this.httpFailures = httpFailures;
        this.detectedJobError = detectedJobError;
        this.detectedMachineConfigurationError = detectedMachineConfigurationError;
        this.detectedInvalidScalingRule = detectedInvalidScalingRule;
        this.detectedScalingRuleLimit = scalingImpossibleRuleLimit;
   }

    public int getHttpFailures() {
        return httpFailures;
    }

    public void setHttpFailures(int httpFailures) {
        this.httpFailures = httpFailures;
    }

    public boolean isDetectedJobError() {
        return detectedJobError;
    }

    public void setDetectedJobError(boolean detectedJobError) {
        this.detectedJobError = detectedJobError;
    }

    public boolean isDetectedMachineConfigurationError() {
        return detectedMachineConfigurationError;
    }

    public void setDetectedMachineConfigurationError(boolean detectedMachineConfigurationError) {
        this.detectedMachineConfigurationError = detectedMachineConfigurationError;
    }

    public boolean isDetectedInvalidScalingRule() {
        return detectedInvalidScalingRule;
    }

    public void setDetectedInvalidScalingRule(boolean detectedInvalidScalingRule) {
        this.detectedInvalidScalingRule = detectedInvalidScalingRule;
    }

    public boolean isDetectedScalingRuleLimit() {
        return detectedScalingRuleLimit;
    }

    public void setDetectedScalingRuleLimit(boolean detectedScalingRuleLimit) {
        this.detectedScalingRuleLimit = detectedScalingRuleLimit;
    }

    public void clearErrors(){
        this.detectedJobError = false;
        this.detectedMachineConfigurationError = false;
        this.detectedInvalidScalingRule = false;
        this.detectedScalingRuleLimit = false;
    }
}
