package com.example.demo.dto.response;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Set;

import com.example.demo.dto.request.InvoiceDTO;
import com.example.demo.dto.request.OrderItemDTO;
import com.example.demo.dto.request.OrderUserDTO;
import com.example.demo.dto.request.ProductDTO;
import com.example.demo.enums.OrderStatusType;
import com.example.demo.enums.OrderType;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {

	private OrderType orderType;
	private OrderUserDTO user;
	private List<ProductDTO> productsOrder;
	private BigDecimal totalAmount;
	private OrderStatusType orderStatus;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date orderDate;
	
	private Set<InvoiceDTO> invoices;

	private List<OrderItemDTO> orderItems;

}
