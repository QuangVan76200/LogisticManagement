package com.example.demo.serviceimpl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dao.IStockDao;
import com.example.demo.dto.response.StockCountByDateDTO;
import com.example.demo.service.IStockService;

@Service
public class StockServiceImpl implements IStockService {

	@Autowired
	private IStockDao stockDao;

	@Override
	public List<StockCountByDateDTO> getStockCountByDate(Long warehouseId) {

		List<Object[]> results = stockDao.getStockInfoByShelf(warehouseId);
		List<StockCountByDateDTO> stockInfo = new ArrayList<>();

		for (Object[] result : results) {
			String warehouseName = (String) result[0];
			String shelfName = (String) result[1];
			Integer totalStock = (Integer) result[2];

			StockCountByDateDTO dto = new StockCountByDateDTO(warehouseName, shelfName, totalStock);
			stockInfo.add(dto);
		}

		return stockInfo;
	}

}
