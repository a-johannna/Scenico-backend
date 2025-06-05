package com.example.demo1.security;

import com.example.demo1.models.entidades.UserModel;
import com.example.demo1.repositories.IUserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * Servicio personalizado para cargar usuarios por su nombre de usuario (email).
 * Utilizado por Spring Security durante el proceso de autenticación.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final IUserRepository userRepository;

    /**
     * Constructor con inyección del repositorio de usuarios
     * @param userRepository repositorio que accede a los datos de los usuarios
     */
    public CustomUserDetailsService(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Carga el usuario desde la base de datos usando su email.
     * Este método es llamado automáticamente por Spring Security al autenticar.
     * @param username correo electrónico del usuario que funciona como nombre de usuario
     * @return objeto UserDetails con credenciales y roles de usuario
     * @throws UsernameNotFoundException si no se encuentra un usuario con ese email
     * @throws IllegalStateException si el usuario no tine asignado un rol (por el momento no se utiliza)
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserModel user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + email));

        if (user.getTypeUser() == null) {
            throw new IllegalStateException("El usuario no tiene un rol asignado");
        }


        List<SimpleGrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority("ROLE_" + user.getTypeUser().name())
        );

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                authorities
        );
    }
}