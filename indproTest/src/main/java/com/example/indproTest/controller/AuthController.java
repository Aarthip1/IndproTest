package com.example.indproTest.controller;

import com.example.indproTest.dto.GlobalResponseBO;
import com.example.indproTest.dto.JwtAuthResponse;
import com.example.indproTest.dto.LoginRequest;
import com.example.indproTest.dto.SignUpRequest;
import com.example.indproTest.entity.User;
import com.example.indproTest.security.JwtTokenProvider;
import com.example.indproTest.service.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @PostMapping("/signup")
    public ResponseEntity<GlobalResponseBO> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {
        log.info("Processing signup request for username: {}", signUpRequest.getUsername());
        User user = userService.createUser(signUpRequest);
        log.info("User registered successfully: {}", user.getUsername());
        return GlobalResponseBO.ok(user);
    }

    @PostMapping("/login")
    public ResponseEntity<GlobalResponseBO> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        log.info("Processing login request for username: {}", loginRequest.getUsername());
        
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                loginRequest.getUsername(),
                loginRequest.getPassword()
            )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);
        
        log.info("User logged in successfully: {}", loginRequest.getUsername());
        return GlobalResponseBO.ok(new JwtAuthResponse(jwt, "Bearer", loginRequest.getUsername()));
    }
}
