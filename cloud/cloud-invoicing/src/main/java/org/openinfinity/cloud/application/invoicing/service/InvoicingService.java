package org.openinfinity.cloud.application.invoicing.service;

import java.util.Collection;

import org.openinfinity.cloud.domain.Instance;
import org.openinfinity.cloud.domain.Job;
import org.openinfinity.cloud.service.administrator.InstanceService;
import org.openinfinity.cloud.service.administrator.JobService;
import org.openinfinity.cloud.service.invoicing.InstanceShareDetailService;
import org.openinfinity.cloud.service.invoicing.InstanceShareService;
import org.openinfinity.cloud.service.invoicing.InstanceTblService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component("invoicingService")
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
