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

import org.apache.log4j.Logger;
import org.openinfinity.cloud.domain.Cluster;
import org.openinfinity.cloud.domain.Machine;
import org.openinfinity.cloud.domain.UsagePeriod;
import org.openinfinity.cloud.domain.ssp.Account;
import org.openinfinity.cloud.domain.ssp.Invoice;
import org.openinfinity.cloud.domain.ssp.InvoiceItem;
import org.openinfinity.cloud.service.administrator.ClusterService;
import org.openinfinity.cloud.service.administrator.MachineService;
import org.openinfinity.cloud.service.ssp.InvoiceService;
import org.openinfinity.cloud.service.usage.UsageService;
import org.openinfinity.core.exception.SystemException;
import org.openinfinity.core.util.ExceptionUtil;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;

/**
 * @author Vedran Bartonicek
 * @version 1.3.0
 * @since 1.3.0
 */
@Component("invoiceCreatorItemProcessor")
public class InvoiceCreatorItemProcessor implements ItemProcessor<Account, InvoiceAggregator> {
	private static final Logger LOG = Logger.getLogger(InvoiceCreatorItemProcessor.class.getName());

	@Autowired
	UsageService usageService;

	@Autowired
	InvoiceService invoiceService;

    @Autowired
    MachineService machineService;

    @Autowired
    ClusterService clusterService;

	@Override
	public InvoiceAggregator process(Account account) throws Exception {
		try {
			LOG.debug("InvoiceCreatorItemProcessor::process() ENTER, account id:" + account.getOrganizationId());

            HashSet<InvoiceItem> invoiceItems = new HashSet<InvoiceItem>();
			Invoice lastInvoice = invoiceService.loadLast(account.getId());
            UsagePeriod usagePeriod = usageService.loadUsagePeriodPerMachine(account.getOrganizationId().intValue(), lastInvoice.getPeriodTo(), new Date());

            LOG.debug("accountId:" + account.getId());
            LOG.debug("usagePeriod start:" + usagePeriod.getStartTime());
            LOG.debug("usagePeriod end:" + usagePeriod.getEndTime());

            Invoice invoice = new Invoice(account.getId(),
                    new Timestamp(usagePeriod.getStartTime().getTime()),
                    new Timestamp(usagePeriod.getEndTime().getTime()),
                    null,
                    Invoice.STATE_NEW);

            Map<Integer, Long> uptimePerMachine = usagePeriod.getUptimePerMachine();
            Assert.notNull(uptimePerMachine);
            for (Map.Entry<Integer, Long> entry : uptimePerMachine.entrySet()) {
                Integer machineId = entry.getKey();
                Machine machine = machineService.getMachine(machineId);
                Cluster cluster = clusterService.getCluster(machine.getClusterId());
                invoiceItems.add(new InvoiceItem(invoice.getId(), machineId, machine.getClusterId(), entry.getValue(), cluster.getMachineType()));
            }
            return new InvoiceAggregator(invoice, invoiceItems);
        }
		catch(SystemException e){
		    ExceptionUtil.throwBusinessViolationException(e.getMessage(), e);
			return null;
		}			
	}
}