package org.openinfinity.cloud.application.invoicing.view.instanceshare;

import java.util.Date;

import org.openinfinity.cloud.domain.InstanceShare;

public class InstanceShareBean {
    @Override
    public String toString() {
        return "InstanceShareBean [id=" + getId()+ 
        		", name=" + getPeriodStart() + "]";
    }
    public InstanceShareBean(InstanceShare instanceShare) {
        super();
        this.instanceShare=instanceShare;
    }
    private InstanceShare instanceShare;
    public long getId() {
        return instanceShare.getId();
    }
	public Date getPeriodStart() {
		return instanceShare.getPeriodStart();
	}
}
