package com.example.demo1.models.dtos.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequestDTO {
    @NotBlank(message = "El username es obligatorio")
    private String username;
    
    @NotBlank(message = "La contraseña es obligatoria")
    private String password;
}