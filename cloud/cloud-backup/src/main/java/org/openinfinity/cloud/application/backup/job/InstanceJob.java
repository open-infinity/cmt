package org.openinfinity.cloud.application.backup.job;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.openinfinity.cloud.application.backup.CloudBackup;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Base POJO class for InstanceBackupJob and InstanceRestoreJob.
 * 
 * @author Timo Saarinen
 */
abstract public class InstanceJob {
	private Logger logger = Logger.getLogger(InstanceJob.class);

	/**
	 * Eucalyptus (or other) instance identifier. This is used
	 */
	private String jobName;

	/**
	 * Hostname or ip address of the virtual machine.
	 */
	private String hostname;
	
	/**
	 * Unix username
	 */
	private String username;
	
	/**
	 * Virtual machine SSH port number.
	 */
	private int port = 22;
	
	/**
	 * Unix password if key is not given.
	 */
	private String password;

	/**
	 * Directory, where the package is to be stored in the local host.
	 */
	private String localPackageDirectory;

	/**
	 * TOAS instance id.
	 */
	private int toasInstanceId = -1;
	
	/**
	 * The default constructor.
	 */
	public InstanceJob() {
	}

	protected ClassPathXmlApplicationContext context;
	
	/**
	 * List of commands to run.
	 */
	protected List<Command> commands = new LinkedList<Command>();
	
	/**
	 * Needed by Quartz Scheduler.
	 */
	public final void run() throws Exception {
		assert context != null;
		
		logger.debug("Executing commands");
		List<Command> finished_commands = new LinkedList<Command>();
		try {
			// Execute the commands
			for (Command cmd : commands) {
				cmd.execute();
				finished_commands.add(cmd);
			}
		} catch (Exception e) {
			// One of the commands failed. Use the finished list to undo the action.
			logger.warn("Job " + jobName + " failed. Trying undo.");
			try {
				Collections.reverse(finished_commands);
				for (Command cmd : finished_commands) {
					cmd.undo();
				}
			} catch (Exception ee) {
				logger.warn("Job " + jobName + " undo failed too!", ee);
			}
			
			// Throw a new exception
			throw new RuntimeException("Job execution failed!", e);
		}
	}

	// ---- Getters & Setters ----------------------------------------------------------------------
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getHostname() {
		return hostname;
	}
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getJobName() {
		return jobName;
	}
	public void setJobName(String name) {
		this.jobName = name;
	}
	public String getLocalPackageDirectory() {
		return localPackageDirectory;
	}
	public void setLocalPackageDirectory(String localPackageDirectory) {
		this.localPackageDirectory = localPackageDirectory;
	}
	public int getToasInstanceId() {
		return toasInstanceId;
	}
	public void setToasInstanceId(int toasInstanceId) {
		this.toasInstanceId = toasInstanceId;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}


}
