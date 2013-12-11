package org.openinfinity.cloud.service.backup;

import java.util.List;

import org.openinfinity.cloud.domain.BackupRule;

/**
 * Service for communicating with the Cloud Backup and scheduling backup jobs.
 * 
 * @author Timo Saarinen
 */
public interface BackupService {
	/**
	 * Run cluster backup now. This is needed for manual backups only.
	 * @param clusterId
	 */
	public void backupCluster(int clusterId);
	
	/**
	 * Analyzes the given cluster restore need based on the content of the 
	 * given cluster info. The restore approach will be filled in the object.
	 * 
	 * @param info
	 */
	public void analyze(RestoreInfo info);
	
	/**
	 * Restores backup of the old cluster to the new cluster..
	 * 
	 * @param oldCluster
	 * @param newCluster
	 */
	public void fullRestore(int oldClusterId, int newClusterId);

	/**
	 * Repairs the given cluster based on the cluster info. It's expected, 
	 * that the caller sets new machine ids in the info. 
	 * 
	 * @param info
	 */
	public void partialRestore(RestoreInfo info);

	
	/**
	 * Get all backup rules of the given cluster.
	 * 
	 * @param cluster_id Primary key of cluster_tbl
	 * @return List of BackupRule objects related to the given cluster.
	 */
	public List<BackupRule> getClusterBackupRules(int cluster_id);

	/**
	 * Delete all backup rules of the given cluster.
	 * 
	 * @param cluster_id Primary key of cluster_tbl
	 */
	public void deleteClusterBackupRules(int cluster_id);

	/**
	 * Add new backup rule for cluster.
	 * 
	 * @param br BackupRule to be created
	 */
	public void createBackupRule(BackupRule br);
	
	/**
	 * Delete backup rule.
	 * 
	 * @param br BackupRule to be deleted
	 */
	public void deleteBackupRule(BackupRule br);
	
	
	/**
	 * Update backup rule.
	 * 
	 * @param br BackupRule to be deleted
	 */
	public void updateBackupRule(BackupRule br);
	
}
