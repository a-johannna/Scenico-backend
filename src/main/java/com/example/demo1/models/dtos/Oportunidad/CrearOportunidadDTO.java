package com.example.demo1.models.dtos.Oportunidad;

import com.example.demo1.models.enums.EstadoOportunidad;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CrearOportunidadDTO {

    @NotBlank(message = "El título es obligatorio")
    @Size(min = 5, max = 100, message = "El título debe tener entre 5 y 100 carácteres.")
    private String titulo;

    @NotBlank(message = "La descripción es obligatoria")
    @Size(min =1, max = 2000, message = "La descripción debe de tener al menos un carácter.")
    private String descripcion;

    @NotBlank(message = "La categoría es obligatoria")
    @Pattern(regexp = "^[A-Za-zÁ-ÿ\\s]{3,50}$", message = "La categoría debe contener solo letras y espacios")
    private String categoria;

    @NotBlank(message = "Los requisitos son obligatorios")
    @Size(min =15, max = 500, message = "Los réquisitos al menos tiene que ocupar 15 carácteres.")
    private String requisitos;


    private EstadoOportunidad estado;


    @NotBlank(message = "La ubicación es obligatoria")
    private String ubicacion;


}
