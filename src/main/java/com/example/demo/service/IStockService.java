package com.example.demo.service;

import java.util.List;

import com.example.demo.dto.response.StockCountByDateDTO;

public interface IStockService {
	List<StockCountByDateDTO> getStockCountByDate(Long warehouseId);

}
