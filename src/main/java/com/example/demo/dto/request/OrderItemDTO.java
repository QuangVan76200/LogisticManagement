package com.example.demo.dto.request;

import org.springframework.beans.BeanUtils;

import com.example.demo.dto.response.OrderDTO;
import com.example.demo.entity.OrderItem;

import lombok.Data;

@Data
public class OrderItemDTO {

	private Long id;

	private Long orderId;

	private ProductDTO productId;

	private int quantity;

	public OrderItemDTO() {

	}

	public OrderItemDTO(OrderItem item) {
		BeanUtils.copyProperties(item, this);
		this.productId = new ProductDTO(item.getProductId());
		this.orderId = item.getOrderId().getId();
	}
}
