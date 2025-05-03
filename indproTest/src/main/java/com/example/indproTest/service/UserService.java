package com.example.indproTest.service;

import com.example.indproTest.dto.SignUpRequest;
import com.example.indproTest.entity.User;
import com.example.indproTest.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User createUser(SignUpRequest signUpRequest) {
        // Check if username exists
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            throw new RuntimeException("Username '" + signUpRequest.getUsername() + "' is already taken");
        }

        // Check if email exists
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new RuntimeException("Email '" + signUpRequest.getEmail() + "' is already registered");
        }

        User user = new User();
        user.setUsername(signUpRequest.getUsername());
        user.setEmail(signUpRequest.getEmail());
        user.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));

        return userRepository.save(user);
    }
}
