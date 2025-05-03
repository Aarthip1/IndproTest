package com.example.indproTest.exception;

public class UnauthorizedException extends BaseException {
    public UnauthorizedException(String resource) {
        super("authorization", "unauthorized", String.format("You are not authorized to access this %s", resource));
    }
}
