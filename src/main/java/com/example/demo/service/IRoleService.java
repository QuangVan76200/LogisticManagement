package com.example.demo.service;

import java.util.List;
import java.util.Optional;

import com.example.demo.entity.Role;
import com.example.demo.entity.RoleName;

public interface IRoleService {

	Optional<Role> findByRoleName(RoleName rolename);
}
