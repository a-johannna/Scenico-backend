package com.example.demo1.models.dtos.Portafolio;

import com.example.demo1.models.enums.TipoArchivo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PortafolioRequestDTO {

    @NotBlank(message = "Este campo es obligatorio")
    private String titulo;
    @NotBlank(message = "Por favor, introduzca una descripción.")
    private String descripcion;
    @NotNull(message = "Tipo de archivo obligatorio.")
    private TipoArchivo tipoArchivo;
    @Pattern(regexp = "^(http|https)://.*$", message = "La URL del archivo debe ser válida")
    private String urlArchivo;
    @Pattern(regexp = "^(http|https)://.*$", message = "La URL de la imagen debe ser válida")
    private String urlImagen;
    private String nombreImagen;
    private String descripcionImagen;
    private String etiquetas;

    public PortafolioRequestDTO(String titulo, String descripcion, TipoArchivo tipoArchivo, String urlArchivo, String urlImagen, String nombreImagen, String descripcionImagen, String etiquetas) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.tipoArchivo = tipoArchivo;
        this.urlArchivo = urlArchivo;
        this.urlImagen = urlImagen;
        this.nombreImagen = nombreImagen;
        this.descripcionImagen = descripcionImagen;
        this.etiquetas = etiquetas;
    }

}
