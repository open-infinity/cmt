package org.openinfinity.cloud.application.backup.job;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Paths;
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
 * Takes care of encyrpting/decrypting a package. This implementation is based on 
 * standard JCE API.
 * 
 * @author Timo Saarinen
 */
public class CipherCommand implements Command {
	final static String ALGORITHM = "Blowfish";
	
	private Logger logger = Logger.getLogger(CipherCommand.class);

	private InstanceJob job;
	
	public CipherCommand(InstanceJob job) {
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
		logger.info("Ciphering using " + ALGORITHM + " algorithm");

		// Get cipher
		SecretKey secret_key = getSecretKey();
		Cipher cipher = Cipher.getInstance(ALGORITHM);
		cipher.init(Cipher.ENCRYPT_MODE, secret_key);

		// Create plain input
		File plainFile = job.getLocalBackupFile();
		FileInputStream fis = new FileInputStream(plainFile);
		
		// Create ciphered output
		File cipherFile = new File(plainFile + ".ciphered"); 
		FileOutputStream fos = new FileOutputStream(cipherFile);
		CipherOutputStream cos = new CipherOutputStream(fos, cipher);
		
		// Copy all data
		//IOUtils.copy(fis, cos);
		Tools.copyStreams(fis, cos);
		
		// Close streams
		cos.flush();
		fos.flush();
		cos.close();
		fos.close();
		fis.close();
		
		// Update backup file name and delete plain one
		job.setLocalBackupFile(cipherFile);
		plainFile.delete();
		
		logger.info("Package ciphered successfully.");
	}
	
	/**
	 * Decipher backup package.
	 */
	private void decipher() throws Exception {
		logger.info("Deciphering using " + ALGORITHM + " algorithm");

		// Get cipher
		SecretKey secret_key = getSecretKey();
		Cipher cipher = Cipher.getInstance(ALGORITHM);
		cipher.init(Cipher.DECRYPT_MODE, secret_key);

		// Create ciphered input
		File cipherFile = job.getLocalBackupFile();
		FileInputStream fis = new FileInputStream(cipherFile);
		CipherInputStream cis = new CipherInputStream(fis, cipher);
		
		// Create ciphered output
		File plainFile = Tools.replaceExtension(cipherFile, null); 
		FileOutputStream fos = new FileOutputStream(plainFile);
		
		// Copy all data
		//IOUtils.copy(cis, fos);
		Tools.copyStreams(cis, fos);
		
		// Close streams
		fos.flush();
		fos.close();
		fis.close();

		// Update backup file name and delete the ciphered
		job.setLocalBackupFile(plainFile);
		cipherFile.delete();
		
		logger.info("Package deciphered successfully.");
	}

	/**
	 * Read or create a new symmetric key to be used for encryption.
	 * @return secret key, never null
	 * @throws NoSuchAlgorithmException
	 * @throws BackupException
	 * @throws IOException
	 */
	private SecretKey getSecretKey() throws NoSuchAlgorithmException, BackupException, IOException {
		//
		// TODO: This method is not finished
		//
		
		// Bouncy Castle provider is used to get better randomness for the keys
		Security.addProvider(new BouncyCastleProvider());

		File key_file = new File("/tmp/cloud_backup_secret.key"); // FIXME
		if (key_file.exists()) {
			// Load secret key from local file
			byte[] bytes = Files.readAllBytes(Paths.get(key_file.toString()));

			SecretKey secret_key = new SecretKeySpec(bytes, 0, bytes.length, ALGORITHM);
			return secret_key;
		} else {
			// Generate key
			KeyGenerator keygen = KeyGenerator.getInstance(ALGORITHM);
		    SecretKey secret_key = keygen.generateKey();
		    
		    // Save key to local file
		    byte[] bytes = secret_key.getEncoded();
		    if (bytes != null) {
		    	FileOutputStream fos = new FileOutputStream(key_file);
		    	fos.write(bytes);
		    	fos.flush();
		    	fos.close();
		    } else {
		    	throw new BackupException("Couldn't encode the symmetric key!");
		    }
		    
		    return secret_key;
		}
	}
}
