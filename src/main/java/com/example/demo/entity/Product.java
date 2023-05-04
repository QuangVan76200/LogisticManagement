package com.example.demo.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

import com.example.demo.enums.MeasurementUnit;

import lombok.Data;

@Entity
@DynamicUpdate
@DynamicInsert
@Data
@Table(name = "Product", uniqueConstraints = { @UniqueConstraint(columnNames = { "productCode" }) })
public class Product implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ProductID")
	private Long productId;

	@Column(name = "Name", nullable = false)
	private String name;

	@Column(name = "Description")
	private String description;

	@Column(name = "Price", nullable = false)
	private BigDecimal price;

	@Column(name = "Quantity", nullable = false)
	private Integer quantity;

	@Column(name = "Weight", nullable = false)
	private BigDecimal weight;

	@Enumerated(EnumType.STRING)
	@Column(name = "measurement_unit")
	private MeasurementUnit measurementUnit;

	private String productCode;

	@Lob
	@Column(name = "avatar")
	private String avatar;

	@ElementCollection
	@Column(name = "images")
	private List<String> images;

	@OneToMany(mappedBy = "productId", cascade = CascadeType.ALL)
	private Set<OrderItem> orderItem;

	@OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
	private List<Stock> listStock;
	
	
	public void addStock(Stock stock) {
		listStock.add(stock);
		stock.setProduct(this);
	}

}
