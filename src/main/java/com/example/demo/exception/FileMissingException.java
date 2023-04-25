package com.example.demo.exception;

import org.springframework.http.HttpStatus;

import com.example.demo.common.BaseRuntimeException;

public class FileMissingException extends BaseRuntimeException {

    public FileMissingException() {
        super("Product image cannot be blank", HttpStatus.BAD_REQUEST);
    }

    // Constructor with message and status code
    public FileMissingException(String message, HttpStatus status) {
        super(message, status);
    }
}
