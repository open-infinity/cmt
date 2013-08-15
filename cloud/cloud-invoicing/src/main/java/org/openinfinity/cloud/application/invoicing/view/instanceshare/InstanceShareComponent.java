package org.openinfinity.cloud.application.invoicing.view.instanceshare;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.openinfinity.cloud.application.invoicing.view.InvoiceShareViewImpl;
import org.openinfinity.cloud.domain.InstanceShare;
import org.openinfinity.cloud.domain.InstanceShareDetail;

import com.vaadin.data.Item;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.server.UserError;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.DateField;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

public class InstanceShareComponent extends CustomComponent{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /* User interface components are stored in session. */
    private Table sharesTable;
    private FieldGroup instanceShareFieldGroup=new FieldGroup();
    public FieldGroup getInstanceShareFieldGroup() {
        return instanceShareFieldGroup;
    }

    private FieldGroup instanceShareDetailFieldGroup=new FieldGroup();

    public FieldGroup getInstanceShareDetailFieldGroup() {
        return instanceShareDetailFieldGroup;
    }

    private Table shareDetailsTable;
    private Collection<InstanceShareDetailBean> removedShareDetails;

    private long newShareDetailId=-1;
    private long newShareId=-1;

    private GridLayout shareForm;
    private GridLayout shareDetailForm;

    private HorizontalLayout instanceShareFormControls;
    private HorizontalLayout instanceShareDetailFormControls;

    private HorizontalLayout instanceShareTableControls;
    private HorizontalLayout instanceShareDetailTableControls;

    private Button saveInstanceShareBtn;


    public Table getSharesTable() {
        return sharesTable;
    }

    public Table getShareDetailsTable() {
        return shareDetailsTable;
    }

    public void setInstanceShares(Collection<InstanceShare> instanceShares) {

        sharesTable.removeAllItems();

        BeanItemContainer<InstanceShareBean> beans=new BeanItemContainer<InstanceShareBean>(InstanceShareBean.class);
        for (InstanceShare item:instanceShares){
            beans.addBean(new InstanceShareBean(item));
        }

        sharesTable.setContainerDataSource(beans);
        sharesTable.setColumnHeader("periodStart", "Period");
        sharesTable.setVisibleColumns(new Object[]{"periodStart"});
        
        //Enable add button
        setControlsEnabled(new Component[]{addInstanceShareBtn},true);


    }

    public void setInstanceShareDetails(InstanceShare item){
        
        //remove items from view
        shareDetailsTable.removeAllItems();
        //remove also items from list that contains removed items
        removedShareDetails=null;

        Collection<InstanceShareDetail> details= (item==null ? Collections.<InstanceShareDetail>emptyList() : item.getInstanceShareDetails() );

        BeanItemContainer<InstanceShareDetailBean> beans=new BeanItemContainer<InstanceShareDetailBean>(InstanceShareDetailBean.class);

        for (InstanceShareDetail detail:details){
            beans.addBean(new InstanceShareDetailBean(detail));
        }

        shareDetailsTable.setContainerDataSource(beans);
        shareDetailsTable.setColumnHeader("orderNumber", "Order No");
        shareDetailsTable.setColumnHeader("costPool", "Cost Pool");
        shareDetailsTable.setColumnHeader("description", "Description");
        shareDetailsTable.setColumnHeader("sharePercent", "Share %");
        shareDetailsTable.setVisibleColumns(new Object[]{"orderNumber","costPool","sharePercent","description"});

        if (item!=null && item.getInstanceShareInvoices()!=null && item.getInstanceShareInvoices().size()>0){
            //if there is invoices for share then disable buttons so that user cannot make changes to this share
            setControlsEnabled(new Component[]{deleteInstanceShareBtn, saveInstanceShareBtn,addInstanceShareDetailBtn, deleteInstanceShareDetailBtn,saveInstanceShareDetailBtn, this.invoiceShareViewImpl.getSaveFormBtn(), this.invoiceShareViewImpl.getCancelFormBtn()},false);
        }else{
            setControlsEnabled(new Component[]{deleteInstanceShareBtn, saveInstanceShareBtn,addInstanceShareDetailBtn, deleteInstanceShareDetailBtn,saveInstanceShareDetailBtn,this.invoiceShareViewImpl.getSaveFormBtn(), this.invoiceShareViewImpl.getCancelFormBtn()},true);
        }

    }   

    private void setControlsEnabled(Component[] objects, boolean enabled) {

        for (int i=0;i<objects.length;i++){
            objects[i].setEnabled(enabled);
        }

    }



    public Item getInstanceShareItemDataSource() {
        return instanceShareFieldGroup.getItemDataSource();
    }

    public Item getInstanceShareDetailItemDataSource() {
        return instanceShareDetailFieldGroup.getItemDataSource();
    }

    public Button getSaveInstanceShareBtn() {
        return saveInstanceShareBtn;
    }

    private Button deleteInstanceShareBtn;
    public Button getDeleteInstanceShareBtn() {
        return deleteInstanceShareBtn;
    }

    private Button saveInstanceShareDetailBtn;

    private DateField periodStart;

    private TextField orderNo;

    private Button deleteInstanceShareDetailBtn;

    private Button addInstanceShareDetailBtn;

    private InvoiceShareViewImpl invoiceShareViewImpl;

    private List<InstanceShareBean> removedShares;

    private Button addInstanceShareBtn;


    private Component buildInstanceShareForm(){
        shareForm=new GridLayout(2,3);
        shareForm.setSpacing(true);
        periodStart=new DateField("Start of period:");
        periodStart.setRequired(true);
        periodStart.setRequiredError("Start of period is mandatory");
        periodStart.setImmediate(true);
        instanceShareFieldGroup.bind(periodStart, "periodStart");
        shareForm.addComponent(periodStart);

        return shareForm;
    }

    private Component buildInstanceShareTableControls(final InvoiceShareViewImpl invoiceShareViewImpl) {
        instanceShareTableControls=new HorizontalLayout();
        instanceShareTableControls.setSpacing(true);
        addInstanceShareBtn = new Button("Add", new ClickListener() {
            public void buttonClick(ClickEvent event) {
                editInstanceShare(null);
                periodStart.focus();
                setControlsEnabled(new Component[]{deleteInstanceShareBtn, addInstanceShareDetailBtn, deleteInstanceShareDetailBtn,saveInstanceShareDetailBtn, invoiceShareViewImpl.getSaveFormBtn(), invoiceShareViewImpl.getCancelFormBtn()},false);
                setControlsEnabled(new Component[]{saveInstanceShareBtn},true);

            }
        });

        deleteInstanceShareBtn = new Button("Delete");
        deleteInstanceShareBtn.addClickListener(invoiceShareViewImpl);

        instanceShareTableControls.addComponent(addInstanceShareBtn);
        instanceShareTableControls.addComponent(deleteInstanceShareBtn);
        return instanceShareTableControls;
    }

    private Component buildInstanceShareFormControls(ClickListener clickListener) {
        instanceShareFormControls=new HorizontalLayout();
        instanceShareFormControls.setSpacing(true);
        saveInstanceShareBtn = new Button("OK", new ClickListener() {
            public void buttonClick(ClickEvent event) {
                try {
                        instanceShareFieldGroup.commit();
                        setControlsEnabled(new Component[]{deleteInstanceShareBtn, addInstanceShareDetailBtn, deleteInstanceShareDetailBtn,saveInstanceShareDetailBtn, invoiceShareViewImpl.getSaveFormBtn(), invoiceShareViewImpl.getCancelFormBtn()},true);
                        setControlsEnabled(new Component[]{addInstanceShareBtn},false);
                } catch (CommitException e) {
                    saveInstanceShareBtn.setComponentError(new UserError(e.getMessage()));
                }
            }
        });
        saveInstanceShareBtn.addClickListener(clickListener);
        Button discard = new Button("Discard", new ClickListener() {
            public void buttonClick(ClickEvent event) {
                instanceShareFieldGroup.discard();
                editInstanceShare(null);
                setControlsEnabled(new Component[]{deleteInstanceShareBtn, addInstanceShareDetailBtn, deleteInstanceShareDetailBtn,saveInstanceShareDetailBtn, invoiceShareViewImpl.getSaveFormBtn(), invoiceShareViewImpl.getCancelFormBtn()},true);
                sharesTable.setSelectable(true);

            }
        });
        instanceShareFormControls.addComponent(saveInstanceShareBtn);
        instanceShareFormControls.addComponent(discard);
        return instanceShareFormControls;
    }

    public void editInstanceShare(InstanceShareBean instanceShare){
        if (instanceShare==null){
            instanceShare=new InstanceShareBean(newShareId--);
        }
        BeanItem<InstanceShareBean> item=new BeanItem<InstanceShareBean>(instanceShare);
        instanceShareFieldGroup.setItemDataSource(item);
        
        //make sure that right row is selected
        sharesTable.select(instanceShare);
        
        //remove error from btn
        saveInstanceShareBtn.setComponentError(null);
        

    }

    private TextField createTextField(String caption, boolean required, String requiredMessage){
        TextField field=new TextField(caption);
        field.setNullRepresentation("");
        field.setRequired(required);
        if (required && requiredMessage!=null){
            field.setRequiredError(requiredMessage);
        }
        
        if (required){
            field.addValidator(new StringLengthValidator(requiredMessage,1,255, required));
        }
        field.setImmediate(true);
        return field;

    }
    private Component buildInstanceShareDetailForm(){
        shareDetailForm=new GridLayout(2,3);
        shareDetailForm.setSpacing(true);
        orderNo=createTextField("Order No:", true, "Order no is mandatory");
        orderNo.setMaxLength(8);
        TextField costPool=createTextField("Cost Pool:", true, "Cost pool is mandatory");
        costPool.setRequired(true);
        costPool.setMaxLength(10);
        TextField sharePercent=createTextField("Share %:", true,"Share % is mandatory");
        TextField description=createTextField("Description:",false,null);
        description.setMaxLength(256);

        instanceShareDetailFieldGroup.bind(orderNo, "orderNumber");
        instanceShareDetailFieldGroup.bind(costPool, "costPool");
        instanceShareDetailFieldGroup.bind(sharePercent, "sharePercent");
        instanceShareDetailFieldGroup.bind(description, "description");

        shareDetailForm.addComponent(orderNo);
        shareDetailForm.addComponent(costPool);
        shareDetailForm.addComponent(sharePercent);
        shareDetailForm.addComponent(description);

        return shareDetailForm;
    }

    private Component buildInstanceShareDetailTableControls(final InvoiceShareViewImpl invoiceShareViewImpl) {
        instanceShareDetailTableControls=new HorizontalLayout();
        instanceShareDetailTableControls.setSpacing(true);
        addInstanceShareDetailBtn = new Button("Add", new ClickListener() {
            public void buttonClick(ClickEvent event) {
                editInstanceShareDetail(null);
                orderNo.focus();
            }
        });

        deleteInstanceShareDetailBtn = new Button("Delete");
        deleteInstanceShareDetailBtn.addClickListener(invoiceShareViewImpl);

        instanceShareDetailTableControls.addComponent(addInstanceShareDetailBtn);
        instanceShareDetailTableControls.addComponent(deleteInstanceShareDetailBtn);
        return instanceShareDetailTableControls;
    }

    private Component buildInstanceShareDetailFormControls(ClickListener clickListener) {
        instanceShareDetailFormControls = new HorizontalLayout();
        instanceShareDetailFormControls.setSpacing(true);
//        saveInstanceShareDetailBtn = new Button("Save");
        saveInstanceShareDetailBtn = new Button("OK", new ClickListener() {
            public void buttonClick(ClickEvent event) {
                try {
                    instanceShareDetailFieldGroup.commit();
                    setControlsEnabled(new Component[]{addInstanceShareDetailBtn, deleteInstanceShareDetailBtn,saveInstanceShareDetailBtn, invoiceShareViewImpl.getSaveFormBtn(), invoiceShareViewImpl.getCancelFormBtn()},true);
                    setControlsEnabled(new Component[]{addInstanceShareBtn,deleteInstanceShareBtn},false);

                } catch (CommitException e1) {
         
                    saveInstanceShareDetailBtn.setComponentError(new UserError(e1.getMessage()));

                    //e1.printStackTrace();
                };
            }
        });
        saveInstanceShareDetailBtn.addClickListener(clickListener);
        Button discard = new Button("Discard", new ClickListener() {
            public void buttonClick(ClickEvent event) {
                instanceShareDetailFieldGroup.discard();
                editInstanceShareDetail(null);
            }
        });
        instanceShareDetailFormControls.addComponent(saveInstanceShareDetailBtn);
        instanceShareDetailFormControls.addComponent(discard);
        return instanceShareDetailFormControls;
    }

    public void editInstanceShareDetail(InstanceShareDetailBean instanceShareDetail){
        if (instanceShareDetail==null){
            instanceShareDetail=new InstanceShareDetailBean(newShareDetailId--);
        }
        BeanItem<InstanceShareDetailBean> item=new BeanItem<InstanceShareDetailBean>(instanceShareDetail);
        instanceShareDetailFieldGroup.setItemDataSource(item);
        
        //clear error from save button
        saveInstanceShareDetailBtn.setComponentError(null);
    }


    public InstanceShareComponent(InvoiceShareViewImpl invoiceShareViewImpl){
        this.invoiceShareViewImpl=invoiceShareViewImpl;
        // A layout structure used for composition
        HorizontalLayout mainLayout = new HorizontalLayout();
        mainLayout.setSpacing(true);

        // Shares
        Panel sharePanel = new Panel("Shares");
        mainLayout.addComponent(sharePanel);                       
        VerticalLayout shareLayout = new VerticalLayout();
        shareLayout.setSpacing(true);
        sharePanel.setContent(shareLayout);

        // Compose from multiple components
        shareLayout.addComponent(buildInstanceShareTableControls(invoiceShareViewImpl));
        shareLayout.addComponent(buildSharesTable(invoiceShareViewImpl));
        shareLayout.addComponent(buildInstanceShareForm());
        shareLayout.addComponent(buildInstanceShareFormControls(invoiceShareViewImpl));

        // Share details
        Panel detailPanel = new Panel("Details");
        mainLayout.addComponent(detailPanel);        
        VerticalLayout detailLayout = new VerticalLayout();
        detailLayout.setSpacing(true);
        detailPanel.setContent(detailLayout);

        // Compose from multiple components
        detailLayout.addComponent(buildInstanceShareDetailTableControls(invoiceShareViewImpl));
        detailLayout.addComponent(buildShareDetailsTable(invoiceShareViewImpl));
        detailLayout.addComponent(buildInstanceShareDetailForm());
        detailLayout.addComponent(buildInstanceShareDetailFormControls(invoiceShareViewImpl));

        // Set the size as undefined at all levels
        sharePanel.getContent().setSizeUndefined();
        sharePanel.setSizeUndefined();

        detailPanel.getContent().setSizeUndefined();
        detailPanel.setSizeUndefined();
        mainLayout.setSizeUndefined();
        setSizeUndefined();

        // The composition root MUST be set
        setCompositionRoot(mainLayout);
    }

    private Component buildShareDetailsTable(
            InvoiceShareViewImpl invoiceShareViewImpl) {
        //Setup shareDetailsTable
        shareDetailsTable=new Table();
        shareDetailsTable.setSelectable(true);
        shareDetailsTable.setPageLength(8);

        // Handle selection change.
        shareDetailsTable.addItemClickListener(invoiceShareViewImpl);

        BeanItemContainer<InstanceShareDetailBean> beans=new BeanItemContainer<InstanceShareDetailBean>(InstanceShareDetailBean.class);
        shareDetailsTable.setContainerDataSource(beans);
        shareDetailsTable.setColumnHeader("orderNumber", "Order No");
        shareDetailsTable.setColumnHeader("costPool", "Cost Pool");
        shareDetailsTable.setColumnHeader("description", "Description");
        shareDetailsTable.setColumnHeader("sharePercent", "Share %");
        shareDetailsTable.setVisibleColumns(new Object[]{"orderNumber","costPool","sharePercent","description"});

        return shareDetailsTable;
    }

    private Component buildSharesTable(InvoiceShareViewImpl invoiceShareViewImpl) {
        //Initialize shares table
        sharesTable=new Table();
        sharesTable.setSelectable(true);
        sharesTable.setImmediate(true);
        sharesTable.setPageLength(8);

        // Handle selection change.
        sharesTable.addItemClickListener(invoiceShareViewImpl);

        BeanItemContainer<InstanceShareBean> shareBeans=new BeanItemContainer<InstanceShareBean>(InstanceShareBean.class);
        sharesTable.setContainerDataSource(shareBeans);

        sharesTable.setColumnHeader("periodStart", "Period");
        sharesTable.setVisibleColumns(new Object[]{"periodStart"});

        return sharesTable;
    }

    public void addShareToView(InstanceShareBean item) {
        if (sharesTable.getContainerDataSource().containsId(item)){
            sharesTable.getContainerDataSource().removeItem(item);
        }
        sharesTable.getContainerDataSource().addItem(item);
        
    }
    
    public Collection<InstanceShareBean> getInstanceSharesFromView(){
        Collection<InstanceShareBean> itemIds = (Collection<InstanceShareBean>) sharesTable.getContainerDataSource().getItemIds();
        return itemIds;
    }

    public Collection<InstanceShareDetailBean> getInstanceShareDetailsFromView() {
        Collection<InstanceShareDetailBean> itemIds = (Collection<InstanceShareDetailBean>) shareDetailsTable.getContainerDataSource().getItemIds();
        return itemIds;
    }

    public void addShareDetailToView(InstanceShareDetailBean item) {
        if (shareDetailsTable.getContainerDataSource().containsId(item)){
            shareDetailsTable.getContainerDataSource().removeItem(item);
        }
        shareDetailsTable.getContainerDataSource().addItem(item);
    }

    public void removeShareFromView(InstanceShareBean item) {
        if (sharesTable.getContainerDataSource().containsId(item)){
            sharesTable.getContainerDataSource().removeItem(item);
            if (removedShares==null){
                removedShares=new ArrayList<InstanceShareBean>();
            }
            removedShares.add(item);
            
            setInstanceShareSelectable(true);
            setControlsEnabled(new Component[]{addInstanceShareBtn},true);
            
            
        }
    }

    public Collection<InstanceShareBean> getRemovedShares() {
        return (removedShares==null ? Collections.<InstanceShareBean>emptyList() : removedShares);
    }

    public void removeShareDetailFromView(InstanceShareDetailBean item) {
        if (shareDetailsTable.getContainerDataSource().containsId(item)){
            shareDetailsTable.getContainerDataSource().removeItem(item);
            if (removedShareDetails==null){
                removedShareDetails=new ArrayList<InstanceShareDetailBean>();
            }
            removedShareDetails.add(item);
        }
    }
    
    public Collection<InstanceShareDetailBean> getRemovedShareDetails(){
        return (removedShareDetails==null ? Collections.<InstanceShareDetailBean>emptyList() : removedShareDetails);
    }

    public void setInstanceShareSelectable(boolean selectable) {
        this.sharesTable.setSelectable(selectable);
        this.sharesTable.markAsDirty();
        if (selectable=false){
            addInstanceShareBtn.setEnabled(false);
        }
        
    }

    public void removeAllFromDeletedSharesAndDetails() {
        removedShares=null;
        removedShareDetails=null;
        
    }


}
