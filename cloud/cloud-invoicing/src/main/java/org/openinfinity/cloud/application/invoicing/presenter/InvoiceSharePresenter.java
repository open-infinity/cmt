package org.openinfinity.cloud.application.invoicing.presenter;

import org.openinfinity.cloud.application.invoicing.model.InvoiceShareModel;

import org.openinfinity.cloud.application.invoicing.view.InvoiceShareView;
import org.openinfinity.cloud.application.invoicing.view.InvoiceShareView.InvoiceShareViewListener;


public class InvoiceSharePresenter implements InvoiceShareViewListener {
    InvoiceShareModel model;
    InvoiceShareView  view;

    public InvoiceSharePresenter(InvoiceShareModel model,
            InvoiceShareView  view) {
        this.model = model;
        this.view  = view;
        view.addListener(this);
        
        view.setInstanceSelectionSource(model.getInstanceContainer());

    }

    public void buttonClick(String buttonName) {
        System.out.println("DBG: button clicked is " + buttonName);

    }
}