package org.openinfinity.cloud.application.invoicing.service;

import java.util.Collection;

import org.openinfinity.cloud.domain.Instance;
import org.openinfinity.cloud.service.administrator.ClusterService;
import org.openinfinity.cloud.service.administrator.InstanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component("invoicingService")
public class InvoicingService {
   
    @Autowired
    @Qualifier("instanceService")
    private InstanceService instanceService;

    
    public Collection<Instance> getOrganizationInstances(Long organizationId) {
        return instanceService.getOrganizationInstances(organizationId);
    }

    public InstanceService getInstanceService() {
        return instanceService;
    }

    public void setInstanceService(InstanceService instanceService) {
        this.instanceService = instanceService;
    }
    
    

}
