package com.example.demo.dto.request;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;

import com.example.demo.dto.response.UserDTO;
import com.example.demo.dto.response.WareTransactionDetailDTO;
import com.example.demo.entity.WareTransaction;
import com.example.demo.entity.WareTransactionDetail;
import com.example.demo.enums.WareTransactionType;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class WareTransactionRequestDTO {

	private Long transactionId;
	
	private UserDTO user;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss")
	private LocalDateTime transactionDate;

	private WareTransactionType transactionType;

	private String note;
}
