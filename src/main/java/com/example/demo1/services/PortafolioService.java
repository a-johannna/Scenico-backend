package com.example.demo1.services;


import com.example.demo1.mappers.PortafolioMapper;
import com.example.demo1.models.dtos.Portafolio.PortafolioPubliDTO;
import com.example.demo1.models.dtos.Portafolio.PortafolioRequestDTO;
import com.example.demo1.models.entidades.Portafolio;
import com.example.demo1.models.entidades.UserModel;
import com.example.demo1.repositories.IPortafolioRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class PortafolioService {

    private final IPortafolioRepository portafolioRepository;


    public PortafolioService(IPortafolioRepository portafolioRepository) {
        this.portafolioRepository = portafolioRepository;
    }

    public PortafolioPubliDTO crearPortafolio(PortafolioRequestDTO dto, UserModel user) {
        Portafolio portafolio = PortafolioMapper.toEntity(dto);
        portafolio.setUserModel(user);
        portafolio.setFechaCreacion(LocalDateTime.now());

        Portafolio saved = portafolioRepository.save(portafolio);
        return PortafolioMapper.toPubliDTO(saved);
    }

    public PortafolioPubliDTO actualizarPortafolio(Long idPortafolio, PortafolioRequestDTO dto, UserModel user) {
        // 1. Buscar el portafolio
        Portafolio portafolio = portafolioRepository.findById(idPortafolio)
                .orElseThrow(() -> new IllegalArgumentException("Portafolio no encontrado"));

        // 2. Verificar que el usuario sea el dueño
        if (!portafolio.getUserModel().getUuid().equals(user.getUuid())) {
            throw new SecurityException("No tienes permiso para modificar este portafolio");
        }

        // 3. Actualizar los campos
        portafolio.setTitulo(dto.getTitulo());
        portafolio.setDescripcion(dto.getDescripcion());
        portafolio.setTipoArchivo(dto.getTipoArchivo());
        portafolio.setUrlArchivo(dto.getUrlArchivo());
        portafolio.setUrlImagen(dto.getUrlImagen());
        portafolio.setNombreImagen(dto.getNombreImagen());
        portafolio.setDescripcionImagen(dto.getDescripcionImagen());
        portafolio.setEtiquetas(dto.getEtiquetas());

        // 4. Guardar cambios
        Portafolio actualizado = portafolioRepository.save(portafolio);
        return PortafolioMapper.toPubliDTO(actualizado);
    }


}
