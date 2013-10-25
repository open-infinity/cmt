package org.openinfinity.cloud.application.backup.job;

import java.util.List;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.log4j.Logger;
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
	
	public void execute() throws Exception {
		// TODO
		runCommand("date > /tmp/timotestaa");
	}
	
	public void undo() throws Exception {
		// TODO
	}

	private void streamCommandToFile() {
		logger.trace("streamCommandToFile()");
		// TODO: executeRemoteCommandAndStreamOutputToFile();
	}
	
	/**
	 * Run one command in the remote machine.
	 */
	private void runCommand(String cmd) throws BackupException {
		List<String> cmds = new ArrayList<String>(1);
		cmds.add(cmd);
		runCommands(cmds);
	}
	
	/**
	 * Run commands in the remote machine.
	 */
	private void runCommands(Collection<String> cmd) throws BackupException {
		try {
			// Decided authentication method
			Key private_ssh_key = null;
			if (job.getPassword() == null) {				
				if (job.getToasInstanceId() != -1) {
					private_ssh_key = getPrivateSshKey();
					if (private_ssh_key == null) {
						throw new BackupException("KeyService returned null for instance id " + job.getToasInstanceId() + ". Does the instance really exist?");
					}
				} else {
					throw new BackupException("No password or TOAS instance id available for SSH.");
				}
			}
			
			logger.trace("Executing remote command: " + cmd);
			SSHGateway.executeRemoteCommands(
					private_ssh_key.getSecret_key().getBytes(), 
					(byte[])null, 
					job.getHostname(), 
					job.getPort(), 
					job.getUsername(), 
					job.getPassword(), 
					cmd);
		} catch (Exception e) {
			throw new BackupException("Remote shell command execution failed:" + e.getMessage(), e);
		}
	}

	/**
	 * Get private SSH key for the current job.
	 * @return
	 * @throws BackupException
	 */
	private Key getPrivateSshKey() throws BackupException {
		// Get private key from Key service
		if (job.getToasInstanceId() != -1) {
			logger.trace("Getting SSH private key from KeyService");
			KeyService keyService = (KeyService) job.context.getBean("keyService");
			return keyService.getKeyByInstanceId(job.getToasInstanceId());
		} else {
			return null;
		}
	}
}
