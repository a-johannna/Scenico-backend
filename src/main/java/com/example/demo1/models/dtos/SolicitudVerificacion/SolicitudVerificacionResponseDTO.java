package com.example.demo1.models.dtos.SolicitudVerificacion;

import com.example.demo1.models.enums.EstadoSolicitud;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class SolicitudVerificacionResponseDTO {

    private Long idSolicitud;
    private String descripcion;
    private String archivoDemoUrl;
    private LocalDateTime fechaSoclicitud;
    private EstadoSolicitud estadoSolicitud;
    private String observacionesAdmin;
    private String nombreUsuario;

    public SolicitudVerificacionResponseDTO(Long idSolicitud, String descripcion, String archivoDemoUrl, LocalDateTime fechaSoclicitud, EstadoSolicitud estadoSolicitud, String observacionesAdmin, String nombreUsuario) {
        this.idSolicitud = idSolicitud;
        this.descripcion = descripcion;
        this.archivoDemoUrl = archivoDemoUrl;
        this.fechaSoclicitud = fechaSoclicitud;
        this.estadoSolicitud = estadoSolicitud;
        this.observacionesAdmin = observacionesAdmin;
        this.nombreUsuario = nombreUsuario;
    }

}
