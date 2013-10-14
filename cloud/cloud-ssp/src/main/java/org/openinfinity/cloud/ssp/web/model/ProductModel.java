package org.openinfinity.cloud.ssp.web.model;

import lombok.Data;

import org.openinfinity.cloud.domain.ssp.Product;

@Data
public class ProductModel {
	
	private Product product;
	
	private Long uptime;
	
	private Long cost;

}
