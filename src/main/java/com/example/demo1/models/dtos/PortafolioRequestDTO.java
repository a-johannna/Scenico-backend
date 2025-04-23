package com.example.demo1.models.dtos;

import com.example.demo1.models.enums.TipoArchivo;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PortafolioRequestDTO {

    private String titulo;
    private String descripcion;
    private TipoArchivo tipoArchivo;
    private String urlArchivo;
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
