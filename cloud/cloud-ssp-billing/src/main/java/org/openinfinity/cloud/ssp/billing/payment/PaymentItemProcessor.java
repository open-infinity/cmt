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

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.log4j.Logger;
import org.openinfinity.cloud.domain.UsageHour;
import org.openinfinity.cloud.domain.UsagePeriod;
import org.openinfinity.cloud.domain.ssp.Account;
import org.openinfinity.cloud.domain.ssp.Invoice;
import org.openinfinity.cloud.service.administrator.InstanceService;
import org.openinfinity.cloud.service.usage.UsageService;
import org.openinfinity.core.exception.SystemException;
import org.openinfinity.core.util.ExceptionUtil;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Vedran Bartonicek
 * @version 1.3.0
 * @since 1.3.0
 */
@Component("paymentItemProcessor")
public class PaymentItemProcessor implements ItemProcessor<Account, UsagePeriod> {
	private static final Logger LOG = Logger.getLogger(PaymentItemProcessor.class.getName());

			
	@Autowired
	InstanceService instanceService;
	
	@Autowired
	UsageService usageService;
	
	@Autowired
	InvoiceService invoiceService;
		
	@Override
	public Account process(Account account) throws Exception {
		try {
			LOG.debug("-------------------------------");
			LOG.debug("Processing account id:" + account.getOrganizationId());
						
			// get startTime
			Invoice previousInvoice = invoiceService.getPreviousInvoice(account.getId());
			Date startTime = previousInvoice.getPeriodTo();

			// get endTime
			Date endTime = new Date();
						
			// get usage for period
			UsagePeriod usagePeriod = usageService.loadUsagePeriod(account.getOrganizationId().intValue(), startTime, endTime);

			// create and return InvoiceDataContainer
			InvoiceDataContainer invoiceDataContainer = new InvoiceDataContainer(usagePeriod, account);
			
			/*
			for (UsageHour usageHour : usagePeriod.getUsageHours()){
				LOG.debug("usage hour time:" + usageHour.getTimeStamp());
				LOG.debug("usage hour state:" + usageHour.getVirtualMachineState());
			}
			LOG.debug("startTime:" + startTime.getTime() + " " + startTime);
			LOG.debug("endTime:" + endTime.getTime() + " " + endTime);
			LOG.debug("uptime:" + usagePeriod.getUptime());
			LOG.debug("downtime:" + usagePeriod.getDowntime());
			*/
			
			return account;
		}
		catch(SystemException e){
		    ExceptionUtil.throwBusinessViolationException(e.getMessage(), e);
			return null;
		}			
	}
}