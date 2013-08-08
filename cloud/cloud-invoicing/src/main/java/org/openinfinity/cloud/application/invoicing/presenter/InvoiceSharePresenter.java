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

            if (item instanceof InstanceShareBean){ // save instance share
                
                /* changed to save via additional Submit button
                model.saveInstanceShare(((InstanceShareBean)item).toDomainObject());
                view.setInstanceShares(model.getInstanceShares(model.getSelectedInstance().getInstanceId()));

                InstanceShare instanceShare = model.getInstanceShare(model.getSelectedInstanceShare().getId());
                view.setInstanceShareDetails( instanceShare);

                view.editInstanceShare(null);
                */
                view.getInstanceShareComponent().addShareToView((InstanceShareBean) item);
                view.editInstanceShare(null);
                
                
                
                
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
                view.getInstanceShareComponent().addShareDetailToView((InstanceShareDetailBean) item);
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
        
        if ("Submit".equals(buttonName)){
            //validate submit
            BigDecimal sumOfShares=calculateSharePercent(view.getInstanceShareComponent().getInstanceShareDetailsFromView());
            if (sumOfShares.compareTo(BIGDECIMAL100)==0){
                //TODO: notification to user
                if ("Pending".equals(model.getSelectedInstance().getStatus())){
                    //updating instance state to Starting status
                    Notification.show("Updating instance to starting state");
                    Collection<Job> jobsForInstance = model.getJobsForInstance(model.getSelectedInstance().getInstanceId());
                    for (Job job:jobsForInstance){
                        if (job.getJobStatus()==JobService.CLOUD_JOB_PENDING && "create_instance".equals(job.getJobType())){
                            model.updateJobStatus(job.getJobId(),JobService.CLOUD_JOB_CREATED);
                        }
                    }
                    model.updateInstanceStatus(model.getSelectedInstance().getInstanceId(), "Starting");
                    model.getSelectedInstance().setStatus("Starting");
                    
                }
                for (InstanceShareBean share:view.getInstanceShareComponent().getInstanceSharesFromView()){
                    InstanceShare saveInstanceShare = model.saveInstanceShare(share.toDomainObject());
                    share.setInstanceShare(saveInstanceShare);

                }
                
                for (InstanceShareDetailBean detail: view.getInstanceShareComponent().getInstanceShareDetailsFromView()){
                    model.saveInstanceShareDetail(detail);
                    
                }
                
                //Update view
                view.setInstanceShares(model.getInstanceShares(model.getSelectedInstance().getInstanceId()));
                InstanceShare instanceShare = model.getInstanceShare(model.getSelectedInstanceShare().getId());
                view.setInstanceShareDetails( instanceShare);
                
            }else{
                view.showConfirmDialog("Share percent", "Instance share percent sum must be 100% before submit", "OK", null);
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

        //TODO: now this causes database fetch each time instance is selected; instead should populate it when popuating drop down list
        model.setSelectedInstance(value);

        view.editInstanceShare(null);
        view.editInstanceShareDetail(null);

        //Notification.show("Value changed:", value.toString(),
        //        Type.TRAY_NOTIFICATION);

    }

    @Override
    public void instanceShareSelected(InstanceShareBean item, boolean force) {
        
        boolean skipSelection=false;
        
        //check if previous selection was done and it is not complete
        /*if (model.getSelectedInstanceShare()!=null){
            InstanceShare instanceShare = model.getInstanceShare(model.getSelectedInstanceShare().getId());
            if (instanceShare.calculateSharePercent().compareTo(BIGDECIMAL100) != 0  && !force){
                skipSelection=true;
                view.showConfirmDialogForShareSelection(item, model.getSelectedInstanceShare(), "Please confirm", "Calculated sum of share detail percents differs from 100 %","Continue","Undo selection");
                
            }
        } else{

            Notification.show("Item " +
                    item + " clicked.");
            
        }*/
        if (!skipSelection){
        
            InstanceShare instanceShare = model.getInstanceShare(item.getId());
            view.setInstanceShareDetails(instanceShare);
            model.setSelectedInstanceShare(item);
            view.editInstanceShare(item);
            view.editInstanceShareDetail(null);
        }

    }

    @Override
    public void cancelChanges() {
        instanceSelected(model.getSelectedInstance());
    }

    
    @Override
    public void instanceShareDetailSelected(InstanceShareDetailBean item) {
        view.editInstanceShareDetail(item);

        //Notification.show("Item " +
        //        item + " clicked.");


    }

}