package org.openinfinity.cloud.application.invoicing.model;

import java.util.ArrayList;
import java.util.Collection;

import org.openinfinity.cloud.application.invoicing.service.InvoicingService;
import org.openinfinity.cloud.application.invoicing.utility.ApplicationContextProvider;
import org.openinfinity.cloud.application.invoicing.view.instance.InstanceSelectionBean;
import org.openinfinity.cloud.application.invoicing.view.instanceshare.InstanceShareBean;
import org.openinfinity.cloud.domain.Instance;
import org.openinfinity.cloud.domain.InstanceShare;

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

    public void setInstanceShares(Collection<InstanceShare> instanceShares) {
        this.instanceShares = instanceShares;
    }

    private InvoicingService invoicingService;

    private Long organizationId=(long) 10495;

    public InvoiceShareModel(){
        // Some sample beans
        ArrayList<InstanceSelectionBean> beans = new ArrayList<InstanceSelectionBean>();

        invoicingService = ApplicationContextProvider.getContext().getBean(InvoicingService.class);
        instances = invoicingService.getOrganizationInstances(organizationId);
     }    
}