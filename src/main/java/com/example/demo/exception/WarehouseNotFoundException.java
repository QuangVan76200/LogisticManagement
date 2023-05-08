package com.example.demo.exception;

import org.springframework.http.HttpStatus;

import com.example.demo.common.BaseRuntimeException;

public class WarehouseNotFoundException extends BaseRuntimeException {
	
	public WarehouseNotFoundException() {
        super("Warehouse not Found Exception", HttpStatus.BAD_REQUEST);
    }

    // Constructor with message and status code
    public WarehouseNotFoundException(String message, HttpStatus status) {
        super(message, status);
    }

}
