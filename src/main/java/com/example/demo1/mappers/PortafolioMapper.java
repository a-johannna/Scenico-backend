package com.example.demo1.mappers;

import com.example.demo1.models.dtos.Portafolio.PortafolioRequestDTO;
import com.example.demo1.models.entidades.Portafolio;
import com.example.demo1.models.dtos.Portafolio.PortafolioPubliDTO;

public class PortafolioMapper {
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
