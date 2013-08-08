package org.openinfinity.cloud.application.invoicing.view.instanceshare;

import java.util.Date;

import org.openinfinity.cloud.domain.InstanceShare;

public class InstanceShareBean {
    @Override
    public String toString() {
        return "InstanceShareBean [id=" + getId() + ", name="
                + getPeriodStart() + "]";
    }

    public InstanceShareBean(InstanceShare instanceShare) {
        super();
        this.instanceShare = instanceShare;
    }

    public InstanceShareBean(long i) {
        instanceShare=new InstanceShare();
        instanceShare.setId(i);
    }

    private InstanceShare instanceShare;

    public void setInstanceShare(InstanceShare instanceShare) {
        this.instanceShare = instanceShare;
    }

    public long getId() {
        return instanceShare.getId();
    }

    public void setId(Long id) {
        instanceShare.setId(id);
    }

    public Date getPeriodStart() {
        return instanceShare.getPeriodStart();
    }

    public void setPeriodStart(Date periodStart) {
        instanceShare.setPeriodStart(periodStart);
    }

    public InstanceShare toDomainObject() {
        return instanceShare;
    }

}
