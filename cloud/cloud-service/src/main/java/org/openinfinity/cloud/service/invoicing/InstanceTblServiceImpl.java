package org.openinfinity.cloud.service.invoicing;

import java.util.Date;
import java.util.List;

import org.openinfinity.cloud.domain.InstanceShare;
import org.openinfinity.cloud.domain.InstanceShareDetail;
import org.openinfinity.cloud.domain.InstanceTbl;
import org.openinfinity.cloud.domain.repository.invoice.InstanceShareRepository;
import org.openinfinity.cloud.domain.repository.invoice.InstanceTblRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("instanceTblService")
@Transactional
public class InstanceTblServiceImpl implements InstanceTblService{
    @Autowired
    InstanceTblRepository repository;

    public InstanceTbl findOne(Long id) {
        return repository.findOne(id);
    }

}
