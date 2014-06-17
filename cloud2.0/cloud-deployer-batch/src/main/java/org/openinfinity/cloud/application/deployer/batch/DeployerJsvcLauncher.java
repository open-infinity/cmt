package org.openinfinity.cloud.application.deployer.batch;

import org.apache.commons.daemon.Daemon;
import org.apache.commons.daemon.DaemonContext;
import org.apache.commons.daemon.DaemonInitException;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class DeployerJsvcLauncher implements Daemon {
	ClassPathXmlApplicationContext spring = null;
	
	@Override
	public void destroy() {
		if(this.spring != null) {
			this.spring.close();
		}
	}

	@Override
	public void init(DaemonContext arg0) throws DaemonInitException, Exception {
		this.spring = new ClassPathXmlApplicationContext("/cloud-deployer-batch-context.xml");
	}

	@Override
	public void start() throws Exception {
		this.spring.start();
	}

	@Override
	public void stop() throws Exception {
		if(this.spring != null) {
			this.spring.stop();
		}
		/*
        JobExecution jobExecution = jobLauncher.run(jobOperatorJob, new JobParameters());
        Assert.assertTrue(jobExecution.getStatus().isLessThanOrEqualTo(BatchStatus.STARTED));
        Set<Long> runningExecutions = jobOperator.getRunningExecutions(jobOperatorJob.getName());
        Assert.assertEquals(1,runningExecutions.size());
        Long executionId = runningExecutions.iterator().next();
        boolean stopMessageSent = jobOperator.stop(executionId);
        Assert.assertTrue(stopMessageSent);
        waitForTermination(jobOperatorJob);
        runningExecutions = jobOperator.getRunningExecutions(jobOperatorJob.getName());
        Assert.assertEquals(0,runningExecutions.size());
		*/
		/*
Set<Long> executions = jobOperator.getRunningExecutions("sampleJob");
jobOperator.stop(executions.iterator().next());  		 
		 */
	}

	/*
    private void waitForTermination(Job job) throws NoSuchJobException,
                    InterruptedException {
            int timeout = 10000;
            int current = 0;
            while (jobOperator.getRunningExecutions(job.getName()).size() > 0
                            && current < timeout) {
                    Thread.sleep(100);
                    current += 100;
            }
            if(jobOperator.getRunningExecutions(job.getName()).size() > 0) {
                    throw new IllegalStateException("the execution hasn't stopped " +
                                    "in the expected period (timeout = "+timeout+" ms)." +
                                    "Consider increasing the timeout before checking if it's a bug.");
            }
    }
	*/
	
}
