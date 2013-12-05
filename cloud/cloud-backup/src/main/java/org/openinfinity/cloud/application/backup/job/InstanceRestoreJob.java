package org.openinfinity.cloud.application.backup.job;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Retrieves backup package from S3 storage, decrypts it and extracts it to virtual machine.
 * 
 * @see Command
 * @author Timo Saarinen
 */
public class InstanceRestoreJob extends InstanceJob {
	public InstanceRestoreJob(ClusterInfo clustter, ClassPathXmlApplicationContext context) throws BackupException {
		assert context != null;
		this.cluster = cluster;
		this.context = context;
		
		commands.add(new StorageCommand(this));
		commands.add(new CipherCommand(this));
		commands.add(new RemoteMachineCommand(this));
	}
}
