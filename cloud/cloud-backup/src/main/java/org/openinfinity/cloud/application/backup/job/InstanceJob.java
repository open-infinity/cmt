package org.openinfinity.cloud.application.backup.job;

/**
 * Base POJO class for InstanceBackupJob and InstanceRestoreJob.
 * 
 * @author Timo Saarinen
 */
public class InstanceJob {
	/**
	 * Eucalyptus (or other) instance iden
	 */
	private String instanceId;

	/**
	 * Hostname or ip address of the virtual machine.
	 */
	private String hostname;
	
	/**
	 * Unix username
	 */
	private String username;
	
	/**
	 * SSH key location, when accessing the host. Key is preferred over password always.
	 */
	private String sshKeyFile;
	
	/**
	 * Unix password if key is not given.
	 */
	private String password;

	/**
	 * Directory, where the package is to be stored in the local host.
	 */
	private String localPackageDirectory;

	/**
	 * The default constructor.
	 */
	public InstanceJob() {
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
	public String getSshKeyFile() {
		return sshKeyFile;
	}
	public void setSshKeyFile(String sshKeyFile) {
		this.sshKeyFile = sshKeyFile;
	}
	public String getInstanceId() {
		return instanceId;
	}
	public void setInstanceId(String instanceId) {
		this.instanceId = instanceId;
	}
	public String getLocalPackageDirectory() {
		return localPackageDirectory;
	}
	public void setLocalPackageDirectory(String localPackageDirectory) {
		this.localPackageDirectory = localPackageDirectory;
	}
}
