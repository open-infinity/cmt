package org.openinfinity.cloud.application.backup.job;

import org.apache.log4j.Logger;

/**
 * Transfers packages to/from S3 storage.
 * 
 * @author Timo Saarinen
 */
public class StorageCommand implements Command {
	private Logger logger = Logger.getLogger(StorageCommand.class);

	private InstanceJob job;

	public StorageCommand(InstanceJob job) {
		this.job = job;
	}

	public void execute() throws Exception {
		if (job instanceof InstanceBackupJob) {
			store();
		} else if (job instanceof InstanceRestoreJob) {
			restore();
		} else {
			throw new BackupException("Unexpected base class " + job.getClass());
		}
	}
	
	public void undo() throws Exception {
		if (job instanceof InstanceBackupJob) {
			// TODO: delete the file in S3 storage
		} else if (job instanceof InstanceRestoreJob) {
			job.getLocalBackupFile().delete();
		} else {
			throw new BackupException("Unexpected base class " + job.getClass());
		}
	}

	/**
	 * Transfer the file to S3 storage.
	 */
	public void store() {
		
	}
	
	/**
	 * Transfer the file from S3 storage. Doesn't delete anything in S3.
	 */
	public void restore() {
		
	}
}
