package org.openinfinity.cloud.application.backup.job;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.NoSuchAlgorithmException;
import java.security.Security;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.openinfinity.cloud.application.backup.CloudBackup;
import org.openinfinity.core.crypto.CryptoSupport;

import com.sun.jna.Native.ffi_callback;

/**
 * Takes care of encyrpting/decrypting a package. This implementation depends on
 * Open Infinity Core CryptoSupport module, which currently relies on keyzcar 0.71g 
 * library, which relies on the standard JCE itself. This approach is in harmony with 
 * the high-level architecture.
 * 
 * @author Timo Saarinen
 */
public class CipherCommand2 implements Command {
	private Logger logger = Logger.getLogger(CipherCommand2.class);

	private InstanceJob job;
	
	private static final int BUFFER_SIZE = 65536;
	
	public CipherCommand2(InstanceJob job) {
		this.job = job;
	}
	
	public void execute() throws Exception {
		if (job.getLocalBackupFile() != null) {
			if (job instanceof InstanceBackupJob) {
				if (job.useCipher()) {
					cipher();
				} else {
					logger.info("Ciphering disbled.");
				}
			} else if (job instanceof InstanceRestoreJob) {
				if (job.useCipher()) {
					decipher();
				} else {
					logger.info("(De)ciphering disbled.");
				}
			} else {
				throw new BackupException("Unexpected base class " + job.getClass());
			}
		}
	}

	public void undo() throws Exception {
		if (job instanceof InstanceBackupJob) {
			if (job.useCipher()) {
				decipher();
			}
		} else if (job instanceof InstanceRestoreJob) {
			if (job.useCipher()) {
				cipher();
			}
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
		CryptoSupport crypto = (CryptoSupport)CloudBackup.getInstance().getContext().getBean("cryptoSupport");

		// Input file
		File plainFile = job.getLocalBackupFile();
		RandomAccessFile rafi = new RandomAccessFile(plainFile, "r");
		FileChannel fci = rafi.getChannel();
		MappedByteBuffer mbbi = fci.map(FileChannel.MapMode.READ_ONLY, 0, fci.size());
		
		// Output file
		File cipherFile = new File(plainFile + ".ciphered"); 
		RandomAccessFile rafo = new RandomAccessFile(cipherFile, "rw");
		FileChannel fco = rafo.getChannel();
		MappedByteBuffer mbbo = fco.map(FileChannel.MapMode.READ_WRITE, 0, fci.size() * 2);

		// Cipher
		crypto.encrypt(mbbi, mbbo);

		// Close files
		rafo.close();
		rafi.close();
		
		/* THIS WORKS ONLY IF BUFFER_SIZE * 2 IS GREATER THAN THE OUTPUT FILE
		// Create input stream
		File plainFile = job.getLocalBackupFile();
		FileInputStream fis = new FileInputStream(plainFile);
        FileChannel fic = fis.getChannel();
        ByteBuffer bbi = ByteBuffer.allocate(BUFFER_SIZE);

		// Create output stream		
		File cipherFile = new File(plainFile + ".ciphered"); 
		FileOutputStream fos = new FileOutputStream(cipherFile);
        FileChannel foc = fos.getChannel();
		ByteBuffer bbo = ByteBuffer.allocate(BUFFER_SIZE * 2);

		// Cipher
		int n;
		logger.debug("0: bbi=" + bbi + " bbo=" + bbo);
		while ((n = fic.read(bbi)) != -1) {
			if (n == 0) {
				continue;
			} else {
				bbi.flip();
				bbo.clear();
				crypto.encrypt(bbi, bbo);
				//Tools.copyByteBuffer(bbi, bbo);
				bbo.flip();
				foc.write(bbo);
				bbi.clear();
			}
		}
		

		// Close input/output elements
		fos.flush();
		foc.close();
		fos.close();
		fic.close();
		fis.close();
*/		

		// Update backup file name and delete plain one
		job.setLocalBackupFile(cipherFile);
		plainFile.delete();
		
		logger.info("Package ciphered successfully.");
	}
	
	/**
	 * Decipher backup package.
	 */
	private void decipher() throws Exception {
		logger.trace("decipher");

		// Initiate the oicore crypto module
		CryptoSupport crypto = (CryptoSupport)CloudBackup.getInstance().getContext().getBean("cryptoSupport");

		// Input file
		File cipherFile = job.getLocalBackupFile();
		RandomAccessFile rafi = new RandomAccessFile(cipherFile, "r");
		FileChannel fci = rafi.getChannel();
		MappedByteBuffer mbbi = fci.map(FileChannel.MapMode.READ_ONLY, 0, fci.size());
		
		// Output file
		File plainFile = Tools.replaceExtension(cipherFile, null); 
		RandomAccessFile rafo = new RandomAccessFile(plainFile, "rw");
		FileChannel fco = rafo.getChannel();
		MappedByteBuffer mbbo = fco.map(FileChannel.MapMode.READ_WRITE, 0, fci.size());

		// Cipher
		crypto.decrypt(mbbi, mbbo);

		// Close files
		rafo.close();
		rafi.close();
		
		// Update backup file name and delete plain one
		job.setLocalBackupFile(plainFile);
		cipherFile.delete();

/*
		// Create input stream
		File cipherFile = job.getLocalBackupFile();
		FileInputStream fis = new FileInputStream(cipherFile);
        FileChannel fic = fis.getChannel();
        ByteBuffer bbi = ByteBuffer.allocate(BUFFER_SIZE);

		// Create output stream		
		File plainFile = Tools.replaceExtension(cipherFile, null); 
		FileOutputStream fos = new FileOutputStream(plainFile);
        FileChannel foc = fos.getChannel();
		ByteBuffer bbo = ByteBuffer.allocate(BUFFER_SIZE);

		// Decipher
		int n;
		while ((n = fic.read(bbi)) != -1) {
			if (n == 0) {
				continue;
			} else {
//				bbi.position(0);
//				bbi.limit(n);
				//bbo.position(0);
				//bbo.limit(n);
				bbi.flip();
				bbo.clear();
				crypto.decrypt(bbi, bbo);
				//Tools.copyByteBuffer(bbi, bbo);
				bbo.flip();
				foc.write(bbo);
				bbi.clear();
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
*/		
		
		logger.info("Package deciphered successfully.");
	}

}
