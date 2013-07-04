package org.openinfinity.cloud.application.invoicing.presenter;

import org.openinfinity.cloud.application.invoicing.model.InvoiceShareModel;

import org.openinfinity.cloud.application.invoicing.view.InvoiceShareView;
import org.openinfinity.cloud.application.invoicing.view.InvoiceShareView.InvoiceShareViewListener;

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
        
        view.setInstanceSelectionSource(model.getInstanceContainer());
        view.setInstanceShareSource(model.getInstanceShareContainer());

    }

    @Override
    public void buttonClick(String buttonName) {
        Notification.show("Button clicked:", buttonName,
                Type.TRAY_NOTIFICATION);

    }

    @Override
    public void valueChange(Object value) {
        Notification.show("Value changed:", value.toString(),
                Type.TRAY_NOTIFICATION);
        
    }
}