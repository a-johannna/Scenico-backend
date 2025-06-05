/**
 * JwtAuthenticationFilter.java
 * Proyecto: Scénico - Plataforma para artistas emergentes
 * Descripción: Filtro de seguridad que se ejecuta en cada petición HTTP entrante.
 * Su función es interceptar la solicitud, extraer el token JWT, validarlo y establecer
 * el contexto de seguridad para autenticar al usuario si el token es válido.
 * Autor: Andrea Johanna Villavicencio Lema
 * Fecha: Mayo de 2025
 * Email: johannna.villavicencio@gmail.com
 */
package com.example.demo1.security;

import com.example.demo1.services.JwtTokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtro personalizado de Spring Security que válida el token JWT en cada petición.
 */
@Component
@Lazy
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenService jwtTokenService;
    private final UserDetailsService userDetailsService;

    /**
     * Constructor con inyección de servicios necesarios.
     * @param jwtTokenService       servicio que maneja la lógica de tokens JWT
     * @param userDetailsService    servicio que recupera datos del usuario desde la base de datos.
     */
    public JwtAuthenticationFilter(JwtTokenService jwtTokenService, UserDetailsService userDetailsService) {
        this.jwtTokenService = jwtTokenService;
        this.userDetailsService = userDetailsService;
    }

    /**
     * Método que intercepta todas las peticiones HTTP para validar y establecer la autenticación
     * @param request               petición HTTP entrante
     * @param response              respuesta HTTP saliente
     * @param filterChain           cadena de filtros a ejecutar
     * @throws ServletException     en caso de error del filtro
     * @throws IOException          en caso de error de entrada o salida
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getServletPath();
        System.out.println("REQUEST PATH: " + path);


        if (path.startsWith("/api/v1/users/register") || path.startsWith("/api/v1/users/login")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String jwt = getJwtFromRequest(request);

            if (jwt != null && jwtTokenService.validateToken(jwt)) {
                String email = jwtTokenService.getUsernameFromToken(jwt);
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            logger.error("No se pudo establecer la autenticación del usuario", e);
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Extrae el token JWT desde la cabecera Authorization
     * @param request       petición HTTP
     * @return              token JWT sin el prefijo "Bearer ", o null si no existe
     */
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}