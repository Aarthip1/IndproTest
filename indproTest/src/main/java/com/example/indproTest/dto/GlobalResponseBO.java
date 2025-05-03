package com.example.indproTest.dto;

import com.example.indproTest.utils.APIConstants;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class GlobalResponseBO {
    private Integer code = APIConstants.SUCCESS;
    private String message = APIConstants.SUCCESS_STRING;
    private List<ErrorBO> errors = new ArrayList<>();
    private Boolean success = true;
    private Object data;
    private LocalDateTime timestamp = LocalDateTime.now();

    public GlobalResponseBO(Object data) {
        this.data = data;
    }

    public GlobalResponseBO(String errorField, String errorMessage) {
        this.code = APIConstants.BAD_REQUEST;
        this.message = APIConstants.BAD_REQUEST_STRING;
        this.success = false;
        this.errors = Arrays.asList(new ErrorBO(errorField, errorMessage));
    }

    public GlobalResponseBO(Integer code, String message, String errorField, String errorMessage) {
        this.code = code;
        this.message = message;
        this.success = false;
        this.errors = Arrays.asList(new ErrorBO(errorField, errorMessage));
    }

    public static ResponseEntity<GlobalResponseBO> ok(Object data) {
        GlobalResponseBO response = new GlobalResponseBO(data);
        return ResponseEntity.ok(response);
    }

    public static ResponseEntity<GlobalResponseBO> error(String field, String message) {
        GlobalResponseBO response = new GlobalResponseBO(field, message);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    public static ResponseEntity<GlobalResponseBO> error(Integer code, String message, String field, String errorMessage) {
        GlobalResponseBO response = new GlobalResponseBO(code, message, field, errorMessage);
        return ResponseEntity.status(code).body(response);
    }

    public static ResponseEntity<GlobalResponseBO> unauthorized() {
        GlobalResponseBO response = new GlobalResponseBO(
            APIConstants.UNAUTHORIZED,
            APIConstants.UNAUTHORIZED_STRING,
            "auth",
            "Invalid or expired token"
        );
        return ResponseEntity.status(APIConstants.UNAUTHORIZED).body(response);
    }

    public static ResponseEntity<GlobalResponseBO> conflict(String field, String message) {
        GlobalResponseBO response = new GlobalResponseBO(
            APIConstants.CONFLICT,
            APIConstants.CONFLICT_STRING,
            field,
            message
        );
        return ResponseEntity.status(APIConstants.CONFLICT).body(response);
    }

    public static ResponseEntity<GlobalResponseBO> notFound(String field, String message) {
        GlobalResponseBO response = new GlobalResponseBO(
            APIConstants.NOT_FOUND,
            APIConstants.NOT_FOUND_STRING,
            field,
            message
        );
        return ResponseEntity.status(APIConstants.NOT_FOUND).body(response);
    }
}
