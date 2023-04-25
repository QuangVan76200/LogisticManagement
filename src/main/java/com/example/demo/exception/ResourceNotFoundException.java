package com.example.demo.exception;

import org.springframework.http.HttpStatus;

import com.example.demo.common.BaseRuntimeException;

public class ResourceNotFoundException extends BaseRuntimeException {

    public ResourceNotFoundException(Long id) {
        super("Resource with " + id + " not found", HttpStatus.NOT_FOUND);
    }
}