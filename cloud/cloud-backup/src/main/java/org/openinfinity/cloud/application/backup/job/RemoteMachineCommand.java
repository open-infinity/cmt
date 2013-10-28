package org.openinfinity.cloud.application.backup.job;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.List;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.log4j.Logger;
import org.apache.log4j.helpers.FileWatchdog;
import org.openinfinity.cloud.domain.Key;
import org.openinfinity.cloud.service.administrator.KeyService;
import org.openinfinity.cloud.util.ssh.SSHGateway;
import org.openinfinity.core.util.IOUtil;

/**
 * Compresses/extracts virtual machine directories over SSH.
 * 
 * @author Timo Saarinen
 */
public class RemoteMachineCommand implements Command {
	private Logger logger = Logger.getLogger(RemoteMachineCommand.class);

	private InstanceJob job;

	public RemoteMachineCommand(InstanceJob job) throws BackupException {
		this.job = job;
	}

	/**
	 * The main logic to create the backup package.
	 */
	public void execute() throws Exception {
		if (job instanceof InstanceBackupJob) {
			backup();
		} else if (job instanceof InstanceRestoreJob) {
			restore();
		} else {
			throw new BackupException("Unexpected base class " + job.getClass());
		}
	}

	/**
	 * Deletes the local backup file.
	 */
	public void undo() throws Exception {
		if (job instanceof InstanceBackupJob) {
			File f = job.getLocalBackupFile();
			if (f != null) {
				f.delete();
				job.setLocalBackupFile(null);
			}
		} else if (job instanceof InstanceRestoreJob) {
			throw new BackupException("Extracted backup can't be undoed!");
		} else {
			throw new BackupException("Unexpected base class " + job.getClass());
		}

	}

	/**
	 * Create and compress backup file.
	 */
	private void backup() throws Exception {
		// Decide the filename
		File package_file = new File(job.getLocalPackageDirectory(),
				"instance" + job.getToasInstanceId() + "-"
						+ job.getUsername() + "@" + job.getHostname()
						+ "-backup.tar.xz");
		logger.debug("Local filename for backup will be " + package_file);

		// THe command to be executed in the remote host
		String package_command = "tar -cJ /opt"; // TODO: not hard-coded

		// Create and compress the backup package remotely
		logger.info("Creating and compressing backup package in the remote host and saving it to "
				+ package_file.getAbsolutePath() + " in local host");
		int remote_exit_status = runRemoteCommand(package_command,
				package_file.getAbsolutePath());
		if (remote_exit_status > 0) {
			throw new BackupException(
					"Remote packaging command failed with return code "
							+ remote_exit_status);
		}

		// Ensure integrity of the package
		logger.info("Testing package integrity in " + package_file);
		int local_exit_status = runLocalCommand("tar -tJf " + package_file
				+ "");
		if (local_exit_status > 0) {
			// Delete and fail
			package_file.delete();
			logger.fatal("Backup package integrity check failed!");
			throw new BackupException(
					"Backup package integrity check failed with return code "
							+ local_exit_status + "! Package file deleted.");
		} else {
			logger.info("Package integrity check succeeded.");
		}

		// Let the job know the filename
		job.setLocalBackupFile(package_file);

		// We are done
		logger.info("Packaging completed successfully.");
	}
	
	/**
	 * Extract the backup file to remote host.
	 */
	private void restore() throws Exception {
		throw new BackupException("UNIMPLEMENTED!"); // TODO
	}
	
	/**
	 * Run one command in the remote machine.
	 * 
	 * @param cmd
	 *            Command to be executed in the remote host
	 * @param local_stdout_file
	 *            Local file, where the output is streamed
	 * @return Return value of the command or -1
	 */
	private int runRemoteCommand(String cmd, String local_stdout_file)
			throws BackupException {
		int retval = -1;
		List<String> cmds = new ArrayList<String>(1);
		cmds.add(cmd);

		try {
			// Decided authentication method
			Key private_ssh_key = null;
			if (job.getPassword() == null) {
				if (job.getToasInstanceId() != -1) {
					private_ssh_key = getPrivateSshKey();
					if (private_ssh_key == null) {
						throw new BackupException(
								"KeyService returned null for instance id "
										+ job.getToasInstanceId()
										+ ". Does the instance really exist?");
					}
				} else {
					throw new BackupException(
							"No password or TOAS instance id available for SSH.");
				}
			}

			// SSH Gateway call
			logger.debug("Executing remote command: " + cmd);
			retval = SSHGateway.executeRemoteCommandAndStreamOutputToFile(
					private_ssh_key.getSecret_key().getBytes(), (byte[]) null,
					job.getHostname(), job.getPort(), job.getUsername(),
					job.getPassword(), cmd, local_stdout_file);
			logger.trace("Command execution finished with return value "
					+ retval + ".");
		} catch (Exception e) {
			throw new BackupException("Remote shell command execution failed:"
					+ e.getMessage(), e);
		}
		return retval;
	}

	/**
	 * Get private SSH key for the current job.
	 * 
	 * @return
	 * @throws BackupException
	 */
	private Key getPrivateSshKey() throws BackupException {
		// Get private key from Key service
		if (job.getToasInstanceId() != -1) {
			logger.trace("Getting SSH private key from KeyService");
			KeyService keyService = (KeyService) job.context
					.getBean("keyService");
			return keyService.getKeyByInstanceId(job.getToasInstanceId());
		} else {
			return null;
		}
	}

	/**
	 * Execute command in local host. Quote marks are not permitted as part of
	 * the command.
	 * 
	 * @return exit value of the command
	 */
	private int runLocalCommand(String cmd) throws IOException {
		logger.debug("Running local command: " + cmd);
		try {
			// Run and wait for completion of the command
			Process p = Runtime.getRuntime().exec(cmd);
			return p.waitFor();
		} catch (InterruptedException exception) {
			logger.warn("Local command waiting was interrupted unexpectedly");
		}
		return -1;
	}
}
