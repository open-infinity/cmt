package org.openinfinity.cloud.application.backup;

import org.apache.commons.daemon.Daemon;
import org.apache.commons.daemon.DaemonContext;
import org.apache.commons.daemon.DaemonInitException;
import org.apache.log4j.Logger;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Entry point for jsvc.
 * 
 * @author Timo Saarinen
 */
public class CloudBackupDaemon implements Daemon {

	private Logger logger = Logger.getLogger(CloudBackupDaemon.class);

	private ClassPathXmlApplicationContext context = null;
	private CloudBackup backup;
	
	@Override
	public void init(DaemonContext arg0) throws DaemonInitException, Exception {
		logger.debug("Daemon initializing");
		context = new ClassPathXmlApplicationContext("/cloud-backup-context.xml");
		backup = new CloudBackup(context);
	}

	@Override
	public void start() throws Exception {
		logger.debug("Daemon starting");
		if(context != null) {
			context.start();
			backup.initialize();
		}
	}

	@Override
	public void stop() throws Exception {
		logger.debug("Daemon stopping");
		if(context != null) {
			backup.cleanup();
			context.stop();
		}
	}

	@Override
	public void destroy() {
		logger.debug("Daemon destroying");
		if(context != null) {
			backup = null;
			context.close();
			context = null;
		}
	}

	/**
	 * Non-jsvc entry point.
	 */
	public void startFromMain() throws Exception {
		logger.debug("Daemon started from main");
		init(null);
		start();		
	}
}
