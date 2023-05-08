package com.example.demo.service;

import java.util.List;

import com.example.demo.dto.request.WarehouseDTO;

public interface IWarehouseService {

	WarehouseDTO findById(Long id);

    List<WarehouseDTO> findAll();

    WarehouseDTO newWarehouse(WarehouseDTO warehouseDto);

    void deleteById(Long id);
}
