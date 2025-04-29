package com.example.demo1.models.dtos.Oportunidad;


import com.example.demo1.models.enums.EstadoOportunidad;
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
    private String ubicacion;
    private LocalDateTime fechaCierre;
    private EstadoOportunidad estadoOportunidad;
    private String nombreEmpresa;
    private boolean empresaVerificada;
    private LocalDateTime fechaPublicacion;
    private int diasRestantes;

    public OportunidadPublicDTO(Long id, String titulo, String descripcion, String categoria, String ubicacion, LocalDateTime fechaCierre, EstadoOportunidad estadoOportunidad, String nombreEmpresa, boolean empresaVerificada, LocalDateTime fechaPublicacion, int diasRestantes) {
        this.id = id;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.categoria = categoria;
        this.ubicacion = ubicacion;
        this.fechaCierre = fechaCierre;
        this.estadoOportunidad = estadoOportunidad;
        this.nombreEmpresa = nombreEmpresa;
        this.empresaVerificada = empresaVerificada;
        this.fechaPublicacion = fechaPublicacion;
        this.diasRestantes = diasRestantes;
    }

    public OportunidadPublicDTO() {

    }
}
