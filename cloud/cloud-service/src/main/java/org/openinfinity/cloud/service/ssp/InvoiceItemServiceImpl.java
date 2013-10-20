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
package org.openinfinity.cloud.service.ssp;

import org.openinfinity.cloud.domain.repository.ssp.InvoiceItemRepository;
import org.openinfinity.cloud.domain.ssp.InvoiceItem;
import org.openinfinity.core.annotation.AuditTrail;
import org.openinfinity.core.annotation.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.Collection;

/**
 * InvoiceItem service implementation.
 * 
 * @author Vedran Bartonicek
 */
@Service
public class InvoiceItemServiceImpl implements InvoiceItemService {

	@Autowired
	InvoiceItemRepository invoiceItemRepository;
	
	@Override
	@Log
	@AuditTrail
	public InvoiceItem create(InvoiceItem invoiceItem) {
		return invoiceItemRepository.create(invoiceItem);
	}

	@Override
	@Log
	@AuditTrail
	public void update(InvoiceItem invoiceItem) {
		invoiceItemRepository.update(invoiceItem);		
	}

	@Override
	@Log
	@AuditTrail
	public Collection<InvoiceItem> loadAll() {
		return invoiceItemRepository.loadAll();
	}

	@Override
	@Log
	@AuditTrail
	public InvoiceItem load(BigInteger id) {
		return invoiceItemRepository.load(id);
	}

    @Override
    @Log
    @AuditTrail
    public void delete(InvoiceItem invoiceItem) {
        // TODO Auto-generated method stub

    }

}
