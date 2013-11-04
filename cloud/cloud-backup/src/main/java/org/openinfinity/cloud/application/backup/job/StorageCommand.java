package org.openinfinity.cloud.application.backup.job;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.util.HashMap;
import java.util.Map;

import javax.naming.Context;

import org.apache.log4j.Logger;
import org.openinfinity.cloud.domain.repository.deployer.BucketRepository;
import org.openinfinity.core.util.IOUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Transfers packages to/from S3 storage and deletes the local file.
 * 
 * @author Timo Saarinen
 */
public class StorageCommand implements Command {
	private Logger logger = Logger.getLogger(StorageCommand.class);

	private static final String BUCKET_ROOT_NAME = "backup";
	
	private InstanceJob job;

	private BucketRepository bucketRepository;

	public StorageCommand(InstanceJob job) {
		this.job = job;
		this.bucketRepository = (BucketRepository) job.context.getBean("jetS3Repository");
	}

	public void execute() throws Exception {
		if (job instanceof InstanceBackupJob) {
			store();
		} else if (job instanceof InstanceRestoreJob) {
			restore();
		} else {
			throw new BackupException("Unexpected base class " + job.getClass());
		}
	}
	
	public void undo() throws Exception {
		if (job instanceof InstanceBackupJob) {
			// Currently we don't have any need to undo the last step, so let's disable it :)
			if (false) {
				// Delete the backup from bucket repository
				logger.info("Deleting backup file from bucket repository");
				bucketRepository.deleteObject(getBucketNameForJob(), getBucketKeyForJob());
			}
		} else if (job instanceof InstanceRestoreJob) {
			job.getLocalBackupFile().delete();
			job.setLocalBackupFile(null);
		} else {
			throw new BackupException("Unexpected base class " + job.getClass());
		}
	}

	/**
	 * Transfer the file to S3 storage.
	 */
	public void store() throws Exception {
		logger.trace("store");
		
		// Ensure that root bucket exists
		String info = "This bucket is for backups";
		bucketRepository.createBucket(
				new ByteArrayInputStream(info.getBytes()), 
				BUCKET_ROOT_NAME, "info", new HashMap<String, String>());
		
		// Upload the package to S3 repository as a bucket
		FileInputStream fis = new FileInputStream(job.getLocalBackupFile());
		Map<String, String> metadataMap = new HashMap<String, String>();
		metadataMap.put("toasInstanceId", "" + job.getToasInstanceId());
		metadataMap.put("hostname", job.getHostname());
		metadataMap.put("username", job.getUsername());
		metadataMap.put("virtualMachineId", job.getVirtualMachineInstanceId());
		metadataMap.put("filename", job.getLocalBackupFile().getName());
		logger.info("Storing the backup to S3 repository: name=" + getBucketNameForJob() 
				+ " key=" + getBucketKeyForJob());
		String location = bucketRepository.createBucket(
				fis, getBucketNameForJob(), getBucketKeyForJob(), metadataMap);
		logger.debug("bucket created in location=" + location);
		fis.close();
		
		// Finally delete the local backup file
		File f = job.getLocalBackupFile();
		if (f != null) {
			f.delete();
			job.setLocalBackupFile(null);
		} else {
			throw new BackupException("Local backup not defined!");
		}
		logger.trace("store complete");
	}
	
	/**
	 * Transfer the file from S3 storage. Doesn't delete anything in S3.
	 */
	public void restore() throws Exception {
		logger.trace("restore");

		// Decide local package filename
		String package_filename = "instance" + job.getToasInstanceId() + "-"
				+ job.getUsername() + "@" + job.getHostname()
				+ "-backup.tar.xz";
		job.setLocalBackupFile(new File(job.getLocalPackageDirectory(), package_filename));
		
		// Download package file from S3 repository
		logger.info("Downloading backup bucket from S3 storage" 
				+ " (name=" + getBucketNameForJob() 
				+ " key=" + getBucketKeyForJob() + ")");
		InputStream is = bucketRepository.load(getBucketNameForJob(), getBucketKeyForJob());
		if (is == null) throw new BackupException("Bucket repository returned null instead of input stream");
		FileOutputStream fos = new FileOutputStream(job.getLocalBackupFile());
		IOUtil.copyStream(is, fos);
		logger.debug("Backup bucket saved to local file " + job.getLocalBackupFile());
		
		logger.trace("restore completed");
	}

	private String getBucketNameForJob() {
		return BUCKET_ROOT_NAME + "/instance_" + job.getToasInstanceId();
	}
	
	private String getBucketKeyForJob() {
		return "vm_" + job.getVirtualMachineInstanceId();
	}
}
