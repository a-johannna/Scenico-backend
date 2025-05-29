/**
 * PortafolioMapper.java
 * Proyecto: Scénico -Plataforma para artistas emergentes
 * Descripción: Clase que transforma datos entre la entidad Portafolio y los DTO correspondientes.
 * Aísla la lógica de conversión entre el modelo de dominio y la capa expuesta al público.
 * Autor: Andrea Johanna Villavicencio Lema
 * Fecha: Mayo de 2025
 * Email: johannna.villavicencio@gmail.com
 */
package com.example.demo1.mappers;

import com.example.demo1.models.dtos.Portafolio.PortafolioRequestDTO;
import com.example.demo1.models.entidades.Portafolio;
import com.example.demo1.models.dtos.Portafolio.PortafolioPubliDTO;

/**
 * Clase encargada de transformar datos entre la entidad Portafolio y el DTO de entrada.
 */
public class PortafolioMapper {

    /**
     * Convierte una entidad Portafolio a un DTO público (PortafolioPubliDTO),
     * que será expuesto al cliente en respuestas GET.
     * @param portafolio    entidad que representa una obra publicada
     * @return              DTO con los datos visibles al público general
     */
    public static PortafolioPubliDTO toPubliDTO(Portafolio portafolio){
        return new PortafolioPubliDTO(
                portafolio.getTitulo(),
                portafolio.getIdPortafolio(),
                portafolio.getDescripcion(),
                portafolio.getTipoArchivo(),
                portafolio.getUrlArchivo(),
                portafolio.getUrlImagen(),
                portafolio.getDescripcionImagen(),
                portafolio.getEtiquetas(),
                portafolio.getUserModel().getUsername()

        );
    }

    /**
     * Convierte un DTO de solicitud (PortafolioRequestDTO) a una entidad Portafolio.
     * Se utiliza normalmente cuando un artista sube o edita un portafolio.
     * @param dto   DTO con los datos proporcionados por el cliente
     * @return      nueva instancia de Portafolio lista para guardar en la base de datos
     */
    public static Portafolio toEntity(PortafolioRequestDTO dto){
        Portafolio portafolio = new Portafolio();
        portafolio.setTitulo(dto.getTitulo());
        portafolio.setDescripcion(dto.getDescripcion());
        portafolio.setTipoArchivo(dto.getTipoArchivo());
        portafolio.setUrlArchivo(dto.getUrlArchivo());
        portafolio.setUrlImagen(dto.getUrlImagen());
        portafolio.setNombreImagen(dto.getNombreImagen());
        portafolio.setDescripcionImagen(dto.getDescripcionImagen());
        portafolio.setEtiquetas(dto.getEtiquetas());
        return portafolio;

    }
}
