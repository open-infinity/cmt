package org.openinfinity.cloud.application.invoicing.model;

import java.util.Collection;
import java.util.Collections;

import org.openinfinity.cloud.application.invoicing.model.user.User;
import org.openinfinity.cloud.application.invoicing.service.InvoicingService;
import org.openinfinity.cloud.application.invoicing.utility.ApplicationContextProvider;
import org.openinfinity.cloud.application.invoicing.view.InvoiceShareView;
import org.openinfinity.cloud.application.invoicing.view.instance.InstanceSelectionBean;
import org.openinfinity.cloud.application.invoicing.view.instanceshare.InstanceShareBean;
import org.openinfinity.cloud.application.invoicing.view.instanceshare.InstanceShareDetailBean;
import org.openinfinity.cloud.domain.Instance;
import org.openinfinity.cloud.domain.InstanceShare;
import org.openinfinity.cloud.domain.InstanceShareDetail;
import org.openinfinity.cloud.domain.InstanceTbl;
import org.openinfinity.cloud.domain.Job;
import org.openinfinity.cloud.service.invoicing.InstanceShareDetailService;
import org.openinfinity.cloud.service.invoicing.InstanceShareService;
import org.openinfinity.cloud.service.invoicing.InstanceTblService;

/**
 * Business Model for invoicing functionality
 * @author kilpopas
 *
 */
public class InvoiceShareModel{

    private InvoicingService invoicingService;

    public void saveChanges(InvoiceShareModel model, InvoiceShareView view) {
        invoicingService.saveChanges(model, view);
    }

    public void updateInstanceStatus(int instanceId, String status) {
        invoicingService.updateInstanceStatus(instanceId, status);
    }

    public Collection<Job> getJobsForInstance(int instanceId) {
        return invoicingService.getJobsForInstance(instanceId);
    }

    public void updateJobStatus(int id, int status) {
        invoicingService.updateJobStatus(id, status);
    }

    private Collection<Instance> instances=null;
    private Collection<InstanceShare> instanceShares;

    public Collection<Instance> getInstances() {
        return instances;
    }

    public void setInstances(Collection<Instance> instances) {
        this.instances = instances;
    }

    public Collection<InstanceShare> getInstanceSharesFromModel() {
        return instanceShares;
    }
    
    public Collection<InstanceShare> getInstanceShares(long instanceId) {
        instanceShares = invoicingService.getInstanceShareService().findByInstanceId(instanceId);
        return instanceShares;
    }

    public InstanceShare getInstanceShare(long instanceShareId){
        return invoicingService.getInstanceShareService().findOne(instanceShareId);
    }

    public Collection<InstanceShareDetail> getInstanceShareDetails(long instanceShareId) {
        InstanceShare share=invoicingService.getInstanceShareService().findOne(instanceShareId);
        return (share==null ? Collections.<InstanceShareDetail>emptyList():share.getInstanceShareDetails());

    }

    public void setInstanceShares(Collection<InstanceShare> instanceShares) {
        this.instanceShares = instanceShares;
    }

    private User user;

    /*private InstanceShareBean selectedInstanceShare;
    private InstanceSelectionBean selectedInstance;
    private InstanceTbl selectedInstanceTbl;

    public InstanceSelectionBean getSelectedInstance() {
        return selectedInstance;
    }

    public void setSelectedInstance(InstanceSelectionBean value) {
        //TODO: optimize, now each select does database query
        
        InstanceTblService instanceTblService = invoicingService.getInstanceTblService();
        this.selectedInstanceTbl = instanceTblService.findOne( Integer.valueOf(value.getInstanceId()).longValue());
        
        this.selectedInstance=value;
    }

    public InstanceShareBean getSelectedInstanceShare() {
        return selectedInstanceShare;
    }
    
    public void setSelectedInstanceShare(InstanceShareBean bean) {
        this.selectedInstanceShare=bean;

    }

    *
    */
    
    public InstanceTbl getInstanceTbl(InstanceSelectionBean value){
        InstanceTblService instanceTblService = invoicingService.getInstanceTblService();
        return instanceTblService.findOne( Integer.valueOf(value.getInstanceId()).longValue());

    }

    public InvoiceShareModel(User user){
        this.user=user;
        //Initialize instances list for selection
        invoicingService = ApplicationContextProvider.getContext().getBean(InvoicingService.class);
        instances = invoicingService.getUserInstances(user.getOrganizationIds());
    }

    public void deleteInstanceShare(InstanceShareBean item) {
        invoicingService = ApplicationContextProvider.getContext().getBean(InvoicingService.class);        
        InstanceShareService instanceShareService = invoicingService.getInstanceShareService();
        instanceShareService.delete(item.toDomainObject());


    }

    public InstanceShare saveInstanceShare(InstanceShare item) {
        invoicingService = ApplicationContextProvider.getContext().getBean(InvoicingService.class);        
        InstanceShareService instanceShareService = invoicingService.getInstanceShareService();
        InstanceShare share = instanceShareService.save(item);
        return share;

    }

    public void saveInstanceShareDetail(InstanceShareDetailBean item) {
        invoicingService = ApplicationContextProvider.getContext().getBean(InvoicingService.class);        
        InstanceShareDetailService instanceShareDetailService = invoicingService.getInstanceShareDetailService();
        instanceShareDetailService.save(item.toDomainObject());

    }

    public void deleteInstanceShareDetail(InstanceShareDetailBean item) {
        invoicingService = ApplicationContextProvider.getContext().getBean(InvoicingService.class);        
        InstanceShareDetailService instanceShareDetailService = invoicingService.getInstanceShareDetailService();
        instanceShareDetailService.delete(item.toDomainObject());

    }
}
