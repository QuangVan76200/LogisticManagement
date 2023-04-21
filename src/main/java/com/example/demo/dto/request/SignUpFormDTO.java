package com.example.demo.dto.request;

import java.util.Map;
import java.util.Set;

import lombok.Data;

@Data
public class SignUpFormDTO {

	Map<String, String> requestMap;
	Set<String> roles;

	public SignUpFormDTO() {

	}

	public SignUpFormDTO(Map<String, String> requestMap, Set<String> roles) {
		this.requestMap = requestMap;
		this.roles = roles;
	}

}
