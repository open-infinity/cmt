package org.openinfinity.cloud.application.backup.job;

import java.io.File;

import org.apache.log4j.Logger;

/**
 * Asks Cloud Deployer to redeploy applications to the target.
 * 
 * @author Timo Saarinen
 */
public class RedeployCommand {
	private Logger logger = Logger.getLogger(RedeployCommand.class);

	private InstanceJob job;

	public RedeployCommand(InstanceJob job) {
		this.job = job;
	}

	public void execute() throws Exception {
	}
	
	public void undo() throws Exception {
	}

}
