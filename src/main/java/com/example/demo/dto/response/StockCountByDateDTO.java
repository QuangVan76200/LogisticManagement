package com.example.demo.dto.response;

import javax.persistence.Column;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockCountByDateDTO {

	@Column(name = "warehouseName")
    private String warehouseName;
    
    @Column(name = "shelfName")
    private String shelfName;
    
    @Column(name = "totalStock")
    private Integer totalStock;

}
