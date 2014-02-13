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
            case SCALING_FAILED:
                notification = msgClusterScalingFailed(d);
                break;
            case LOAD_FETCHING_FAILED:
                notification = msgGroupLoadFetchingFailed(d);
                break;
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

    private String msgClusterScalingFailed(ScalingData d) {
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
}
