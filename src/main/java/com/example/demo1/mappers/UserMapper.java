/**
 * UserMapper.java
 * Proyecto: Scénico -Plataforma para artistas emergentes
 * Descripción: Clase encargada de convertir entre UserModel
 * y objetos de transferencia de datos (DTO), tanto para solicitudes como respuestas.
 * Autor: Andrea Johanna Villavicencio Lema
 * Fecha: Mayo de 2025
 * Email: johannna.villavicencio@gmail.com
 */

package com.example.demo1.mappers;

import com.example.demo1.models.dtos.UserModel.CreateUserDTO;
import com.example.demo1.models.dtos.UserModel.UpdateUserDTO;
import com.example.demo1.models.dtos.UserModel.UserResponseDTO;
import com.example.demo1.models.entidades.UserModel;
import com.example.demo1.services.PasswordEncoderService;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Mapper encargado de transformar datos entre la entidad UserModel y los DTO relacionados.
 * De este modo garantiza que la lógica del modelo no se exponga directamente en la API.
 */
@Component
public class UserMapper {

    /**
     * Servicio para codificar contraseñas antes de almacenarlas.
     * Se utiliza durante la conversión de DTO y entidad para garantizar seguridad.
     */
    private final PasswordEncoderService passwordEncoderService;

    /**
     * Constructor del mapper que inyecta el servicio encargado de codificar contraseñas.
     * Esto permite aplicar cifrado seguro al transformar datos de un DTO a entidad.
     * @param passwordEncoderService servicio responsable de codificar contraseñas
     */
    public UserMapper(PasswordEncoderService passwordEncoderService) {
        this.passwordEncoderService = passwordEncoderService;
    }

    /**
     *  Convierte los datos de UserModel a un UserResponseDTO, datos que se van a exponer al público.
     * @param userModel entidad UserModel a convertir
     * @return UserResponseDTO con los datos públicos del usuario
     */
    public UserResponseDTO toResponseDTO(UserModel userModel) {
        if (userModel == null) {
            return null;
        }

        UserResponseDTO dto = new UserResponseDTO();
        dto.setUuid(userModel.getUuid());
        dto.setUsername(userModel.getUsername());
        dto.setFirstName(userModel.getFirstName());
        dto.setLastName(userModel.getLastName());
        dto.setEmail(userModel.getEmail());
        dto.setLocation(userModel.getLocation());
        dto.setTypeUser(userModel.getTypeUser().name());
        dto.setPhotoProfile(userModel.getPhotoProfile());
        dto.setDescription(userModel.getDescription());
        dto.setVerified(Boolean.TRUE.equals(userModel.getVerified()));
        return dto;
    }

    /**
     * Convierte un objeto CreateUserDTO a una entidad UserModel lista para ser persistida.
     * Además, codifica la contraseña, establece valores por defecto y fechas de auditoria.
     * @param dto objeto con lso datos de entrada del nuevo usuario (desde el frontend)
     * @return instancia nueva de UserModel construida con los datos del DTO
     */
    public UserModel toEntity(CreateUserDTO dto) {
        UserModel userModel = new UserModel();
        userModel.setUsername(dto.getUsername());
        userModel.setPassword(passwordEncoderService.encodePassword(dto.getPassword()));
        userModel.setFirstName(dto.getFirstName());
        userModel.setLastName(dto.getLastName());
        userModel.setEmail(dto.getEmail());
        userModel.setLocation(dto.getLocation());
        userModel.setPhotoProfile(dto.getPhotoProfile());
        userModel.setDescription(dto.getDescription());
        userModel.setTypeUser(dto.getTypeUser());
        userModel.setVerified(false);
        userModel.setCreatedAt(LocalDateTime.now());
        userModel.setUpdateAt(LocalDateTime.now());
        return userModel;

    }

    /**
     * Actualiza los campos de un UserModel existente utilizando los datos recibidos desde el DTO correspondiente.
     * @param dto objeto con los datos actualizados desde el frontend
     * @param userModel entidad de UserModel existente que se desea modificar
     */
    public void updateUserFromDTO(UpdateUserDTO dto, UserModel userModel) {
        if (dto.getUsername() != null) {
            userModel.setUsername(dto.getUsername());
        }
        if (dto.getPassword() != null) {
            userModel.setPassword(passwordEncoderService.encodePassword(dto.getPassword()));
        }
        if (dto.getFirstName() != null) {
            userModel.setFirstName(dto.getFirstName());
        }
        if (dto.getLastName() != null) {
            userModel.setLastName(dto.getLastName());
        }
        if (dto.getEmail() != null) {
            userModel.setEmail(dto.getEmail());
        }
        if (dto.getLocation() != null) {
            userModel.setLocation(dto.getLocation());
        }
        if (dto.getPhotoProfile() != null) {
            userModel.setPhotoProfile(dto.getPhotoProfile());
        }
        if (dto.getDescription() != null) {
            userModel.setDescription(dto.getDescription());
        }
        if (dto.getTypeUser() != null) {
            userModel.setTypeUser(dto.getTypeUser());
        }
        userModel.setUpdateAt(LocalDateTime.now());
    }



}