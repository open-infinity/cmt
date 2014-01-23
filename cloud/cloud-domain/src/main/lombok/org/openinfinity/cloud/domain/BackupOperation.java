package org.openinfinity.cloud.domain;

import java.sql.Date;
import java.util.Map;

import org.openinfinity.core.annotation.NotScript;

import lombok.Data;


/**
 * Backup task to be performed by Cloud Backup process. 
 * This class is needed by repository layer to offer 
 * an communication interface between BackupService and
 * cloud backup process using relational database.
 * 
 * @author Timo Saarinen
 */
@Data
public class BackupOperation {
	/**
	 * Commission id.
	 */
	@NotScript
	private int id;

	/**
	 * Operation name: 'backup', 'full-restore', 'partial-restore', 'refresh-schedules'.
	 */
	@NotScript
	private String operation;
	
	public static final String BACKUP = "backup";
	public static final String FULL_RESTORE = "full-restore";
	public static final String PARTIAL_RESTORE = "partial-restore";
	public static final String REFRESH_SCHEDULES = "refresh-schedules";

	/**
	 * Time, when the operation was updated last time.
	 */
	@NotScript
	private Date updated;
	
	/**
	 * Target cluster id.
	 */
	@NotScript
	private int targetClusterId = -1;
	
	/**
	 * Source cluster id. In case of partial restore this is expected to be null.
	 * In case of full restore, this have to be supplied.
	 */
	@NotScript
	private int sourceClusterId = -1;
	
	/**
	 * Operation state: 'requested', 'in-progress', 'succeeded', 'failed'. 
	 */
	@NotScript
	private String state;

	/**
	 * Operation state description.
	 */
	@NotScript
	private String description;
	
	public static final String REQUESTED = "requested";
	public static final String IN_PROGRESS = "in-progress";
	public static final String SUCCEEDED = "succeeded";
	public static final String FAILED = "failed";
	
//	/**
//	 * Mapping from old IP addresses to new ones. This map have to be filled,
//	 * if one or more cluster nodes have been replaced.
//	 */
//	@NotScript
//	private Map<String, String> ipMapping;
}
