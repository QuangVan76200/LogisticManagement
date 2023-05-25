package com.example.demo.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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

import com.example.demo.enums.OrderStatusType;
import com.example.demo.enums.OrderType;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Entity
@DynamicUpdate
@DynamicInsert
@Data

@Table(name = "orders")
public class Order implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "OrderID")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false, referencedColumnName = "userId")
	private User user;

	@Column(name = "OrderDate", nullable = false)
	private LocalDateTime orderDate;

	@Enumerated(EnumType.STRING)
	@Column(name = "Status", nullable = false, length = 20)
	private OrderStatusType status;

	@Enumerated(EnumType.STRING)
	@Column(name = "TypeOrder", nullable = false, length = 50)
	private OrderType typeOrder;

	@OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
	private List<Invoice> invoices = new ArrayList<>();

	@OneToMany(mappedBy = "orderId", cascade = CascadeType.ALL)
	@JsonIgnore
	private List<OrderItem> orderItem = new ArrayList<>();;

	public void addInvoice(Invoice invoice) {
		invoices.add(invoice);
		invoice.setOrder(this);
	}

	@Override
	public String toString() {
		return "Order [id=" + id + ", user=" + user + ", orderDate=" + orderDate + ", status=" + status + ", typeOrder="
				+ typeOrder + "]";
	}

}
