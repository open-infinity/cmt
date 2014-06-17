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

package org.openinfinity.cloud.ssp.billing.accountauthorizer;


import org.apache.log4j.Logger;
import org.openinfinity.cloud.domain.ssp.Invoice;
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
@Component("notificationItemWriter")
public class InvoiceManagerItemWriter implements ItemWriter<Invoice> {
	private static final Logger LOG = Logger.getLogger(InvoiceManagerItemWriter.class.getName());

    @Autowired
    InvoiceService invoiceService;

    @Override
	public void write(List<? extends Invoice> items) throws Exception {
        for (Invoice item : items){
            LOG.debug("writing item:" + item.getId());
            invoiceService.update(item);
        }

    }
}
	  
