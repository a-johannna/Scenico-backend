package com.example.demo1.mappers;

import com.example.demo1.models.entidades.Oportunidad;
import com.example.demo1.models.dtos.OportunidadPublicDTO;

public class OportunidadMapper {
    public static OportunidadPublicDTO toOportunidadPublicDTO(Oportunidad oportunidad) {
        return new OportunidadPublicDTO(
        oportunidad.getId(),
        oportunidad.getTitulo(),
        oportunidad.getDescripcion(),
        oportunidad.getCategoria(),
        oportunidad.getRequisitos(),
        oportunidad.getUbicacion(),
        oportunidad.getFecha(),
        oportunidad.getEstadoOportunidad().name(),
        oportunidad.getUsuarioEmpresa().getUsername()
        );
    }
}
