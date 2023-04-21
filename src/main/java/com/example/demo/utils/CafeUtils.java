package com.example.demo.utils;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public abstract class CafeUtils {
	
	private CafeUtils() {
		
	}
	
	public static ResponseEntity<String> getResponse(String message, HttpStatus httpStatus) {
		return new ResponseEntity<String>(message, httpStatus);
	}

}
