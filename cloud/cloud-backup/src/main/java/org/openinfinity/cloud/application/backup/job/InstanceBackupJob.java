package org.openinfinity.cloud.application.backup.job;

import org.openinfinity.cloud.service.administrator.MachineService;
import org.openinfinity.cloud.util.ssh.SSHGateway;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Creates compressed backup of virtual machine, encrypts it and transfer it to S3 storage.
 * 
 * @author Timo Saarinen
 */
public class InstanceBackupJob extends InstanceJob {
	public InstanceBackupJob(ClusterInfo cluster, int machineId) throws BackupException {
		super(cluster, cluster, machineId);
		
		commands.add(new RemoteMachineCommand(this));
		commands.add(new CipherCommand(this));
		commands.add(new StorageCommand(this));
	}
}
