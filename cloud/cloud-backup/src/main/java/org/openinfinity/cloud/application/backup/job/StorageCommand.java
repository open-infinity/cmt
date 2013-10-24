package org.openinfinity.cloud.application.backup.job;

/**
 * Transfers packages to/from S3 storage.
 * 
 * @author Timo Saarinen
 */
public class StorageCommand implements Command {
	private InstanceJob job;

	public StorageCommand(InstanceJob job) {
		this.job = job;
	}

	public void execute() throws Exception {
		// TODO
	}
	
	public void undo() throws Exception {
		// TODO
	}

}
