/**
 * PasswordEncoderService.java
 * Proyecto: Scénico - Plataforma para artistas emergentes
 * Descripción: Servicio que encapsula la lógica de encriptación de contraseñas utilizando BCrypt.
 * Proporciona métodos para codificar contraseñas y verificar coincidencias seguras.
 * Autor: Andrea Johanna Villavicencio Lema
 * Fecha: Mayo de 2025
 * Email: johannna.villavicencio@gmail.com
 */
package com.example.demo1.services;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Servicio encargado de codificar y validar contraseñas utilizando el algoritmo BCrypt.
 */
@Service
public class PasswordEncoderService {

    private final BCryptPasswordEncoder passwordEncoder;

    /**
     * Constructor que inicializa el codificador BCrypt.
     */
    public PasswordEncoderService() {
        this.passwordEncoder = new BCryptPasswordEncoder();

    }

    /**
     * Codifica una contraseña en texto plano utilizando BCrypt.
     *
     * @param rawPassword contraseña sin codificar
     * @return contraseña codificada de forma segura
     */
    public String encodePassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    /**
     * Verifica si una contraseña en texto plano coincide con una codificada.
     *
     * @param rawPassword     contraseña sin codificar proporcionada por el usuario
     * @param encodedPassword contraseña codificada almacenada
     * @return true si coinciden, false en caso contrario
     */
    public boolean matches(String rawPassword, String encodedPassword) {
        return  passwordEncoder.matches(rawPassword, encodedPassword);
    }
}
