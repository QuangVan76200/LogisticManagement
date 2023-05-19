package com.example.demo.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.dto.response.StockCountByDateDTO;
import com.example.demo.entity.Stock;

@Repository
public interface IStockDao extends JpaRepository<Stock, Long> {

	@Query(value = "SELECT w.name AS warehouseName, s.name AS shelfName, COUNT(*) AS totalStock" +
	        " FROM Warehouse w" +
	        " INNER JOIN Shelf s ON w.WarehouseID = s.WarehouseID" +
	        " INNER JOIN Stock st ON s.ShelfID = st.ShelfID" +
	        " WHERE w.warehouseid = :warehouseId AND CONVERT(DATE, st.last_update_date) = CONVERT(DATE, GETDATE())" +
	        " GROUP BY w.name, s.name", nativeQuery = true)
	List<Object[]> getStockInfoByShelf(@Param("warehouseId") Long warehouseId);

}
