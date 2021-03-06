package org.openinfinity.cloud.ssp.billing.invoicecreator;

import org.apache.commons.daemon.Daemon;
import org.apache.commons.daemon.DaemonContext;
import org.apache.commons.daemon.DaemonInitException;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class InvoiceCreatorApplication implements Daemon {
    ClassPathXmlApplicationContext spring = null;
    
    @Override
    public void destroy() {
        if(this.spring != null) {
            this.spring.close();
        }
    }

    @Override
    public void init(DaemonContext arg0) throws DaemonInitException, Exception {
        this.spring = new ClassPathXmlApplicationContext("/META-INF/spring/cloud-ssp-invoice-context.xml");
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
