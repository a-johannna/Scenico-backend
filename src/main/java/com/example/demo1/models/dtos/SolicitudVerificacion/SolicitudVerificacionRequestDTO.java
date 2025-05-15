package com.example.demo1.models.dtos.SolicitudVerificacion;

import com.example.demo1.models.enums.RoleName;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SolicitudVerificacionRequestDTO {

    @NotBlank(message= "Este campo es obligatorio")
    private String descripcion;

    @NotBlank(message = "Este campo es obligatorio")
    @Pattern(regexp = "^(http|https)://.*$", message = "La URL debe comenzar con http:// o https://")
    private String archivoDemoUrl;

    @NotBlank(message = "Debe especificar el rol")
    private RoleName rolSolicitado;

    public SolicitudVerificacionRequestDTO(String descripcion, String archivoDemoUrl, RoleName rolSolicitado) {
        this.descripcion = descripcion;
        this.archivoDemoUrl = archivoDemoUrl;
        this.rolSolicitado = rolSolicitado;
    }
}
