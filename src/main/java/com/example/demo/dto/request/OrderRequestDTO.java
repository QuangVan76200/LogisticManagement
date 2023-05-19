package com.example.demo.dto.request;

import java.time.LocalDateTime;

import com.example.demo.enums.OrderStatusType;
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
	private OrderStatusType orderStatus;
	private LocalDateTime orderDate;

}
