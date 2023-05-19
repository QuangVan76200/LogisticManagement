package com.example.demo.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.response.StockCountByDateDTO;
import com.example.demo.dto.response.WareTransactionDTO;
import com.example.demo.jwt.JWTFilter;
import com.example.demo.service.IStockService;
import com.example.demo.utils.CafeUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * request Controller
 * 
 * @author VanLQ
 */

@Slf4j
@RestController
@RequestMapping("/api/auth/stock")
public class StockController {

	@Autowired
	private IStockService stockService;

	@Autowired
	JWTFilter jwtFilter;

	@GetMapping(path = "/getStockCountByDate/{warehouseId}")
	ResponseEntity<?> listWarehouseByUserName(@PathVariable Long warehouseId) {
		try {
			if (jwtFilter.isStaff() || jwtFilter.isManager() || jwtFilter.isCustomer()) {
				List<StockCountByDateDTO> getStockCountByDate = stockService.getStockCountByDate(warehouseId);
				return CafeUtils.getResponseData("succesfully", HttpStatus.OK, getStockCountByDate);
			} else {
				return new ResponseEntity<List<WareTransactionDTO>>(new ArrayList<>(), HttpStatus.UNAUTHORIZED);
			}
		} catch (Exception e) {
			log.error("An error occurred while getting the list of waretransaction: " + e.getMessage(), e);
			return new ResponseEntity<List<WareTransactionDTO>>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	}

}
