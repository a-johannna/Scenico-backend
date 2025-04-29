package com.example.demo1.services;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordEncoderService {

    private final BCryptPasswordEncoder passwordEncoder;

    public PasswordEncoderService() {
        this.passwordEncoder = new BCryptPasswordEncoder();

    }

    public String encodePassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }
}
