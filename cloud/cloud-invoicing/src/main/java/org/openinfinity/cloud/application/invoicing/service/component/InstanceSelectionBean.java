package org.openinfinity.cloud.application.invoicing.service.component;

import org.openinfinity.cloud.domain.InstanceTbl;

public class InstanceSelectionBean {
    @Override
    public String toString() {
        return "InstanceSelectionBean [id=" + id + ", abbreviation="
                + abbreviation + ", fullName=" + fullName + "]";
    }
    public InstanceSelectionBean(String id, String abbreviation, String fullName) {
        super();
        this.id = id;
        this.abbreviation = abbreviation;
        this.fullName = fullName;
    }
    private InstanceTbl entity;
    
    private String id;
    private String abbreviation;
 
    private String fullName;

    public String getAbbreviation() {
        return abbreviation;
    }
    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public InstanceTbl getEntity() {
        return entity;
    }
    public void setEntity(InstanceTbl entity) {
        this.entity = entity;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getFullName() {
        return fullName;
    }
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
  
}
