package org.openinfinity.cloud.application.backup;

/**
 * Cloud properties from backup.properties file which is read into
 * application context.
 * 
 * @author Timo Saarinen
 */
public class BackupProperties {
	private String remoteBackupCommand;
	private String remoteRestoreCommand;
	private String remoteSyncCommand;
	private String temporaryDirectory;
	private boolean cipher = true;
	
	public String getRemoteBackupCommand() {
		return remoteBackupCommand;
	}
	public void setRemoteBackupCommand(String remoteBackupCommand) {
		this.remoteBackupCommand = remoteBackupCommand;
	}
	public String getRemoteRestoreCommand() {
		return remoteRestoreCommand;
	}
	public void setRemoteRestoreCommand(String remoteRestoreCommand) {
		this.remoteRestoreCommand = remoteRestoreCommand;
	}
	public String getRemoteSyncCommand() {
		return remoteSyncCommand;
	}
	public void setRemoteSyncCommand(String remoteSyncCommand) {
		this.remoteSyncCommand = remoteSyncCommand;
	}
	public String getTemporaryDirectory() {
		return temporaryDirectory;
	}
	public void setTemporaryDirectory(String temporaryDirectory) {
		this.temporaryDirectory = temporaryDirectory;
	}
	public boolean isCipher() {
		return cipher;
	}
	public void setCipher(boolean cipher) {
		this.cipher = cipher;
	}
}
