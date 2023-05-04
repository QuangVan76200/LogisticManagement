package com.example.demo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockDTO {

	private Long stockId;
	
	private Long productCode;

	private String productName;

	private Long totalQuantity;
}
