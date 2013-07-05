package org.openinfinity.cloud.application.invoicing.view.instanceshare;

import java.util.Collection;
import java.util.Date;

import org.openinfinity.cloud.application.invoicing.view.InvoiceShareViewImpl;
import org.openinfinity.cloud.domain.InstanceShare;
import org.openinfinity.cloud.domain.InstanceShareDetail;

import com.vaadin.data.Item;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.ColumnGenerator;
import com.vaadin.ui.VerticalLayout;

public class InstanceShareComponent extends CustomComponent{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /* User interface components are stored in session. */
    private Table sharesTable;
    private Table shareDetailsTable;	

    public void setInstanceShareDetails(Collection<InstanceShareDetail> details){
        shareDetailsTable.removeAllItems();

        for (InstanceShareDetail detail:details){
            shareDetailsTable.addItem(new Object[]{detail.getOrderNumber(),detail.getCostPool(),detail.getSharePercent(), detail.getDescription()},detail.getId());
        }
        
        //add empty item
        if (details.size()==0)
            shareDetailsTable.addItem();
    }

    public void setInstanceShares(Collection<InstanceShare> instanceShares) {
        // first clean up and then insert all rows from collection
        sharesTable.removeAllItems();

        for (InstanceShare share:instanceShares){
            sharesTable.addItem(new Object[]{share.getPeriodStart()},share.getId().intValue());
        }
        
        if (instanceShares.size()==0)
            sharesTable.addItem();

        sharesTable.setPageLength(8);


        // Create a Collection container using id property as the key
        /*instanceShareContainer = new BeanItemContainer<InstanceShareBean>(InstanceShareBean.class);
        instanceShareContainer.addAll(instanceShareBeans);*/
        /*
        if (instanceShareContainer.size() == 0) {
            sharesList.setVisible(false);
        }
        else {
            sharesList.setVisible(true);
            sharesList.setContainerDataSource(instanceShareContainer);
            sharesList.setVisibleColumns(new String[] { "periodStart" });
            sharesList.setColumnHeaderMode(ColumnHeaderMode.HIDDEN);
            sharesList.setSelectable(true);
            sharesList.setImmediate(true);

            sharesList.addValueChangeListener(new Property.ValueChangeListener() {
                public void valueChange(ValueChangeEvent event) {
                    Object id = sharesList.getValue();
                }
            });
        }
         */
    }


    public InstanceShareComponent(InvoiceShareViewImpl invoiceShareViewImpl){

        //Initialize shares table
        sharesTable=new Table();
        //sharesTable.setEditable(true);
        sharesTable.setSelectable(true);

        // Handle selection change.
        sharesTable.addItemClickListener(invoiceShareViewImpl);

        // Define the names and data types of columns.
        sharesTable.addContainerProperty("Period",     Date.class,  null); 
        sharesTable.addContainerProperty("Add Row",        Button.class,    null);
        sharesTable.addContainerProperty("Remove Row",        Button.class,    null);
        
        sharesTable.addGeneratedColumn("Add", new ColumnGenerator() {            
            @Override public Object generateCell(final Table source, final Object itemId, Object columnId) {
                Button button = new Button("Add");
                button.addClickListener(new ClickListener() {
                    @Override public void buttonClick(ClickEvent event) {
                        Notification.show("item= " +
                                itemId + " clicked.");
                        //sharesTable.addItem(createSharesItem(new Date(),new Long(sharesTable.size()+1)),new Long(sharesTable.size()+1));
                        source.getContainerDataSource().addItem();
                        //source.getContainerDataSource().removeItem(itemId);
                    }
                });
                return button;
            }
        });
        sharesTable.addGeneratedColumn("Delete", new ColumnGenerator() {
            @Override public Object generateCell(final Table source, final Object itemId, Object columnId) {
                Button button = new Button("Delete");
                button.addClickListener(new ClickListener() {
                    @Override public void buttonClick(ClickEvent event) {
                        Notification.show("item= " +
                                itemId + " clicked.");
                        source.getContainerDataSource().removeItem(itemId);
                    }
                });
                return button;
            }
        });

 
        sharesTable.setVisibleColumns(new String[]{"Period", "Add", "Delete"});
        //Initialize details table
        shareDetailsTable=new Table();
        shareDetailsTable.setEditable(true);

        // Define the names and data types of columns.
        shareDetailsTable.addContainerProperty("Order No", String.class,  null); 
        shareDetailsTable.addContainerProperty("Cost Pool", String.class,    null);
        shareDetailsTable.addContainerProperty("Share %", Integer.class,    null);
        shareDetailsTable.addContainerProperty("Description", String.class,    null);

        shareDetailsTable.addGeneratedColumn("Add", new ColumnGenerator() {            
            @Override public Object generateCell(final Table source, final Object itemId, Object columnId) {
                Button button = new Button("Add");
                button.addClickListener(new ClickListener() {
                    @Override public void buttonClick(ClickEvent event) {
                        Notification.show("item= " +
                                itemId + " clicked.");
                        //sharesTable.addItem(createSharesItem(new Date(),new Long(sharesTable.size()+1)),new Long(sharesTable.size()+1));
                        source.getContainerDataSource().addItem();
                        //source.getContainerDataSource().removeItem(itemId);
                    }
                });
                return button;
            }
        });
        shareDetailsTable.addGeneratedColumn("Delete", new ColumnGenerator() {
            @Override public Object generateCell(final Table source, final Object itemId, Object columnId) {
                Button button = new Button("Delete");
                button.addClickListener(new ClickListener() {
                    @Override public void buttonClick(ClickEvent event) {
                        Notification.show("item= " +
                                itemId + " clicked.");
                        source.getContainerDataSource().removeItem(itemId);
                    }
                });
                return button;
            }
        });

 
        
        //shareDetailsTable.setVisibleColumns(new String[]{"Order No","Cost Pool","Share %","Description"});

        
        shareDetailsTable.addItem(new Object[]{},1);
        shareDetailsTable.setPageLength(8);
        shareDetailsTable.setEditable(true);

        // A layout structure used for composition
        HorizontalLayout mainLayout = new HorizontalLayout();

        // Shares
        Panel sharePanel = new Panel("Shares");
        mainLayout.addComponent(sharePanel);                       
        VerticalLayout shareLayout = new VerticalLayout();
        sharePanel.setContent(shareLayout);

        // Compose from multiple components
        shareLayout.addComponent(sharesTable);
        shareLayout.addComponent(new Button("Create new"));

        // Share details
        Panel detailPanel = new Panel("Details");
        mainLayout.addComponent(detailPanel);        
        VerticalLayout detailLayout = new VerticalLayout();
        detailPanel.setContent(detailLayout);

        // Compose from multiple components
        detailLayout.addComponent(shareDetailsTable);
        detailLayout.addComponent(new Button("Save"));

        // Set the size as undefined at all levels
        sharePanel.getContent().setSizeUndefined();
        sharePanel.setSizeUndefined();
        detailPanel.getContent().setSizeUndefined();
        detailPanel.setSizeUndefined();
        mainLayout.setSizeFull();
        setSizeUndefined();

        // The composition root MUST be set
        setCompositionRoot(mainLayout);
    }

}
