package com.example.demo.controller;

import java.util.ArrayList;
import java.util.List;

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
import com.example.demo.dto.request.ProductDTO;
import com.example.demo.dto.request.WareTransactionRequestDTO;
import com.example.demo.dto.request.WarehouseDTO;
import com.example.demo.dto.response.WareTransactionDTO;
import com.example.demo.jwt.JWTFilter;
import com.example.demo.service.IWareTransactionService;
import com.example.demo.utils.CafeUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * request Controller
 * 
 * @author VanLQ
 */

@Slf4j
@RestController
@RequestMapping("/api/auth/wareTransaction")
public class WareTransactionController {

	@Autowired
	JWTFilter jwtFilter;

	@Autowired
	private IWareTransactionService wareTransactionService;

	@GetMapping(path = "/listAll")
	ResponseEntity<?> findAll() {
		try {
			if (jwtFilter.isStaff() || jwtFilter.isManager() || jwtFilter.isCustomer()) {
				List<WareTransactionDTO> listAllProduct = wareTransactionService.findAll();
				return CafeUtils.getResponseData("succesfully", HttpStatus.OK, listAllProduct);
			} else {
				return new ResponseEntity<List<ProductDTO>>(new ArrayList<>(), HttpStatus.UNAUTHORIZED);
			}
		} catch (Exception e) {
			log.error("An error occurred while getting the list of products: " + e.getMessage(), e);
		}
		return new ResponseEntity<List<WareTransactionDTO>>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@GetMapping(path = "/findById/{id}")
	public WareTransactionDTO findById(@PathVariable Long id) {
		return this.wareTransactionService.findById(id);

	}

	@PostMapping(path = "/updateWareTransaction")
	public ResponseEntity<?> newWareHouse(@RequestBody(required = true) WareTransactionRequestDTO wareTransactionDTO) {
		try {
			log.info("Inside new WareTransaction Controller. WarehouseDTO: {}", wareTransactionDTO);

			if (jwtFilter.isManager() || jwtFilter.isAdmin()) {
				return CafeUtils.getResponseData("Successfull", HttpStatus.OK,
						wareTransactionService.updateWarehouseTransaction(wareTransactionDTO));
			}
		} catch (Exception e) {
			log.error("An error occurred during waretransaction creation: " + e.getMessage(), e);
		}
		return CafeUtils.getResponseData(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.BAD_REQUEST, null);
	}

	@GetMapping(path = "/findByUserName/{userName}")
	ResponseEntity<?> listWarehouseByUserName(@PathVariable String userName, String filePath) {
		try {
			if (jwtFilter.isStaff() || jwtFilter.isManager() || jwtFilter.isCustomer()) {
				List<WareTransactionDTO> listAllProduct = wareTransactionService.findByUserAndExportData(userName,
						filePath);
				return CafeUtils.getResponseData("succesfully", HttpStatus.OK, listAllProduct);
			} else {
				return new ResponseEntity<List<WareTransactionDTO>>(new ArrayList<>(), HttpStatus.UNAUTHORIZED);
			}
		} catch (Exception e) {
			log.error("An error occurred while getting the list of waretransaction: " + e.getMessage(), e);
		}
		return new ResponseEntity<List<WareTransactionDTO>>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
	}

}
