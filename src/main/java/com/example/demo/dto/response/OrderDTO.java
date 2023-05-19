package com.example.demo.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;

import org.springframework.beans.BeanUtils;

import com.example.demo.dto.request.InvoiceDTO;
import com.example.demo.dto.request.OrderItemDTO;
import com.example.demo.dto.request.OrderUserDTO;
import com.example.demo.dto.request.ProductDTO;
import com.example.demo.entity.Order;
import com.example.demo.entity.OrderItem;
import com.example.demo.enums.OrderStatusType;
import com.example.demo.enums.OrderType;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class OrderDTO {

	private OrderType orderType;
	private OrderUserDTO user;
	private List<ProductDTO> productsOrder;
	private BigDecimal totalAmount;
	private OrderStatusType orderStatus;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime orderDate;

	private Set<InvoiceDTO> invoices;

	private List<OrderItemDTO> orderItems;

	public OrderDTO() {

	}

	public OrderDTO(Order order) {
		BeanUtils.copyProperties(order, this, "user");
		List<OrderItem> items = order.getOrderItem();
		if (items != null) {
			items.forEach(item -> {
				OrderItemDTO orderItemDTO = new OrderItemDTO(item);
				this.orderItems.add(orderItemDTO);
			});
		}
	}

}
