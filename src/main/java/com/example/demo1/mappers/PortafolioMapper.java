package com.example.demo1.mappers;

import com.example.demo1.models.entidades.Portafolio;
import com.example.demo1.models.dtos.PortafolioPubliDTO;

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
}
