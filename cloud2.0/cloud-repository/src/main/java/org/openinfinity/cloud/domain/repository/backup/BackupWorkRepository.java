package org.openinfinity.cloud.domain.repository.backup;

import java.util.List;

import org.openinfinity.cloud.domain.BackupOperation;

/**
 * Communication interface between backup service and backup process using 
 * relational database as a transport, that meets Open Infinity 
 * reference architecture.
 * 
 * @author Timo Saarinen
 */
public interface BackupWorkRepository {
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
}
