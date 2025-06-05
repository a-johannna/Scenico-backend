/**
 * JwtTokenService.java
 * Proyecto: Scénico - Plataforma para artistas emergentes
 * Descripción: Servicio responsable de la generación, validación y extracción de información
 * desde tokens JWT. Permite autenticar usuarios y obtener su información a partir del token.
 * Autor: Andrea Johanna Villavicencio Lema
 * Fecha: Mayo de 2025
 * Email: johannna.villavicencio@gmail.com
 */
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

/**
 * Servicio que gestiona la generación, validación y lectura de tokens JWT.
 * Utiliza la clave secreta definida en la configuración para firmar y verificar tokens.
 */
@Service
public class JwtTokenService {

    private final JwtConfiguration jwtConfiguration;
    private final SecretKey key;

    /**
     * Constructor que inicializa el servicio con la clave secreta para JWT.
     *
     * @param jwtConfiguration configuración externa con los valores de expiración y clave secreta
     */
    public JwtTokenService(JwtConfiguration jwtConfiguration) {
        this.jwtConfiguration = jwtConfiguration;
        this.key = Keys.hmacShaKeyFor(jwtConfiguration.getJwtSecret().getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Genera un token JWT para un usuario autenticado.
     * Incluye claims personalizados como UUID, ID y rol.
     *
     * @param userModel entidad del usuario autenticado
     * @return token JWT firmado
     */
    public String generateToken(UserModel userModel) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtConfiguration.getJwtExpirationMs());

        // Obtener el rol directamente desde el enum typeUser
        String role = userModel.getTypeUser() != null ? userModel.getTypeUser().name() : "USER";

        return Jwts.builder()
                .subject(userModel.getEmail())
                .claim("role", role)
                .claim("userId", userModel.getId_user())
                .claim("uuid", userModel.getUuid().toString())
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(key)
                .compact();
    }

    /**
     * Valida si un token JWT es correcto y no ha expirado.
     *
     * @param token token JWT a validar
     * @return true si el token es válido, false si es inválido o ha expirado
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Extrae el nombre de usuario (username) desde el token JWT.
     *
     * @param token token JWT válido
     * @return nombre de usuario contenido en el token
     */
    public String getUsernameFromToken(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    /**
     * Extrae el UUID del usuario desde el token JWT.
     *
     * @param token token JWT válido
     * @return UUID del usuario
     * @throws IllegalStateException si no se encuentra el campo "uuid"
     */
    public UUID getUuidFromToken(String token) {
        Claims claims = getClaims(token);
        String uuidString = claims.get("uuid", String.class);

        if (uuidString == null) {
            throw new IllegalStateException("El token no contiene UUID");
        }

        return UUID.fromString(uuidString);
    }

    /**
     * Extrae todos los claims del token JWT.
     *
     * @param token token JWT
     * @return objeto Claims con los datos del token
     * @throws IllegalStateException si el token es inválido
     */
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


    /**
     * Extrae el token JWT desde la cabecera Authorization de la solicitud actual.
     * Espera el formato "Bearer {token}".
     *
     * @return el token JWT como String
     * @throws IllegalStateException si el token no se encuentra o tiene formato incorrecto
     */
    public String resolveToken() {
        HttpServletRequest request =
                ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes()))
                        .getRequest();

        String bearerToken = request.getHeader("Authorization");

        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        throw new IllegalStateException("Token de autorización no encontrado o con formato incorrecto en la cabecera 'Authorization'.");
    }
}



