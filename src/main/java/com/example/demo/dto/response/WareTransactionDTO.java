package com.example.demo.dto.response;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;

import com.example.demo.entity.WareTransaction;
import com.example.demo.entity.WareTransactionDetail;
import com.example.demo.enums.WareTransactionType;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class WareTransactionDTO {
	
	private Long transactionId;

	private UserDTO user;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss")
	private LocalDateTime transactionDate;

	private WareTransactionType transactionType;

	private String note;

	private List<WareTransactionDetailDTO> transactionDetails = new ArrayList<>();

	public WareTransactionDTO() {
	}

	public WareTransactionDTO(WareTransaction wareTransaction) {
		BeanUtils.copyProperties(wareTransaction, this, "user");
		List<WareTransactionDetail> details = wareTransaction.getTransactionDetails();
		if (details != null) {
			details.forEach(detail -> {
				WareTransactionDetailDTO deatailDTO = new WareTransactionDetailDTO(detail);

				this.transactionDetails.add(deatailDTO);
			});
		}
		if (wareTransaction != null) {
			this.user = new UserDTO(wareTransaction.getUser());
		}
	}

}
