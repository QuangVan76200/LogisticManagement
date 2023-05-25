package com.example.demo.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.Invoice;

@Repository
public interface I_InvocieDao extends JpaRepository<Invoice, Long> {

}
