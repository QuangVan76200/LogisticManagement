package com.example.demo.dto.response;

import com.example.demo.dto.request.StockDTO;
import com.example.demo.entity.WareTransactionDetail;

import lombok.Data;

@Data
public class WareTransactionDetailDTO {

	private Long transactionDetailId;

	private StockDTO stock;

	public WareTransactionDetailDTO() {
		
	}

	public WareTransactionDetailDTO(WareTransactionDetail detail) {
		this.transactionDetailId = detail.getTransactionDetailId();
		this.stock = new StockDTO(detail.getStock());

	}
}
