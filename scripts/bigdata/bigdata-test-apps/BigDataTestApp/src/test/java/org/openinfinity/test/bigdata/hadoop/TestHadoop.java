package org.openinfinity.test.bigdata.hadoop;

import java.io.IOException;

import org.junit.*;

import com.jcraft.jsch.*;

import static org.junit.Assert.*;

import org.openinfinity.test.bigdata.common.SSHConnection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.hadoop.mapreduce.JobRunner;

/**
 * This application tests Hadoop cluster.
 */
public class TestHadoop {
	private static ApplicationContext applicationContext = new ClassPathXmlApplicationContext(
			"/spring-application-context.xml");

	public static ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	/**
	 * Ensure, that connection to Hadoop is available and that required
	 * structures exist.
	 * 
	 * @throws Exception
	 */
	@BeforeClass
	public static void setUpClass() throws Exception {
	}

	@Test
	public void testNormalWriteAndRead() throws JSchException, IOException {
		// Get HMaster hostname
		HadoopProperties p = getApplicationContext().getBean(
				HadoopProperties.class); 
		System.out.println("HMaster is " + p.getHmasterHost());

		// Prepare HDFS file system over SSH		
		SSHConnection ssh = new SSHConnection("root", p.getHmasterHost(), p.getHmasterSshKeyFilename());

		// Test SSH connection
		assertTrue(ssh.execute("uptime") == 0);

		// Input dir
		ssh.execute("su - hdfs -s /bin/bash -c 'hadoop fs -mkdir /input'");
		assertTrue(ssh.execute("su - hdfs -s /bin/bash -c 'hadoop fs -chmod -R 777 /input'") == 0);
		assertTrue(ssh.execute("su - hdfs -s /bin/bash -c 'hadoop fs -chown -R hbase /input'") == 0);
		
		// Output dir
		ssh.execute("su - hdfs -s /bin/bash -c 'hadoop fs -mkdir /output'");
		assertTrue(ssh.execute("su - hdfs -s /bin/bash -c 'hadoop fs -chmod -R 777 /output'") == 0);
		assertTrue(ssh.execute("su - hdfs -s /bin/bash -c 'hadoop fs -chown -R hbase /output'") == 0);

		// Upload content
		assertTrue(ssh.execute("wget -o /tmp/gulliver.txt http://www.gutenberg.org/files/829/829-0.txt") == 0);
		assertTrue(ssh.execute("hadoop dfs -put /tmp/gulliver.txt /input/gulliver.txt") == 0);
		assertTrue(ssh.execute("rm /tmp/gulliver.txt") == 0);
		
		// Run job
		try {
			JobRunner jr = (JobRunner) getApplicationContext().getBean("hadoopTestJobRunner");
			jr.call();
		} catch (Exception e) {
			e.printStackTrace();
			assert(false);
		}
		
		ssh.disconnect();
	}

	@Test
	public void testNormalMapReduce() {
	}

}
