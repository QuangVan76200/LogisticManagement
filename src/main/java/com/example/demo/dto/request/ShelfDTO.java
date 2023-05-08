package com.example.demo.dto.request;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.example.demo.entity.Shelf;
import com.example.demo.entity.Stock;

import lombok.Data;

@Data
public class ShelfDTO {

	private String name;
	private String location;
	private Integer capacity;
	private WarehouseDTO warehouse;
	private List<StockDTO> listStock;

	public ShelfDTO() {
	}

	public ShelfDTO(Shelf shelf) {
		this.name = shelf.getName();
		this.location = shelf.getLocation();
		this.capacity = shelf.getCapacity();
		shelf.getWarehouse();
		this.warehouse = new WarehouseDTO(shelf.getWarehouse());
		this.listStock = new ArrayList<>();
		List<Stock> listStock = shelf.getListStock();
		if (listStock != null && shelf.getListStock().size() >= 0) {
			listStock.forEach(stock -> {
				StockDTO stockDTO = new StockDTO();
				this.listStock.add(stockDTO);
			});
		}
	}

	// Getters and setters

}
