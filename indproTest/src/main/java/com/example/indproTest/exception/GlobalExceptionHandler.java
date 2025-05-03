package com.example.indproTest.exception;

import com.example.indproTest.dto.GlobalResponseBO;
import com.example.indproTest.utils.APIConstants;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<GlobalResponseBO> handleResourceNotFoundException(ResourceNotFoundException ex) {
        log.error("Resource not found: {}", ex.getMessage(), ex);
        return GlobalResponseBO.error(
            APIConstants.NOT_FOUND,
            APIConstants.NOT_FOUND_STRING,
            "resource",
            "Resource not found"
        );
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<GlobalResponseBO> handleUnauthorizedException(UnauthorizedException ex) {
        log.error("Unauthorized access: {}", ex.getMessage(), ex);
        return GlobalResponseBO.error(
            APIConstants.FORBIDDEN,
            APIConstants.FORBIDDEN_STRING,
            "access",
            "You don't have permission to perform this action"
        );
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<GlobalResponseBO> handleBadCredentialsException(BadCredentialsException ex) {
        log.error("Invalid credentials: {}", ex.getMessage(), ex);
        return GlobalResponseBO.error(
            APIConstants.UNAUTHORIZED,
            APIConstants.UNAUTHORIZED_STRING,
            "credentials",
            "Invalid username or password"
        );
    }

    @ExceptionHandler({SignatureException.class, ExpiredJwtException.class})
    public ResponseEntity<GlobalResponseBO> handleJwtException(Exception ex) {
        log.error("JWT token error: {}", ex.getMessage(), ex);
        return GlobalResponseBO.error(
            APIConstants.UNAUTHORIZED,
            APIConstants.UNAUTHORIZED_STRING,
            "token",
            "Invalid or expired token"
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<GlobalResponseBO> handleValidationException(MethodArgumentNotValidException ex) {
        String field = ex.getBindingResult().getFieldError() != null ?
                      ex.getBindingResult().getFieldError().getField() : "validation";
        String message = ex.getBindingResult().getFieldError() != null ?
                        ex.getBindingResult().getFieldError().getDefaultMessage() : "Validation failed";
        
        log.error("Validation error on field '{}': {}", field, message, ex);
        return GlobalResponseBO.error(
            APIConstants.BAD_REQUEST,
            APIConstants.BAD_REQUEST_STRING,
            field,
            message
        );
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<GlobalResponseBO> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        log.error("Data integrity violation: {}", ex.getMessage(), ex);
        return GlobalResponseBO.error(
            APIConstants.CONFLICT,
            APIConstants.CONFLICT_STRING,
            "data",
            "Data integrity violation. Please check your input."
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<GlobalResponseBO> handleGenericException(Exception ex) {
        log.error("Unexpected error: {}", ex.getMessage(), ex);
        return GlobalResponseBO.error(
            APIConstants.SERVER_ERROR,
            APIConstants.SERVER_ERROR_STRING,
            "server",
            "An unexpected error occurred. Please try again later."
        );
    }
}