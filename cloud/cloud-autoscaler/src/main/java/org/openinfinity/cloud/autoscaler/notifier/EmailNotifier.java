package org.openinfinity.cloud.autoscaler.notifier;

import org.apache.log4j.Logger;
import org.openinfinity.cloud.autoscaler.util.ScalingData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Component;

@Component
public class EmailNotifier implements Notifier{

    private static final Logger LOG = Logger.getLogger(EmailNotifier.class.getName());

    @Autowired
    private MailSender mailSender;

    @Autowired
    private SimpleMailMessage templateMessage;

    @Value("${cloud.zone}")
    private String cloudZone;

    public void setMailSender(MailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void setTemplateMessage(SimpleMailMessage templateMessage) {
        this.templateMessage = templateMessage;
    }

    @Override
    public void notify(ScalingData d, NotificationType t){
        SimpleMailMessage msg = new SimpleMailMessage(this.templateMessage);
        String notification = null;
        switch (t){
            case SCALING_FAILED_RULE_LIMIT:
                notification = msgScalingFailedRuleLimit(d);
                break;
            case LOAD_FETCHING_FAILED:
                notification = msgGroupLoadFetchingFailed(d);
                break;
            case PREVIOUS_SCALING_FAILED:
                notification = msgPerviousScalingFailed(d);
                break;
            case MACHINE_CONFIGURATION_ERROR:
                notification = msgMachineConfigurationError(d);
            default:
                break;
        }
        msg.setText(notification);
        LOG.info(notification);
        this.mailSender.send(msg);
    }

    private String msgGroupLoadFetchingFailed(ScalingData d) {
        return "Autoscaling of the cluster is not possible.\n" +
               "Cluster load average is not available." + "\n\n" +
               "Severity: " + "HIGH\n" +
               "cloud zone: " + cloudZone + "\n" +
               "instance id: " + d.getCluster().getInstanceId() + "\n" +
               "cluster id: " + d.getCluster().getId() + "\n" +
               "attempts:" + d.getFailures();
    }

    private String msgScalingFailedRuleLimit(ScalingData d) {
        return "Scaling out attempt failed.\n" +
               "Load average for the cluster is too high, and cluster maximum size limit has been reached." + "\n\n" +
               "Severity: " + "CRITICAL\n" +
               "cloud zone: " + cloudZone + "\n" +
               "instance id: " + d.getCluster().getInstanceId() + "\n" +
               "cluster id: " + d.getCluster().getId() + "\n" +
               "load:" + d.getLoad() + "\n" +
               "max load threshold:" + d.getThreshold() + "\n" +
               "max cluster size:" + d.getScalingRule().getMaxNumberOfMachinesPerCluster();
    }

    private String msgPerviousScalingFailed(ScalingData d) {
        return "There is a scaling job failure for this cluster.\n" +
               "This indicates a problem with cloud infrastructure.\n" +
               "Autoscaler can not scale this cluster before the problem is solved and\n" +
               "scaling rule's job id is reset\n\n" +
               "Severity: " + "CRITICAL\n" +
               "cloud zone: " + cloudZone + "\n" +
               "instance id: " + d.getCluster().getInstanceId() + "\n" +
               "cluster id: " + d.getCluster().getId() + "\n" +
               "load:" + d.getLoad() + "\n" +
               "max load threshold:" + d.getThreshold() + "\n" +
               "max cluster size:" + d.getScalingRule().getMaxNumberOfMachinesPerCluster() + "\n" +
               "failed job id:" + d.getScalingRule().getJobId();
    }

    private String msgMachineConfigurationError(ScalingData d) {
        return "There are machines in the cluster which are not configured.\n" +
               "This indicates a problem with cloud infrastructure.\n" +
               "Autoscaler can not scale this cluster before the problem is solved and\n" +
               "all machines in the cluster are configured.\n\n" +
               "Severity: " + "CRITICAL\n" +
               "cloud zone: " + cloudZone + "\n" +
               "instance id: " + d.getCluster().getInstanceId() + "\n" +
               "cluster id: " + d.getCluster().getId() + "\n";
    }

}
