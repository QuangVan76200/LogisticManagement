package com.example.demo.service;

import java.util.List;

import com.example.demo.dto.request.ShelfDTO;
import com.example.demo.entity.Shelf;

public interface IShelfService {

	ShelfDTO findById(Long id);

	List<ShelfDTO> findAll();

	ShelfDTO newShelf(ShelfDTO shelfDto);

	void deleteById(Long id);
	
	public boolean isShelfEmpty(Shelf shelf);

}
