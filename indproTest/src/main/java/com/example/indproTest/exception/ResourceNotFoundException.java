package com.example.indproTest.exception;

public class ResourceNotFoundException extends BaseException {
    public ResourceNotFoundException(String resource, Long id) {
        super(resource.toLowerCase(), "not_found", String.format("%s with id %d not found", resource, id));
    }
}
