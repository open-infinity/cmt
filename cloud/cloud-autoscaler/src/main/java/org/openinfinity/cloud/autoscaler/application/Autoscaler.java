package org.openinfinity.cloud.autoscaler.application;

import org.apache.commons.daemon.Daemon;
import org.apache.commons.daemon.DaemonContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Autoscaler implements Daemon {
    private ClassPathXmlApplicationContext spring = null;
    
    @Override
    public void destroy() {
        if(this.spring != null) {
            this.spring.close();
        }
    }

    @Override
    public void init(DaemonContext arg0){
        this.spring = new ClassPathXmlApplicationContext("/META-INF/spring/cloud-autoscaler-context.xml");   
    }

    @Override
    public void start() throws Exception {
        this.spring.start();
    }

    @Override
    public void stop() throws Exception {
        if(this.spring != null) {
            this.spring.stop();
        }
    }

}
