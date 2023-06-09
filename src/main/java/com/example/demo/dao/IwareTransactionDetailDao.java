package com.example.demo.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.WareTransactionDetail;

@Repository
public interface IwareTransactionDetailDao extends JpaRepository<WareTransactionDetail, Long> {

}
