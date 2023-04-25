package com.example.demo.dto.request;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductSearchCriteriaDTO {

	private String name;
	private BigDecimal price;
	private BigDecimal weight;

}
