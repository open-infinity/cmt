package org.openinfinity.cloud.application.backup;

import java.util.LinkedList;

import org.aspectj.weaver.NewConstructorTypeMunger;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.quartz.SchedulerException;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.test.context.TestExecutionListeners;

import com.thoughtworks.xstream.mapper.OuterClassMapper;

public class DynamicQuartzSchedulerManagerTest {
	private ClassPathXmlApplicationContext context = null;
	
	private DynamicQuartzSchedulerManager dynamicQuartzSchedulerManager;

	private Object runCountLockObject = new LinkedList(); 
	private int testJobRunCount = 0;
	
	/**
	 * Get app context and scheduler. Start the scheduler.
	 */
	@Before
	public void initialize() throws Exception {
		// Get application context
		context = new ClassPathXmlApplicationContext("/cloud-backup-context.xml");
		Assert.assertNotNull(context);
		
		// Create scheduler
		dynamicQuartzSchedulerManager = (DynamicQuartzSchedulerManager) context.getBean("dynamicQuartzSchedulerManager");
		dynamicQuartzSchedulerManager.context = context;
		
		// Start the scheduler
		dynamicQuartzSchedulerManager.start();
	}
	
	/**
	 * This test starts a simple job and ends, after it is run two times.
	 */
	@Test
	public void testJobScheduling() throws Exception {
		Assert.assertNotNull(dynamicQuartzSchedulerManager);
		
		testJobRunCount = 0;
		dynamicQuartzSchedulerManager.addJob("test-job", new TestJob(), "* * * * * ?");

		// Wait until the test jobs are run
		while (testJobRunCount < 2) {
			Thread.sleep(1000);
		}
		
		Assert.assertEquals(2, testJobRunCount);
	}

	/**
	 * Stop the scheduler.
	 */
	@After
	public void cleanup() throws Exception {
		dynamicQuartzSchedulerManager.stop();
	}
	
	/**
	 * Class for Quartz scheduler
	 */
	private class TestJob {
		public void run() {
			synchronized(runCountLockObject) {
				testJobRunCount++;
			}
		}
	}
}
