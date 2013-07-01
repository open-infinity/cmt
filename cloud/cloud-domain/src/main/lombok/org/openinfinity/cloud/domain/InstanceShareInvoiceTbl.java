package org.openinfinity.cloud.domain;

import java.io.Serializable;
import javax.persistence.*;

import java.sql.Timestamp;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;


/**
 * The persistent class for the instance_share_invoice_tbl database table.
 * 
 */
@Entity
@Getter
@Setter
@Table(name="instance_share_invoice_tbl")
public class InstanceShareInvoiceTbl implements Serializable {
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
	@Column(name="period_end")
	private Date periodEnd;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="period_start")
	private Date periodStart;

	@Column(name="total_usage")
	private int totalUsage;

	//bi-directional many-to-one association to InstanceShareTbl
	@ManyToOne
	@JoinColumn(name="instance_share_id")
	private InstanceShareTbl instanceShareTbl;

	public InstanceShareInvoiceTbl() {
	}


}