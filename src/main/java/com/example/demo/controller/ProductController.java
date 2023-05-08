package com.example.demo.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.constants.CafeConstants;
import com.example.demo.dao.IProductDao;
import com.example.demo.dto.request.ProductDTO;
import com.example.demo.dto.request.ProductSearchCriteriaDTO;
import com.example.demo.dto.response.UserDTO;
import com.example.demo.jwt.JWTFilter;
import com.example.demo.service.IProductService;
import com.example.demo.utils.CafeUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * request Controller
 * 
 * @author VanLQ
 */

@Slf4j
@RestController
@RequestMapping("/api/auth/product")
public class ProductController {

	@Autowired
	private IProductDao productDao;

	@Autowired
	private IProductService productService;

	@Autowired
	JWTFilter jwtFilter;

	@GetMapping(path = "/listAll")
	ResponseEntity<?> findAll() {
		try {
			if (jwtFilter.isStaff() || jwtFilter.isManager() || jwtFilter.isCustomer()) {
				List<ProductDTO> listAllProduct = productService.findAll();
				return CafeUtils.getResponseData("succesfully", HttpStatus.OK, listAllProduct);
			} else {
				return new ResponseEntity<List<ProductDTO>>(new ArrayList<>(), HttpStatus.UNAUTHORIZED);
			}
		} catch (Exception e) {
			log.error("An error occurred while getting the list of products: " + e.getMessage(), e);
		}
		return new ResponseEntity<List<ProductDTO>>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@PostMapping(path = "/newProduct")
	ResponseEntity<?> newProduct(@ModelAttribute ProductDTO productDTO) {
		log.info("Inside newProductController. ProductDTO: {}", productDTO);
		try {
			System.out.println( jwtFilter.isStaff());
			if ( jwtFilter.isStaff() || jwtFilter.isManager()) {
				return CafeUtils.getResponseData("succesfully", HttpStatus.OK, productService.newProduct(productDTO));
			}
		} catch (Exception e) {
			log.error("An error occurred during product creation: " + e.getMessage(), e);
		}

		return CafeUtils.getResponseData(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.BAD_REQUEST, null);
	}

	@PostMapping("/delete/{id}")
	public void deleteById(@PathVariable Long id) {
		this.productService.deleteById(id);
	}

	@PostMapping(path = "/search")
	public ResponseEntity<?> searchProduct(@RequestBody(required = true) ProductSearchCriteriaDTO criteria) {
		try {
			if (jwtFilter.isStaff() || jwtFilter.isManager() || jwtFilter.isCustomer()) {
				List<ProductDTO> searchPRoduct = productService.searchProduct(criteria);
				if(searchPRoduct.isEmpty()) {
					return CafeUtils.getResponseData("There is no product as you requested", HttpStatus.OK, searchPRoduct);
				}
				return CafeUtils.getResponseData("succesfully", HttpStatus.OK, searchPRoduct);
			} else {
				return new ResponseEntity<List<ProductDTO>>(new ArrayList<>(), HttpStatus.UNAUTHORIZED);
			}
		} catch (Exception e) {
			log.error("Search maybe is wrongh: " + e.getMessage(), e);
		}
		return CafeUtils.getResponseData(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.BAD_REQUEST, null);
	}

}
