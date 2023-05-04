package com.example.demo.dto.request;

import java.math.BigDecimal;
import java.util.Date;

import com.example.demo.enums.InvoiceStatusType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceDTO {

	private static final long serialVersionUID = 1L;

	private Long id;

	private Long orderId;

	private Long userId;

	private Date date;

	private Date dueDate;

	private BigDecimal total;

	private BigDecimal tax;

	private BigDecimal discount;

	private InvoiceStatusType status;

	// getters and setters
}
