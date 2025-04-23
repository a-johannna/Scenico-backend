package com.example.demo1.models.dtos.Oportunidad;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CrearOportunidadDTO {

    @NotBlank(message = "El título es obligatorio")
    @Size(max = 100)
    private String titulo;

    @NotBlank(message = "La descripción es obligatoria")
    @Size(max = 2000)
    private String descripcion;

    @NotBlank(message = "La categoría es obligatoria")
    private String categoria;

    @NotBlank(message = "Los requisitos son obligatorios")
    @Size(max = 500)
    private String requisitos;

    @NotBlank(message = "La ubicación es obligatoria")
    private String ubicacion;

    @Future(message = "La fecha de cierre debe ser posterior a la fecha actual")
    private LocalDateTime fechaCierre;

}
