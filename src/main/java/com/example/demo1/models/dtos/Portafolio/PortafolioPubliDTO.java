package com.example.demo1.models.dtos.Portafolio;

import com.example.demo1.models.enums.TipoArchivo;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Collections;
import java.util.List;

@Getter
@Setter
public class PortafolioPubliDTO {
    private String titulo;
    private Long idPortafolio;
    private String descripcion;
    private TipoArchivo tipoArchivo;
    private String urlArchivo;
    private String urlImagen;
    private String descripcionImagen;
    private List<String> etiquetas;
    private String nombreUsuario;


    public PortafolioPubliDTO(String titulo, Long idPortafolio, String descripcion, TipoArchivo tipoArchivo, String urlArchivo, String urlImagen, String descripcionImagen, List<String> etiquetas, String nombreUsuario) {
        this.titulo = titulo;
        this.idPortafolio = idPortafolio;
        this.descripcion = descripcion;
        this.tipoArchivo = tipoArchivo;
        this.urlArchivo = urlArchivo;
        this.urlImagen = urlImagen;
        this.descripcionImagen = descripcionImagen;
        this.etiquetas = etiquetas != null ? etiquetas: List.of();
        this.nombreUsuario = nombreUsuario;
    }


}
