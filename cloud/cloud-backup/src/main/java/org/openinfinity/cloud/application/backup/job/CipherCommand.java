package org.openinfinity.cloud.application.backup.job;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import org.apache.log4j.Logger;
import org.aspectj.weaver.NewConstructorTypeMunger;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.provider.PEMUtil;
import org.bouncycastle.openssl.PEMReader;
import org.bouncycastle.util.encoders.Base64;
import org.openinfinity.cloud.domain.Key;
import org.openinfinity.cloud.service.administrator.KeyService;

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

		Cipher cipher = Cipher.getInstance("RSA");
		PrivateKey private_key = getSecretKey();
		
	    // Initialize the cipher for encryption
	    cipher.init(Cipher.ENCRYPT_MODE, private_key);

	    // Our cleartext
	    byte[] cleartext = "This is just an example".getBytes();

	    // Encrypt the cleartext
	    byte[] ciphertext = cipher.doFinal(cleartext);

	    // Initialize the same cipher for decryption
	    cipher.init(Cipher.DECRYPT_MODE, private_key);

	    // Decrypt the ciphertext
	    byte[] cleartext1 = cipher.doFinal(ciphertext);
	}
/*	
	public void cipher() throws Exception {
		logger.trace("cipher");
		
		// Use SSH keys for ciphering
		makeLocalKeys();
		
		// http://stackoverflow.com/questions/7143514/how-to-encrypt-a-large-file-in-openssl-using-public-key
		try {
			// Cipher!!
			File ciphered_file = new File(job.getLocalBackupFile() + ".ssl");
			int r = Tools.runLocalCommand("openssl rsautl -encrypt" 
					+ " -inkey " + public_key_file 
					+ " -pubin" 
					+ " -in " + job.getLocalBackupFile() 
					+ " -out " + ciphered_file, logger);
			if (r != 0) { 
				throw new BackupException("Failed to cipher backup package (exit code " + r + ")!");
			} else {
				job.getLocalBackupFile().delete();
				job.setLocalBackupFile(ciphered_file);
			}
		} finally {
			private_ssh_key_file.delete();
			public_key_file.delete();
		}
	}
*/	
	
	/**
	 * Decipher backup package.
	 */
	public void decipher() throws Exception {
		logger.trace("decipher");

/*		
		// Use SSH keys for deciphering
		makeLocalKeys();
		
		try {
			// Decipher!!
			File deciphered_file = new File(job.getLocalBackupFile() + ".plain");
			int r = Tools.runLocalCommand("openssl rsautl -decrypt" 
					+ " -inkey " + private_ssh_key_file 
					+ " -in " + job.getLocalBackupFile() 
					+ " -out " + deciphered_file, logger);
			if (r != 0) { 
				throw new BackupException("Failed to decipher backup package (exit code " + r + ")!");
			} else {
				job.getLocalBackupFile().delete();
				job.setLocalBackupFile(deciphered_file);
			}
		} finally {
			private_ssh_key_file.delete();
			public_key_file.delete();
		}
*/		
	}

	/**
	 * Get SSH RSA private key of the current instance.  
	 * @throws InvalidKeySpecException 
	 * @throws NoSuchAlgorithmException 
	 * @throws IOException 
	 */
	private PrivateKey getSecretKey() throws InvalidKeySpecException, NoSuchAlgorithmException, NoSuchProviderException, IOException {
		// Get the unencrypted PEM encoded RSA private key, which will be used for encryption
		KeyService keyService = (KeyService) job.context
				.getBean("keyService");
		Key rsa_key = keyService.getKeyByInstanceId(job.getToasInstanceId());

		// Convert the secret key to JCE key
		String pem_key = rsa_key.getSecret_key(); 

	    logger.trace(pem_key);

/*	    
	    pem_key = pem_key.replace("-----BEGIN RSA PRIVATE KEY-----\n", "");
	    pem_key = pem_key.replace("-----END RSA PRIVATE KEY-----\n", "");
	    pem_key = pem_key.replace("\n", "");
	    logger.trace(pem_key);
	    byte[] bytes = Base64.decode(pem_key);
*/	    

	    /*
		PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(bytes);
		KeyFactory kf = KeyFactory.getInstance("RSA");
		PrivateKey private_key = kf.generatePrivate(spec);
		*/

		Security.addProvider(new BouncyCastleProvider());
		KeyPair kp = (KeyPair) new PEMReader(new StringReader(pem_key)).readObject();
		PrivateKey private_key = kp.getPrivate();
		
/*		
		KeyFactory privateKeyFactory = KeyFactory.getInstance("RSA");
		PKCS8EncodedKeySpec encodedKeySpec = new PKCS8EncodedKeySpec(bytes);
		//X509EncodedKeySpec encodedKeySpec = new X509EncodedKeySpec(bytes);
		PrivateKey private_key = privateKeyFactory.generatePrivate(encodedKeySpec);
*/		

/*
		//add BouncyCastle as a provider if you want
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
		//create a keyfactory - use whichever algorithm and provider
		KeyFactory kf = KeyFactory.getInstance("DSA", "BC");
		//for private keys use PKCS8EncodedKeySpec; for public keys use X509EncodedKeySpec
		PKCS8EncodedKeySpec ks = new PKCS8EncodedKeySpec(bytes);
		PrivateKey private_key = kf.generatePrivate(ks);
*/		
		
		return private_key;
	}

	/**
	 * Get SSH RSA private key of the current instance.  
	 */
	private SecretKey getSecretKey___________________________________() {
		// Get the unencrypted PEM encoded RSA private key, which will be used for encryption
		KeyService keyService = (KeyService) job.context
				.getBean("keyService");
		Key rsa_key = keyService.getKeyByInstanceId(job.getToasInstanceId());

		// Convert the secret key to JCE key
		byte[] b = rsa_key.getSecret_key().getBytes(); 
		SecretKey originalKey = new SecretKeySpec(b, 0, b.length, "RSA");
		
		return originalKey;
	}
	
	/**
	 * Copies the SSH private key to local file system and creates a public key out if it.
	 */
	private void makeLocalKeys() throws BackupException, IOException {
		String email = job.getInstanceEmail(); 
		File privateKeyFilename = new File("/tmp/" + job.getToasInstanceId() + "-private-key.pem");
		File publicKeyFilename = new File("/tmp/" + job.getToasInstanceId() + "-public-key.pem");
		
		// Get SSH key, which will be used for encryption and save it to a file
		KeyService keyService = (KeyService) job.context
				.getBean("keyService");
		Key private_ssh_key = keyService.getKeyByInstanceId(job.getToasInstanceId());

		// Create an empty file, to get the permissions right
		FileOutputStream fos = new FileOutputStream(privateKeyFilename);
		fos.flush();
		fos.close();

		// Write the private key in PEM format
		fos = new FileOutputStream(privateKeyFilename, true);
		PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(
				private_ssh_key.getSecret_key().getBytes());
		fos = new FileOutputStream(privateKeyFilename);
		fos.write(pkcs8EncodedKeySpec.getEncoded());
		fos.close();
		
		//fos.flush();
		fos.close();
		
		// First we need to create a certificate (self-signed) for our ssh key
		int r = -1;
		Tools.runLocalCommand("openssl req -new -x509 -key ~/.ssh/id_rsa -out ssh-cert.pem", logger);
		
		// We can now import it in GnuPG
		Tools.runLocalCommand("openssl pkcs12 -export -in ssh-certs.pem -inkey ~/.ssh/id_rsa -out ssh-key.p12", logger);
		Tools.runLocalCommand("gpgsm --import ssh-key.p12", logger);
/*		
		// Make public key out of the private key 
		int r = Tools.runLocalCommand("openssl rsa" 
				+ " -in " + privateKeyFilename + " -inform PEM"
				+ " -out " + publicKeyFilename + " -outform PEM" 
				+ " -pubout", logger);
		if (r != 0) 
			throw new BackupException("Failed to create public key (exit code " + r + ")!");
*/			
	}
}
