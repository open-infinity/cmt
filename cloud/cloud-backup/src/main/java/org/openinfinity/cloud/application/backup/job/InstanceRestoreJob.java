package org.openinfinity.cloud.application.backup.job;

import org.openinfinity.cloud.service.administrator.MachineService;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Retrieves backup package from S3 storage, decrypts it and extracts it to virtual machine.
 * 
 * @see Command
 * @author Timo Saarinen
 */
public class InstanceRestoreJob extends InstanceJob {
	public InstanceRestoreJob(ClusterInfo cluster, int machineId) throws BackupException {
		super(cluster, cluster, machineId);
		
		commands.add(new StorageCommand(this));
		commands.add(new CipherCommand(this));
		commands.add(new RemoteMachineCommand(this));
	}
	
	public InstanceRestoreJob(ClusterInfo target_cluster, ClusterInfo source_cluster, int machineId) throws BackupException {
		super(target_cluster, source_cluster, machineId);

		commands.add(new StorageCommand(this));
		commands.add(new CipherCommand(this));
		commands.add(new RemoteMachineCommand(this));
	}
}
