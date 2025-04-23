package com.example.demo1.config;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CorsConfigurationSetup {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        // Configuración CORS
        config.setAllowCredentials(true);
        config.addAllowedOrigin("http://localhost:3000"); // URL de tu frontend
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");

        // Configuración adicional recomendada
        config.setMaxAge(3600L); // Tiempo de cache para pre-flight requests

        // Registrar la configuración para todos los endpoints de la API
        source.registerCorsConfiguration("/api/**", config);

        return source;
    }
}