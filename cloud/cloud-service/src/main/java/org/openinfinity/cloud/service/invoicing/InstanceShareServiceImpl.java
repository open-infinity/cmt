package org.openinfinity.cloud.service.invoicing;

import java.util.List;

import org.openinfinity.cloud.domain.InstanceShare;
import org.openinfinity.cloud.domain.repository.invoice.InstanceShareRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service("instanceShareService")
public class InstanceShareServiceImpl implements InstanceShareService{
    @Autowired
    InstanceShareRepository repository;

    /* (non-Javadoc)
     * @see org.openinfinity.cloud.service.invoicing.InstanceShareService2#delete(org.openinfinity.cloud.domain.InstanceShare)
     */
    @Override
    public void delete(InstanceShare arg0) {
        repository.delete(arg0);
    }

    /* (non-Javadoc)
     * @see org.openinfinity.cloud.service.invoicing.InstanceShareService2#delete(java.lang.Long)
     */
    @Override
    public void delete(Long arg0) {
        repository.delete(arg0);
    }

    /* (non-Javadoc)
     * @see org.openinfinity.cloud.service.invoicing.InstanceShareService2#findAll(org.springframework.data.domain.Pageable)
     */
    @Override
    public Page<InstanceShare> findAll(Pageable arg0) {
        return repository.findAll(arg0);
    }

    /* (non-Javadoc)
     * @see org.openinfinity.cloud.service.invoicing.InstanceShareService2#findByInstanceId(long)
     */
    @Override
    public List<InstanceShare> findByInstanceId(long instanceId) {
        return repository.findByInstanceId(instanceId);
    }

    /* (non-Javadoc)
     * @see org.openinfinity.cloud.service.invoicing.InstanceShareService2#findOne(java.lang.Long)
     */
    @Override
    public InstanceShare findOne(Long arg0) {
        return repository.findOne(arg0);
    }

    /* (non-Javadoc)
     * @see org.openinfinity.cloud.service.invoicing.InstanceShareService2#save(S)
     */
    @Override
    public <S extends InstanceShare> S save(S arg0) {
        return repository.save(arg0);
    }

}
