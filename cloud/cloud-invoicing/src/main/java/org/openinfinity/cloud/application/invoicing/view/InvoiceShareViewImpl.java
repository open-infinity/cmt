package org.openinfinity.cloud.application.invoicing.view;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.openinfinity.cloud.application.invoicing.view.instance.InstanceSelectionBean;
import org.openinfinity.cloud.application.invoicing.view.instance.InstanceSelectionComponent;
import org.openinfinity.cloud.application.invoicing.view.instanceshare.InstanceShareBean;
import org.openinfinity.cloud.application.invoicing.view.instanceshare.InstanceShareComponent;
import org.openinfinity.cloud.application.invoicing.view.instanceshare.InstanceShareDetailBean;
import org.openinfinity.cloud.domain.Instance;
import org.openinfinity.cloud.domain.InstanceShare;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.BeanItem;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;


public class InvoiceShareViewImpl extends CustomComponent implements InvoiceShareView, ClickListener,ValueChangeListener,ItemClickListener {

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

        instanceShareComponent = new InstanceShareComponent(this);
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
    @SuppressWarnings("unchecked")
    @Override
    public void buttonClick(ClickEvent event) {
        Object item=null;
        if ("Save".equals(event.getButton().getCaption())){

            //Saving of InstanceShare; get InstanceShareBean from form data
            if (event.getButton().equals(instanceShareComponent.getSaveInstanceShareBtn())){
                item= ((BeanItem<InstanceShareBean>)instanceShareComponent.getInstanceShareItemDataSource()).getBean();                
            }else{
                item= ((BeanItem<InstanceShareDetailBean>)instanceShareComponent.getInstanceShareDetailItemDataSource()).getBean();
            }
        }
        if ("Delete".equals(event.getButton().getCaption())){
            if (event.getButton().equals(instanceShareComponent.getDeleteInstanceShareBtn())){
                item=instanceShareComponent.getSharesTable().getValue();
            }else{
                item=instanceShareComponent.getShareDetailsTable().getValue();
            }
        }

        for (InvoiceShareViewListener listener: listeners)
            listener.buttonClick(event.getButton().getCaption(),item);
    }

    public void setInstances(Collection<Instance> instances) {
        instanceSelectionComponent.setInstances(instances);

    }

    @Override
    public void valueChange(ValueChangeEvent event) {
        for (InvoiceShareViewListener listener: listeners)
            if (event.getProperty().getValue() instanceof InstanceSelectionBean){
                listener.instanceSelected((InstanceSelectionBean) event.getProperty().getValue());
            }else{
                throw new IllegalArgumentException("Not Implemented");
            }

    }


    @Override
    public void itemClick(ItemClickEvent event) {
        for (InvoiceShareViewListener listener: listeners){
            if (event.getItemId() instanceof InstanceShareBean){
                listener.instanceShareSelected((InstanceShareBean)event.getItemId());
            }else if (event.getItemId() instanceof InstanceShareDetailBean) {
                listener.instanceShareDetailSelected((InstanceShareDetailBean)event.getItemId());               
            }
        }

    }

    @Override
    public void setInstanceShares(Collection<InstanceShare> instanceShares) {
        instanceShareComponent.setInstanceShares(instanceShares);       

    }
    @Override
    public void setInstanceShareDetails(InstanceShare item) {
        instanceShareComponent.setInstanceShareDetails(item);     

    }

    @Override
    public void editInstanceShare(InstanceShareBean instanceShare){
        instanceShareComponent.editInstanceShare(instanceShare);
    }

    @Override
    public void editInstanceShareDetail(InstanceShareDetailBean instanceShareDetail){
        instanceShareComponent.editInstanceShareDetail(instanceShareDetail);
    }

}