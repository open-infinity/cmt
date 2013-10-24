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
import org.openinfinity.cloud.domain.ssp.Invoice;
import org.openinfinity.cloud.domain.ssp.InvoiceItem;
import org.openinfinity.cloud.service.ssp.InvoiceItemService;
import org.openinfinity.cloud.service.ssp.InvoiceService;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Batch writer.
 *
 * @author Vedran Bartonicek
 * @version 1.3.0
 * @since 1.3.0
 */
@Component("invoiceCreatorItemWriter")
public class InvoiceCreatorItemWriter implements ItemWriter<InvoiceAggregator> {
	private static final Logger LOG = Logger.getLogger(InvoiceCreatorItemWriter.class.getName());

    @Autowired
    InvoiceService invoiceService;

    @Autowired
    InvoiceItemService invoiceItemService;

    @Override
	public void write(List<? extends InvoiceAggregator> items) throws Exception {
        for (InvoiceAggregator invoiceAggregator : items) {
            LOG.debug("write ENTER");
            Invoice invoice = invoiceService.create(invoiceAggregator.getInvoice());
            for (InvoiceItem invoiceItem : invoiceAggregator.getInvoiceItems()){
                invoiceItem.setInvoiceId(invoice.getId());
                invoiceItemService.create(invoiceItem);
            }
        }
        LOG.debug("write EXIT");
    }
}

