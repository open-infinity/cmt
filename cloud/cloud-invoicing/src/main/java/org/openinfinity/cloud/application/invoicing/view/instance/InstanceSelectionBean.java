package org.openinfinity.cloud.application.invoicing.view.instance;

import java.util.List;

import org.openinfinity.cloud.domain.Instance;
import org.openinfinity.cloud.domain.InstanceParameter;

public class InstanceSelectionBean {
    @Override
    public String toString() {
        return "InstanceSelectionBean [id=" + instance.getInstanceId()+ ", name=" + instance.getName() + "]";
    }
    public InstanceSelectionBean(Instance instance) {
        super();
        this.instance=instance;
    }
    private Instance instance;
    public int getCloudType() {
        return instance.getCloudType();
    }
    public int getInstanceId() {
        return instance.getInstanceId();
    }
    public String getName() {
        return instance.getName();
    }
    public Long getOrganizationid() {
        return instance.getOrganizationid();
    }
    public String getOrganizationName() {
        return instance.getOrganizationName();
    }
    public List<InstanceParameter> getParameters() {
        return instance.getParameters();
    }
    public String getStatus() {
        return instance.getStatus();
    }
    public int getUserId() {
        return instance.getUserId();
    }
    public String getUserName() {
        return instance.getUserName();
    }
    public String getZone() {
        return instance.getZone();
    }
    public Instance toDomainObject() {
        return instance;
    }


}
