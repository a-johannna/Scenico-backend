package com.example.demo1.mappers;

import com.example.demo1.models.dtos.SolicitudVerificacion.SolicitudVerificacionRequestDTO;
import com.example.demo1.models.dtos.SolicitudVerificacion.SolicitudVerificacionResponseDTO;
import com.example.demo1.models.entidades.SolicitudVerificacion;

public class SolicitudVerificacionMapper {

    public static SolicitudVerificacionResponseDTO toResponseDTO(SolicitudVerificacion solicitudVerif){
        return new SolicitudVerificacionResponseDTO(
                solicitudVerif.getIdSolicitud(),
                solicitudVerif.getDescripcion(),
                solicitudVerif.getArchivoDemoUrl(),
                solicitudVerif.getDateSolicitud(),
                solicitudVerif.getEstadoSolicitud(),
                solicitudVerif.getObservacionesAdmin(),
                solicitudVerif.getUser().getUsername()
        );
    }
    public static SolicitudVerificacion toEntity(SolicitudVerificacionRequestDTO dto) {
        SolicitudVerificacion solicitudVerif = new SolicitudVerificacion();
        solicitudVerif.setDescripcion(dto.getDescripcion());
        solicitudVerif.setArchivoDemoUrl(dto.getArchivoDemoUrl());
        return  solicitudVerif;
    }
}