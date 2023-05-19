package com.example.demo.serviceimpl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dao.IShelfDao;
import com.example.demo.dao.IUSerDao;
import com.example.demo.dao.IWarehouseDao;
import com.example.demo.dto.request.ShelfDTO;
import com.example.demo.dto.request.WarehouseDTO;
import com.example.demo.entity.Shelf;
import com.example.demo.entity.User;
import com.example.demo.entity.Warehouse;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.jwt.JWTFilter;
import com.example.demo.service.IWarehouseService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Transactional
public class WarehouseServiceImpl implements IWarehouseService {

	@Autowired
	IWarehouseDao warehouseDao;

	@Autowired
	private IUSerDao userDao;

	@Autowired
	private JWTFilter jwtFilter;

	@Autowired
	private IShelfDao shelfDao;

	@Override
	public WarehouseDTO findById(Long id) {
		Warehouse warehouse = warehouseDao.findById(id).orElseThrow(() -> new ResourceNotFoundException(id));
		System.out.println("print warehouse " + warehouse);
		return new WarehouseDTO(warehouse);
	}

	@Override
	public List<WarehouseDTO> findAll() {

		return warehouseDao.findAll().stream().map(warehouse -> new WarehouseDTO(warehouse))
				.collect(Collectors.toList());
	}

	@Override
	public WarehouseDTO newWarehouse(WarehouseDTO warehouseDto) {
		log.info("inside new ware hosue ", warehouseDto);

		String userName = warehouseDto.getManager().getUserName();
		User currentManager = userDao.findByUserName(userName).orElseThrow(() -> new RuntimeException("User invalid"));

		Warehouse warehouse = new Warehouse();

		warehouse.setName(warehouseDto.getName());
		warehouse.setAddress(warehouseDto.getAddress());
		warehouse.setManager(currentManager);
		warehouse.setAddress(warehouse.getAddress());

		// Set max capacity of shelves to 50
		int maxCapacity = 10;

		List<Shelf> shelfDTOs = new ArrayList<>();

		if (shelfDTOs.isEmpty()) {
			// Create new shelves
			warehouse.setShelves(new ArrayList<>());
			for (int i = 1; i <= maxCapacity; i++) {
				Shelf shelf = new Shelf();
				shelf.setName(warehouse.getName() + String.format("%02d", i));
				shelf.setLocation("Location " + String.format("%02d", i));
				shelf.setCapacity(10);
				shelf.setWarehouse(warehouse);
				shelf.setListStock(new ArrayList<>(10));
				shelfDao.save(shelf);
				ShelfDTO shelfDTO = new ShelfDTO();
				BeanUtils.copyProperties(shelf, shelfDTO);

				if (warehouse.getShelves() == null) {
					warehouse.setShelves(new ArrayList<>());
				}
				shelfDTOs.add(shelf);

			}
			warehouse.setShelves(shelfDTOs);
		}
		warehouseDao.save(warehouse);
		WarehouseDTO warehouseDTO2 = new WarehouseDTO();
		BeanUtils.copyProperties(warehouse, warehouseDTO2);
		return warehouseDTO2;
	}

	@Override
	public void deleteById(Long id) {
		// TODO Auto-generated method stub
		warehouseDao.deleteById(id);
	}

}
