/**
 * JwtConfiguration.java
 * Proyecto: Scénico - Plataforma para artistas emergentes
 * Descripción: Clase de configuración que carga los valores relacionados con los tokens JWT
 * desde el archivo de propiedades de la aplicación. Estos valores se utilizan para firmar los
 * tokens y controlar su tiempo de expiración.
 * Autor: Andrea Johanna Villavicencio Lema
 * Fecha: Mayo de 2025
 * Email: johannna.villavicencio@gmail.com
 */
package com.example.demo1.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;


/**
 * Configuración para el sistema de autenticación JWT.
 * Proporciona acceso a la clave secreta y al tiempo de expiración del token.
 */
@Getter
@Configuration
public class JwtConfiguration {

    /**
     * Clave secreta utilizada para firmar y verificar tokens JWT.
     * Si no se especifica en el archivo de configuración, se usa un valor por defecto.
     */
    @Value("${jwt.secret:w3bh00ks$3cr3t_wi7h_m0r3_than_32_chars}")
    private String jwtSecret;

    /**
     * Tiempo de expiración del token JWT en milisegundos.
     * Valor por defecto: 86400000 ms (24 horas).
     */
    @Value("${jwt.expiration:86400000}")
    private int jwtExpirationMs;

}