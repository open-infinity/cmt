package org.openinfinity.cloud.application.backup.job;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.security.NoSuchAlgorithmException;
import java.security.Security;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.openinfinity.core.crypto.CryptoSupport;

/**
 * Takes care of encyrpting/decrypting a package.
 * 
 * @author Timo Saarinen
 */
public class CipherCommand2 implements Command {
	private Logger logger = Logger.getLogger(CipherCommand2.class);

	private InstanceJob job;
	
	public CipherCommand2(InstanceJob job) {
		this.job = job;
	}
	
	public void execute() throws Exception {
		if (job instanceof InstanceBackupJob) {
			cipher();
		} else if (job instanceof InstanceRestoreJob) {
			decipher();
		} else {
			throw new BackupException("Unexpected base class " + job.getClass());
		}
	}

	public void undo() throws Exception {
		if (job instanceof InstanceBackupJob) {
			decipher();
		} else if (job instanceof InstanceRestoreJob) {
			cipher();
		} else {
			throw new BackupException("Unexpected base class " + job.getClass());
		}
	}

	/**
	 * Cipher backup package.
	 */
	private void cipher() throws Exception {
		logger.trace("cipher");

		CryptoSupport crypto = new CryptoSupport();
		

/*		
		// Get cipher
		SecretKey secret_key = getInstanceSecretKey();
		Cipher cipher = Cipher.getInstance("DES");
		cipher.init(Cipher.ENCRYPT_MODE, secret_key);

		// Create plain input
		File plainFile = job.getLocalBackupFile();
		FileInputStream fis = new FileInputStream(plainFile);
		
		// Create ciphered output
		File cipherFile = new File(plainFile, ".des"); 
		FileOutputStream fos = new FileOutputStream(cipherFile);
		CipherOutputStream cos = new CipherOutputStream(fos, cipher);
		
		// Copy all data
		IOUtils.copy(fis, cos);
		
		// Close streams
		cos.flush();
		fos.flush();
		cos.close();
		fos.close();
		fis.close();
		
		// Update backup file name and delete plain one
		job.setLocalBackupFile(cipherFile);
		plainFile.delete();
*/		
	}
	
	/**
	 * Decipher backup package.
	 */
	private void decipher() throws Exception {
		logger.trace("decipher");

/*		
		// Get cipher
		SecretKey secret_key = getInstanceSecretKey();
		Cipher cipher = Cipher.getInstance("DES");
		cipher.init(Cipher.ENCRYPT_MODE, secret_key);

		// Create ciphered input
		File cipherFile = job.getLocalBackupFile();
		FileInputStream fis = new FileInputStream(cipherFile);
		CipherInputStream cis = new CipherInputStream(fis, cipher);
		
		// Create ciphered output
		File plainFile = Tools.replaceExtension(cipherFile, null); 
		FileOutputStream fos = new FileOutputStream(plainFile);
		
		// Copy all data
		IOUtils.copy(cis, fos);
		
		// Close streams
		fos.flush();
		fos.close();
		fis.close();

		// Update backup file name and delete the ciphered
		job.setLocalBackupFile(plainFile);
		plainFile.delete();
*/		
	}

}
