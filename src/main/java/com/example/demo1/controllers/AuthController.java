package com.example.demo1.controllers;

import com.example.demo1.models.dtos.auth.LoginRequestDTO;
import com.example.demo1.models.dtos.auth.LoginResponseDTO;
import com.example.demo1.models.entidades.UserModel;
import com.example.demo1.services.AuthenticationService;
import com.example.demo1.services.EmailService;
import com.example.demo1.services.JwtTokenService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
public class AuthController {

    private final AuthenticationService authenticationService;
    private final JwtTokenService jwtTokenService;
    private final EmailService emailService;

    public AuthController(AuthenticationService authenticationService, JwtTokenService jwtTokenService, EmailService emailService) {
        this.authenticationService = authenticationService;
        this.jwtTokenService = jwtTokenService;
        this.emailService = emailService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDTO loginRequest) {
        try {
            UserModel user = authenticationService.authenticate(
                loginRequest.getEmail(),
                loginRequest.getPassword()
            );

            String jwt = jwtTokenService.generateToken(user);

            // Obtener el único rol del usuario
            String role = user.getTypeUser() != null
                    ? user.getTypeUser().name()
                    : "UNASSIGNED";

            // Devolver la respuesta como DTO
            return ResponseEntity.ok(new LoginResponseDTO(
                    jwt,
                    user.getIdUser(),
                    user.getUsername(),
                    role
            ));

        }  catch (BadCredentialsException e) {
            return ResponseEntity.badRequest().body("Credenciales inválidas");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error en el servidor: " + e.getMessage());
        }

    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody LoginRequestDTO request) {
        UserModel user = authenticationService.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        String token = UUID.randomUUID().toString(); // Puedes almacenar esto en DB si lo necesitas
        String resetLink = "http://localhost:4200/auth/reset-password?token=" + token;

        emailService.sendResetEmail(user.getEmail(), "Restablece tu contraseña",
                "<p>Haz clic en el siguiente enlace para restablecer tu contraseña:</p>" +
                        "<a href=\"" + resetLink + "\">Restablecer contraseña</a>");

        return ResponseEntity.ok("Correo enviado");
    }


}