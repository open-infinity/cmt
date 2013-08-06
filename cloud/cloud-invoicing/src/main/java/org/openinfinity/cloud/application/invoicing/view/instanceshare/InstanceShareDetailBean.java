package org.openinfinity.cloud.application.invoicing.view.instanceshare;

import java.math.BigDecimal;
import java.sql.Timestamp;

import org.openinfinity.cloud.domain.InstanceShareDetail;

public class InstanceShareDetailBean {

    private InstanceShareDetail detail;

    public InstanceShareDetailBean(InstanceShareDetail detail) {
        super();
        this.detail = detail;
    }

    public InstanceShareDetailBean(long id) {
        super();
        this.detail=new InstanceShareDetail();
        this.detail.setId(id);
    }

 
    public String getCostPool() {
        return detail.getCostPool();
    }

    public Timestamp getCreated() {
        return detail.getCreated();
    }

    public int getCreatedBy() {
        return detail.getCreatedBy();
    }

    public String getDescription() {
        return detail.getDescription();
    }

    public Long getId() {
        return detail.getId();
    }

    public Timestamp getModified() {
        return detail.getModified();
    }

    public int getModifiedBy() {
        return detail.getModifiedBy();
    }

    public String getOrderNumber() {
        return detail.getOrderNumber();
    }

    public String getSharePercent() {
        return detail.getSharePercent()==null ? null :detail.getSharePercent().toString();
    }

    public void setCostPool(String costPool) {
        detail.setCostPool(costPool);
    }

    public void setCreated(Timestamp created) {
        detail.setCreated(created);
    }

    public void setCreatedBy(int createdBy) {
        detail.setCreatedBy(createdBy);
    }

    public void setDescription(String description) {
        detail.setDescription(description);
    }

    public void setId(Long id) {
        detail.setId(id);
    }

    public void setModified(Timestamp modified) {
        detail.setModified(modified);
    }

    public void setModifiedBy(int modifiedBy) {
        detail.setModifiedBy(modifiedBy);
    }

    public void setOrderNumber(String orderNumber) {
        detail.setOrderNumber(orderNumber);
    }

    public void setSharePercent(String sharePercent) {
        detail.setSharePercent(new BigDecimal(sharePercent));
    }

    public String toString() {
        return detail.toString();
    }

    public InstanceShareDetail toDomainObject() {
        return detail;
    }

}
