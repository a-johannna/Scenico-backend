package com.example.demo1.models.dtos.Oportunidad;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CrearOportunidadDTO {

    @NotBlank(message = "El título es obligatorio")
    @Size(min = 5, max = 100, message = "El título debe tener entre 5 y 100 carácteres.")
    private String titulo;

    @NotBlank(message = "La descripción es obligatoria")
    @Size(min =30, max = 2000, message = "La descripción debe de tener al menos 30 carácteres.")
    private String descripcion;

    @NotBlank(message = "La categoría es obligatoria")
    @Pattern(regexp = "^[A-Za-zÁ-ÿ\\s]{3,50}$", message = "La categoría debe contener solo letras y espacios")
    private String categoria;

    @NotBlank(message = "Los requisitos son obligatorios")
    @Size(min =15, max = 500, message = "Los réquisitos al menos tiene que ocupar 15 carácteres.")
    private String requisitos;

    @NotBlank(message = "La ubicación es obligatoria")
    private String ubicacion;

    @Future(message = "La fecha de cierre debe ser posterior a la fecha actual")
    @NotNull(message = "Este campo es obligatorio.")
    private LocalDateTime fechaCierre;

}
