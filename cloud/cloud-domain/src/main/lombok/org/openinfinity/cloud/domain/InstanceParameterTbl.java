package org.openinfinity.cloud.domain;

import java.io.Serializable;
import javax.persistence.*;

import lombok.Getter;
import lombok.Setter;


/**
 * The persistent class for the instance_parameter_tbl database table.
 * 
 */
@Entity
@Getter
@Setter
@Table(name="instance_parameter_tbl")
public class InstanceParameterTbl implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Long id;

	private String pkey;

	private String pvalue;

	//bi-directional many-to-one association to InstanceTbl
	@ManyToOne
	@JoinColumn(name="instance_id")
	private InstanceTbl instanceTbl;

	public InstanceParameterTbl() {
	}

}