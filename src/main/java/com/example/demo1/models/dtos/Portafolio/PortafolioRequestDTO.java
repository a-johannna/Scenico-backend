package com.example.demo1.models.dtos.Portafolio;

import com.example.demo1.models.enums.TipoArchivo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.util.Collections;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.Nullable;

@Setter
@Getter
public class PortafolioRequestDTO {

    @NotBlank(message = "Este campo es obligatorio")
    private String titulo;
    @NotBlank(message = "Por favor, introduzca una descripción.")
    @Nullable
    private String descripcion;
    @Nullable
    private TipoArchivo tipoArchivo;
    @Pattern(regexp = "^(http|https)://.*$", message = "La URL del archivo debe ser válida")
    @Nullable
    private String urlArchivo;
    @Pattern(regexp = "^(http|https)://.*$", message = "La URL de la imagen debe ser válida")
    @Nullable
    private String urlImagen;
    @Nullable
    private String nombreImagen;
    @Nullable
    private String descripcionImagen;
    @Nullable
    private List<String> etiquetas;


    public PortafolioRequestDTO(String titulo, @Nullable String descripcion, @Nullable TipoArchivo tipoArchivo, @Nullable String urlArchivo, @Nullable String urlImagen, @Nullable String nombreImagen, @Nullable String descripcionImagen, List<String> etiquetas) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.tipoArchivo = tipoArchivo;
        this.urlArchivo = urlArchivo;
        this.urlImagen = urlImagen;
        this.nombreImagen = nombreImagen;
        this.descripcionImagen = descripcionImagen;
        this.etiquetas = etiquetas != null ? etiquetas: List.of();
    }

}
