package org.openinfinity.cloud.application.backup.job;

/**
 * The exception class of cloud-backup application.
 * @author Timo Saarinen
 */
public class BackupException extends Exception {
	public BackupException(String msg) {
		super(msg);
	}
	
	public BackupException(String msg, Throwable e) {
		super(msg, e);
	}
}
