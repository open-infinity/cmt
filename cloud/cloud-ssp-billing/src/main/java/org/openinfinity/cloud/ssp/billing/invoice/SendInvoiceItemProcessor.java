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

package org.openinfinity.cloud.ssp.billing.invoice;

import org.apache.log4j.Logger;
import org.openinfinity.cloud.domain.ssp.Invoice;
import org.openinfinity.core.exception.SystemException;
import org.openinfinity.core.util.ExceptionUtil;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

/**
 * @author Vedran Bartonicek
 * @version 1.3.0
 * @since 1.3.0
 */
@Component("sendInvoiceItemProcessor")
public class SendInvoiceItemProcessor implements ItemProcessor<Invoice, Invoice> {
	private static final Logger LOG = Logger.getLogger(SendInvoiceItemProcessor.class.getName());


	@Override
	public Invoice process(Invoice invoice) throws Exception {
		try {
			LOG.debug("SendInvoiceItemProcessor, processing invoice id:" + invoice.getId());
            invoice.setState(1);
            return invoice;
		}
		catch(SystemException e){
		    ExceptionUtil.throwBusinessViolationException(e.getMessage(), e);
			return null;
		}			
	}
}