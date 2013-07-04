package org.openinfinity.cloud.service.invoicing;

import java.util.List;

import org.openinfinity.cloud.domain.InstanceShare;
import org.openinfinity.cloud.domain.repository.invoice.InstanceShareRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class InstanceShareServiceImpl implements InstanceShareService{
    @Autowired
    InstanceShareRepository repository;

    public void delete(InstanceShare arg0) {
        repository.delete(arg0);
    }

    public void delete(Long arg0) {
        repository.delete(arg0);
    }

    public Page<InstanceShare> findAll(Pageable arg0) {
        return repository.findAll(arg0);
    }

    public List<InstanceShare> findByInstanceId(long instanceId) {
        return repository.findByInstanceId(instanceId);
    }

    public InstanceShare findOne(Long arg0) {
        return repository.findOne(arg0);
    }

    public <S extends InstanceShare> S save(S arg0) {
        return repository.save(arg0);
    }

}
