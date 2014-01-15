package org.openinfinity.cloud.application.backup.job;

import org.openinfinity.cloud.util.ssh.SSHGateway;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Deletes virtual machine backup files from the storage.
 * 
 * @author Timo Saarinen
 */
public class InstanceDeleteJob extends InstanceJob {
	public InstanceDeleteJob(ClusterInfo cluster, int machineId) throws BackupException {
		super(cluster, cluster, machineId);
		
		commands.add(new StorageCommand(this));
	}
}
