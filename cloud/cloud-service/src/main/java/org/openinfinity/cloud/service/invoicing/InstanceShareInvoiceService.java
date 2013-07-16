package org.openinfinity.cloud.service.invoicing;

import java.util.List;

import org.openinfinity.cloud.domain.InstanceShareInvoice;

public interface InstanceShareInvoiceService {
	List<InstanceShareInvoice> findByInstanceShareId(long instanceId);

	InstanceShareInvoice save(InstanceShareInvoice instanceShareInvoice);
	
	List<InstanceShareInvoice> save(List<InstanceShareInvoice> instanceShareInvoices);
}
