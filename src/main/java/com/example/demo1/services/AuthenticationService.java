package com.example.demo1.services;

import com.example.demo1.repositories.IUserRepository;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    private final PasswordEncoderService passwordEncoderService;
    private final IUserRepository userRepository;

    public AuthenticationService(PasswordEncoderService passwordEncoderService, IUserRepository userRepository) {
        this.passwordEncoderService = passwordEncoderService;
        this.userRepository = userRepository;
    }

    public boolean authenticateUser(String username, String rawPassword) {
        return userRepository.findByUsername(username)
                .map(userModel -> passwordEncoderService.matches(rawPassword, userModel.getPassword()))
                .orElse(false);
    }
}
