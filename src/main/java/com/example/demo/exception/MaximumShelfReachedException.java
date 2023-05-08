package com.example.demo.exception;

import org.springframework.http.HttpStatus;

import com.example.demo.common.BaseRuntimeException;

public class MaximumShelfReachedException extends BaseRuntimeException{

	
	public MaximumShelfReachedException() {
        super("Size of Shelf is Full", HttpStatus.BAD_REQUEST);
    }

    // Constructor with message and status code
    public MaximumShelfReachedException(String message, HttpStatus status) {
        super(message, status);
    }
}
