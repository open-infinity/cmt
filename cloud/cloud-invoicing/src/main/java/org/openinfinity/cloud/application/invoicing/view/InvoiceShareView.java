package org.openinfinity.cloud.application.invoicing.view;

import java.util.Collection;

import org.openinfinity.cloud.application.invoicing.view.instance.InstanceSelectionBean;
import org.openinfinity.cloud.application.invoicing.view.instanceshare.InstanceShareBean;
import org.openinfinity.cloud.domain.Instance;
import org.openinfinity.cloud.domain.InstanceShare;
import org.openinfinity.cloud.domain.InstanceShareDetail;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.ItemClickEvent;


/**
 * View displays data and receives user interaction
 * @author kilpopas
 *
 */
public interface InvoiceShareView{
    
    public interface InvoiceShareViewListener{
        public void buttonClick(final String buttonName);
        public void instanceSelected(InstanceSelectionBean value);
        public void instanceShareSelected(Object value);
    }
    
    public void addListener(InvoiceShareViewListener listener);
    
    public void setInstances(Collection<Instance> instances);

    public void setInstanceShares(Collection<InstanceShare> instanceShares);
    
    public void setInstanceShareDetails(Collection<InstanceShareDetail> instanceShareDetails);
    
}