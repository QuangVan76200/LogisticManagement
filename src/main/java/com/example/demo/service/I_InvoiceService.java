package com.example.demo.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.example.demo.dto.request.InvoiceRequestDTO;
import com.example.demo.dto.response.InvoiceResponseDTO;

public interface I_InvoiceService {

	ResponseEntity<?> newInvoice(InvoiceRequestDTO invoice);

	InvoiceResponseDTO findById(Long id);
	
	List<InvoiceResponseDTO> listInvoiceByOrder();

}
