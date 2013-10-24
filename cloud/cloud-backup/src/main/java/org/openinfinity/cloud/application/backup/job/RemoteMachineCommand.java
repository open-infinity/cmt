package org.openinfinity.cloud.application.backup.job;

import java.util.List;
import java.util.ArrayList;
import java.util.Collection;

import org.openinfinity.cloud.domain.Key;
import org.openinfinity.cloud.service.administrator.KeyService;
import org.openinfinity.cloud.util.ssh.SSHGateway;

/**
 * Compresses/extracts virtual machine directories over SSH.
 * 
 * @author Timo Saarinen
 */
public class RemoteMachineCommand implements Command {

	private InstanceJob job;
	private Key privateSshKey;
	
	public RemoteMachineCommand(InstanceJob job) throws BackupException {
		this.job = job;

		// Get private key for SSH
		if (job.getPassword() == null) {
			if (job.getToasInstanceId() != -1) {
				KeyService keyService = (KeyService) job.context.getBean("keyService");
				privateSshKey = keyService.getKey(job.getToasInstanceId());
			} else {
				throw new BackupException("No password or TOAS instance id available for SSH");
			}
		}
	}
	
	public void execute() throws Exception {
		// TODO
		runCommand("ls");;
	}
	
	public void undo() throws Exception {
		// TODO
	}

	private void streamCommandToFile() {
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
			SSHGateway.executeRemoteCommands(
					privateSshKey.getSecret_key().getBytes(), 
					(byte[])null, 
					job.getHostname(), 
					job.getPort(), 
					job.getUsername(), 
					job.getPassword(), 
					cmd);
		} catch (Exception e) {
			throw new BackupException("Command execution failed:" + cmd, e);
		}
	}
	
}
