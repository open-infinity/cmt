/*
* Copyright (c) 2013 the original author or authors.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.openinfinity.cloud.service.ssp;

import org.openinfinity.cloud.domain.repository.ssp.InvoiceRepository;
import org.openinfinity.cloud.domain.ssp.Invoice;
import org.openinfinity.core.annotation.AuditTrail;
import org.openinfinity.core.annotation.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.Collection;

/**
* Invoice service implementation.
* 
* @author Vedran Bartonicek
*/
@Service
public class InvoiceServiceImpl implements InvoiceService {

    @Autowired
    InvoiceRepository invoiceRepository;
      
    @Override
    @Log
    @AuditTrail
    public Invoice create(Invoice invoice) {
        return invoiceRepository.create(invoice);
    }

    @Override
    @Log
    @AuditTrail
    public void update(Invoice invoice) {
        invoiceRepository.update(invoice);
    }

    @Override
    @Log
    @AuditTrail
    public Collection<Invoice> loadAll() {
        return invoiceRepository.loadAll();
    }

    @Override
    @Log
    @AuditTrail
    public Invoice load(BigInteger id) {
        return invoiceRepository.load(id);
    }

    @Override
    @Log
    @AuditTrail
    public void delete(Invoice invoice) {
        // TODO Autogenerated method stub

    }

    @Override
    @Log
    @AuditTrail
    public Invoice loadLast(BigInteger accountId) {
         return invoiceRepository.loadLast(accountId);
    }
}