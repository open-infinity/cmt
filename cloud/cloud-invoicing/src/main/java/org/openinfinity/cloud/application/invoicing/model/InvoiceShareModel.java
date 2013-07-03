package org.openinfinity.cloud.application.invoicing.model;

import java.util.ArrayList;

import org.openinfinity.cloud.application.invoicing.service.component.InstanceSelectionBean;

import com.vaadin.data.util.BeanItemContainer;

/**
 * Business Model for invoicing functionality
 * @author kilpopas
 *
 */
public class InvoiceShareModel{
    
    private BeanItemContainer<InstanceSelectionBean> instanceContainer=null;
    
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
        beans.add(new InstanceSelectionBean("id1", "1", "First")); // id, abbreviation, stateFullName
        beans.add(new InstanceSelectionBean("id2", "2", "Second"));
        beans.add(new InstanceSelectionBean("id3", "3", "Third"));

        // Create a Collection container using id property as the key
        instanceContainer = new BeanItemContainer<InstanceSelectionBean>(InstanceSelectionBean.class);
        instanceContainer.addAll(beans);

    }
    
}