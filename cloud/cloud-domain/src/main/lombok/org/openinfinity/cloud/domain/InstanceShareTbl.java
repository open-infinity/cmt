package org.openinfinity.cloud.domain;

import java.io.Serializable;
import javax.persistence.*;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.Setter;


/**
 * The persistent class for the instance_share_tbl database table.
 * 
 */
@Entity
@Getter
@Setter
@Table(name="instance_share_tbl")
public class InstanceShareTbl implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Long id;

	private Timestamp created;

	@Column(name="created_by")
	private int createdBy;

	private Timestamp modified;

	@Column(name="modified_by")
	private int modifiedBy;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="period_start")
	private Date periodStart;

	//bi-directional many-to-one association to InstanceShareDetailTbl
	@OneToMany(mappedBy="instanceShareTbl",cascade=CascadeType.ALL)
	private List<InstanceShareDetailTbl> instanceShareDetailTbls;

	//bi-directional many-to-one association to InstanceShareInvoiceTbl
	@OneToMany(mappedBy="instanceShareTbl",cascade={CascadeType.PERSIST,
                CascadeType.REFRESH,CascadeType.MERGE})
	private List<InstanceShareInvoiceTbl> instanceShareInvoiceTbls;

	//bi-directional many-to-one association to InstanceTbl
	@ManyToOne
	@JoinColumn(name="instance_id")
	private InstanceTbl instanceTbl;

	public InstanceShareTbl() {
	}

	public InstanceShareDetailTbl addInstanceShareDetailTbl(InstanceShareDetailTbl instanceShareDetailTbl) {
	    if (instanceShareDetailTbls==null){
	        instanceShareDetailTbls=new ArrayList<InstanceShareDetailTbl>();
	    }
		getInstanceShareDetailTbls().add(instanceShareDetailTbl);
		instanceShareDetailTbl.setInstanceShareTbl(this);

		return instanceShareDetailTbl;
	}

	public InstanceShareDetailTbl removeInstanceShareDetailTbl(InstanceShareDetailTbl instanceShareDetailTbl) {
		getInstanceShareDetailTbls().remove(instanceShareDetailTbl);
		instanceShareDetailTbl.setInstanceShareTbl(null);

		return instanceShareDetailTbl;
	}

	public InstanceShareInvoiceTbl addInstanceShareInvoiceTbl(InstanceShareInvoiceTbl instanceShareInvoiceTbl) {
	    if (instanceShareInvoiceTbls==null){
	        instanceShareInvoiceTbls=new ArrayList<InstanceShareInvoiceTbl>();
	    }
		getInstanceShareInvoiceTbls().add(instanceShareInvoiceTbl);
		instanceShareInvoiceTbl.setInstanceShareTbl(this);

		return instanceShareInvoiceTbl;
	}

	public InstanceShareInvoiceTbl removeInstanceShareInvoiceTbl(InstanceShareInvoiceTbl instanceShareInvoiceTbl) {
		getInstanceShareInvoiceTbls().remove(instanceShareInvoiceTbl);
		instanceShareInvoiceTbl.setInstanceShareTbl(null);

		return instanceShareInvoiceTbl;
	}

}