package com.example.demo.dto.request;

import java.util.ArrayList;
import java.util.List;

import com.example.demo.dto.response.UserDTO;
import com.example.demo.entity.Shelf;
import com.example.demo.entity.Warehouse;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
public class WarehouseDTO {

	private String name;
	private String address;
	@JsonIgnore
	private UserDTO manager;
	@JsonIgnore
	private List<ShelfDTO> shelves;

	public WarehouseDTO() {
	}

	public WarehouseDTO(Warehouse warehouse) {
		this.name = warehouse.getName();
		this.address = warehouse.getAddress();
		
		this.manager = new UserDTO(warehouse.getManager());
		this.shelves = new ArrayList<>();
		List<Shelf> shelves = warehouse.getShelves();
		 if (shelves != null && !shelves.isEmpty()) {
			shelves.forEach(shelf -> {
				ShelfDTO shelfDTO = new ShelfDTO();
				this.shelves.add(shelfDTO);
			});
		}
	}

	// Getters and setters

}
