package com.example.demo.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;

import com.example.demo.dto.request.OrderRequestDTO;
import com.example.demo.dto.response.OrderDTO;

public interface IOrderService {

	OrderDTO findById(Long id);

	List<OrderDTO> findAll();

	ResponseEntity<?> newOrder(OrderRequestDTO orderDTO);

	public List<OrderDTO> listOrderByOrderDate(Date fromDate, Date toDate);

	ResponseEntity<?> updateStatusOrder(Map<String, String> request); // Id :Order and set new :Status

	ResponseEntity<?> updateInforOrder(OrderDTO orderDTO);

	public void processOrder(Long orderId);

	public void cancelOrder(Long orderId);

}
