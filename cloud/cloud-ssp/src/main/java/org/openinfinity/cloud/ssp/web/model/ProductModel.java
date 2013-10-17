package org.openinfinity.cloud.ssp.web.model;

import lombok.Data;

import org.openinfinity.cloud.domain.ssp.Subscription;

@Data
public class ProductModel {
	
	private Subscription product;
	
	private Long uptime;
	
	private Long cost;

}
