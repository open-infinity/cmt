package org.openinfinity.cloud.application.backup.job;

import java.io.File;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.openinfinity.cloud.application.backup.CloudBackup;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.amazonaws.services.identitymanagement.model.GetAccountSummaryRequest;

/**
 * Base POJO class for InstanceBackupJob and InstanceRestoreJob.
 * 
 * @see Command
 * 
 * @author Timo Saarinen
 */
abstract public class InstanceJob implements ApplicationContextAware {
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
	 * Eucalyptus instance id.
	 */
	private String virtualMachineInstanceId;
	
	/**
	 * Directory, where the package is to be stored in the local host.
	 */
	private String localPackageDirectory;

	/**
	 * File object representing location of the local file. This is set by Command class subclasses.
	 */
	private File localBackupFile;
	
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
		logger.debug("Executing commands");
		List<Command> finished_commands = new LinkedList<Command>();
		try {
			// Execute the commands
			for (Command cmd : commands) {
				logger.debug("Executing " + cmd.getClass().getSimpleName());
				cmd.execute();
				finished_commands.add(cmd);
			}
			logger.debug("All commands executed successfully.");
		} catch (Exception e) {
			// One of the commands failed. Use the finished list to undo the action.
			if (finished_commands.size() > 0) {
				logger.error("Job " + jobName + " failed. Trying undo. " + e.getMessage(), e);
				try {
					Collections.reverse(finished_commands);
					for (Command cmd : finished_commands) {
						logger.info("Undoing " + cmd.getClass().getSimpleName());
						cmd.undo();
					}
					logger.info("" + finished_commands.size() + " undo steps completed");
				} catch (Exception ee) {
					logger.warn("Job " + jobName + " undo failed too! " + ee.getMessage(), ee);
				}
			} else {
				logger.error("Job " + jobName + " failed. " + e.getMessage(), e);
			}
			
			// It would be pointless to throw anything at this point
		}
	}

	/**
	 * Returns instance email address. This is needed by GPG encryption/decryption.
	 * @throws BackupException if TOAS instance id is not available
	 * @return Email address, which can be used by GPG.
	 */
	public String getInstanceEmail() throws BackupException {
		if (toasInstanceId != -1) {
			return "toas-instance-" + toasInstanceId + "@tieto.com";
		} else {
			throw new BackupException("Can't generate instance email address, because TOAS instance id is not available."); 
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
	public File getLocalBackupFile() {
		return localBackupFile;
	}
	public void setLocalBackupFile(File localBackupFile) {
		this.localBackupFile = localBackupFile;
	}
	public String getVirtualMachineInstanceId() {
		return virtualMachineInstanceId;
	}
	public void setVirtualMachineInstanceId(String virtualMachineInstanceId) {
		this.virtualMachineInstanceId = virtualMachineInstanceId;
	}

	@Override
	public void setApplicationContext(ApplicationContext context) {
		logger.trace("setApplicationContext(" + context + ")");
		this.context = (ClassPathXmlApplicationContext) context;
	}
}
