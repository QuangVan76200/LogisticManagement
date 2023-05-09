package com.example.demo.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.Order;

@Repository
public interface IOrderDao extends JpaRepository<Order, Long> {

}