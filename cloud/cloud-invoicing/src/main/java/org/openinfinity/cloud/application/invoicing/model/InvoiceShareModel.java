package org.openinfinity.cloud.application.invoicing.model;

import java.util.Collection;
import java.util.Collections;

import org.openinfinity.cloud.application.invoicing.model.user.User;
import org.openinfinity.cloud.application.invoicing.service.InvoicingService;
import org.openinfinity.cloud.application.invoicing.utility.ApplicationContextProvider;
import org.openinfinity.cloud.application.invoicing.view.instance.InstanceSelectionBean;
import org.openinfinity.cloud.application.invoicing.view.instanceshare.InstanceShareBean;
import org.openinfinity.cloud.application.invoicing.view.instanceshare.InstanceShareDetailBean;
import org.openinfinity.cloud.domain.Instance;
import org.openinfinity.cloud.domain.InstanceShare;
import org.openinfinity.cloud.domain.InstanceShareDetail;
import org.openinfinity.cloud.domain.InstanceTbl;
import org.openinfinity.cloud.service.invoicing.InstanceShareDetailService;
import org.openinfinity.cloud.service.invoicing.InstanceShareService;
import org.openinfinity.cloud.service.invoicing.InstanceTblService;

/**
 * Business Model for invoicing functionality
 * @author kilpopas
 *
 */
public class InvoiceShareModel{

    private Collection<Instance> instances=null;
    private Collection<InstanceShare> instanceShares;

    public Collection<Instance> getInstances() {
        return instances;
    }

    public void setInstances(Collection<Instance> instances) {
        this.instances = instances;
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

    private InvoicingService invoicingService;

    private User user;

    private InstanceShare selectedInstanceShare;
    private InstanceTbl selectedInstance;

    public InstanceTbl getSelectedInstance() {
        return selectedInstance;
    }

    public InstanceShare getSelectedInstanceShare() {
        return selectedInstanceShare;
    }

    public InvoiceShareModel(){
        //Initialize instances list for selection
        invoicingService = ApplicationContextProvider.getContext().getBean(InvoicingService.class);
        instances = invoicingService.getUserInstances(user.getOrganizationIds());
    }

    public void setSelectedInstanceShare(InstanceShareBean bean) {
        this.selectedInstanceShare=bean.toDomainObject();

    }

    public void setSelectedInstance(InstanceSelectionBean value) {
        //TODO: optimize, now each select does database query
        InstanceTblService instanceTblService = invoicingService.getInstanceTblService();
        this.selectedInstance = instanceTblService.findOne( Integer.valueOf(value.getInstanceId()).longValue());
    }

    public void deleteInstanceShare(InstanceShareBean item) {
        invoicingService = ApplicationContextProvider.getContext().getBean(InvoicingService.class);        
        InstanceShareService instanceShareService = invoicingService.getInstanceShareService();
        instanceShareService.delete(item.toDomainObject());


    }

    public void saveInstanceShare(InstanceShareBean item) {
        invoicingService = ApplicationContextProvider.getContext().getBean(InvoicingService.class);        
        InstanceShareService instanceShareService = invoicingService.getInstanceShareService();
        if (item.toDomainObject().getInstanceTbl()==null){
            item.toDomainObject().setInstanceTbl(this.selectedInstance);
        }
        instanceShareService.save(item.toDomainObject());

    }

    public void saveInstanceShareDetail(InstanceShareDetailBean item) {
        invoicingService = ApplicationContextProvider.getContext().getBean(InvoicingService.class);        
        InstanceShareDetailService instanceShareDetailService = invoicingService.getInstanceShareDetailService();
        if (item.toDomainObject().getInstanceShare()==null){
            item.toDomainObject().setInstanceShare(this.selectedInstanceShare);
        }
        instanceShareDetailService.save(item.toDomainObject());

    }

    public void deleteInstanceShareDetail(InstanceShareDetailBean item) {
        invoicingService = ApplicationContextProvider.getContext().getBean(InvoicingService.class);        
        InstanceShareDetailService instanceShareDetailService = invoicingService.getInstanceShareDetailService();
        instanceShareDetailService.delete(item.toDomainObject());

    }
}
