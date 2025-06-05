/**
 * OportunidadMapper.java
 * Proyecto: Scénico - Plataforma para artistas emergentes
 * Descripción: Clase utilitaria encargada de transformar objetos de tipo Oportunidad entre sus
 * representaciones de entidad y DTO (Data Transfer Objects) para mantener una arquitectura
 * limpia y separar la lógica interna del backend de la información expuesta al cliente.
 * Autor: Andrea Johanna Villavicencio Lema
 * Fecha: Mayo de 2025
 * Email: johannna.villavicencio@gmail.com
 */
package com.example.demo1.mappers;

import com.example.demo1.models.dtos.Oportunidad.CrearOportunidadDTO;
import com.example.demo1.models.dtos.Oportunidad.OportunidadResponseDTO;
import com.example.demo1.models.entidades.Oportunidad;
import com.example.demo1.models.dtos.Oportunidad.OportunidadPublicDTO;
import com.example.demo1.models.entidades.UserModel;
import com.example.demo1.models.enums.EstadoOportunidad;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Clase mapper para convertir entre la entidad Oportunidad y sus DTOs asociados.
 * Facilita el intercambio de datos entre capas del sistema.
 */
public class OportunidadMapper {

    /**
     * Convierte una entidad Oportunidad a su DTO completo de respuesta (OportunidadResponseDTO),
     * utilizado para mostrar información detallada incluyendo los datos del usuario empresa.
     * @param oportunidad   entidad Oportunidad a convertir.
     * @return              DTO con los datos completos de la oportunidad.
     */
    public static OportunidadResponseDTO toResponseDTO(Oportunidad oportunidad) {
        OportunidadResponseDTO dto = new OportunidadResponseDTO();
        dto.setId(oportunidad.getId());
        dto.setTitulo(oportunidad.getTitulo());
        dto.setDescripcion(oportunidad.getDescripcion());
        dto.setCategoria(oportunidad.getCategoria());
        dto.setRequisitos(oportunidad.getRequisitos());
        dto.setUbicacion(oportunidad.getUbicacion());
        dto.setFecha(oportunidad.getFecha());
        dto.setFechaCierre(oportunidad.getFechaCierre());
        dto.setEstadoOportunidad(oportunidad.getEstado());

        OportunidadResponseDTO.UserInfoDTO userInfo = getUserInfoDTO(oportunidad);

        dto.setUsuarioEmpresa(userInfo);



        return dto;
    }

    /**
     * Método auxiliar para construir un DTO con la información pública del usuario empresa
     * asociada a la oportunidad.
     *
     * @param oportunidad oportunidad que contiene el usuario empresa.
     * @return DTO con información del usuario.
     */
    private static OportunidadResponseDTO.UserInfoDTO getUserInfoDTO(Oportunidad oportunidad) {
        OportunidadResponseDTO.UserInfoDTO userInfo = new OportunidadResponseDTO.UserInfoDTO();
        UserModel empresa = oportunidad.getUsuarioEmpresa();
        userInfo.setUsername(empresa.getUsername());
        userInfo.setEmail(empresa.getEmail());
        userInfo.setVerified(Boolean.TRUE.equals(empresa.isVerified()));
        userInfo.setFirstName(empresa.getFirstName());
        userInfo.setLastName(empresa.getLastName());
        userInfo.setLocation(empresa.getPhotoProfile());
        userInfo.setPhotoProfile(empresa.getPhotoProfile());
        return userInfo;
    }

    /**
     * Convierte un DTO de creación de oportunidad (CrearOportunidadDTO) en una entidad Oportunidad.
     * No incluye la asignación del usuario empresa ni fecha de cierre, que deben añadirse después.
     * @param dto        DTO con los datos proporcionados desde el frontend.
     * @return           nueva instancia de Oportunidad.
     */
    public static Oportunidad toEntity(CrearOportunidadDTO dto) {
        Oportunidad oportunidad = new Oportunidad();
        oportunidad.setTitulo(dto.getTitulo());
        oportunidad.setDescripcion(dto.getDescripcion());
        oportunidad.setCategoria(dto.getCategoria());
        oportunidad.setRequisitos(dto.getRequisitos());
        oportunidad.setUbicacion(dto.getUbicacion());
//        oportunidad.setFechaCierre(dto.getFechaCierre());
        oportunidad.setFecha((LocalDateTime.now()));
        oportunidad.setEstado(EstadoOportunidad.ABIERTO);

        return oportunidad;
    }

    /**
     * Convierte una entidad Oportunidad en un DTO público para mostrar listados resumidos.
     * @param oportunidad   entidad a convertir.
     * @return              DTO con datos básicos visibles públicamente.
     */
    public static OportunidadPublicDTO toOportunidadPublicDTO(Oportunidad oportunidad) {
        OportunidadPublicDTO dto = new OportunidadPublicDTO();
        dto.setId(oportunidad.getId());
        dto.setTitulo(oportunidad.getTitulo());
        dto.setDescripcion(oportunidad.getDescripcion());
        dto.setCategoria(oportunidad.getCategoria());
        dto.setUbicacion(oportunidad.getUbicacion());
        dto.setFechaCierre(oportunidad.getFechaCierre());
        dto.setEstadoOportunidad(oportunidad.getEstado());
        dto.setNombreEmpresa(oportunidad.getUsuarioEmpresa().getUsername());
        dto.setEmpresaVerificada(oportunidad.getUsuarioEmpresa().isVerified());
        dto.setFechaPublicacion(oportunidad.getFecha());

        long diasRestantes = ChronoUnit.DAYS.between(
                LocalDateTime.now(),
                oportunidad.getFechaCierre()
        );
        dto.setDiasRestantes((int) diasRestantes);

        return  dto;

    }

    /**
     * Actualiza una entidad Oportunidad existente con los datos proporcionados por un DTO.
     * Se utiliza para operaciones de edición.
     * @param oportunidad entidad original que se desea actualizar.
     * @param dto DTO con los nuevos datos.
     */
    public static void updateFromDTO(Oportunidad oportunidad, @Valid CrearOportunidadDTO dto) {
        oportunidad.setTitulo(dto.getTitulo());
        oportunidad.setDescripcion(dto.getDescripcion());
        oportunidad.setCategoria(dto.getCategoria());
        oportunidad.setRequisitos(dto.getRequisitos());
        oportunidad.setUbicacion(dto.getUbicacion());
//        oportunidad.setFechaCierre(dto.getFechaCierre());

    }




}