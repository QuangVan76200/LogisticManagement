package com.example.demo.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.springframework.beans.BeanUtils;

import com.example.demo.dto.request.InvoiceRequestDTO;
import com.example.demo.dto.request.OrderItemDTO;
import com.example.demo.dto.request.OrderRequestDTO;
import com.example.demo.dto.request.OrderUserDTO;
import com.example.demo.dto.request.ProductDTO;
import com.example.demo.entity.Invoice;
import com.example.demo.enums.InvoiceStatusType;
import com.example.demo.enums.OrderStatusType;
import com.example.demo.enums.OrderType;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class InvoiceResponseDTO {

	private static final long serialVersionUID = 1L;

	private Long id;

	private OrderRequestDTO oderDTO;

	private OrderUserDTO user;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime date;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime dueDate;

	private BigDecimal total;

	private BigDecimal tax;

	private BigDecimal discount;

	private InvoiceStatusType status;

	public InvoiceResponseDTO(Invoice invoice) {
		BeanUtils.copyProperties(invoice, this, "user");
	}

}
