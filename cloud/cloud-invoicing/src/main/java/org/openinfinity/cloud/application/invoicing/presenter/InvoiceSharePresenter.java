package org.openinfinity.cloud.application.invoicing.presenter;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;

import org.openinfinity.cloud.application.invoicing.model.InvoiceShareModel;
import org.openinfinity.cloud.application.invoicing.view.InvoiceShareView;
import org.openinfinity.cloud.application.invoicing.view.InvoiceShareView.InvoiceShareViewListener;
import org.openinfinity.cloud.application.invoicing.view.instance.InstanceSelectionBean;
import org.openinfinity.cloud.application.invoicing.view.instanceshare.InstanceShareBean;
import org.openinfinity.cloud.application.invoicing.view.instanceshare.InstanceShareDetailBean;
import org.openinfinity.cloud.domain.InstanceShare;
import org.openinfinity.cloud.domain.InstanceShareDetail;
import org.openinfinity.cloud.domain.InstanceTbl;
import org.openinfinity.cloud.domain.Job;
import org.openinfinity.cloud.service.administrator.JobService;

import com.vaadin.ui.Notification;


public class InvoiceSharePresenter implements InvoiceShareViewListener {
    InvoiceShareModel model;
    InvoiceShareView  view;
    private static final BigDecimal BIGDECIMAL100=new BigDecimal("100");

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
        //Notification.show("Button clicked:", buttonName,
        //        Type.TRAY_NOTIFICATION);
        if ("OK".equals(buttonName)){

            if (item instanceof InstanceShareBean){ // Adding share to table
                
                /* changed to save via additional Submit button
                model.saveInstanceShare(((InstanceShareBean)item).toDomainObject());
                view.setInstanceShares(model.getInstanceShares(model.getSelectedInstance().getInstanceId()));

                InstanceShare instanceShare = model.getInstanceShare(model.getSelectedInstanceShare().getId());
                view.setInstanceShareDetails( instanceShare);

                view.editInstanceShare(null);
                */
                view.addShareToView((InstanceShareBean) item);
                view.editInstanceShare((InstanceShareBean) item);
                //if share does not contain any detail the details table in view is initialized, otherwise it is left empty
                if (((InstanceShareBean) item).toDomainObject().getInstanceShareDetails()==null){
                    view.setInstanceShareDetails(null);
                }
                view.setChanged(true);
                
                //consider capturing of editInstanceShare and model.setSelectedInstanceShare to same method etc
                view.setSelectedInstanceShare((InstanceShareBean) item);
                view.setInstanceShareSelectable(false);
                
                
                
                
            }else{ //save instance share details
                //Save from view to model, commented due to saving first to view
                /*
                model.saveInstanceShareDetail((InstanceShareDetailBean) item);
                InstanceShare instanceShare = model.getInstanceShare(model.getSelectedInstanceShare().getId());
                view.setInstanceShareDetails( instanceShare);
                view.editInstanceShareDetail(null);
                
                if (instanceShare.calculateSharePercent().compareTo(BIGDECIMAL100)==0){
                    //TODO: notification to user
                    if ("Pending".equals(instanceShare.getInstanceTbl().getInstanceStatus())){
                        //updating instance state to Starting status
                        Notification.show("Updating instance to starting state");
                        Collection<Job> jobsForInstance = model.getJobsForInstance(instanceShare.getInstanceTbl().getInstanceId().intValue());
                        for (Job job:jobsForInstance){
                            if (job.getJobStatus()==JobService.CLOUD_JOB_PENDING && "create_instance".equals(job.getJobType())){
                                model.updateJobStatus(job.getJobId(),JobService.CLOUD_JOB_CREATED);
                            }
                        }
                        model.updateInstanceStatus(instanceShare.getInstanceTbl().getInstanceId().intValue(), "Starting");
                        instanceShare.getInstanceTbl().setInstanceStatus("Starting");
                        
                    }
                    
                }*/
                view.addShareDetailToView((InstanceShareDetailBean) item);
                view.editInstanceShareDetail(null);
                view.setChanged(true);
                view.setInstanceShareSelectable(false);

                
                
            }
        }
        if ("Delete".equals(buttonName)){
            if (item instanceof InstanceShareBean){
                /*model.deleteInstanceShare((InstanceShareBean)item);
                view.setInstanceShares(model.getInstanceShares(model.getSelectedInstance().getInstanceId()));
                view.editInstanceShare(null);*/
                Collection<InstanceShareBean> instanceSharesFromView = view.getInstanceSharesFromView();
                if (instanceSharesFromView.size()<2){
                    Notification.show("You cannot delete last share");
                }else{
                
                    view.removeShareFromView((InstanceShareBean) item);                
                    view.setInstanceShareDetails(null);
                    view.setSelectedInstanceShare(null);
                    view.editInstanceShare(null);
                    view.editInstanceShareDetail(null);
                }

            }else if (item instanceof InstanceShareDetailBean){
                
                /*
                model.deleteInstanceShareDetail((InstanceShareDetailBean)item);

                InstanceShare instanceShare = model.getInstanceShare(model.getSelectedInstanceShare().getId());
                view.setInstanceShareDetails( instanceShare);
                view.editInstanceShareDetail(null);
                */
                view.removeShareDetailFromView((InstanceShareDetailBean) item);
                InstanceShareBean selectedInstanceShare = view.getSelectedInstanceShare();
                selectedInstanceShare.toDomainObject().removeInstanceShareDetail(((InstanceShareDetailBean) item).toDomainObject());

                view.editInstanceShareDetail(null);
                view.setInstanceShareSelectable(false);

                
            }
        }
        
        if ("Submit".equals(buttonName)){
            //validate submit
            //BigDecimal sumOfShares=calculateSharePercent(view.getInstanceShareDetailsFromView());
            if (view.getSelectedInstanceShare()==null || (view.getSelectedInstanceShare()!=null && calculateSharePercent(view.getInstanceShareDetailsFromView()).compareTo(BIGDECIMAL100)==0)){
                
                //save changes in transaction
                model.saveChanges(model, view);
                
                //Update view
                view.setInstanceShares(model.getInstanceShares(view.getSelectedInstance().getInstanceId()));
                if (view.getSelectedInstanceShare()!=null){
                    InstanceShare instanceShare = model.getInstanceShare(view.getSelectedInstanceShare().getId());
                    view.setInstanceShareDetails( instanceShare);
                }
                view.setInstanceShareSelectable(true);
                view.setChanged(false);
                view.removeAllFromDeletedSharesAndDetails();
                
            }else{
                view.showConfirmDialog("Check share percent", "Instance share percent sum must be 100% before submit", "OK", null);
            }
        }

    }
    
    private BigDecimal calculateSharePercent(Collection<InstanceShareDetailBean> details){
        BigDecimal ret = new BigDecimal("0");
        for (InstanceShareDetailBean detail: details){
            ret=ret.add(detail.toDomainObject().getSharePercent());
        }
        
        //round up to 2 decimal places (causes 99.999 to round up to 100)
        return ret.setScale(2, BigDecimal.ROUND_HALF_UP);
    }


    @Override
    public void instanceSelected(InstanceSelectionBean value) {

        view.setInstanceShares(model.getInstanceShares(value.getInstanceId()));
        view.setInstanceShareDetails(null);
        view.removeAllFromDeletedSharesAndDetails();

        //TODO: now this causes database fetch each time instance is selected; instead should populate it when popuating drop down list
        if (value.toInstanceTbl()==null){
            InstanceTbl instanceTbl = model.getInstanceTbl(value);
            value.setInstanceTbl(instanceTbl);
        }

        view.setSelectedInstance(value);

        view.editInstanceShare(null);
        view.editInstanceShareDetail(null);

        //Notification.show("Value changed:", value.toString(),
        //        Type.TRAY_NOTIFICATION);

    }

    @Override
    public void instanceShareSelected(InstanceShareBean item, boolean force) {
        
        boolean skipSelection=false;
        
        //check if previous selection was done and it is not complete
        //if (view.isChanged()&&!force){
            //TODO: does not work
                //skipSelection=true;
                //view.editInstanceShare(view.getSelectedInstanceShare());
                //TODO: not needed if implement this by changing table mode to not selectable
                //view.showConfirmDialogForShareSelection(item, view.getSelectedInstanceShare(), "Please confirm", "There might be uncommitted changed: Are you sure you want to continue","OK","Cancel");
                
        //}
        if (!skipSelection){
        
            InstanceShare instanceShare = model.getInstanceShare(item.getId());
            view.setInstanceShareDetails(instanceShare);
            //set details from instance share fetched from database
            item.toDomainObject().setInstanceShareDetails(instanceShare.getInstanceShareDetails());
            view.setSelectedInstanceShare(item);
            view.editInstanceShare(item);
            view.editInstanceShareDetail(null);
            view.setChanged(false);
        }

    }

    @Override
    public void cancelChanges() {
        instanceSelected(view.getSelectedInstance());
        view.setInstanceShareSelectable(true);

    }

    
    @Override
    public void instanceShareDetailSelected(InstanceShareDetailBean item) {
        view.editInstanceShareDetail(item);

        //Notification.show("Item " +
        //        item + " clicked.");


    }

}