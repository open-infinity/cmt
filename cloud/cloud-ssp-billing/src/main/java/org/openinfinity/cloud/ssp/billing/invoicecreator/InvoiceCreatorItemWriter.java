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
import org.openinfinity.cloud.service.ssp.InvoiceItemService;
import org.openinfinity.cloud.service.ssp.InvoiceService;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

/**
 * Batch writer.
 *
 * @author Vedran Bartonicek
 * @version 1.3.0
 * @since 1.3.0
 */
@Component("invoiceCreatorItemWriter")
public class InvoiceCreatorItemWriter implements ItemWriter<InvoiceCreatorDataContainer> {
	private static final Logger LOG = Logger.getLogger(InvoiceCreatorItemWriter.class.getName());

    @Autowired
    InvoiceService invoiceService;

    @Autowired
    InvoiceItemService invoiceItemService;

    @Autowired
    MachineService machineService;

    @Autowired
    ClusterService clusterService;

    private UsagePeriod usagePeriod;

    private Account account;

    @Override
	public void write(List<? extends InvoiceCreatorDataContainer> items) throws Exception {
        for (InvoiceCreatorDataContainer invoiceData : items) {
			account = invoiceData.getAccount();
			usagePeriod = invoiceData.getUsagePeriod();
            LOG.debug("Writting invoicecreator");
			Invoice invoice = new Invoice(account.getId(),
                                          new Timestamp(usagePeriod.getStartTime().getTime()),
                                          new Timestamp(usagePeriod.getEndTime().getTime()),
                                          null,
                                          Invoice.STATE_NEW);
            invoice = invoiceService.create(invoice);
            Map<Integer, BigInteger> uptimePerMachine = usagePeriod.getUptimePerMachine();
            Assert.notNull(uptimePerMachine);
            for (Map.Entry<Integer, BigInteger> entry : uptimePerMachine.entrySet()) {
				Integer machineId = entry.getKey();
                Machine machine = machineService.getMachine(machineId);
                Cluster cluster = clusterService.getCluster(machine.getClusterId());
                invoiceItemService.create(new InvoiceItem(invoice.getId(), machineId, machine.getClusterId(), entry.getValue(), cluster.getMachineType()));
            }
        }
    }
}

