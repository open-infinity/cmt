package org.openinfinity.cloud.domain;

import java.io.Serializable;
import javax.persistence.*;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;


/**
 * The persistent class for the instance_tbl database table.
 * 
 */
@Entity
@Getter
@Setter
@Table(name="instance_tbl")
public class InstanceTbl implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="instance_id")
	@GeneratedValue
	private Long instanceId;

	@Column(name="cloud_type")
	private int cloudType;

	@Column(name="cloud_zone")
	private String cloudZone;

	@Column(name="instance_active")
	private int instanceActive;

	@Column(name="instance_name")
	private String instanceName;

	@Column(name="instance_status")
	private String instanceStatus;

	@Column(name="organization_id")
	private int organizationId;

	@Column(name="user_id")
	private int userId;

	//bi-directional many-to-one association to InstanceParameterTbl
	@OneToMany(mappedBy="instanceTbl",cascade=CascadeType.ALL)
	private List<InstanceParameterTbl> instanceParameterTbls;

	//bi-directional many-to-one association to InstanceShareTbl
	@OneToMany(mappedBy="instanceTbl",cascade={CascadeType.PERSIST,
	        CascadeType.REFRESH,CascadeType.MERGE})
	private List<InstanceShare> instanceShareTbls;

	public InstanceTbl() {
	}


	public InstanceParameterTbl addInstanceParameterTbl(InstanceParameterTbl instanceParameterTbl) {
	    if (instanceParameterTbls==null){
	        instanceParameterTbls=new ArrayList<InstanceParameterTbl>();
	    }
		getInstanceParameterTbls().add(instanceParameterTbl);
		instanceParameterTbl.setInstanceTbl(this);

		return instanceParameterTbl;
	}

	public InstanceParameterTbl removeInstanceParameterTbl(InstanceParameterTbl instanceParameterTbl) {
	    
		getInstanceParameterTbls().remove(instanceParameterTbl);
		instanceParameterTbl.setInstanceTbl(null);

		return instanceParameterTbl;
	}

	public InstanceShare addInstanceShareTbl(InstanceShare instanceShareTbl) {
	    if (instanceShareTbls==null){
	        instanceShareTbls=new ArrayList<InstanceShare>();
	    }
	    getInstanceShareTbls().add(instanceShareTbl);
		instanceShareTbl.setInstanceTbl(this);

		return instanceShareTbl;
	}

	public InstanceShare removeInstanceShareTbl(InstanceShare instanceShareTbl) {
		getInstanceShareTbls().remove(instanceShareTbl);
		instanceShareTbl.setInstanceTbl(null);

		return instanceShareTbl;
	}

}