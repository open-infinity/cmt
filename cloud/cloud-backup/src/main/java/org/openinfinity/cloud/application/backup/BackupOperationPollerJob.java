package org.openinfinity.cloud.application.backup;

import java.util.List;

import org.openinfinity.cloud.service.backup.BackupService;
import org.openinfinity.cloud.application.backup.job.BackupException;
import org.openinfinity.cloud.domain.*;
import org.openinfinity.cloud.domain.repository.backup.*;
import org.apache.log4j.Logger;

/**
 * Polls repository layer to receive in-coming backup/restore requests. 
 * 
 * @author Timo Saarinen
 */
public class BackupOperationPollerJob {
	private Logger logger = Logger.getLogger(BackupOperationPollerJob.class);
	
	private BackupWorkRepository backupWorkRepository;

	private int lastBackupOperationId = -1;
	
	private BackupOperationPollerJob(BackupWorkRepository repository) {
		backupWorkRepository = repository;
	}

	/**
	 * Singleton method
	 */
	public static BackupOperationPollerJob getInstance(BackupWorkRepository repository, CloudBackup backup) {
		if (instanceOfBackupOperationPollerJob == null) {
			if (repository != null) {
				instanceOfBackupOperationPollerJob = new BackupOperationPollerJob(repository);
			} else {
				throw new BackupException("Repository must not be null!");
			}
		}
		return instanceOfBackupOperationPollerJob;
	}
	private static BackupOperationPollerJob instanceOfBackupOperationPollerJob = null;
	
	/**
	 * Needed by Quartz Scheduler.
	 */
	public final void run() throws Exception {
		logger.debug("run()");
		int last_id = lastBackupOperationId;
		for (BackupOperation op : backupWorkRepository.readBackupOperationsAfter(lastBackupOperationId)) {
			logger.debug("run(): " + op);
			last_id = op.getId();
		}
		lastBackupOperationId = last_id;
	}
}
