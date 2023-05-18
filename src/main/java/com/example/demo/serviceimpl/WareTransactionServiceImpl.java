package com.example.demo.serviceimpl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.common.ExcelExporter;
import com.example.demo.dao.IWareTransactionDao;
import com.example.demo.dto.request.WareTransactionRequestDTO;
import com.example.demo.dto.response.UserDTO;
import com.example.demo.dto.response.WareTransactionDTO;
import com.example.demo.entity.User;
import com.example.demo.entity.WareTransaction;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.service.IWareTransactionService;

@Service
public class WareTransactionServiceImpl implements IWareTransactionService {

	private final String STRING_PATH = "E:\\TraiiningHTML\\ProjectE-commerce\\export_file";

	@Autowired
	private IWareTransactionDao transactionDao;

	@Autowired
	private ExcelExporter excelUtils;

	@Override
	public WareTransactionDTO findById(Long id) {
		WareTransaction wareTransaction = transactionDao.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException(id));
		return new WareTransactionDTO(wareTransaction);
	}

	@Override
	public List<WareTransactionDTO> findAll() {
		return transactionDao.findAll().stream().map(ware -> new WareTransactionDTO(ware)).collect(Collectors.toList());
	}

	@Override
	public void deleteById(Long id) {
		// TODO Auto-generated method stub
		transactionDao.deleteById(id);

	}

	@Override
	public WareTransactionDTO updateWarehouseTransaction(WareTransactionRequestDTO wareTransactionDto) {
		Long id = wareTransactionDto.getTransactionId();

		WareTransaction currTransaction = transactionDao.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException(id));

		if (wareTransactionDto.getUser() != null) {
			UserDTO userDTO = wareTransactionDto.getUser();
			User setUser = new User();
			BeanUtils.copyProperties(userDTO, setUser);
			currTransaction.setUser(setUser);
		}

		if (wareTransactionDto.getTransactionDate() != null) {
			currTransaction.setTransactionDate(wareTransactionDto.getTransactionDate());
		}

		if (wareTransactionDto.getTransactionType() != null) {
			currTransaction.setTransactionType(wareTransactionDto.getTransactionType());
		}

		if (wareTransactionDto.getNote() != null) {
			currTransaction.setNote(wareTransactionDto.getNote());
		}

		transactionDao.save(currTransaction);

		WareTransactionDTO wareTransactionDTO = new WareTransactionDTO(currTransaction);
		return wareTransactionDTO;
	}

		@Override
		public List<WareTransactionDTO> findByUserAndExportData(String userName, String filePath) {
	
			List<WareTransactionDTO> transactions = transactionDao.findByUserUserName(userName).stream()
					.map(warehouse -> new WareTransactionDTO(warehouse)).collect(Collectors.toList());
	
			try {
				filePath = STRING_PATH;
				ExcelExporter.exportToExcel(transactions, filePath);
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			return transactions;
		}

}
