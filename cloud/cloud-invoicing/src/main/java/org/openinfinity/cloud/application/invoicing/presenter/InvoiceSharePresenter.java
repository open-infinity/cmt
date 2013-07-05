package org.openinfinity.cloud.application.invoicing.presenter;

import java.lang.reflect.Array;

import java.util.Collection;
import java.util.Collections;

import org.openinfinity.cloud.application.invoicing.model.InvoiceShareModel;

import org.openinfinity.cloud.application.invoicing.view.InvoiceShareView;
import org.openinfinity.cloud.application.invoicing.view.InvoiceShareView.InvoiceShareViewListener;
import org.openinfinity.cloud.application.invoicing.view.instance.InstanceSelectionBean;
import org.openinfinity.cloud.domain.InstanceShare;
import org.openinfinity.cloud.domain.InstanceShareDetail;

import com.vaadin.event.ItemClickEvent;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;


public class InvoiceSharePresenter implements InvoiceShareViewListener {
    InvoiceShareModel model;
    InvoiceShareView  view;

    public InvoiceSharePresenter(InvoiceShareModel model,
            InvoiceShareView  view) {
        this.model = model;
        this.view  = view;
        view.addListener(this);
        
        view.setInstances(model.getInstances());
        view.setInstanceShares(Collections.<InstanceShare> emptyList());

    }

    @Override
    public void buttonClick(String buttonName) {
        Notification.show("Button clicked:", buttonName,
                Type.TRAY_NOTIFICATION);

    }

    @Override
    public void instanceSelected(InstanceSelectionBean value) {
        
        view.setInstanceShares(model.getInstanceShares(value.getInstanceId()));
        Notification.show("Value changed:", value.toString(),
                Type.TRAY_NOTIFICATION);
        
    }

    @Override
    public void instanceShareSelected(Object item) {
        ItemClickEvent event=(ItemClickEvent) item;
        view.setInstanceShareDetails(model.getInstanceShareDetails((Long)event.getItemId()));
        
        Notification.show("Item " +
                item + " clicked.");

        
    }
}