package org.openinfinity.cloud.application.backup.job;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.security.NoSuchAlgorithmException;
import java.security.Security;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.openinfinity.core.crypto.CryptoSupport;

/**
 * Takes care of encyrpting/decrypting a package. This implementation depends on
 * Open Infinity Core CryptoSupport module, which currently relies on keyzcar 0.71g 
 * library, which relies on JCE itself.
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

		// Initiate the oicore crypto module
		CryptoSupport crypto = (CryptoSupport)job.context.getBean("cryptoSupport");

		// Create input stream
		File plainFile = job.getLocalBackupFile();
		FileInputStream fis = new FileInputStream(plainFile);
        FileChannel fic = fis.getChannel();
        ByteBuffer bbi = ByteBuffer.allocate(8192);

		// Create output stream		
		File cipherFile = new File(plainFile + ".ciphered"); 
		FileOutputStream fos = new FileOutputStream(cipherFile);
        FileChannel foc = fos.getChannel();
		ByteBuffer bbo = ByteBuffer.allocate(8192);

		// Cipher
		int n;
		while ((n = fic.read(bbi)) != -1) {
			if (n == 0) {
				continue;
			} else {
				bbi.position(0);
				bbi.limit(n);
				bbo.position(0);
				bbo.limit(n);
				crypto.encrypt(bbi, bbo);
				foc.write(bbo);
			}
		}

		// Close input/output elements
		fos.flush();
		foc.close();
		fos.close();
		fic.close();
		fis.close();

		// Update backup file name and delete plain one
		job.setLocalBackupFile(cipherFile);
		plainFile.delete();
	}
	
	/**
	 * Decipher backup package.
	 */
	private void decipher() throws Exception {
		logger.trace("decipher");

		// Initiate the oicore crypto module
		CryptoSupport crypto = (CryptoSupport)job.context.getBean("cryptoSupport");

		// Create input stream
		File cipherFile = job.getLocalBackupFile();
		FileInputStream fis = new FileInputStream(cipherFile);
        FileChannel fic = fis.getChannel();
        ByteBuffer bbi = ByteBuffer.allocate(8192);

		// Create output stream		
		File plainFile = Tools.replaceExtension(cipherFile, null); 
		FileOutputStream fos = new FileOutputStream(plainFile);
        FileChannel foc = fos.getChannel();
		ByteBuffer bbo = ByteBuffer.allocate(8192);

		// Decipher
		int n;
		while ((n = fic.read(bbi)) != -1) {
			if (n == 0) {
				continue;
			} else {
				bbi.position(0);
				bbi.limit(n);
				bbo.position(0);
				bbo.limit(n);
				crypto.decrypt(bbi, bbo);
				foc.write(bbo);
			}
		}

		// Close input/output elements
		fos.flush();
		foc.close();
		fos.close();
		fic.close();
		fis.close();

		// Update backup file name and delete plain one
		job.setLocalBackupFile(plainFile);
		cipherFile.delete();
	}

}
