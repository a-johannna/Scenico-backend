/**
 * PortafolioService.java
 * Proyecto: Scénico -Plataforma para artistas emergentes
 * Descripción: Servicio que gestiona la lógica de negocio para los portafolios de artistas.
 * Permite crear y actualizar obras vinculadas a un usuario.
 * Autor: Andrea Johanna Villavicencio Lema
 * Fecha: Mayo de 2025
 * Email: johannna.villavicencio@gmail.com
 */
package com.example.demo1.services;

import com.example.demo1.mappers.PortafolioMapper;
import com.example.demo1.models.dtos.Portafolio.PortafolioPubliDTO;
import com.example.demo1.models.dtos.Portafolio.PortafolioRequestDTO;
import com.example.demo1.models.entidades.Portafolio;
import com.example.demo1.models.entidades.UserModel;
import com.example.demo1.repositories.IPortafolioRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Servicio que gestiona la creación y actualización de portafolios artísticos.
 */
@Service
public class PortafolioService {

    private final IPortafolioRepository portafolioRepository;

    /**
     * Constructor que inyecta el repositorio de portafolios.
     * @param portafolioRepository repositorio para acceder a la base de datos
     */
    public PortafolioService(IPortafolioRepository portafolioRepository) {
        this.portafolioRepository = portafolioRepository;
    }

    /**
     * Crea un nuevo portafolio artístico asociado a un usuario.
     * @param dto        datos enviados por el usuario (título, descripción, archivo, etc.)
     * @param user       usuario autenticado que crea el portafolio
     * @return           DTO con los datos públicos del portafolio recién creado
     */
    public PortafolioPubliDTO crearPortafolio(PortafolioRequestDTO dto, UserModel user) {
        Portafolio portafolio = PortafolioMapper.toEntity(dto);
        portafolio.setUserModel(user);
        portafolio.setFechaCreacion(LocalDateTime.now());

        Portafolio saved = portafolioRepository.save(portafolio);
        return PortafolioMapper.toPubliDTO(saved);
    }

    /**
     * Actualiza un portafolio existente, validando que el usuario autenticado sea el propietario.
     * @param idPortafolio  Identificación del portafolio a actualizar
     * @param dto           nuevos datos del portafolio
     * @param user          usuario autenticado que realiza la modificación
     * @return              DTO con los datos actualizados y visibles públicamente
     */
    public PortafolioPubliDTO actualizarPortafolio(Long idPortafolio, PortafolioRequestDTO dto, UserModel user) {

        Portafolio portafolio = portafolioRepository.findById(idPortafolio)
                .orElseThrow(() -> new IllegalArgumentException("Portafolio no encontrado"));


        if (!portafolio.getUserModel().getUuid().equals(user.getUuid())) {
            throw new SecurityException("No tienes permiso para modificar este portafolio");
        }


        portafolio.setTitulo(dto.getTitulo());
        portafolio.setDescripcion(dto.getDescripcion());
        portafolio.setTipoArchivo(dto.getTipoArchivo());
        portafolio.setUrlArchivo(dto.getUrlArchivo());
        portafolio.setUrlImagen(dto.getUrlImagen());
        portafolio.setNombreImagen(dto.getNombreImagen());
        portafolio.setDescripcionImagen(dto.getDescripcionImagen());
        portafolio.setEtiquetas(dto.getEtiquetas());


        Portafolio actualizado = portafolioRepository.save(portafolio);
        return PortafolioMapper.toPubliDTO(actualizado);
    }


}
