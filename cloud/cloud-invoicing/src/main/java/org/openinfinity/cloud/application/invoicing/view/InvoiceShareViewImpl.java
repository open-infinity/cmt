package org.openinfinity.cloud.application.invoicing.view;

import java.util.ArrayList;
import java.util.Collection;

import java.util.List;

import org.openinfinity.cloud.application.invoicing.view.instance.InstanceSelectionBean;
import org.openinfinity.cloud.application.invoicing.view.instance.InstanceSelectionComponent;
import org.openinfinity.cloud.application.invoicing.view.instanceshare.InstanceShareBean;
import org.openinfinity.cloud.application.invoicing.view.instanceshare.InstanceShareComponent;
import org.openinfinity.cloud.domain.Instance;
import org.openinfinity.cloud.domain.InstanceShare;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;


public class InvoiceShareViewImpl extends CustomComponent implements InvoiceShareView, ClickListener,ValueChangeListener {

    public InstanceSelectionComponent getInstanceSelectionComponent() {
        return instanceSelectionComponent;
    }

    public InstanceShareComponent getInstanceShareComponent() {
        return instanceShareComponent;
    }

    InstanceSelectionComponent instanceSelectionComponent;
    InstanceShareComponent instanceShareComponent;

    public InvoiceShareViewImpl() {
        super();

        Panel mainPanel=new Panel();

        VerticalLayout mainLayout=new VerticalLayout();
        this.setCompositionRoot(mainPanel);
        mainPanel.setContent(mainLayout);

        instanceSelectionComponent = new InstanceSelectionComponent(this);
        mainLayout.addComponent(instanceSelectionComponent);

        instanceShareComponent = new InstanceShareComponent("Hello");
        mainLayout.addComponent(instanceShareComponent);

        // Set the size as undefined at all levels
        mainPanel.getContent().setSizeUndefined();
        mainPanel.setSizeUndefined();
        setSizeUndefined();

    }

    private static final long serialVersionUID = 1L;

    /* Only the presenter registers one listener... */
    List<InvoiceShareViewListener> listeners =
            new ArrayList<InvoiceShareViewListener>();

    public void addListener(InvoiceShareViewListener listener) {
        listeners.add(listener);
    }

    /** Relay button clicks to the presenter with an
     *  implementation-independent event */
    @Override
    public void buttonClick(ClickEvent event) {
        for (InvoiceShareViewListener listener: listeners)
            listener.buttonClick(event.getButton().getId());
    }

    public void setInstances(Collection<Instance> instances) {
        instanceSelectionComponent.setInstances(instances);

    }

    @Override
    public void valueChange(ValueChangeEvent event) {
        for (InvoiceShareViewListener listener: listeners)
            listener.valueChange(event.getProperty().getValue());

    }

    @Override
    public void setInstanceShares(Collection<InstanceShare> instanceShares) {
        instanceShareComponent.setInstanceShares(instanceShares);       
        
    }

}