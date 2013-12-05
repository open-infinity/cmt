package org.openinfinity.cloud.application.backup.job;

import org.openinfinity.cloud.util.ssh.SSHGateway;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Creates compressed backup of virtual machine, encrypts it and transfer it to S3 storage.
 * 
 * @author Timo Saarinen
 */
public class InstanceBackupJob extends InstanceJob {
	public InstanceBackupJob(ClusterInfo cluster, ClassPathXmlApplicationContext context) throws BackupException {
		assert context != null;
		this.cluster = cluster;
		this.context = context;
		
		commands.add(new RemoteMachineCommand(this));
		commands.add(new CipherCommand(this));
		commands.add(new StorageCommand(this));
	}
}
