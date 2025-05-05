package com.example.demo1.config;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfigurationSetup {

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        // Permite solicitudes desde tu frontend
        config.addAllowedOrigin("http://localhost:4200"); // URL de tu frontend Angular

        // Permite los métodos HTTP necesarios
        config.addAllowedMethod("*");

        // Permite todos los headers
        config.addAllowedHeader("*");

        // Permite enviar cookies (necesario para autenticación)
        config.setAllowCredentials(true);

        source.registerCorsConfiguration("/api/**", config);
        return new CorsFilter(source);

    }
}