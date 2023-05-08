package com.example.demo.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.Shelf;

@Repository
public interface IShelfDao extends JpaRepository<Shelf, Long>{
	
	@Query(value = "SELECT *\r\n"
			+ "FROM shelf\r\n"
			+ "WHERE (SELECT COUNT(*) FROM stock WHERE stock.shelfid = shelf.shelfid) < shelf.capacity", nativeQuery = true)
	List<Shelf> findAvailableShelves();
	


}
