package com.example.demo.dto.request;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.entity.Product;
import com.example.demo.enums.MeasurementUnit;

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

	private MultipartFile avatar;

	private List<MultipartFile> images;

	public ProductDTO(Product product) {
		BeanUtils.copyProperties(product, this);
	}

	public void setImages(List<MultipartFile> images) {
		this.images = images;
	}

	public List<MultipartFile> getImages() {
		return images;
	}

}
