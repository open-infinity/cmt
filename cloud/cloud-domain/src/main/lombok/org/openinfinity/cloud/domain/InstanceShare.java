package org.openinfinity.cloud.domain;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

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
public class InstanceShare implements Serializable {
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

	@NotNull(message="Start of period is mandatory")
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="period_start")
	private Date periodStart;

	//bi-directional many-to-one association to InstanceShareDetailTbl
	@OneToMany(mappedBy="instanceShare",cascade=CascadeType.ALL)
	private List<InstanceShareDetail> instanceShareDetails;

	//bi-directional many-to-one association to InstanceShareInvoiceTbl
	@OneToMany(mappedBy="instanceShare",cascade={CascadeType.PERSIST,
                CascadeType.REFRESH,CascadeType.MERGE})
	private List<InstanceShareInvoice> instanceShareInvoices;

	//bi-directional many-to-one association to InstanceTbl
	@ManyToOne
	@JoinColumn(name="instance_id")
	private InstanceTbl instanceTbl;

	public InstanceShare() {
	}

	public InstanceShareDetail addInstanceShareDetail(InstanceShareDetail instanceShareDetail) {
	    if (instanceShareDetails==null){
	        instanceShareDetails=new ArrayList<InstanceShareDetail>();
	    }
		getInstanceShareDetails().add(instanceShareDetail);
		instanceShareDetail.setInstanceShare(this);

		return instanceShareDetail;
	}

	public InstanceShareDetail removeInstanceShareDetail(InstanceShareDetail instanceShareDetailTbl) {
		getInstanceShareDetails().remove(instanceShareDetailTbl);
		instanceShareDetailTbl.setInstanceShare(null);

		return instanceShareDetailTbl;
	}

	public InstanceShareInvoice addInstanceShareInvoice(InstanceShareInvoice instanceShareInvoiceTbl) {
	    if (instanceShareInvoices==null){
	        instanceShareInvoices=new ArrayList<InstanceShareInvoice>();
	    }
		getInstanceShareInvoices().add(instanceShareInvoiceTbl);
		instanceShareInvoiceTbl.setInstanceShare(this);

		return instanceShareInvoiceTbl;
	}

	public InstanceShareInvoice removeInstanceShareInvoice(InstanceShareInvoice instanceShareInvoiceTbl) {
		getInstanceShareInvoices().remove(instanceShareInvoiceTbl);
		instanceShareInvoiceTbl.setInstanceShare(null);

		return instanceShareInvoiceTbl;
	}
	
	public BigDecimal calculateSharePercent(){
	    BigDecimal ret = new BigDecimal("0");
	    for (InstanceShareDetail detail: this.instanceShareDetails){
	        ret=ret.add(detail.getSharePercent());
	    }
	    
	    //round up to 2 decimal places (causes 99.999 to round up to 100)
	    return ret.setScale(2, BigDecimal.ROUND_HALF_UP);
	}
	
	public String toCSV(String delimiter) {
		return periodStart.toString();
	}

}