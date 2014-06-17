package org.openinfinity.cloud.application.backup.job;

/**
 * Parameters given to cluster-sync command in the remote system.
 * 
 * @author Timo Saarinen
 */
public interface ClusterSyncOperations {
	public static final String BEFORE_BACKUP  = "before-backup";
	public static final String AFTER_BACKUP   = "after-backup";
	public static final String BEFORE_RESTORE = "before-restore";
	public static final String AFTER_RESTORE  = "after-restore";
}
