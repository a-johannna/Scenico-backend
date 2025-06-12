/**
 * SecurityConfig.java
 * Proyecto: Scénico - Plataforma para artistas emergentes
 * Descripción: Clase de configuración de seguridad que define las políticas de acceso,
 * la autenticación mediante JWT, y la gestión de CORS. Utiliza Spring Security para proteger
 * rutas y gestionar sesiones sin estado (stateless).
 * Autor: Andrea Johanna Villavicencio Lema
 * Fecha: Mayo de 2025
 * Email: johannna.villavicencio@gmail.com
 */
package com.example.demo1.config;

import com.example.demo1.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.List;


/**
 * Clase principal de configuración de seguridad de la aplicación.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final UserDetailsService userDetailsService;
    private final BCryptPasswordEncoder passwordEncoder;

    /**
     * Constructor con inyección de dependencias
     * @param jwtAuthenticationFilter   filtro personalizado para manejar autenticación JWT
     * @param userDetailsService        servicio que carga detalles del usuario
     * @param passwordEncoder           codificador de contraseñas (BCrypt)
     */
    public SecurityConfig(@Lazy JwtAuthenticationFilter jwtAuthenticationFilter,
                          UserDetailsService userDetailsService,
                          @Lazy BCryptPasswordEncoder passwordEncoder) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Define la cadena de filtros de seguridad para manejar las solicitudes HTTP.
     * @param http          instancia de HttpSecurity
     * @return              configuración del filtro de seguridad
     * @throws Exception    si ocurre un error en la configuración
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
               .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/api/v1/users/register").permitAll()
                        .requestMatchers(HttpMethod.POST,"/api/v1/users/register").permitAll()
                        .requestMatchers("/ping").permitAll()
                        .requestMatchers(HttpMethod.POST,"/api/v1/users/login").permitAll()
                        .requestMatchers(HttpMethod.POST,"/api/v1/portafolios/**").permitAll()
                        .requestMatchers(HttpMethod.GET,"/api/v1/portafolios/**").permitAll()
                        .requestMatchers(HttpMethod.GET,"/api/v1/users/**").permitAll()
                        .requestMatchers(HttpMethod.GET,"/api/v1/users/uuid/{uuid}").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/api/v1/users/uuid/**").permitAll()
                        .requestMatchers(HttpMethod.DELETE,"/api/v1/users/uuid/**").permitAll()
                        .requestMatchers("/api/v1/users/forgot-password").permitAll()
                        .requestMatchers(HttpMethod.GET, "api/v1/**").permitAll()
                        .requestMatchers("/error").permitAll()
                        .requestMatchers("/api/v1/users/upload-photo/**").permitAll()
                        .requestMatchers("/api/v1/users/portafolios/**").permitAll()
                        .requestMatchers(HttpMethod.GET,"/api/v1/users/empresas/oportunidades/empresa/**").hasRole("ENTERPRISE")
                        .requestMatchers(HttpMethod.POST,"/api/v1/users/empresas/oportunidades/uuid/").hasRole("ENTERPRISE")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/users/empresas/oportunidades/**").hasRole("ENTERPRISE")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/users/empresas/oportunidades/**").hasRole("ENTERPRISE")
                        .anyRequest().authenticated())
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Configura el proveedor de autenticación con los servicios personalizados.
     * @return proveedor de autenticación con BCrypt y UserDetailsService
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }

    /**
     * Proporciona el gestor de autenticación utilizado en el proceso de login.
     * @param authConfig    configuración de autenticación global
     * @return              instancia de AuthenticationManager
     * @throws Exception    si ocurre un error durante la inicialización
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    /**
     * Proporciona el codificador de contraseñas (BCrypt).
     * @return instancia de BCryptPasswordEncoder
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configura las políticas CORS para permitir el acceso de peticiones HTTP
     * desde el frontend.
     * @return configuración de CORS permitiendo métodos y orígenes específicos
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:4200"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        // configuration.setExposedHeaders(List.of("x-auth-token"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }




}