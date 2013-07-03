package org.openinfinity.cloud.application.invoicing.model;

import java.util.ArrayList;
import java.util.Collection;

import org.openinfinity.cloud.application.invoicing.service.InvoicingService;
import org.openinfinity.cloud.application.invoicing.service.component.InstanceSelectionBean;
import org.openinfinity.cloud.application.invoicing.utility.ApplicationContextProvider;
import org.openinfinity.cloud.domain.Instance;

import com.vaadin.data.util.BeanItemContainer;

/**
 * Business Model for invoicing functionality
 * @author kilpopas
 *
 */
public class InvoiceShareModel{
    
    private BeanItemContainer<InstanceSelectionBean> instanceContainer=null;
    
    private InvoicingService invoicingService;

    private Long organizationId=(long) 10495;
    
    public BeanItemContainer<InstanceSelectionBean> getInstanceContainer() {
        return instanceContainer;
    }

    public void setInstanceContainer(
            BeanItemContainer<InstanceSelectionBean> instanceContainer) {
        this.instanceContainer = instanceContainer;
    }

    public InvoiceShareModel(){
        // Some sample beans
        ArrayList<InstanceSelectionBean> beans = new ArrayList<InstanceSelectionBean>();

        invoicingService = ApplicationContextProvider.getContext().getBean(InvoicingService.class);
        Collection<Instance> organizationInstances = invoicingService.getOrganizationInstances(organizationId);
        for (Instance instance:organizationInstances){
            beans.add(new InstanceSelectionBean(instance));
        }

        // Create a Collection container using id property as the key
        instanceContainer = new BeanItemContainer<InstanceSelectionBean>(InstanceSelectionBean.class);
        instanceContainer.addAll(beans);

    }
    
}