package com.example.demo.jwt;

import java.util.ArrayList;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.demo.dao.IUSerDao;
import com.example.demo.entity.User;
import com.example.demo.security.userprical.UserPrinciple;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserDetailService implements UserDetailsService {

	@Autowired
	IUSerDao userDao;

	private User userDetail;

	@Override
	@Transactional
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		log.info("inside loadByUserName");
		User user = userDao.findByUserName(username)
				.orElseThrow(() -> new UsernameNotFoundException("user not found -> username or password " + username));
		userDetail = user;
		return UserPrinciple.build(user);
	}

	public User getUserDetail() {
		return userDetail;
	}

}
