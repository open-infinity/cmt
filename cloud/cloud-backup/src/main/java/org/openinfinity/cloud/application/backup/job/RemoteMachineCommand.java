package org.openinfinity.cloud.application.backup.job;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;

import org.apache.log4j.Level;
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
			job.setTimestamp(new Date());
			if (job.getVirtualCluster().isMongo()) {
				backupMongo();
			} else {
				backup();
			}
		} else if (job instanceof InstanceRestoreJob) {
			if (job.getVirtualCluster().isMongo()) {
				restoreMongo();
			} else {
				restore();
			}
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
	protected void backup() throws Exception {
		logger.info("Backup for " + job.getHostname() + " (" + job.getLogicalMachineName() + ")");
		
		// Decide the filename
		File package_file = new File("cluster-" + job.getStorageCluster().getClusterId() + "-"
				+ job.getLogicalMachineName() + "-backup.tar.xz");
		
		logger.debug("Local filename for backup will be " + package_file);

		// The command to be executed in the remote host
		String package_command = CloudBackup.getBackupProperties().getRemoteBackupCommand();
		if (package_command == null) throw new NullPointerException("remoteBackupCommand is null");
		//String package_command = "/opt/openinfinity/3.0.0/backup/stream-backup"; // FIXME: non-hardcoded path

		// Test that the backup file exists
		int remote_exit_status1 = runRemoteCommand("touch " + package_command, null, "/dev/null");
		if (remote_exit_status1 > 0) {
			throw new BackupException(
					"Remote backup scripts don't exist on host " + job.getHostname() + " (" + job.getLogicalMachineName() + ")");
		}
		
		// Create and compress the backup package remotely
		logger.info("Creating and compressing backup package in the remote host and saving it to "
				+ package_file.getAbsolutePath() + " in local host");
		int remote_exit_status2 = runRemoteCommand(package_command,
				null, package_file.getAbsolutePath());
		if (remote_exit_status2 > 0) {
			throw new BackupException(
					"Remote packaging command failed with return code "
							+ remote_exit_status2 + " on host " + job.getHostname() + " (" + job.getLogicalMachineName() + ")");
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
	protected void restore() throws Exception {
		logger.info("Restore for " + job.getHostname() + " (" + job.getLogicalMachineName() + ")");
		
		if (job.getLocalBackupFile() != null) {
			// The backup file
			File package_file = new File(
					CloudBackup.getBackupProperties().getTemporaryDirectory(), 
					job.getLocalBackupFile().toString());
			
			// The command to be execute in the remote host
			String restore_command = CloudBackup.getBackupProperties().getRemoteRestoreCommand();
			//String restore_command = "/opt/openinfinity/3.0.0/backup/stream-restore"; // FIXME: non-hardcoded
	
			// Test that the backup file exists
			int remote_exit_status1 = runRemoteCommand("touch " + restore_command, null, "/dev/null");
			if (remote_exit_status1 > 0) {
				throw new BackupException(
						"Remote restore scripts don't exist on host " + job.getHostname() + " (" + job.getLogicalMachineName() + ")");
			}
			
			// Stream the backup package to the remote host, where it's extracted
			logger.info("Streaming the package file to remote host where it's extracted");
			int remote_exit_status2 = runRemoteCommand(restore_command,
					package_file.getAbsolutePath(), null);
			if (remote_exit_status2 > 0) {
				throw new BackupException(
						"Remote packaging command failed with return code "
								+ remote_exit_status2 + " on host " + job.getHostname() + " (" + job.getLogicalMachineName() + ")");
			}
					
			// Finally delete the local backup file
			package_file.delete();
			job.setLocalBackupFile(null);
		} else {
			logger.info("No local backup file given. Skipping restore.");
		}
	}
	
	/**
	 * Backup MongoDB cluster as a special case. 
	 * This is a workaround for mongodump and mongorestore limitations. 
	 */
	protected void backupMongo() throws Exception {
		if ("config-1".equals(job.getLogicalMachineName())) {
			logger.info("Mongo backup for " + job.getHostname() + " (" + job.getLogicalMachineName() + ")");

			// Decide the filename
			File package_file = new File(
					CloudBackup.getBackupProperties().getTemporaryDirectory(), 
					"cluster-" + job.getStorageCluster().getClusterId() + "-" + job.getLogicalMachineName() + "-backup.tar.xz");
			logger.debug("Local filename for backup will be " + package_file);

			// Decide temporary directory
			String local_backup_dir = CloudBackup.getBackupProperties().getTemporaryDirectory() + "/" + job.getJobName();
			String local_backup_dump_dir = local_backup_dir + "/dump"; 
			logger.debug("Using directory " + local_backup_dir);
			{
				String cmd = "mkdir -p " + local_backup_dump_dir; // TODO: API instead of shell
				int rv = executeLocal(cmd, Level.ERROR);
				if (rv != 0) {
					throw new BackupException("Failed to create directory " + local_backup_dump_dir);
				}
			}

			// Get list of databases, excluding 'config' database, which is an internal one 
			Set<String> databases = new TreeSet<String>();
			{
				String tmp_file = local_backup_dir + "/dbnames";
				String tmp_script = local_backup_dir + "/dblist.sh";
				String script_content = 
						"#!/bin/sh\n" +
						"echo \"db.getMongo().getDBNames().forEach(function(name) { if (name != 'config') print(name); });\" | mongo  --quiet " + job.getHostname() + ":27017 > " + tmp_file + "\n";
				writeStringToFile(tmp_script, script_content);
				String cmd = "/bin/sh " + tmp_script;
				int rv = executeLocal(cmd, Level.ERROR);
				if (rv != 0) {
					logger.error("Command failed (" + rv + "): " + cmd);
					throw new BackupException("Mongo backup command returned with exit value " + rv);
				}
				for (String db : readFileAsString(tmp_file).split("\n")) {
					databases.add(db);
				}
				logger.debug("" + databases.size() + " databases found.");
				new File(tmp_file).delete();
				new File(tmp_script).delete();
			}

			// Iterate all databases
			for (String db_name : databases) {
				// Backup mongo databases using local mongodump command
				{
					String cmd = "mongodump" 
							+ " --host " + job.getHostname() 
							+ " --port 27017" // TODO: parameter
							+ " --db " + db_name
							+ " --out " + local_backup_dump_dir;
					int rv = executeLocal(cmd, Level.ERROR);
					if (rv != 0) {
						logger.error("Command failed (" + rv + "): " + cmd);
						throw new BackupException("Mongo backup command returned with exit value " + rv);
					}
				}
			}
			
			// Create backup archive from the local dump
			{
				String cmd = "tar -cvJf " + package_file + " -C " + local_backup_dump_dir + " .";
				int rv = executeLocal(cmd, Level.ERROR);
				if (rv != 0) {
					logger.error("Command failed (" + rv + "): " + cmd);
					throw new BackupException("Mongo backup package command returned with exit value " + rv);
				}
			}
			
			// Delete local dump
			{
				String cmd = "rm -fR " + local_backup_dir; // TODO: API instead of shell
				int rv = executeLocal(cmd, Level.ERROR);
				if (rv != 0) {
					throw new BackupException("Failed delete " + local_backup_dir);
				}
			}

			// Let the job know the filename
			job.setLocalBackupFile(package_file);
	
			// We are done
			logger.info("Packaging completed successfully.");
		} else {
			logger.info("Skipping " + job.getHostname() + " (" + job.getLogicalMachineName() + ")");
		}
	}
	
	/**
	 * Restore MongoDB cluster as a special case.
	 * This is a workaround for mongodump and mongorestore limitations. 
	 */
	protected void restoreMongo() throws Exception {
		logger.info("Mongo restore for " + job.getHostname() + " (" + job.getLogicalMachineName() + ")");
		
		if (job.getLocalBackupFile() != null) {
			// The backup file
			File package_file = job.getLocalBackupFile();
			if (package_file == null) throw new NullPointerException("package_file is null!");
	
			// Decide temporary directory
			String local_backup_dir = CloudBackup.getBackupProperties().getTemporaryDirectory() + "/" + job.getJobName();
			String local_backup_dump_dir = local_backup_dir + "/dump"; 
			logger.debug("Using directory " + local_backup_dir);
			{
				String cmd = "mkdir -p " + local_backup_dump_dir; // TODO: API instead of shell
				int rv = executeLocal(cmd, Level.ERROR);
				if (rv != 0) {
					throw new BackupException("Failed to create directory " + local_backup_dump_dir);
				}
			}
			
			// Extract the backup archive locally
			{
				String cmd = "tar -xvJf " + package_file + " -C " + local_backup_dump_dir + "";
				int rv = executeLocal(cmd, Level.ERROR);
				if (rv != 0) {
					logger.error("Command failed (" + rv + "): " + cmd);
					throw new BackupException("Mongo restore package command returned with exit value " + rv);
				}
			}
			
			
			// Restore mongo databases using local mongorestore command
			{
				String cmd = "mongorestore --verbose"
						+ " --drop" // We want a clean insert
						+ " --host " + job.getHostname() 
						+ " --port 27017" // TODO: parameter
						+ " " + local_backup_dump_dir;
				int rv = executeLocal(cmd, Level.ERROR);
				if (rv != 0) {
					logger.error("Command failed (" + rv + "): " + cmd);
					throw new BackupException("Mongo backup command returned with exit value " + rv);
				}
			}

			// Delete local dump
			{
				String cmd = "rm -fR " + local_backup_dir; // TODO: API instead of shell
				int rv = executeLocal(cmd, Level.ERROR);
				if (rv != 0) {
					throw new BackupException("Failed delete " + local_backup_dir);
				}
			}
			
			// Finally delete the local backup file
			package_file.delete();
			job.setLocalBackupFile(null);
		} else {
			logger.info("No local backup file given. Skipping mongo restore.");
		}
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
			if (e.getCause() != null && e.getCause().getMessage() != null) {
				throw new BackupException(e.getCause().getMessage(), e);
			} else {
				throw new BackupException("Remote shell command execution failed:"
						+ e.getMessage(), e);
			}
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

	/**
	 * Execute local command.
	 * @param cmd Shell command to run 
	 * @return Command exit value
	 * @throws InterruptedException 
	 */
	private int executeLocal(String cmd, Level stderrLogLevel) throws IOException, InterruptedException {
		logger.trace("executeLocal: " + cmd);
		
		// Start command
		Process process = Runtime.getRuntime().exec(cmd);
		
		// Open streams
		BufferedReader stderr = new BufferedReader(
				new InputStreamReader(process.getErrorStream()));
        
		// Print stderr to log
        String s = null;
        while ((s = stderr.readLine()) != null) {
        	logger.log(stderrLogLevel, s);
        }		
		// Close and return exit value
		stderr.close();
		return process.waitFor();
	}

	/**
	 * Writes the given string to a file
	 */
	private void writeStringToFile(String uri, String content) throws IOException {
		Files.write(Paths.get(uri), content.getBytes());
	}
	
	/**
	 * Read all files from file to string.
	 */
	private String readFileAsString(String uri) throws IOException {
		return new String(Files.readAllBytes(Paths.get(uri)));
	}
}
