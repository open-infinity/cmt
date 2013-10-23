package org.openinfinity.cloud.application.backup.job;

/**
 * Creates compressed backup of virtual machine, encrypts it and transfer it to S3 storage.
 * 
 * @author Timo Saarinen
 */
public class InstanceBackupJob extends InstanceJob {
	private String hostname;
	private String username;
	private String sshKeyFile;
	
	public void run() {
		// TODO
	}
}
