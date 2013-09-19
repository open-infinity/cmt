package ssp.domain.entity;

import java.math.BigDecimal;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.NonNull;

import org.springframework.format.annotation.NumberFormat;

@Data
public class CatalogueProduct {
	
	private Product product;
	
	@NumberFormat @DecimalMin("0.00") @NotNull @NonNull
	private BigDecimal price;

}
