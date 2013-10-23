package org.openinfinity.cloud.application.backup;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Cloud backup command line entry-point.
 * 
 * @author Timo Saarinen
 */
public class App {

	public static void main(String[] args) {
		try {
			CloudBackupDaemon cp = new CloudBackupDaemon();
			cp.startFromMain();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
