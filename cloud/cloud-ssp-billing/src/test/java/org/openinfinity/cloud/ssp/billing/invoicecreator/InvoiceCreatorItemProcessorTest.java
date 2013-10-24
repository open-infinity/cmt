/*
 * Copyright (c) 2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.openinfinity.cloud.ssp.billing.invoicecreator;

import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * integration tests for SSP billing Invoice creator.
 *
 * @author Vedran Bartonicek
 * @version 1.3.0
 * @since 1.3.0
 */
//, classpath*:META-INF/spring/cloud-ssp-invoice-context.xml"
@ContextConfiguration(locations={"classpath*:META-INF/spring/invoicecreator-integration-test-context.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class InvoiceCreatorItemProcessorTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;


    @Before
    public void setup() {
    }

    @After
    public void teardown() {
    }

    @Test
    @Ignore
    public void simpleItemWriterTest() throws Exception {


        JobExecution jobExecution = jobLauncherTestUtils.launchJob();
        Assert.assertEquals(jobExecution.getStatus(), BatchStatus.COMPLETED);
       //ScalingRule scalingRule = scalingRuleService.getRule(CLUSTER_ID);
       // Assert.assertEquals(2, scalingRule.getClusterSizeOriginal());
       // Assert.assertEquals(2, scalingRule.getScheduledScalingState());
       // Assert.assertEquals("1,5", jobService.getJob(getJobId()).getServices());

    }

}

