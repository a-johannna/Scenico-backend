package com.example.demo1.models.dtos.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;


@Data
public class LoginResponseDTO {
    private String token;
    private String type = "Bearer";
    private Long userId;
    private String username;
    private String roles;

    public LoginResponseDTO(String token, Long userId, String username, String roles) {
        this.token = token;
        this.userId = userId;
        this.username = username;
        this.roles = roles;
    }
}