package org.openinfinity.cloud.application.worker;

import org.apache.commons.daemon.Daemon;
import org.apache.commons.daemon.DaemonContext;
import org.apache.commons.daemon.DaemonInitException;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class CloudWorker implements Daemon {
	ClassPathXmlApplicationContext spring = null;
	
	@Override
	public void destroy() {
		if(this.spring != null) {
			this.spring.close();
		}
	}

	@Override
	public void init(DaemonContext arg0) throws DaemonInitException, Exception {
		this.spring = new ClassPathXmlApplicationContext("/application-context.xml");
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
