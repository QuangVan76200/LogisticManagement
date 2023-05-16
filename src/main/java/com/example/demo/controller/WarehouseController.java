package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.constants.CafeConstants;
import com.example.demo.dao.IWarehouseDao;
import com.example.demo.dto.request.WarehouseDTO;
import com.example.demo.jwt.JWTFilter;
import com.example.demo.service.IWarehouseService;
import com.example.demo.utils.CafeUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * request Controller
 * 
 * @author VanLQ
 */

@Slf4j
@RestController
@RequestMapping("/api/auth/warehouse")
public class WarehouseController {

	@Autowired
	private IWarehouseDao warehouseDao;

	@Autowired
	private IWarehouseService warehouseService;

	@Autowired
	JWTFilter jwtFilter;

	@PostMapping(path = "/newWareHouse")
	public ResponseEntity<?> newWareHouse(@RequestBody(required = true) WarehouseDTO warehouseDTO) {
		try {
			log.info("Inside new Warehouse Controller. WarehouseDTO: {}", warehouseDTO);

			if (jwtFilter.isManager() || jwtFilter.isAdmin()) {
				return CafeUtils.getResponseData("Successfull", HttpStatus.OK,
						warehouseService.newWarehouse(warehouseDTO));
			}
		} catch (Exception e) {
			log.error("An error occurred during warehouse creation: " + e.getMessage(), e);
		}
		return CafeUtils.getResponseData(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.BAD_REQUEST, null);
	}

	@GetMapping(path = "/findById/{id}")
	public WarehouseDTO findById(@PathVariable Long id) {
		return this.warehouseService.findById(id);

	}
}
