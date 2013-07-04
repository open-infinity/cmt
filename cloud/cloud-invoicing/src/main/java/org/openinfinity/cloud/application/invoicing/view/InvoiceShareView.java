package org.openinfinity.cloud.application.invoicing.view;

import org.openinfinity.cloud.application.invoicing.view.instance.InstanceSelectionBean;
import org.openinfinity.cloud.domain.InstanceShare;

import com.vaadin.data.util.BeanItemContainer;


/**
 * View displays data and receives user interaction
 * @author kilpopas
 *
 */
public interface InvoiceShareView{
    
    public interface InvoiceShareViewListener{
        void buttonClick(final String buttonName);
        void valueChange(Object value);
    }
    
    public void addListener(InvoiceShareViewListener listener);
    
    public void setInstanceSelectionSource(BeanItemContainer<InstanceSelectionBean> container);

    public void setInstanceShareSource(BeanItemContainer<InstanceShare> container);
    
}