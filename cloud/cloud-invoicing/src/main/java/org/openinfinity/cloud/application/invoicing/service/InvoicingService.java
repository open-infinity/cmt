package org.openinfinity.cloud.application.invoicing.service;

import java.util.Collection;

import org.openinfinity.cloud.application.invoicing.model.InvoiceShareModel;
import org.openinfinity.cloud.application.invoicing.view.InvoiceShareView;
import org.openinfinity.cloud.application.invoicing.view.instanceshare.InstanceShareBean;
import org.openinfinity.cloud.application.invoicing.view.instanceshare.InstanceShareDetailBean;
import org.openinfinity.cloud.domain.Instance;
import org.openinfinity.cloud.domain.InstanceShare;
import org.openinfinity.cloud.domain.Job;
import org.openinfinity.cloud.service.administrator.InstanceService;
import org.openinfinity.cloud.service.administrator.JobService;
import org.openinfinity.cloud.service.invoicing.InstanceShareDetailService;
import org.openinfinity.cloud.service.invoicing.InstanceShareService;
import org.openinfinity.cloud.service.invoicing.InstanceTblService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.vaadin.ui.Notification;

@Component("invoicingService")
@Transactional
public class InvoicingService {

    @Autowired
    @Qualifier("instanceTblService")
    private InstanceTblService instanceTblService;

    @Autowired
    @Qualifier("instanceService")
    private InstanceService instanceService;

    public void updateInstanceStatus(int instanceId, String status) {
        instanceService.updateInstanceStatus(instanceId, status);
    }

    @Autowired
    @Qualifier("jobService")
    private JobService jobService;

    
    public Collection<Job> getJobsForInstance(int instanceId) {
        return jobService.getJobsForInstance(instanceId);
    }

    public void updateJobStatus(int id, int status) {
        jobService.updateStatus(id, status);
    }

    public Collection<Instance> getUserInstances(
            Collection<Long> organizationIds) {
        return instanceService.getUserInstances(organizationIds);
    }
    
    public void saveChanges(InvoiceShareModel model, InvoiceShareView view){
        //first remove items
        for (InstanceShareDetailBean detail: view.getRemovedShareDetails()){
            if (detail.getId().intValue()!=-1){
                model.deleteInstanceShareDetail(detail);
            }
            
        }
        
        //remove shares
        for (InstanceShareBean share:view.getRemovedShares()){
            //if share id is greater than zero, it has been persisten and need to remove it
            if (share.getId()>0){
                model.deleteInstanceShare(share);
            }
        }
        
        //and then save shares from view
        for (InstanceShareBean share:view.getInstanceSharesFromView()){
            if (share.toDomainObject().getInstanceTbl()==null){
                share.toDomainObject().setInstanceTbl(view.getSelectedInstance().toInstanceTbl());
            }

            InstanceShare saveInstanceShare = model.saveInstanceShare(share.toDomainObject());
            
            share.setInstanceShare(saveInstanceShare);

        }
        
        
        //then save from view
        for (InstanceShareDetailBean detail: view.getInstanceShareDetailsFromView()){
            if (detail.toDomainObject().getInstanceShare()==null){
                detail.toDomainObject().setInstanceShare(view.getSelectedInstanceShare().toDomainObject());
            }

            model.saveInstanceShareDetail(detail);
            
        }

        //update instance to starting stage
        if ("Pending".equals(view.getSelectedInstance().getStatus())){
            //updating instance state to Starting status
            Notification.show("Updating instance to starting state");
            Collection<Job> jobsForInstance = model.getJobsForInstance(view.getSelectedInstance().getInstanceId());
            for (Job job:jobsForInstance){
                if (job.getJobStatus()==JobService.CLOUD_JOB_PENDING && "create_instance".equals(job.getJobType())){
                    model.updateJobStatus(job.getJobId(),JobService.CLOUD_JOB_CREATED);
                }
            }
            model.updateInstanceStatus(view.getSelectedInstance().getInstanceId(), "Starting");
            view.getSelectedInstance().setStatus("Starting");
            
        }

    }

    @Autowired
    @Qualifier("instanceShareService")
    private InstanceShareService instanceShareService;

    @Autowired
    @Qualifier("instanceShareDetailService")
    private InstanceShareDetailService instanceShareDetailService;

    public InstanceTblService getInstanceTblService() {
        return instanceTblService;
    }

    public void setInstanceTblService(InstanceTblService instanceTblService) {
        this.instanceTblService = instanceTblService;
    }

    public InstanceShareDetailService getInstanceShareDetailService() {
        return instanceShareDetailService;
    }

    public void setInstanceShareDetailService(
            InstanceShareDetailService instanceShareDetailService) {
        this.instanceShareDetailService = instanceShareDetailService;
    }

    public InstanceShareService getInstanceShareService() {
        return instanceShareService;
    }

    public void setInstanceShareService(
            InstanceShareService instanceShareService) {
        this.instanceShareService = instanceShareService;
    }

    public Collection<Instance> getOrganizationInstances(Long organizationId) {
        return instanceService.getOrganizationInstances(organizationId);
    }

    public InstanceService getInstanceService() {
        return instanceService;
    }

    public void setInstanceService(InstanceService instanceService) {
        this.instanceService = instanceService;
    }

}
