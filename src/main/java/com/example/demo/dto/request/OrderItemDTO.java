package com.example.demo.dto.request;

import java.math.BigDecimal;

import org.springframework.beans.BeanUtils;

import com.example.demo.dto.response.OrderDTO;
import com.example.demo.entity.OrderItem;

import lombok.Data;

@Data
public class OrderItemDTO {

	private Long id;

	private BigDecimal totalAmount;

	private OrderDTO orderId;

	private ProductDTO productId;

	private int quantity;

	public OrderItemDTO() {

	}

	public OrderItemDTO(OrderItem item) {
		BeanUtils.copyProperties(item, this);
		this.productId = new ProductDTO(item.getProductId());
	}
}
