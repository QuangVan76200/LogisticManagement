package com.example.demo.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import lombok.Data;

@Entity
@DynamicUpdate
@DynamicInsert
@Data
@Table(name = "WareTransactionDetail")
public class WareTransactionDetail implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "TransactionDetailID")
	private Long transactionDetailId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "TransactionID", nullable = false, referencedColumnName = "TransactionID")
	private WareTransaction wareTransaction;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "StockID", nullable = false, referencedColumnName = "StockID")
	private Stock stock;

	@Column(name = "Quantity", nullable = false)
	private Integer quantity;

	// constructors, getters, and setters
}
