package com.example.demo.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.example.demo.enums.InvoiceStatusType;

import lombok.Data;

@Entity
@DynamicUpdate
@DynamicInsert
@Data

@Table(name = "invoice")
public class Invoice implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "invoice_id")
	private Long id;

	@ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false, referencedColumnName = "userId")
	private User user;

	// Date create Invoice
	private LocalDateTime date;

	// Payment due date for the invoice.
	private LocalDateTime dueDate;

	private BigDecimal total;

	private BigDecimal tax;

	private BigDecimal discount;

	@Column(name = "Status", nullable = false, length = 20)
	private InvoiceStatusType status;

}
