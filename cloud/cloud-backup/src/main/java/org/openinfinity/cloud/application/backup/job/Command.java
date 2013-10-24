package org.openinfinity.cloud.application.backup.job;

/**
 * Command pattern. InstanceJob class has the roles of receiver and invoker and 
 * its subclasses the role of client.
 * 
 * @author Timo Saarinen
 */
public interface Command {
	/**
	 * Executes the command.
	 */
	public void execute() throws Exception;
	
	/**
	 * Executes the command.
	 */
	public void undo() throws Exception;
}
