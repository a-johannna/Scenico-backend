package com.example.demo1.models.dtos;


import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class OportunidadPublicDTO {
    private Long id;
    private String titulo;
    private String descripcion;
    private String categoria;
    private String requistos;
    private String ubicacion;
    private LocalDateTime fechaCierre;
    private String estadoOportunidad;
    private String nombreEmpresa;


    public OportunidadPublicDTO(Long id, String titulo, String descripcion, String categoria, String requistos, String ubicacion, LocalDateTime fechaCierre, String estadoOportunidad, String nombreEmpresa) {
        this.id = id;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.categoria = categoria;
        this.requistos = requistos;
        this.ubicacion = ubicacion;
        this.fechaCierre = fechaCierre;
        this.estadoOportunidad = estadoOportunidad;
        this.nombreEmpresa = nombreEmpresa;
    }


}
