package org.openinfinity.cloud.application.backup.job;

/**
 * Takes care of encyrpting/decrypting a package.
 * 
 * @author Timo Saarinen
 */
public class CipherCommand implements Command {
	private InstanceJob job;
	
	public CipherCommand(InstanceJob job) {
		this.job = job;
	}
	
	public void execute() throws Exception {
		// TODO
	}
	
	public void undo() throws Exception {
		// TODO
	}

}
