package org.openinfinity.cloud.application.invoicing.view.instance;


import org.openinfinity.cloud.application.invoicing.view.InvoiceShareView;
import org.openinfinity.cloud.application.invoicing.view.InvoiceShareViewImpl;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.VerticalLayout;

public class InstanceSelectionComponent extends CustomComponent{
    
    BeanItemContainer<InstanceSelectionBean> instanceContainer;
    AbstractSelect selectInstance;

    public BeanItemContainer<InstanceSelectionBean> getInstanceContainer() {
        return instanceContainer;
    }

    public void setInstanceContainer(
            BeanItemContainer<InstanceSelectionBean> instanceContainer) {
        this.instanceContainer = instanceContainer;
        
        selectInstance.setContainerDataSource(instanceContainer);
        selectInstance.sanitizeSelection();
        selectInstance.setValue("id1");

    }

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public InstanceSelectionComponent(InvoiceShareViewImpl invoiceShareViewImpl){
        VerticalLayout main=new VerticalLayout();
        this.setCompositionRoot(main);

        // Select using the container and "stateFullName" property as caption        //Instances drop down
        selectInstance = new NativeSelect("Select an instance:");
     
        selectInstance.setNullSelectionAllowed(false);
        selectInstance.setImmediate(true);
        
        selectInstance.addValueChangeListener(invoiceShareViewImpl);
        main.addComponent(selectInstance);

    }

}
