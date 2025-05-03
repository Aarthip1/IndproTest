package com.example.indproTest.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ErrorBO {
    private String field = "";
    private String code = "";
    private String message = "";

    public ErrorBO(String field, String message) {
        this.field = field;
        this.message = message;
    }

    public static ErrorBO of(String field, String code, String message) {
        return new ErrorBO(field, code, message);
    }

    public static ErrorBO of(String field, String message) {
        return new ErrorBO(field, message);
    }
}
