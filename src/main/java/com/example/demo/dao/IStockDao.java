package com.example.demo.dao;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.dto.response.StockDTO;
import com.example.demo.entity.Stock;

@Repository
public interface IStockDao extends JpaRepository<Stock, Long> {

//	@Query(value = "")
//	 List<StockDTO> findStockByProductAndDate(@Param("productCode") String productCode, @Param("date") LocalDateTime date);
}
