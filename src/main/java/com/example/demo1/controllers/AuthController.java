package com.example.demo1.controllers;

import com.example.demo1.models.dtos.auth.LoginRequestDTO;
import com.example.demo1.models.dtos.auth.LoginResponseDTO;
import com.example.demo1.models.entidades.UserModel;
import com.example.demo1.services.AuthenticationService;
import com.example.demo1.services.JwtTokenService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
public class AuthController {

    private final AuthenticationService authenticationService;
    private final JwtTokenService jwtTokenService;

    public AuthController(AuthenticationService authenticationService, JwtTokenService jwtTokenService) {
        this.authenticationService = authenticationService;
        this.jwtTokenService = jwtTokenService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDTO loginRequest) {
        try {
            UserModel user = authenticationService.authenticate(
                loginRequest.getUsername(), 
                loginRequest.getPassword()
            );

            String jwt = jwtTokenService.generateToken(user);
            String roles = user.getRoles().stream()
                    .map(role -> role.getName().name())
                    .reduce((a, b) -> a + "," + b)
                    .orElse("");

            return ResponseEntity.ok(new LoginResponseDTO(
                jwt,
                user.getIdUser(),
                user.getUsername(),
                roles
            ));
        }  catch (BadCredentialsException e) {
            return ResponseEntity.badRequest().body("Credenciales inválidas");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error en el servidor: " + e.getMessage());
        }

    }
}