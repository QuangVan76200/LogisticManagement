package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.constants.CafeConstants;
import com.example.demo.dao.IOrderDao;
import com.example.demo.dto.request.OrderRequestDTO;
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

	@Autowired
	private IOrderService orderService;

	@Autowired
	JWTFilter jwtFilter;

	@PostMapping(path = "/newOrder")
	public ResponseEntity<?> newOrder(@RequestBody(required = true) OrderRequestDTO orderDTO) {
		log.info("Inside newOrderController. OrderDTO: {}", orderDTO);
		try {
			if (jwtFilter.isStaff() || jwtFilter.isManager()) {
				return CafeUtils.getResponseData("Successfully", HttpStatus.OK, orderService.newOrder(orderDTO));
			}

		} catch (Exception e) {
			log.error("An error occurred during order creation: " + e.getMessage(), e);
		}
		return CafeUtils.getResponseData(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.BAD_REQUEST, null);
	}

}
