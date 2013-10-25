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

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openinfinity.cloud.domain.ssp.Invoice;
import org.openinfinity.cloud.domain.ssp.InvoiceItem;
import org.openinfinity.cloud.service.ssp.InvoiceItemService;
import org.openinfinity.cloud.service.ssp.InvoiceService;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigInteger;
import java.util.Collection;

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

    final int ACCOUNT_ID = 1;
    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private InvoiceService invoiceService;

    @Autowired
    private InvoiceItemService invoiceItemService;

    @Autowired
    @Qualifier("testSspJdbcTemplate")
    private JdbcTemplate jdbcTemplate;

    @Before
    public void setup() {
    }

    @After
    public void teardown() {
        jdbcTemplate.update("delete from invoice where id > 2");
        jdbcTemplate.update("delete from invoice_item");
    }

    @Test
    public void simpleInvoiceCreationTest() throws Exception {
        Invoice invoice = invoiceService.loadLast(BigInteger.valueOf(ACCOUNT_ID));
        BigInteger invoiceId = invoice.getId();
        JobExecution jobExecution = jobLauncherTestUtils.launchJob();
        Assert.assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());
        Invoice invoiceNew = invoiceService.loadLast(BigInteger.valueOf(ACCOUNT_ID));
        Assert.assertEquals(BigInteger.valueOf(invoiceId.intValue() + 1), invoiceNew.getId());

        Collection<InvoiceItem> invoiceItems = invoiceItemService.loadAll();
        Assert.assertEquals(1, invoiceItems.size());
        for (InvoiceItem ii : invoiceItems){
            Assert.assertEquals(invoiceId.intValue() + 1, ii.getInvoiceId().intValue());
        }
    }
}

