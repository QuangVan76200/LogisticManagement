package com.example.demo.dto.response;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;

import com.example.demo.dto.request.InvoiceRequestDTO;
import com.example.demo.dto.request.OrderItemDTO;
import com.example.demo.dto.request.OrderUserDTO;
import com.example.demo.entity.Order;
import com.example.demo.entity.OrderItem;
import com.example.demo.enums.OrderStatusType;
import com.example.demo.enums.OrderType;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
public class OrderDTO {

	private Long id;
	private OrderType typeOrder;
	private OrderUserDTO user;
	private OrderStatusType status;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime orderDate;

	@JsonIgnore
	private List<InvoiceRequestDTO> invoices;

	private List<OrderItemDTO> orderItem = new ArrayList<>();

	public OrderDTO() {

	}

	public OrderDTO(Order order) {
		BeanUtils.copyProperties(order, this, "user");
		List<OrderItem> items = order.getOrderItem();
		if (items != null) {
			items.forEach(item -> {
				OrderItemDTO orderItemDTO = new OrderItemDTO(item);
				this.orderItem.add(orderItemDTO);
			});
		}
		if (order != null) {
			this.user = new OrderUserDTO(order.getUser().getUserName());
		}
	}

}
