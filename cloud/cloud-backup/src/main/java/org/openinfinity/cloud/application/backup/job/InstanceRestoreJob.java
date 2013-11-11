package org.openinfinity.cloud.application.backup.job;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Retrieves backup package from S3 storage, decrypts it and extracts it to virtual machine.
 * 
 * @see Command
 * @author Timo Saarinen
 */
public class InstanceRestoreJob extends InstanceJob {
	public InstanceRestoreJob(ClassPathXmlApplicationContext context) throws BackupException {
		this.context = context;
		
		commands.add(new StorageCommand(this));
		commands.add(new CipherCommand2(this));
		commands.add(new RemoteMachineCommand(this));
	}
}
