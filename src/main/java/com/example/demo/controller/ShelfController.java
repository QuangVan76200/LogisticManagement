package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.constants.CafeConstants;
import com.example.demo.dto.request.ShelfDTO;
import com.example.demo.jwt.JWTFilter;
import com.example.demo.service.IShelfService;
import com.example.demo.utils.CafeUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * request Controller
 * 
 * @author VanLQ
 */

@Slf4j
@RestController
@RequestMapping("/api/auth/shelf")
public class ShelfController {
	
	@Autowired
	private IShelfService iShelfService;
	
	@Autowired
	JWTFilter jwtFilter;

	
	@PostMapping(path = "/newShelf")
	public ResponseEntity<?> newShelf(@RequestBody(required = true) ShelfDTO shelfDTO) {
		log.info("Inside newShelfController. OrderDTO: {}", shelfDTO);
		
		try {
			if (jwtFilter.isStaff() || jwtFilter.isManager()) {
				return CafeUtils.getResponseData("Successfully", HttpStatus.OK, iShelfService.newShelf(shelfDTO));
			}
		} catch (Exception e) {
			log.error("An error occurred during shelf creation: " + e.getMessage(), e);
		}
		return CafeUtils.getResponseData(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.BAD_REQUEST, null);
	}
	
	
	@PostMapping("/delete/{id}")
	public void deleteById(@PathVariable Long id) {
		this.iShelfService.deleteById(id);
	}
}
