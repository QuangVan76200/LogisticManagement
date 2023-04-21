package com.example.demo.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.Role;
import com.example.demo.entity.RoleName;

/**
 * DAO layer processing for requests from Service layer
 * 
 * @author VanLQ
 */

@Repository
public interface IRoleDao extends JpaRepository<Role, Integer> {
	Optional<Role> findByRoleName(RoleName rolename);
}
