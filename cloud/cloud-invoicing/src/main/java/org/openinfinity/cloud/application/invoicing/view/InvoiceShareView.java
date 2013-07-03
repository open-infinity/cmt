package org.openinfinity.cloud.application.invoicing.view;

import org.openinfinity.cloud.application.invoicing.service.component.InstanceSelectionBean;

import com.vaadin.data.util.BeanItemContainer;


/**
 * View displays data and receives user interaction
 * @author kilpopas
 *
 */
public interface InvoiceShareView{
    
    public interface InvoiceShareViewListener{
        void buttonClick(String buttonName);
    }
    
    public void addListener(InvoiceShareViewListener listener);
    
    public void setInstanceSelectionSource(BeanItemContainer<InstanceSelectionBean> container);
    
}