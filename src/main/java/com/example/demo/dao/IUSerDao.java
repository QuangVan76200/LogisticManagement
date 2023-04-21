package com.example.demo.dao;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.dto.response.UserDTO;
import com.example.demo.entity.User;

/**
 * DAO layer processing for requests from Service layer
 * 
 * @author VanLQ
 */

@Repository
public interface IUSerDao extends JpaRepository<User, Long> {

	// get data, email from database
	@Query(value = "select * from users as u where u.email = :email", nativeQuery = true)
	User findByEmail(@Param("email") String email);

	// check user is exist in database
	Optional<User> findByUserName(String userName);

	// check create data, username has exists in database?
	Boolean existsByUserName(String userName);

	// check create data, email has exists in database?
	Boolean existsByEmail(String email);

	@Query(value = "select * from users", nativeQuery = true)
	List<User> listAllUsers();

	@Query(value = "update users set status = :status where user_id = :userId", nativeQuery = true)
	@Transactional
	@Modifying
	Integer updateStatus(@Param("status") Boolean status, @Param("userId") Long userId);

	@Query(value = "select u.email"
			+ "		from users u "
			+ "		inner join user_role ur on u.user_id = ur.user_id "
			+ "		inner join roles r on ur.roles_id = r.rolesid "
			+ "		where r.role_name = 'ADMIN'", nativeQuery = true)
	List<String> getAllAdmin();

}
