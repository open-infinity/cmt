package org.openinfinity.cloud.application.backup.job;

import org.openinfinity.cloud.application.backup.CloudBackup;

public class RemoteMachineClusterCommand extends RemoteMachineCommand implements ClusterSyncOperations {
	
	private String operation; 

	public RemoteMachineClusterCommand(InstanceJob job, String clusterOperation) throws BackupException {
		super(job);
		operation = clusterOperation;
	}

	@Override
	public void execute() throws Exception {
		String cmd = CloudBackup.getBackupProperties().getRemoteSyncCommand() + "  " + operation; 
		if (runRemoteCommand(cmd, "/dev/null", null) > 0) {
			throw new BackupException("Failed to run " + operation + " for the cluster!");
		}
	}

	@Override
	public void undo() throws Exception {
		String cmd = CloudBackup.getBackupProperties().getRemoteSyncCommand() + "  " + reverseOperation(operation); 
		if (runRemoteCommand(cmd, "/dev/null", null) > 0) {
			throw new BackupException("Failed to run " + reverseOperation(operation) + " for the cluster!");
		}
	}

	private String reverseOperation(String op) throws BackupException {
		if (BEFORE_BACKUP.equals(op)) {
			return AFTER_BACKUP;
		} else if (AFTER_BACKUP.equals(op)) {
			return BEFORE_BACKUP;
		} else if (AFTER_RESTORE.equals(op)) {
			return BEFORE_RESTORE;
		} else if (BEFORE_RESTORE.equals(op)) {
			return AFTER_RESTORE;
		} else {
			throw new BackupException("Unexpected operation: " + op);
		}
	}
}
