package org.openinfinity.cloud.application.backup.job;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import javax.xml.bind.DatatypeConverter;

import org.apache.log4j.Logger;
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
		
		// Use SSH keys for ciphering
		File private_ssh_key_file = new File("/tmp/" + job.getToasInstanceId() + "-private-key.pem");
		File public_key_file = new File("/tmp/" + job.getToasInstanceId() + "-public-key.pem");
		makeLocalKeys(private_ssh_key_file, public_key_file);
		
		try {
			// Cipher!!
			File ciphered_file = new File(job.getLocalBackupFile() + ".ssl");
			int r = runLocalCommand("openssl rsautl -encrypt" 
					+ " -inkey " + public_key_file 
					+ " -pubin" 
					+ " -in " + job.getLocalBackupFile() 
					+ " -out " + ciphered_file);
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
	
	/**
	 * Decipher backup package.
	 */
	public void decipher() throws Exception {
		logger.trace("decipher");

		// Use SSH keys for deciphering
		File private_ssh_key_file = new File("/tmp/" + job.getToasInstanceId() + "-private-key.pem");
		File public_key_file = new File("/tmp/" + job.getToasInstanceId() + "-public-key.pem");
		makeLocalKeys(private_ssh_key_file, public_key_file);
		
		try {
			// Decipher!!
			File deciphered_file = new File(job.getLocalBackupFile() + ".plain");
			int r = runLocalCommand("openssl rsautl -decrypt" 
					+ " -inkey " + private_ssh_key_file 
					+ " -in " + job.getLocalBackupFile() 
					+ " -out " + deciphered_file);
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
	}

	/**
	 * Copies the SSH private key to local file system and creates a public key out if it.
	 */
	private void makeLocalKeys(File privateKeyFilename, File publicKeyFilename) throws BackupException, IOException {
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
		PrintWriter pw = new PrintWriter(fos);
		pw.println("-----BEGIN CERTIFICATE-----");
		pw.println(DatatypeConverter.printBase64Binary(private_ssh_key.getSecret_key().getBytes()));
		pw.println("-----END CERTIFICATE-----");
		pw.flush();
		//fos.flush();
		fos.close();

		// Make public key out of the private key 
		int r = runLocalCommand("openssl rsa" 
				+ " -in " + privateKeyFilename + " -inform PEM"
				+ " -out " + publicKeyFilename + " -outform PEM" 
				+ " -pubout");
		if (r != 0) 
			throw new BackupException("Failed to create public key (exit code " + r + ")!");
	}
	
	/**
	 * Execute command in local host. Quote marks are not permitted as part of
	 * the command.
	 * 
	 * @return exit value of the command
	 */
	private int runLocalCommand(String cmd) throws IOException {
		logger.debug("Running local command: " + cmd);
		try {
			// Run and wait for completion of the command
			Process p = Runtime.getRuntime().exec(cmd);
			return p.waitFor();
		} catch (InterruptedException exception) {
			logger.warn("Local command waiting was interrupted unexpectedly");
		}
		return -1;
	}
}
