package com.example.demo.dto.request;

import com.example.demo.entity.Stock;

import lombok.Data;

@Data
public class StockDTO {
	private Long stockId;
	private ProductDTO product;
	private ShelfDTO shelf;
	private Integer quantity;

	public StockDTO() {
	}

	public StockDTO(Stock stock) {
		this.stockId = stock.getStockId();
		this.product = new ProductDTO(stock.getProduct());
		this.shelf = new ShelfDTO(stock.getShelf());
		this.quantity = stock.getQuantity();
	}

	// Getters and setters

}
