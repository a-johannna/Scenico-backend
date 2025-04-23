package com.example.demo1.models.dtos;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SolicitudVerificacionDTO {

    private String descripcion;
    private String archivoDemoUrl;

    public SolicitudVerificacionDTO(String descripcion, String archivoDemoUrl) {
        this.descripcion = descripcion;
        this.archivoDemoUrl = archivoDemoUrl;
    }

}
