/**
 * AuthenticationService.java
 * Proyecto: Scénico - Plataforma para artistas emergentes
 * Descripción: Servicio responsable de gestionar la autenticación de usuarios mediante Spring Security.
 * Permite validar credenciales, recuperar usuarios por email y sirve como base para la
 * generación de tokens JWT utilizados en el sistema.
 * Autor: Andrea Johanna Villavicencio Lema
 * Fecha: Mayo de 2025
 * Email: johannna.villavicencio@gmail.com
 */
package com.example.demo1.services;

import com.example.demo1.models.entidades.UserModel;
import com.example.demo1.repositories.IUserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import java.util.Optional;

/**
 * Servicio de autenticación que encapsula la lógica relacionada con el login de usuarios.
 * Utiliza el sistema de autenticación de Spring Security y consulta los datos mediante JPA.
 */
@Service
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final IUserRepository userRepository;
    private final JwtTokenService jwtTokenService;

    /**
     * Constructor con inyección de dependencias necesarias para la autenticación.
     * @param authenticationManager gestor encargado de verificar las credenciales del usuario.
     * @param userRepository repositorio JPA para acceder a los datos de los usuarios.
     * @param jwtTokenService  servicio encargado de la gestión de tokens JWT.
     */
    public AuthenticationService(AuthenticationManager authenticationManager,
                                 IUserRepository userRepository, JwtTokenService jwtTokenService) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.jwtTokenService = jwtTokenService;
    }

    /**
     * Método que permite autenticar a un usuario mediante su email y contraseña.
     * Si la autenticación es exitosa, se recupera el usuario desde la base de datos.
     * @param email correo electrónico del usuario
     * @param password contraseña del usuario
     * @return instancia de UserModel correspondiente al usuario autenticado.
     * @throws IllegalStateException si el email no existe en el sistema.
     */
    public UserModel authenticate(String email, String password) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(email, password)
        );
        
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new IllegalStateException("Email  no encontrado: " + email));
    }

    /**
     * Método auxiliar para buscar un usuario por su dirección de correo electrónico.
     * @param email correo electrónico a buscar.
     * @return un Optional con el usuario encontrado o vacío si no existe.
     */
    public Optional<UserModel> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }




}