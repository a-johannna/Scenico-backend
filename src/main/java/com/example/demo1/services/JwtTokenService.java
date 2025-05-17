package com.example.demo1.services;

import com.example.demo1.config.JwtConfiguration;
import com.example.demo1.models.entidades.UserModel;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.stream.Collectors;

@Service
public class JwtTokenService {

    private final JwtConfiguration jwtConfiguration;
    private final SecretKey key;

    public JwtTokenService(JwtConfiguration jwtConfiguration) {
        this.jwtConfiguration = jwtConfiguration;
        this.key = Keys.hmacShaKeyFor(jwtConfiguration.getJwtSecret().getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(UserModel userModel) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtConfiguration.getJwtExpirationMs());

        // Obtener el rol directamente desde el enum typeUser
        String role = userModel.getTypeUser() != null ? userModel.getTypeUser().name() : "USER";

        return Jwts.builder()
                .subject(userModel.getUsername())
                .claim("role", role)
                .claim("userId", userModel.getIdUser())
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(key)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public String getUsernameFromToken(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }
}