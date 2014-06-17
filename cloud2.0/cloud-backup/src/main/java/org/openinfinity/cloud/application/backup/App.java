package org.openinfinity.cloud.application.backup;

import org.apache.log4j.Logger;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Cloud backup command line entry-point.
 * 
 * @author Timo Saarinen
 */
public class App {

	public static void main(String[] args) {
		try {
			// No logger here intentionally, because this class is used for debugging
			System.out.println("Cloud Backup started as standalone");

			// Create and run the daemon
			CloudBackupDaemon cp = new CloudBackupDaemon();
			cp.init(null);
			cp.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
