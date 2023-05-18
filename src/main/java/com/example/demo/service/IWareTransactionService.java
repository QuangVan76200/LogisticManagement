package com.example.demo.service;

import java.util.List;

import com.example.demo.dto.request.WareTransactionRequestDTO;
import com.example.demo.dto.response.WareTransactionDTO;

public interface IWareTransactionService {

	WareTransactionDTO findById(Long id);

	List<WareTransactionDTO> findAll();

	WareTransactionDTO updateWarehouseTransaction(WareTransactionRequestDTO wareTransactionDto);

	void deleteById(Long id);

	List<WareTransactionDTO> findByUserAndExportData(String userName, String filePath);

}
