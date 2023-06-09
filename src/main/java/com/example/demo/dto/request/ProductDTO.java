package com.example.demo.dto.request;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.mock.web.MockMultipartFile;

import com.example.demo.entity.Product;
import com.example.demo.entity.Stock;
import com.example.demo.enums.MeasurementUnit;
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
	private int quantity;
	private BigDecimal weight;
	private MeasurementUnit measurementUnit;
	@JsonIgnore
	private List<StockDTO> listStock = new ArrayList<>();

	@JsonIgnore
	private MultipartFile avatar;

	@JsonIgnore
	private List<MultipartFile> images;

	public ProductDTO(Product product) {
		this.productId = product.getProductId();
		this.name = product.getName();
		this.productCode = product.getProductCode();
		this.description = product.getDescription();
		this.price = product.getPrice();
		this.quantity = product.getQuantity();
		this.weight = product.getWeight();
		this.measurementUnit = product.getMeasurementUnit();
		byte[] avatarBytes = product.getAvatar().getBytes();

		Resource avatarResource = new ByteArrayResource(avatarBytes);
		MultipartFile avatarMultipart = new MockMultipartFile("filename", avatarBytes);
		this.avatar = avatarMultipart;


	}

	public void setImages(List<MultipartFile> images) {
		this.images = images;
	}

	public List<MultipartFile> getImages() {
		return images;
	}

}
