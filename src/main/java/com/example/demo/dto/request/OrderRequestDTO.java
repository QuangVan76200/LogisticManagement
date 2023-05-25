package com.example.demo.dto.request;

import java.math.BigDecimal;

import com.example.demo.enums.OrderType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequestDTO {
	private OrderType orderType;
	private OrderUserDTO user;
	private String productCode;
	private Integer quantity;

	private BigDecimal discount;

}
