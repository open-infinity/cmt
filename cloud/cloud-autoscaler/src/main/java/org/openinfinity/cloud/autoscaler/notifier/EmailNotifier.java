package org.openinfinity.cloud.autoscaler.notifier;

import org.apache.log4j.Logger;
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
    public void notifyClusterScalingFailed(int clusterId, int instanceId, float load) {
        LOG.info("ENTER");
        SimpleMailMessage msg = new SimpleMailMessage(this.templateMessage);
        LOG.info("msg created");
        msg.setText("Cluster scaling failed. Average system load [" + load + "%] for cluster [" + clusterId + "], instance [" + instanceId + "]," +
                "cloud zone [" + cloudZone + "]  is + too high, but cluster maximum size limit has been reached.");
        this.mailSender.send(msg);
    }
}
