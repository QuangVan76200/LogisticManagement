package com.example.demo.dto.request;

import java.util.List;
import java.util.stream.Collectors;

import com.example.demo.dto.response.UserDTO;
import com.example.demo.entity.Warehouse;

import lombok.Data;

@Data
public class WarehouseDTO {
	
	private Long id;
	private String name;
	private String address;
	private UserDTO manager;
	private List<ShelfDTO> shelves;

	public WarehouseDTO() {
	}

	public WarehouseDTO(Warehouse warehouse) {
	    this.id = warehouse.getId();
	    this.name = warehouse.getName();
	    this.address = warehouse.getAddress();
	    this.manager = new UserDTO(warehouse.getManager());
	    this.shelves = warehouse.getShelves().stream().map(ShelfDTO::new).collect(Collectors.toList());
	}

	// Getters and setters

}
