package com.example.demo.security.userprical;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.demo.entity.User;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class UserPrinciple implements UserDetails {
	private Long id;
	private String userName;
	private String fullName;
	@JsonIgnore
	private String password;
	private String email;
	private String contactNumber;
	private String avatar;
	private Collection<? extends GrantedAuthority> roles;
	
	public UserPrinciple(User targetUser) {
	}

	public UserPrinciple(Long id, String fullName, String userName, String password, String email, String contactNumber,
			String avatar, Collection<? extends GrantedAuthority> roles) {
		this.id = id;
		this.fullName = fullName;
		this.userName = userName;
		this.password = password;
		this.email = email;
		this.contactNumber = contactNumber;
		this.avatar = avatar;
		this.roles = roles;
	}
	
	public static UserPrinciple build(User user) {
		List<GrantedAuthority> authorities = user.getRoles().stream().map(role -> 
			new SimpleGrantedAuthority(role.getRoleName().name())).collect(Collectors.toList());
		return new UserPrinciple(
				user.getId(),
				user.getFullName(),
				user.getUserName(),
				user.getPassword(),
				user.getEmail(),
				user.getContactNumber(),
				user.getAvatar(),
				authorities
				);
				
	}
	
	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return roles;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getUsername() {
		return userName;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

}
