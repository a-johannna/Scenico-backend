package com.example.demo1.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class JwtConfiguration {
    @Value("${jwt.secret:UeL53jvW0BpTLmW2ePzR4mKnTz9cQxY3}")
    private String jwtSecret;

    @Value("${jwt.expiration:86400000}") // 24 horas en milisegundos
    private int jwtExpirationMs;

}