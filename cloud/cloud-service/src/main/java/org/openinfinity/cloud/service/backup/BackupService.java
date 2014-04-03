package org.openinfinity.cloud.service.backup;

import java.util.List;
import java.util.Set;

import org.openinfinity.cloud.domain.BackupOperation;
import org.openinfinity.cloud.domain.BackupRule;

/**
 * This class takes care of two different functions:
 * 1) direct backup and restore (with BackupOperation objects)
 * 2) managing backup scheduling rules (with BackupRule objects)
 * 
 * @author Timo Saarinen
 */
public interface BackupService {
	/**
	 * Reads new backup operations, that are newer than the given operation.
	 * If the id is -1, all will be retrieved
	 * 
	 * @param id Id or -1
	 */
	public List<BackupOperation> readBackupOperationsAfter(int id);

	/**
	 * Writes backup operation object to database. Either updates an existing one
	 * or inserts a new one, depending on case.
	 * 
	 * @param op Object to be written
	 */
	public void writeBackupOperation(BackupOperation op);
	
	/**
	 * Reads backup operation object from database.
	 * 
	 * @param Backup op id
	 */
	public BackupOperation readBackupOperation(int id);

	/**
	 * Removes the given backup operation from the database.
	 * 
	 * @param op The operation to be removed
	 * @return
	 */
	public boolean deleteBackupOperation(BackupOperation op);

	// -----------------------------------------------------------------------

	
	/**
	 * Returns list of clusters to be backed up.
	 * @return List of cluster ids
	 */
	public Set<Integer> getBackupClusters();
	
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
