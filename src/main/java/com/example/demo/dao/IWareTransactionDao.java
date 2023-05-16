package com.example.demo.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.dto.response.WareTransactionDTO;
import com.example.demo.entity.WareTransaction;

@Repository
public interface IWareTransactionDao extends JpaRepository<WareTransaction, Long> {

	@Query(value = "select w.* from ware_transaction w inner join users u on w.user_id = u.user_id where u.user_name= :userName ", nativeQuery = true)
	List<WareTransaction> findByUserUserName(@Param("userName") String userName);

}
