package com.example.demo1.models.dtos;

import com.example.demo1.models.enums.EstadoSolicitud;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class SolicitudVerificacionResponseDTO {

    private Long idSolicitud;
    private String descrpcion;
    private String archivoDemoUrl;
    private LocalDateTime fechaSoclicitud;
    private EstadoSolicitud estadoSolicitud;
    private String observacionesAdmin;
    private String nombreUsuario;

    public SolicitudVerificacionResponseDTO(Long idSolicitud, String descrpcion, String archivoDemoUrl, LocalDateTime fechaSoclicitud, EstadoSolicitud estadoSolicitud, String observacionesAdmin, String nombreUsuario) {
        this.idSolicitud = idSolicitud;
        this.descrpcion = descrpcion;
        this.archivoDemoUrl = archivoDemoUrl;
        this.fechaSoclicitud = fechaSoclicitud;
        this.estadoSolicitud = estadoSolicitud;
        this.observacionesAdmin = observacionesAdmin;
        this.nombreUsuario = nombreUsuario;
    }

}
