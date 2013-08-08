package org.openinfinity.cloud.application.invoicing.view;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.openinfinity.cloud.application.invoicing.InvoicingUI;
import org.openinfinity.cloud.application.invoicing.view.instance.InstanceSelectionBean;
import org.openinfinity.cloud.application.invoicing.view.instance.InstanceSelectionComponent;
import org.openinfinity.cloud.application.invoicing.view.instanceshare.InstanceShareBean;
import org.openinfinity.cloud.application.invoicing.view.instanceshare.InstanceShareComponent;
import org.openinfinity.cloud.application.invoicing.view.instanceshare.InstanceShareDetailBean;
import org.openinfinity.cloud.domain.Instance;
import org.openinfinity.cloud.domain.InstanceShare;
import org.vaadin.dialogs.ConfirmDialog;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.util.BeanItem;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
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
    private InvoicingUI ui;
    private Button saveFormBtn;
    private Button cancelFormBtn;

    public InvoiceShareViewImpl(InvoicingUI invoicingUI) {
        super();
        this.ui=invoicingUI;

        Panel mainPanel=new Panel();

        VerticalLayout mainLayout=new VerticalLayout();
        this.setCompositionRoot(mainPanel);
        mainPanel.setContent(mainLayout);

        instanceSelectionComponent = new InstanceSelectionComponent(this);
        mainLayout.addComponent(instanceSelectionComponent);

        instanceShareComponent = new InstanceShareComponent(this);
        mainLayout.addComponent(instanceShareComponent);
        
        mainLayout.addComponent(buildFormControls());

        // Set the size as undefined at all levels
        mainPanel.getContent().setSizeUndefined();
        mainPanel.setSizeUndefined();
        setSizeUndefined();

    }

    private Component buildFormControls() {
        HorizontalLayout controls=new HorizontalLayout();
        
        saveFormBtn = new Button("Submit");
        saveFormBtn.addClickListener(this);
        controls.addComponent(saveFormBtn);
        
        cancelFormBtn = new Button("Cancel",new ClickListener(){

            @Override
            public void buttonClick(ClickEvent event) {
                ConfirmDialog.show(ui, "Do you want to cancel all changes", "Press Yes to continue, or No to get back editing", "Yes", "No", new ConfirmDialog.Listener() {

                    public void onClose(ConfirmDialog dialog) {
                        if (dialog.isConfirmed()) {
                            for (InvoiceShareViewListener listener: listeners){
                                listener.cancelChanges();
                            }
                        } else {
                        }
                    }
                });
     
            }
            
        });
        cancelFormBtn.addClickListener(this);
        controls.addComponent(cancelFormBtn);
        // TODO Auto-generated method stub
        return controls;
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
        
        boolean valid=true;
        if ("OK".equals(event.getButton().getCaption())){

            //Saving of InstanceShare; get InstanceShareBean from form data
            if (event.getButton().equals(instanceShareComponent.getSaveInstanceShareBtn())){
                //TODO: now doing extra commit; which is already done from InstanceShareComponent side to handle validations
               try {
                   instanceShareComponent.getInstanceShareFieldGroup().commit();
               } catch (CommitException e) {
                   valid=false;
               }

                item= ((BeanItem<InstanceShareBean>)instanceShareComponent.getInstanceShareItemDataSource()).getBean();                
            }else{
                 //TODO: now doing extra commit; which is already done from InstanceShareComponent side to handle validations
                try {
                    instanceShareComponent.getInstanceShareDetailFieldGroup().commit();
                } catch (CommitException e) {
                    valid=false;
                }
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

        if (valid){//call listeners if valid
            for (InvoiceShareViewListener listener: listeners)
                listener.buttonClick(event.getButton().getCaption(),item);
        }
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
                listener.instanceShareSelected((InstanceShareBean)event.getItemId(),false);
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
        
        //make sure that this instanceShare is selected
        //instanceShareComponent.getSharesTable().select(instanceShare);

    }

    @Override
    public void editInstanceShareDetail(InstanceShareDetailBean instanceShareDetail){
        instanceShareComponent.editInstanceShareDetail(instanceShareDetail);
    }

    @Override
    public void showConfirmDialogForShareSelection(final Object item, final Object previousItem, String message1, String message2, String okCaption, String cancelCaption) {
        ConfirmDialog.show(ui, message1, message2, okCaption, cancelCaption, new ConfirmDialog.Listener() {

                    public void onClose(ConfirmDialog dialog) {
                        if (dialog.isConfirmed()) {
                            for (InvoiceShareViewListener listener: listeners){
                                listener.instanceShareSelected((InstanceShareBean)item,true);
                            }
                            // Confirmed to continue
                        } else {
                            for (InvoiceShareViewListener listener: listeners){
                                listener.instanceShareSelected((InstanceShareBean)previousItem,true);
                            }
                        }
                    }
                });
    }
    
    @Override
    public void showConfirmDialog(String message1, String message2, String okCaption, String cancelCaption) {
        ConfirmDialog.show(ui, message1, message2, okCaption, cancelCaption, new ConfirmDialog.Listener() {

                    public void onClose(ConfirmDialog dialog) {
                        if (dialog.isConfirmed()) {
                        } else {
                        }
                    }
                });
    }

}