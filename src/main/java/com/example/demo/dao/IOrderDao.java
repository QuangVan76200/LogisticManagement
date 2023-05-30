package com.example.demo.dao;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.Order;

@Repository
public interface IOrderDao extends JpaRepository<Order, Long> {

	@Query(value = "select * from orders where  CONVERT(DATE, order_date) between :startDate and :endDate  order by order_date desc", nativeQuery = true)
	List<Order> findByOrderDateBetween(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

}
