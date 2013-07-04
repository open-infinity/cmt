package org.openinfinity.cloud.application.invoicing.service.component;

import java.sql.Date;
import java.sql.Timestamp;

import org.openinfinity.cloud.domain.InstanceTbl;

public class InstanceShareBean {
	
	@Override
    public String toString() {
        return "InstanceShareBean [id=" + id +
        		", instance_id=" + instance_id +
        		", period_start=" + period_start +
        		", created_by=" + created_by +
        		", created" + created +
        		", modified_by" + modified_by +
        		", modified" + modified +
        		"]";
    }
    public InstanceShareBean(String id, String instance_id, Date period_start, String created_by, String modified_by) {
        super();
        this.id = id;
        this.instance_id = instance_id;
        this.period_start = period_start;
        this.created_by = created_by;
        this.created = new Timestamp(System.currentTimeMillis());
        this.modified_by = modified_by;
    }
    private InstanceTbl entity;
    
    private String id;
    private String instance_id;
    private Date   period_start;
    private String created_by;
    private Timestamp created;
    private String modified_by;
    private Timestamp modified;
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
	public String getInstance_id() {
		return instance_id;
	}
	public void setInstance_id(String instance_id) {
		this.instance_id = instance_id;
	}
	public Date getPeriod_start() {
		return period_start;
	}
	public void setPeriod_start(Date period_start) {
		this.period_start = period_start;
	}
	public String getCreated_by() {
		return created_by;
	}
	public void setCreated_by(String created_by) {
		this.created_by = created_by;
	}
	public Timestamp getCreated() {
		return created;
	}
	public void setCreated(Timestamp created) {
		this.created = created;
	}
	public String getModified_by() {
		return modified_by;
	}
	public void setModified_by(String modified_by) {
		this.modified_by = modified_by;
	}
	public Timestamp getModified() {
		return modified;
	}	 
}
