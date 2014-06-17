package org.openinfinity.cloud.application.backup.job;

/**
 * Command pattern. InstanceJob class has the roles of receiver and invoker and 
 * its subclasses the role of client.
 * 
 * @see <a href="https://en.wikipedia.org/wiki/Command_pattern">Command Pattern</a>
 * @author Timo Saarinen
 */
public interface Command {
	/**
	 * Executes the command.
	 */
	public abstract void execute() throws Exception;
	
	/**
	 * Executes the command.
	 */
	public abstract void undo() throws Exception;
}
