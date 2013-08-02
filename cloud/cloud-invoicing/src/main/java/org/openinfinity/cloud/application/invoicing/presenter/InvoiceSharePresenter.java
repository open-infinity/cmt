package org.openinfinity.cloud.application.invoicing.presenter;

import java.util.Collections;

import org.openinfinity.cloud.application.invoicing.model.InvoiceShareModel;
import org.openinfinity.cloud.application.invoicing.view.InvoiceShareView;
import org.openinfinity.cloud.application.invoicing.view.InvoiceShareView.InvoiceShareViewListener;
import org.openinfinity.cloud.application.invoicing.view.instance.InstanceSelectionBean;
import org.openinfinity.cloud.application.invoicing.view.instanceshare.InstanceShareBean;
import org.openinfinity.cloud.application.invoicing.view.instanceshare.InstanceShareDetailBean;
import org.openinfinity.cloud.domain.InstanceShare;

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
    public void buttonClick(String buttonName,Object item) {
        Notification.show("Button clicked:", buttonName,
                Type.TRAY_NOTIFICATION);
        if ("Save".equals(buttonName)){

            if (item instanceof InstanceShareBean){ // save instance share
                model.saveInstanceShare((InstanceShareBean)item);
                view.setInstanceShares(model.getInstanceShares(model.getSelectedInstance().getInstanceId()));

                InstanceShare instanceShare = model.getInstanceShare(model.getSelectedInstanceShare().getId());
                view.setInstanceShareDetails( instanceShare);

                view.editInstanceShare(null);
            }else{ //save instance share details
                //Save from view to model
                model.saveInstanceShareDetail((InstanceShareDetailBean) item);
                InstanceShare instanceShare = model.getInstanceShare(model.getSelectedInstanceShare().getId());
                view.setInstanceShareDetails( instanceShare);
                view.editInstanceShareDetail(null);
            }
        }
        if ("Delete".equals(buttonName)){
            if (item instanceof InstanceShareBean){
                model.deleteInstanceShare((InstanceShareBean)item);
                view.setInstanceShares(model.getInstanceShares(model.getSelectedInstance().getInstanceId()));
                view.editInstanceShare(null);
            }else if (item instanceof InstanceShareDetailBean){
                model.deleteInstanceShareDetail((InstanceShareDetailBean)item);

                InstanceShare instanceShare = model.getInstanceShare(model.getSelectedInstanceShare().getId());
                view.setInstanceShareDetails( instanceShare);
                view.editInstanceShareDetail(null);
            }
        }

    }

    @Override
    public void instanceSelected(InstanceSelectionBean value) {

        view.setInstanceShares(model.getInstanceShares(value.getInstanceId()));
        view.setInstanceShareDetails(null);

        //TODO: now this causes database fetch each time instance is selected; instead should populate it when popuating drop down list
        model.setSelectedInstance(value);

        view.editInstanceShare(null);
        view.editInstanceShareDetail(null);

        Notification.show("Value changed:", value.toString(),
                Type.TRAY_NOTIFICATION);

    }

    @Override
    public void instanceShareSelected(InstanceShareBean item) {
        InstanceShare instanceShare = model.getInstanceShare(item.getId());
        view.setInstanceShareDetails(instanceShare);
        model.setSelectedInstanceShare(item);
        view.editInstanceShare(item);
        view.editInstanceShareDetail(null);

        Notification.show("Item " +
                item + " clicked.");


    }

    @Override
    public void instanceShareDetailSelected(InstanceShareDetailBean item) {
        view.editInstanceShareDetail(item);

        Notification.show("Item " +
                item + " clicked.");


    }

}