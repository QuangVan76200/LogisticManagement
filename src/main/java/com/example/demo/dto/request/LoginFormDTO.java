package com.example.demo.dto.request;

import java.util.Map;
import java.util.Set;

import lombok.Data;

@Data
public class LoginFormDTO {

	Map<String, String> requestMap;

	public LoginFormDTO() {

	}

	public LoginFormDTO(Map<String, String> requestMap) {
		this.requestMap = requestMap;
	}

}
