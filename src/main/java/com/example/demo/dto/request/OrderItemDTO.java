package com.example.demo.dto.request;

import java.math.BigDecimal;

import com.example.demo.dto.response.OrderDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemDTO {

	private Long id;

	private BigDecimal totalAmount;

	private OrderDTO orderId;

	private ProductDTO productId;
	
	private int quantity;
}
