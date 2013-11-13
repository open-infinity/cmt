package org.openinfinity.cloud.application.backup;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

import org.apache.log4j.Logger;
import org.junit.*;
import org.junit.runner.RunWith;
import org.openinfinity.cloud.application.backup.job.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Test ciphering functionality and performance 
 * 
 * @author Timo Saarinen
 */
//@ContextConfiguration(locations = {"classpath*:**/cloud-backup-context.xml"})
//@RunWith(SpringJUnit4ClassRunner.class)
public class CipherCommandTest {
	private Logger logger = Logger.getLogger(StorageCommand.class);
	
	private File pfile;
	private File c1file;
	private File c2file;
	private long checksum;
	private long read1Checksum;
	private long read2Checksum;
	private static final long FILE_SIZE = 50*1024*1024; // 50 MB
	
	//@Autowired
	private ClassPathXmlApplicationContext applicationContext;
	
	public CipherCommandTest() throws IOException, BackupException {
		applicationContext = new ClassPathXmlApplicationContext("classpath*:**/cloud-backup-context.xml");
		assert applicationContext != null;
		
		// Decide temporary file names
		pfile = File.createTempFile("plain", ".bin");
		pfile.deleteOnExit();
		
		// Create 1GB nonsense file
		logger.info("Creating file of size " + FILE_SIZE + " bytes to " + pfile);
		FileOutputStream fos = new FileOutputStream(pfile);
		BufferedOutputStream bos = new BufferedOutputStream(fos, 8192); 
		Random random = new Random(System.currentTimeMillis());
		for (long i = 0; i < FILE_SIZE; i++) {
			byte b = (byte)random.nextLong();
			bos.write(b);
		}
		bos.flush();
		bos.close();
		fos.close();

		// Count checksum
		logger.info("Initial checksum");
		checksum = countFileChecksum(pfile);
	}

	@Test
	public void testCipherCommand1() throws Exception {
		// Cipher
		{
			logger.debug("Size of the original file is: " + Tools.readFileSize(pfile));
			
			// Create a job, which meets the basic requirements of the command
			TestBackupInstanceJob job = new TestBackupInstanceJob();
			job.setLocalBackupFile(pfile);
			job.setLocalPackageDirectory(pfile.getParent());
			
			// Execute the command
			logger.info("Ciphering 1");
			Command cmd = new CipherCommand(job);
			cmd.execute();
			
			// Get ciphered filename for cleanup
			c1file = job.getLocalBackupFile();
			c1file.deleteOnExit();
			logger.debug("Size of the ciphered file is: " + Tools.readFileSize(c1file));
		}

		// Decipher
		{
			// Create a job, which meets the basic requirements of the command
			TestRestoreInstanceJob job = new TestRestoreInstanceJob();
			job.setLocalBackupFile(c1file);
			job.setLocalPackageDirectory(c1file.getParent());
			
			// Execute the command
			logger.info("Dechipering 1");
			Command cmd = new CipherCommand(job);
			cmd.execute();
			
			// Calculate check sum
			logger.info("Checksum 1");
			read1Checksum = countFileChecksum(job.getLocalBackupFile());
			job.getLocalBackupFile().deleteOnExit();
			Assert.assertTrue(checksum == read1Checksum);
		}
	}
	
	@Test
	public void testCipherCommand2() throws Exception {
/*		
		// Cipher
		{
			// Create a job, which meets the basic requirements of the command
			TestBackupInstanceJob job = new TestBackupInstanceJob();
			job.setLocalBackupFile(pfile);
			job.setLocalPackageDirectory(pfile.getParent());
			
			// Execute the command
			logger.info("Ciphering 2");
			Command cmd = new CipherCommand2(job);
			cmd.execute();
			
			// Get ciphered filename for cleanup
			c2file = job.getLocalBackupFile();
			c2file.deleteOnExit();
		}

		// Decipher
		{
			// Create a job, which meets the basic requirements of the command
			TestRestoreInstanceJob job = new TestRestoreInstanceJob();
			job.setLocalBackupFile(c2file);
			job.setLocalPackageDirectory(c2file.getParent());
			
			// Execute the command
			logger.info("Deciphering 2");
			Command cmd = new CipherCommand2(job);
			cmd.execute();
			
			// Calculate check sum
			logger.info("Checksum 2");
			read2Checksum = countFileChecksum(job.getLocalBackupFile());
			//logger.debug("read2Checksum=" + read2Checksum + " checksum" + checksum);
			Assert.assertTrue(checksum == read2Checksum);
		}
*/		
	}
	
	private long countFileChecksum(File f) throws IOException {
		FileInputStream fis = new FileInputStream(f);
		BufferedInputStream bis = new BufferedInputStream(fis, 8192);
		int b;
		int i = 0;
		long sum = 0;
		while ((b = bis.read()) != -1) {
			// FIXME: better algorithm
			sum += b;
			sum = (sum >> 3) | (sum << (64 - 3));
			i++;
		}
		fis.close();
		//logger.debug("countFileChecksum(" + f + "): " + i + " bytes readm checksum=" + sum);
		return sum;
	}

	private class TestBackupInstanceJob extends InstanceBackupJob {
		public TestBackupInstanceJob() throws BackupException {
			super(applicationContext);
			commands.clear();
		}
	}

	private class TestRestoreInstanceJob extends InstanceRestoreJob {
		public TestRestoreInstanceJob() throws BackupException {
			super(applicationContext);
			commands.clear();
		}
	}
}
