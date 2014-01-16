package org.openinfinity.cloud.application.backup.job;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.validation.ReportAsSingleViolation;

import org.apache.log4j.Logger;
import org.openinfinity.cloud.application.backup.CloudBackup;
import org.openinfinity.cloud.domain.Machine;
import org.openinfinity.cloud.service.administrator.MachineService;
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
	private String username = "root";
	
	/**
	 * Virtual machine SSH port number.
	 */
	private int port = 22;
	
	/**
	 * Unix password if key is not given.
	 */
	private String password;

	/**
	 * Logical name, which is used for mapping virtual machines on 
	 * full cluster restore. This is supposed to be something like
	 * loadbalancer, member2, configserver, etc. 
	 */
	private String logicalMachineName;
	
	/**
	 * Directory, where the package is to be stored in the local host.
	 */
	private String localPackageDirectory = "/var/tmp";

	/**
	 * File object representing location of the local file. This is set by Command class subclasses.
	 */
	private File localBackupFile;
	
	/**
	 * List of commands to run.
	 */
	protected List<Command> commands = new LinkedList<Command>();
	
	/**
	 * Existing cluster, where data is read from and written to.
	 * Object taking care of cluster synchronization.
	 */
	protected ClusterInfo virtualCluster;
	
	/**
	 * Cluster identity, when storing/restoring the backup. 
	 * Object taking care of cluster synchronization.
	 */
	protected ClusterInfo storageCluster;
	
	/**
	 * After the job is completed the listener will be informed.
	 */
	private ResultListener listener;
	
	/**
	 * The default constructor.
	 */
	public InstanceJob(ClusterInfo target_cluster, ClusterInfo source_cluster, int machineId, ResultListener listener) {
		this.listener = listener;
		MachineService machineService = CloudBackup.getInstance().getMachineService();
		if (machineService != null) {
			Machine machine = machineService.getMachine(machineId);
			this.hostname = machine.getDnsName();
			this.username = machine.getUserName();
			logger.warn("machineService is null");
		}
		this.virtualCluster = target_cluster;
		this.storageCluster = source_cluster;
		this.logicalMachineName = decideLogicalHostname(machineId);
		this.setJobName(logicalMachineName);
	}

	/**
	 * Make storage file name for this cluster.
	 */
	public String makeStorageFilename() {
		return makeStorageFilename(storageCluster);
	}
	
	/**
	 * Make storage file name for given cluster.
	 */
	public String makeStorageFilename(ClusterInfo cluster) {
		if (cluster == null) throw new BackupException("Cluster info not set");
		if (logicalMachineName == null) throw new BackupException("Logical machine name not set");
		return "cluster-" + cluster.getClusterId() + "-" + logicalMachineName;
	}
	
	/**
	 * Needed by Quartz Scheduler.
	 */
	public final void run() throws Exception {
		logger.debug("Executing commands");
		List<Command> finished_commands = new LinkedList<Command>();
		try {
			// Cluster synchronization
			virtualCluster.start(this);
			
			// Execute the commands
			for (Command cmd : commands) {
				logger.debug("Executing " + cmd.getClass().getSimpleName());
				cmd.execute();
				finished_commands.add(cmd);
				if (listener != null) listener.report(true, null);
			}
			logger.debug("All commands executed successfully.");
		} catch (Exception e) {
			// Inform listener
			if (listener != null) listener.report(false, e.getMessage());
			
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
		} finally {
			// Cluster synchronization
			virtualCluster.finish(this);
		}
	}

	/**
	 * Decide logical machine name, which is used on store and restore
	 */
	private static String decideLogicalHostname(int machineId) {
		if (CloudBackup.getInstance().getMachineService() == null) return "test";

		// Get Machine
		MachineService machineService = CloudBackup.getInstance().getMachineService();
		Machine machine = machineService.getMachine(machineId);

		// We use machine type as base
		String machine_type = machine.getType();
		
		// Get cluster machine list and ensure, that it's ordered by machine id
		Collection<Machine> machines_unordered = machineService.getMachinesInCluster(machine.getClusterId());
		Set<Machine> machines = new TreeSet<Machine>(new Comparator<Machine>() {
			public int compare(Machine m1, Machine m2) {
				if (m1.getId() < m2.getId()) return -1;
				if (m1.getId() > m2.getId()) return 1;
				return 0;
			}
			
			public boolean equals(Object o) {
				if (o != null)
					return (this.getClass().equals(o.getClass()));
				else
					return false;
			}
		});
		for (Machine m : machines_unordered) {
			// Add only machines of same type
			if (m.getType().equals(machine.getType())) {
				machines.add(m);
			}
		}
		
		// Find machine number
		int num = 1;
		for (Machine m : machines) {
			if (m.getId() == machine.getId()) {
				break;
			}
			num++;
		}

		// Combine machine type and order number
		return machine_type + "-" + num;
	}

	/**
	 * Class for resulting status callbacks.
	 */
	public interface ResultListener {
		public void report(boolean success, String description);
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

	@Override
	public String toString() {
		return jobName;
	}

	public String getLogicalMachineName() {
		return logicalMachineName;
	}

	public ClusterInfo getVirtualCluster() {
		return virtualCluster;
	}

	public void setVirtualCluster(ClusterInfo virtualCluster) {
		this.virtualCluster = virtualCluster;
	}

	public ClusterInfo getStorageCluster() {
		return storageCluster;
	}

	public void setStorageCluster(ClusterInfo storageCluster) {
		this.storageCluster = storageCluster;
	}
}
