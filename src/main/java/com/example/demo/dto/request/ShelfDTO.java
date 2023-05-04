package com.example.demo.dto.request;

import java.util.List;
import java.util.stream.Collectors;

import com.example.demo.entity.Shelf;

import lombok.Data;

@Data
public class ShelfDTO {
    
	private Long shelfId;
	private String name;
	private String location;
	private Integer capacity;
	private WarehouseDTO warehouse;
	private List<StockDTO> stocks;

	public ShelfDTO() {
	}

	public ShelfDTO(Shelf shelf) {
	    this.shelfId = shelf.getShelfId();
	    this.name = shelf.getName();
	    this.location = shelf.getLocation();
	    this.capacity = shelf.getCapacity();
	    this.warehouse = new WarehouseDTO(shelf.getWarehouse());
	    this.stocks = shelf.getListStock().stream().map(StockDTO::new).collect(Collectors.toList());
	}

	// Getters and setters

}
