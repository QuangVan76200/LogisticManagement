package com.example.demo.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.constants.CafeConstants;
import com.example.demo.dao.IOrderDao;
import com.example.demo.dto.request.OrderRequestDTO;
import com.example.demo.dto.request.ProductDTO;
import com.example.demo.dto.response.OrderDTO;
import com.example.demo.jwt.JWTFilter;
import com.example.demo.service.IOrderService;
import com.example.demo.utils.CafeUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * request Controller
 * 
 * @author VanLQ
 */

@Slf4j
@RestController
@RequestMapping("/api/auth/order")
public class OrderController {

	@Autowired
	private IOrderDao orderDao;

	private final IOrderService orderService;

	public OrderController(IOrderService orderService) {
		this.orderService = orderService;
	}

	@Autowired
	JWTFilter jwtFilter;

	@PostMapping(path = "/newOrder")
	public ResponseEntity<?> newOrder(@RequestBody(required = true) OrderRequestDTO orderDTO) {
		log.info("Inside newOrderController. OrderDTO: {}", orderDTO);
		try {

//			log.info("print " + !orderDTO.getOrderType().toString().equals("PURCHASE_ORDER"));
			if (jwtFilter.isStaff() || jwtFilter.isManager()) {
				return CafeUtils.getResponseData("Successfully", HttpStatus.OK, orderService.newOrder(orderDTO));
			}

		} catch (Exception e) {
			log.error("An error occurred during order creation: " + e.getMessage(), e);
		}
		return CafeUtils.getResponseData(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.BAD_REQUEST, null);
	}

	@GetMapping(path = "/listAll")
	ResponseEntity<?> findAll() {
		try {
			if (jwtFilter.isStaff() || jwtFilter.isManager() || jwtFilter.isCustomer()) {
				List<OrderDTO> listAllOrders = orderService.findAll();
				return CafeUtils.getResponseData("succesfully", HttpStatus.OK, listAllOrders);
			} else {
				return new ResponseEntity<List<ProductDTO>>(new ArrayList<>(), HttpStatus.UNAUTHORIZED);
			}
		} catch (Exception e) {
			log.error("An error occurred while getting the list of orders: " + e.getMessage(), e);
		}
		return new ResponseEntity<List<ProductDTO>>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@GetMapping(path = "/listOrderByOrderDate")
	ResponseEntity<?> listOrderByOrderDate(
			@RequestParam("fromDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date fromDate,
			@RequestParam("toDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date toDate) {
		try {
			if (jwtFilter.isStaff() || jwtFilter.isManager() || jwtFilter.isCustomer()) {
				List<OrderDTO> listAllOrders = orderService.listOrderByOrderDate(fromDate, toDate);
				return CafeUtils.getResponseData("succesfully", HttpStatus.OK, listAllOrders);
			} else {
				return new ResponseEntity<List<ProductDTO>>(new ArrayList<>(), HttpStatus.UNAUTHORIZED);
			}
		} catch (Exception e) {
			log.error("An error occurred while getting the list of orders: " + e.getMessage(), e);
		}
		return new ResponseEntity<List<ProductDTO>>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
	}

}
