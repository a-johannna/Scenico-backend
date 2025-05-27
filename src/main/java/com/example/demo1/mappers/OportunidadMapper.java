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

public class OportunidadMapper {

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

    public static Oportunidad toEntity(CrearOportunidadDTO dto) {
        Oportunidad oportunidad = new Oportunidad();
        oportunidad.setTitulo(dto.getTitulo());
        oportunidad.setDescripcion(dto.getDescripcion());
        oportunidad.setCategoria(dto.getCategoria());
        oportunidad.setRequisitos(dto.getRequisitos());
        oportunidad.setUbicacion(dto.getUbicacion());
        oportunidad.setFechaCierre(dto.getFechaCierre());
        oportunidad.setFecha((LocalDateTime.now()));
        oportunidad.setEstado(EstadoOportunidad.ABIERTO);

        return oportunidad;
    }

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

    public static void updateFromDTO(Oportunidad oportunidad, @Valid CrearOportunidadDTO dto) {
        oportunidad.setTitulo(dto.getTitulo());
        oportunidad.setDescripcion(dto.getDescripcion());
        oportunidad.setCategoria(dto.getCategoria());
        oportunidad.setRequisitos(dto.getRequisitos());
        oportunidad.setUbicacion(dto.getUbicacion());
        oportunidad.setFechaCierre(dto.getFechaCierre());

    }




}