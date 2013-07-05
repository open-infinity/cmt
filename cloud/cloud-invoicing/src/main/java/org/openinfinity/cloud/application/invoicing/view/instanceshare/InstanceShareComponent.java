package org.openinfinity.cloud.application.invoicing.view.instanceshare;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.text.SimpleDateFormat;

import org.openinfinity.cloud.domain.InstanceShare;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.ColumnHeaderMode;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
public class InstanceShareComponent extends CustomComponent{

    /* User interface components are stored in session. */
    private Table sharesTable;
    private Table shareDetailsTable;	
 
    public void setInstanceShares(Collection<InstanceShare> instanceShares) {
        // first clean up and then insert all rows from collection
        sharesTable.removeAllItems();
        
        int i=0;
        for (InstanceShare share:instanceShares){
            sharesTable.addItem(createSharesItem(share.getPeriodStart()),i++);
        }
        
        /* Add a few items in the table.
        for (int i=0; i<100; i++) {
            Calendar calendar = new GregorianCalendar(2008,0,1);
            calendar.add(Calendar.DAY_OF_YEAR, i);
            sharesTable.addItem(createSharesItem(calendar.getTime()),new Integer(i));
        }
        */
         
        sharesTable.setPageLength(8);
        //layout.addComponent(table);
        
        /*Collection instanceShareBeans=new ArrayList<InstanceShareBean>();
        for (InstanceShare instanceShare:instanceShares){
            instanceShareBeans.add(new InstanceShareBean(instanceShare));
        }*/

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

    private final Object[] createSharesItem(Date periodStart){
        return new Object[] {periodStart,
                createAddRowButton()};
    }

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private class DetailInfoListener implements Button.ClickListener {
           
        @Override
        public void buttonClick(ClickEvent event) {
            String data = (String) event.getButton().getData();
            System.out.println("BUTTON CLICKED : " +  data);
            
            
        }        
    }
    
    private Button createAddRowButton(){
        Button createRowBtn = new Button("+");
        createRowBtn.setData(0);
        createRowBtn.addClickListener(new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
                sharesTable.addItem(createSharesItem(null),new Integer(sharesTable.size()+1));
                // Get the item identifier from the user-defined data.
                Integer iid = (Integer)event.getButton().getData();
                Notification.show("Link " +
                                  iid.intValue() + " clicked.");
            } 
        });
        createRowBtn.addStyleName("link");
        
        return createRowBtn;
        
    }
    public InstanceShareComponent(String message){
        
        //Initialize shares table
        sharesTable=new Table();
        sharesTable.setEditable(true);

        // Define the names and data types of columns.
        sharesTable.addContainerProperty("Period",     Date.class,  null); 
        sharesTable.addContainerProperty("Add Row",        Button.class,    null);
        
        //Initialize details table
        shareDetailsTable=new Table();
        shareDetailsTable.setEditable(true);

        // Define the names and data types of columns.
        shareDetailsTable.addContainerProperty("Order No", String.class,  null); 
        shareDetailsTable.addContainerProperty("Cost Pool", String.class,    null);
        shareDetailsTable.addContainerProperty("Share %", Integer.class,    null);
        shareDetailsTable.addContainerProperty("Description", String.class,    null);
        shareDetailsTable.addItem(new Object[]{null,null, null, null},1);
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
        Label label = new Label(message);
        label.setSizeUndefined(); // Shrink
        shareLayout.addComponent(sharesTable);
        shareLayout.addComponent(new Button("Create new"));

        // Share details
        Panel detailPanel = new Panel("Details");
        mainLayout.addComponent(detailPanel);        
        VerticalLayout detailLayout = new VerticalLayout();
        detailPanel.setContent(detailLayout);

        // Compose from multiple components
        Label label2 = new Label(message);
        label2.setSizeUndefined(); // Shrink
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
