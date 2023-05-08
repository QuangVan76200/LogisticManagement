package com.example.demo.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.Warehouse;

@Repository
public interface IWarehouseDao extends JpaRepository<Warehouse, Long>{
	
	Optional<Warehouse> findByName(String name);

}
