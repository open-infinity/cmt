package org.openinfinity.cloud.application.backup.job;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.security.NoSuchAlgorithmException;
import java.security.Security;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

/**
 * Takes care of encyrpting/decrypting a package.
 * 
 * @author Timo Saarinen
 */
public class CipherCommand implements Command {
	private Logger logger = Logger.getLogger(CipherCommand.class);

	private InstanceJob job;
	
	public CipherCommand(InstanceJob job) {
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
	public void cipher() throws Exception {
		logger.trace("cipher");

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
	}
	
	/**
	 * Decipher backup package.
	 */
	public void decipher() throws Exception {
		logger.trace("decipher");

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
	}

	private SecretKey getInstanceSecretKey() throws NoSuchAlgorithmException, BackupException, IOException {
		// Bouncy Castle provider is used to get better randomness for the keys
		Security.addProvider(new BouncyCastleProvider());

		File key_file = new File("/tmp/instance-" + job.getToasInstanceId() + "_des.key");
		if (key_file.exists()) {
			// Load secret key from local file
			RandomAccessFile f = new RandomAccessFile(key_file.toString(), "r");
			byte[] bytes = new byte[(int)f.length()];
			f.read(bytes);
			f.close();
/* Requires Java 7			
			byte[] bytes = Files.readAllBytes(Paths.get(key_file.toString()));
*/			

			SecretKey secret_key = new SecretKeySpec(bytes, 0, bytes.length, "DES");
			return secret_key;
		} else {
			// Generate key
			KeyGenerator keygen = KeyGenerator.getInstance("DES");
		    SecretKey secret_key = keygen.generateKey();
		    
		    // Save key to local file
		    byte[] bytes = secret_key.getEncoded();
		    if (bytes != null) {
		    	FileOutputStream fos = new FileOutputStream(key_file);
		    	fos.write(bytes);
		    	fos.flush();
		    	fos.close();
		    } else {
		    	throw new BackupException("Can't encode the symmetric key!");
		    }
		    
		    return secret_key;
		}
	}
}
