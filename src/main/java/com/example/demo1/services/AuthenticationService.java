package com.example.demo1.services;

import com.example.demo1.models.entidades.UserModel;
import com.example.demo1.repositories.IUserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final IUserRepository userRepository;

    public AuthenticationService(AuthenticationManager authenticationManager, 
                               IUserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
    }

    public UserModel authenticate(String username, String password) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(username, password)
        );
        
        return userRepository.findByUsername(username)
            .orElseThrow(() -> new IllegalStateException("Usuario no encontrado: " + username));
    }
}