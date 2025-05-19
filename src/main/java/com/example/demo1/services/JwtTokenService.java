package com.example.demo1.services;

import com.example.demo1.config.JwtConfiguration;
import com.example.demo1.models.entidades.UserModel;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

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
                .claim("userId", userModel.getId_user())
                .claim("uuid", userModel.getUuid().toString())
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

    public UUID getUuidFromToken(String token) {
        Claims claims = getClaims(token);
        String uuidString = claims.get("uuid", String.class);

        if (uuidString == null) {
            throw new IllegalStateException("El token no contiene UUID");
        }

        return UUID.fromString(uuidString);
    }

    private Claims getClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (JwtException | IllegalArgumentException e) {
            throw new IllegalStateException("Error al procesar el token: " + e.getMessage());
        }
    }


    public String resolveToken() {
        HttpServletRequest request =
                ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes()))
                        .getRequest();

        String bearerToken = request.getHeader("Authorization");

        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        return null;
    }


}