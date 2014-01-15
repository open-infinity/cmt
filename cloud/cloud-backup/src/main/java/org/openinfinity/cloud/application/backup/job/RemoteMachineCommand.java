package org.openinfinity.cloud.application.backup.job;

import java.io.File;
import java.util.List;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.openinfinity.cloud.application.backup.CloudBackup;
import org.openinfinity.cloud.domain.Key;
import org.openinfinity.cloud.service.administrator.KeyService;
import org.openinfinity.cloud.util.ssh.SSHGateway;

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
		logger.trace("backup");
		
		// Decide the filename
		File package_file = new File("cluster-" + job.getStorageCluster().getClusterId() + "-"
				+ job.getLogicalMachineName() + "-backup.tar.xz");
		
		logger.debug("Local filename for backup will be " + package_file);

		// The command to be executed in the remote host
		String package_command = "/opt/openinfinity/3.0.0/backup/stream-backup"; // FIXME: non-hardcoded

		// Create and compress the backup package remotely
		logger.info("Creating and compressing backup package in the remote host and saving it to "
				+ package_file.getAbsolutePath() + " in local host");
		int remote_exit_status = runRemoteCommand(package_command,
				null, package_file.getAbsolutePath());
		if (remote_exit_status > 0) {
			throw new BackupException(
					"Remote packaging command failed with return code "
							+ remote_exit_status);
		}

		// Ensure integrity of the package
		logger.info("Testing package integrity in " + package_file);
		int local_exit_status = Tools.runLocalCommand("tar -tJf " + package_file
				+ "", logger);
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
		logger.trace("restore");
		
		// The backup file
		File package_file = job.getLocalBackupFile();
		
		// The comand to be execute in the remote host
		String restore_command = "/opt/openinfinity/3.0.0/backup/stream-restore"; // FIXME: non-hardcoded

		// Stream the backup package to the remote host, where it's extracted
		logger.info("Streaming the package file to remote host where it's extracted");
		int remote_exit_status = runRemoteCommand(restore_command,
				package_file.getAbsolutePath(), null);
		if (remote_exit_status > 0) {
			throw new BackupException(
					"Remote packaging command failed with return code "
							+ remote_exit_status);
		}
				
		// Finally delete the local backup file
		package_file.delete();
		job.setLocalBackupFile(null);
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
	protected int runRemoteCommand(String cmd, String local_stdin_file, String local_stdout_file)
			throws BackupException {
		int retval = -1;
		List<String> cmds = new ArrayList<String>(1);
		cmds.add(cmd);

		// Check that only one-way streaming is used
		if (local_stdin_file != null && local_stdout_file != null) {
			throw new BackupException("Currently two-way streaming is not supported");
		}
		
		try {
			// Decided authentication method
			Key private_ssh_key = null;
			if (job.getPassword() == null) {
				if (job.getVirtualCluster().getToasInstanceId() != -1) {
					private_ssh_key = getPrivateSshKey();
					if (private_ssh_key == null) {
						throw new BackupException(
								"KeyService returned null for instance id "
										+ job.getVirtualCluster().getToasInstanceId()
										+ ". Does the instance really exist?");
					}
				} else {
					throw new BackupException(
							"No password or TOAS instance id available for SSH.");
				}
			}

			// SSH Gateway call
			logger.debug("Executing remote command: " + cmd);
			retval = SSHGateway.executeRemoteCommandWithLocalFileStreams(
					private_ssh_key.getSecret_key().getBytes(), 
					(byte[]) null,
					job.getHostname(), 
					job.getPort(), 
					job.getUsername(),
					job.getPassword(), 
					cmd, 
					local_stdin_file, 
					local_stdout_file);
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
		if (job.getVirtualCluster().getToasInstanceId() != -1) {
			logger.trace("Getting SSH private key from KeyService");
			KeyService keyService = (KeyService) CloudBackup.getInstance().getContext()
					.getBean("keyService");
			return keyService.getKeyByInstanceId(job.getVirtualCluster().getToasInstanceId());
		} else {
			return null;
		}
	}
}
