package com.example.demo.dto.request;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

import com.example.demo.enums.InvoiceStatusType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceRequestDTO {

	private static final long serialVersionUID = 1L;

	private OrderRequestDTO oderDTO;

	private OrderUserDTO user;

	private LocalDateTime date;

	private LocalDateTime dueDate;

	private BigDecimal total;

	private BigDecimal tax;

	private BigDecimal discount;

	private InvoiceStatusType status;

	// getters and setters
}
