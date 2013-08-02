package org.openinfinity.cloud.service.invoicing;

import java.util.List;

import org.openinfinity.cloud.domain.InstanceShareDetail;
import org.openinfinity.cloud.domain.InstanceShareInvoice;
import org.openinfinity.cloud.domain.repository.invoice.InstanceShareDetailRepository;
import org.openinfinity.cloud.domain.repository.invoice.InstanceShareInvoiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("instanceShareDetailService")
@Transactional
public class InstanceShareDetailServiceImpl implements
		InstanceShareDetailService {

    public InstanceShareDetail save(InstanceShareDetail entity) {
        return repository.save(entity);
    }

    public List<InstanceShareDetail> findByInstanceShareId(long instanceId) {
        return repository.findByInstanceShareId(instanceId);
    }

    public InstanceShareDetail findOne(Long id) {
        return repository.findOne(id);
    }

    public void delete(Long id) {
        repository.delete(id);
    }

    public void delete(InstanceShareDetail entity) {
        
        InstanceShareDetail findOne = repository.findOne(entity.getId());
        repository.delete(findOne);
    }

    @Autowired
	private InstanceShareDetailRepository repository;

}
