package com.example.indproTest.exception;

import lombok.Getter;

@Getter
public class BaseException extends RuntimeException {
    private final String field;
    private final String code;

    public BaseException(String field, String code, String message) {
        super(message);
        this.field = field;
        this.code = code;
    }

    public BaseException(String field, String message) {
        this(field, "", message);
    }
}
