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

package org.openinfinity.cloud.ssp.billing.payment;


import java.util.List;

import org.apache.log4j.Logger;
import org.openinfinity.cloud.domain.UsagePeriod;
import org.openinfinity.cloud.domain.ssp.Account;
import org.openinfinity.cloud.domain.ssp.Invoice;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;
import java.util.Date;
import java.sql.Timestamp;
/**
 * Batch writer.
 * 
 * @author Vedran Bartonicek
 * @version 1.3.0
 * @since 1.3.0
 */
@Component("paymentItemWriter")
public class PaymentItemWriter implements ItemWriter<InvoiceDataContainer> {
	private static final Logger LOG = Logger.getLogger(PaymentItemWriter.class.getName());

    private UsagePeriod usagePeriod;
    private Account account;

    @Override
	public void write(List<? extends InvoiceDataContainer> items) throws Exception {
	   for (InvoiceDataContainer invoiceData : items) {
			account = invoiceData.getAccount();
			usagePeriod = invoiceData.getUsagePeriod();
					LOG.debug("Writting invoice");
			Invoice invoice = new Invoice(account.getId(), new Timestamp(usagePeriod.getStartTime().getTime()), new Timestamp(usagePeriod.getEndTime().getTime()), 0);
		}
    }
}
	  
