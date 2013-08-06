package org.openinfinity.cloud.application.invoicing.view.instance;


import java.util.ArrayList;
import java.util.Collection;

import org.openinfinity.cloud.application.invoicing.view.InvoiceShareViewImpl;
import org.openinfinity.cloud.domain.Instance;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.VerticalLayout;

public class InstanceSelectionComponent extends CustomComponent{

    private Component buildSelectionList(InvoiceShareViewImpl view){
        selectInstance = new NativeSelect("Select an instance:");
        selectInstance.setNullSelectionAllowed(false);
        selectInstance.setImmediate(true);
        
        selectInstance.setItemCaptionMode(ItemCaptionMode.PROPERTY);
        selectInstance.setItemCaptionPropertyId("name");
        
        BeanItemContainer<InstanceSelectionBean> instanceContainer = new BeanItemContainer<InstanceSelectionBean>(InstanceSelectionBean.class);
        selectInstance.setContainerDataSource(instanceContainer);

        selectInstance.addValueChangeListener(view);
        return selectInstance;

        
    }

    public void setInstances(Collection<Instance> instances) {

        BeanItemContainer<InstanceSelectionBean> instanceContainer = new BeanItemContainer<InstanceSelectionBean>(InstanceSelectionBean.class);
        Collection<InstanceSelectionBean> beans=new ArrayList<InstanceSelectionBean>();
        for (Instance instance:instances){
            beans.add(new InstanceSelectionBean(instance));
        }

        instanceContainer.addAll(beans);

        selectInstance.setContainerDataSource(instanceContainer);
        selectInstance.sanitizeSelection();
        //selectInstance.setValue("id1");
    }

    private NativeSelect selectInstance;


    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public InstanceSelectionComponent(InvoiceShareViewImpl view){
        VerticalLayout main=new VerticalLayout();
        this.setCompositionRoot(main);

        main.addComponent(buildSelectionList(view));

    }

}