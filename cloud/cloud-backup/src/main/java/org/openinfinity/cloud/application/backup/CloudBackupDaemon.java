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

	private CloudBackup backup;

	public CloudBackupDaemon() {
		// The first log line from this app
		logger.debug("Cloud Backup started");
	}

	@Override
	public void init(DaemonContext arg0) throws DaemonInitException, Exception {
		logger.trace("Daemon initializing");
		logger.debug("Getting application context");
		backup = CloudBackup.getInstance();
	}

	@Override
	public void start() throws Exception {
		logger.trace("Daemon starting");
		backup.initialize();
	}

	@Override
	public void stop() throws Exception {
		logger.trace("Daemon stopping");
		backup.cleanup();
	}

	@Override
	public void destroy() {
		logger.trace("Daemon destroying");
		backup = null;
	}
}
