package com.example.demo.dto.request;

import com.example.demo.entity.Stock;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
public class StockDTO {
	private Long stockId;
	private ProductDTO product;
	@JsonIgnore
	private ShelfDTO shelf;

	public StockDTO() {
	}

	public StockDTO(Stock stock) {
		this.stockId = stock.getStockId();
		this.product = new ProductDTO(stock.getProduct());
		this.shelf = new ShelfDTO(stock.getShelf());
	}


}
