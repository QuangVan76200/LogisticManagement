package com.example.demo.serviceimpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.example.demo.dao.IShelfDao;
import com.example.demo.dao.IUSerDao;
import com.example.demo.dao.IWarehouseDao;
import com.example.demo.dto.request.ShelfDTO;
import com.example.demo.dto.request.WarehouseDTO;
import com.example.demo.entity.Shelf;
import com.example.demo.entity.Stock;
import com.example.demo.entity.Warehouse;
import com.example.demo.exception.MaximumShelfReachedException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.exception.WarehouseNotFoundException;
import com.example.demo.jwt.JWTFilter;
import com.example.demo.service.IShelfService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Transactional
public class SheflServiceImpl implements IShelfService {

	@Autowired
	IShelfDao shelfDao;

	@Autowired
	private IUSerDao userDao;

	@Autowired
	private JWTFilter jwtFilter;

	@Autowired
	private IWarehouseDao warehouseDao;

	@Override
	public ShelfDTO findById(Long id) {
		// TODO Auto-generated method stub
		Shelf shelf = shelfDao.findById(id).orElseThrow(() -> new ResourceNotFoundException(id));
		return new ShelfDTO(shelf);
	}

	@Override
	public List<ShelfDTO> findAll() {
		// TODO Auto-generated method stub
		return shelfDao.findAll().stream().map(shelf -> new ShelfDTO(shelf)).collect(Collectors.toList());
	}

	@Override
	public ShelfDTO newShelf(ShelfDTO shelfDto) {
		Optional<Warehouse> optionalWarehouse = warehouseDao.findByName(shelfDto.getWarehouse().getName());
		if (optionalWarehouse.isEmpty()) {
			throw new WarehouseNotFoundException();
		} else {
			Warehouse warehouse = optionalWarehouse.get();

			List<Shelf> shelves = warehouse.getShelves();

			if (shelves == null) {
				shelves = new ArrayList<>();
			}

			if (shelves.size() >= 10) {
				throw new MaximumShelfReachedException("Maximum number of shelves in the warehouse reached",
						HttpStatus.BAD_REQUEST);
			}

			Shelf lastShelf = shelves.isEmpty() ? null : shelves.get(shelves.size() - 1);
			
			

			int lastNumb = lastShelf == null ? 1 : Integer.parseInt(lastShelf.getName().split("_")[1].substring(1))+1;
			int lastClocal = lastShelf == null ? 1 : Integer.parseInt(lastShelf.getLocation().substring(9)) + 1;

			Shelf shelf = new Shelf();
			shelf.setName(warehouse.getName() + String.format("%02d", lastNumb));
			shelf.setLocation("Location " + String.format("%02d", lastClocal));
			shelf.setCapacity(10);
			shelf.setWarehouse(warehouse);
			shelf.setListStock(new ArrayList<>(10));

			shelfDao.save(shelf);

			shelves.add(shelf);
			warehouseDao.save(warehouse);

			ShelfDTO result = new ShelfDTO(shelf);
			BeanUtils.copyProperties(shelf, result);
			return result;

		}

	}

	@Override
	public void deleteById(Long id) {
		shelfDao.deleteById(id);

	}

	@Override
	public boolean isShelfEmpty(Shelf shelf) {
		return shelf.getListStock() == null || shelf.getListStock().size() < shelf.getCapacity();
	}

}
