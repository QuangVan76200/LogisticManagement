package com.example.demo.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.WareTransaction;

@Repository
public interface IWareTransactionDao extends JpaRepository<WareTransaction, Long> {

}
