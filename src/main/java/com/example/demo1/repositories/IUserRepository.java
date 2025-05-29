/**
 * IUserRepository.java
 * Proyecto: Scénico -Plataforma para artistas emergentes
 * Descripción: Interfaz de UserModel que extiende JpaRepository para proporcionar operaciones CRUD
 * y consultas personalizadas sobre la entidad de UserModel.
 * Autor: Andrea Johanna Villavicencio Lema
 * Fecha: Mayo de 2025
 * Email: johannna.villavicencio@gmail.com
 */

package com.example.demo1.repositories;

import com.example.demo1.models.entidades.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;


/**
 * Repositorio para la entidad UserModel.
 * Permite realizar operaciones de persistencia como búsqueda por campos específicos.
 */
@Repository
public interface IUserRepository extends JpaRepository<UserModel, Long> {

    /**
     * Busca según el nombre de usuario (username)
     * @param username nombre de usuario a buscar
     * @return un Optional con el usuario si existe
     */
    Optional<UserModel> findByUsername(String username);

    /**
     * Busca un usuario por el identificador UUID.
     * @param uuid identificador público del usuario.
     * @return un Optional si con el usuario si se encuentra
     */
    Optional<UserModel> findByUuid(UUID uuid);

    /**
     *  Verifica si existe un usuario con el email especificado.
     * @param email correo electrónico a verificar
     * @return true si existe un usuario con ese correo, false en caso contrario.
     */
    boolean existsByEmail(String email);

    /**
     * Comprueba si existe un usuario con el nombre de usuario especificado.
     * @param username nombre de usuario a comprobar
     * @return true si existe, false en caso contrario
     */
    boolean existsByUsername(String username);

    /**
     * Busca al usuario por el correo electrónico.
     * @param email correo electrónico del usuario a buscar
     * @return un Optional con el usuario en el caso de que se encuentre
     */
    Optional<UserModel> findByEmail(String email);


}
