package org.openinfinity.cloud.service.invoicing;

import java.util.List;

import org.openinfinity.cloud.domain.InstanceShareInvoice;
import org.openinfinity.cloud.domain.repository.invoice.InstanceShareInvoiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("instanceShareInvoiceService")
@Transactional
public class InstanceShareInvoiceServiceImpl implements
		InstanceShareInvoiceService {

	@Autowired
	private InstanceShareInvoiceRepository repository;
	
	@Override
	public List<InstanceShareInvoice> findByInstanceShareId(long instanceId) {
		return repository.findByInstanceShareId(instanceId);
	}

	@Override
	public InstanceShareInvoice save(InstanceShareInvoice instanceShareInvoice) {
		return repository.save(instanceShareInvoice);
	}
	
	@Override
	public List<InstanceShareInvoice> save(List<InstanceShareInvoice> instanceShareInvoices) {
		return repository.save(instanceShareInvoices);
	}

}
