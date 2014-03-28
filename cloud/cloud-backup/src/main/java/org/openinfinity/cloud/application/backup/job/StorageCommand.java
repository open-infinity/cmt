package org.openinfinity.cloud.application.backup.job;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.naming.Context;

import org.apache.log4j.Logger;
import org.openinfinity.cloud.application.backup.CloudBackup;
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
	}

	public void execute() throws Exception {
		if (bucketRepository == null)
			bucketRepository = (BucketRepository) CloudBackup.getInstance().getContext().getBean("jetS3Repository");
		
		if (job instanceof InstanceBackupJob) {
			store();
		} else if (job instanceof InstanceRestoreJob) {
			restore();
		} else if (job instanceof InstanceDeleteJob) {
			delete();
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
		} else if (job instanceof InstanceDeleteJob) {
			throw new BackupException("Delete can't be undoed!");
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

		// Create metadata
		Map<String, String> metadataMap = new HashMap<String, String>();
		metadataMap.put("hostname", job.getHostname());
		metadataMap.put("hostrole", job.getLogicalMachineName());
		metadataMap.put("username", job.getUsername());
		metadataMap.put("filename", job.getLocalBackupFile().getName());
		metadataMap.put("encryption", job.isCipher() ? "yes" : "no");
		metadataMap.put("compression-method", job.getCompressionMethod());
		
		// Upload the backup package to S3 repository as a bucket
		FileInputStream fis = new FileInputStream(job.getLocalBackupFile());
		logger.info("Storing the backup to S3 repository: name=" + getBucketNameForJob() 
				+ " key=" + getBucketKeyForJob());
		String location = bucketRepository.createBucket(
				fis, getBucketNameForJob(), getBucketKeyForJob(), metadataMap);
		logger.debug("bucket created in location " + location);
		fis.close();

		// Check that metadata was saved successfully
		logger.trace("Reading the saved metadata and comparing it to the written one");
		Map<String, String> metadataMap2 = bucketRepository.readMetadata(getBucketNameForJob(), getBucketKeyForJob());
		for (String k : metadataMap.keySet()) {
			if (!metadataMap.get(k).equals(metadataMap2.get(k))) {
				throw new BackupException("Metadata mismatch! '" + metadataMap.get(k) + "' != '" + (metadataMap2.get(k)) + "'");
			}
		}
		logger.info("Metadata check succeeded");
		
		// Finally delete the local backup file
		logger.trace("Deleteting local backup file");
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
		String package_filename = "cluster" + job.getStorageCluster().getClusterId() + "-"
				+ job.getLogicalMachineName() + "-backup.tar.xz";
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

	/**
	 * Delete backup file in S3 storage.
	 */
	public void delete() throws Exception {
		// Delete the backup from bucket repository
		logger.info("Deleting backup file from bucket repository");
		bucketRepository.deleteObject(getBucketNameForJob(), getBucketKeyForJob());
	}

	private String getBucketNameForJob() {
		return BUCKET_ROOT_NAME + "/cluster-" + job.getStorageCluster().getClusterId();
	}
	
	private String getBucketKeyForJob() {
		return job.getLogicalMachineName();
	}
}
