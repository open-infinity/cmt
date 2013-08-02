package org.openinfinity.cloud.service.invoicing;

import java.util.List;

import org.openinfinity.cloud.domain.InstanceShareDetail;

public interface InstanceShareDetailService {
    public InstanceShareDetail save(InstanceShareDetail entity);
    public List<InstanceShareDetail> findByInstanceShareId(long instanceId);
    public InstanceShareDetail findOne(Long id);
    public void delete(Long id);
    public void delete(InstanceShareDetail entity);
}
