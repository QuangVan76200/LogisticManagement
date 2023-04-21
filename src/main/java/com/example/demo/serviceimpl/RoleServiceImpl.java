package com.example.demo.serviceimpl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dao.IRoleDao;
import com.example.demo.entity.Role;
import com.example.demo.entity.RoleName;
import com.example.demo.service.IRoleService;

@Service
public class RoleServiceImpl implements IRoleService {

	@Autowired
	IRoleDao roleDao;

	@Override
	public Optional<Role> findByRoleName(RoleName rolename) {
		return roleDao.findByRoleName(rolename);
	}
}
