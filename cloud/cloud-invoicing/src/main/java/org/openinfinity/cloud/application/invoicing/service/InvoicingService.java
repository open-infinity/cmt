package org.openinfinity.cloud.application.invoicing.service;

import org.openinfinity.cloud.service.administrator.ClusterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component("invoicingService")
public class InvoicingService {
    
    @Autowired
    @Qualifier("clusterService")
    private ClusterService clusterService;

}
