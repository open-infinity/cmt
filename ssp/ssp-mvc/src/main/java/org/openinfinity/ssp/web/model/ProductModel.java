package org.openinfinity.ssp.web.model;

import lombok.Data;
import org.openinfinity.domain.entity.Product;

@Data
public class ProductModel {
	
	private Product product;
	
	private Long uptime;
	
	private Long cost;

}
