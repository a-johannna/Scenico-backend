package com.example.demo1.services;

import com.example.demo1.models.entidades.UserModel;
import com.example.demo1.repositories.IUserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import java.util.Optional;


@Service
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final IUserRepository userRepository;
    private final JwtTokenService jwtTokenService;

    public AuthenticationService(AuthenticationManager authenticationManager,
                                 IUserRepository userRepository, JwtTokenService jwtTokenService) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.jwtTokenService = jwtTokenService;
    }

    public UserModel authenticate(String email, String password) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(email, password)
        );
        
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new IllegalStateException("Email  no encontrado: " + email));
    }

    public Optional<UserModel> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }




}