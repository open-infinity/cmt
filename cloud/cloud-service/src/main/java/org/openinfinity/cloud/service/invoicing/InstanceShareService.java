package org.openinfinity.cloud.service.invoicing;

import java.util.List;

import org.openinfinity.cloud.domain.InstanceShare;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface InstanceShareService {

    public abstract void delete(InstanceShare arg0);

    public abstract void delete(Long arg0);

    public abstract Page<InstanceShare> findAll(Pageable arg0);

    public abstract List<InstanceShare> findByInstanceId(long instanceId);

    public abstract InstanceShare findOne(Long arg0);

    public abstract <S extends InstanceShare> S save(S arg0);

}