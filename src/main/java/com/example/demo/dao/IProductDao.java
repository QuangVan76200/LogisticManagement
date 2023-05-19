package com.example.demo.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.Product;

@Repository
public interface IProductDao extends JpaRepository<Product, Long> {

//	@Query(value = "select * from product where name = :name")
	Product findByProductCode(String code);

}
