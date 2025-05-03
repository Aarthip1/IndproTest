package com.example.indproTest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JwtAuthResponse {
    private String token;
    private String type = "Bearer";
    private String username;

    public JwtAuthResponse(String token, String username) {
        this.token = token;
        this.username = username;
    }
}
