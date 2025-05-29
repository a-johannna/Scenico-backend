package com.example.demo1.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class JwtConfiguration {
    @Value("${jwt.secret:w3bh00ks$3cr3t_wi7h_m0r3_than_32_chars}")
//    @Value("${jwt.secret:clave-secreta-muy-segura-123}")
    private String jwtSecret;

    @Value("${jwt.expiration:86400000}") // 24 horas en milisegundos
    private int jwtExpirationMs;

}