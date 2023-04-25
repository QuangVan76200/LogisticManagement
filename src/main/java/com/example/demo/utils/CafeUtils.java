package com.example.demo.utils;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public abstract class CafeUtils {

	private CafeUtils() {

	}

	public static ResponseEntity<String> getResponse(String message, HttpStatus httpStatus) {
		return new ResponseEntity<String>(message, httpStatus);
	}

	public static ResponseEntity<Object> getResponseData(String message, HttpStatus httpStatus, Object data) {
		Map<String, Object> response = new HashMap<>();
		response.put("message", message);
		response.put("data", data);
		return ResponseEntity.status(httpStatus).body(response);
	}

}
