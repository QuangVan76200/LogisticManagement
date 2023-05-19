package com.example.demo.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.example.demo.dto.request.StockDTO;
import com.example.demo.entity.Product;
import com.example.demo.enums.MeasurementUnit;
import com.example.demo.enums.ProductStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {

	private Long productId;
	private String name;
	private String productCode;
	private String description;
	private BigDecimal price;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss")
	private LocalDateTime rerceiptDate;
	private int quantity;
	private BigDecimal weight;
	private ProductStatus productStatus;
	private MeasurementUnit measurementUnit;
	@JsonIgnore
	private List<StockDTO> listStock;

	private String avatar;

	private List<String> images;

	public ProductDTO(Product product) {
		this.productId = product.getProductId();
		this.name = product.getName();
		this.productCode = product.getProductCode();
		this.description = product.getDescription();
		this.price = product.getPrice();
		this.quantity = product.getQuantity();
		this.weight = product.getWeight();
		this.rerceiptDate = product.getRerceiptDate();
		this.productStatus = product.getProductStatus();
		this.measurementUnit = product.getMeasurementUnit();
		this.avatar = product.getAvatar();
		this.images = product.getImages();
		this.listStock = product.getListStock().stream().map(StockDTO::new).collect(Collectors.toList());
	}

}
