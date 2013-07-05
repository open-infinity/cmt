package org.openinfinity.cloud.domain;

import java.io.Serializable;
import javax.persistence.*;

import java.sql.Timestamp;
import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;


/**
 * The persistent class for the instance_share_detail_tbl database table.
 * 
 */
@Entity
@Getter
@Setter
@Table(name="instance_share_detail_tbl")
public class InstanceShareDetail implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Long id;

	@Column(name="cost_pool")
	private String costPool;

	private Timestamp created;

	@Column(name="created_by")
	private int createdBy;

	private String description;

	private Timestamp modified;

	@Column(name="modified_by")
	private int modifiedBy;

	@Column(name="order_number")
	private String orderNumber;

	@Column(name="share_percent")
	private Integer sharePercent;

	//bi-directional many-to-one association to InstanceShareTbl
	@ManyToOne
	@JoinColumn(name="instance_share_id")
	private InstanceShare instanceShare;

	public InstanceShareDetail() {
	}

}