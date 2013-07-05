package org.openinfinity.cloud.application.invoicing.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.openinfinity.cloud.application.invoicing.service.InvoicingService;
import org.openinfinity.cloud.application.invoicing.utility.ApplicationContextProvider;
import org.openinfinity.cloud.application.invoicing.view.instance.InstanceSelectionBean;
import org.openinfinity.cloud.application.invoicing.view.instanceshare.InstanceShareBean;
import org.openinfinity.cloud.domain.Instance;
import org.openinfinity.cloud.domain.InstanceShare;
import org.openinfinity.cloud.domain.InstanceShareDetail;

import com.vaadin.data.util.BeanItemContainer;

/**
 * Business Model for invoicing functionality
 * @author kilpopas
 *
 */
public class InvoiceShareModel{

    private Collection<Instance> instances=null;
    private Collection<InstanceShare> instanceShares;
    
    public Collection<Instance> getInstances() {
        return instances;
    }

    public void setInstances(Collection<Instance> instances) {
        this.instances = instances;
    }

    public Collection<InstanceShare> getInstanceShares(long instanceId) {
        instanceShares = invoicingService.getInstanceShareService().findByInstanceId(instanceId);
        return instanceShares;
    }
    
    public Collection<InstanceShareDetail> getInstanceShareDetails(long instanceShareId) {
        InstanceShare share=invoicingService.getInstanceShareService().findOne(instanceShareId);
        return (share==null ? Collections.<InstanceShareDetail>emptyList():share.getInstanceShareDetails());
        
    }

    public void setInstanceShares(Collection<InstanceShare> instanceShares) {
        this.instanceShares = instanceShares;
    }

    private InvoicingService invoicingService;

    private Long organizationId=(long) 10495;

    public InvoiceShareModel(){
        //Initialize instances list for selection
        invoicingService = ApplicationContextProvider.getContext().getBean(InvoicingService.class);
        instances = invoicingService.getOrganizationInstances(organizationId);
     }    
}