package com.example.demo1.services;

import com.example.demo1.models.dtos.Oportunidad.CrearOportunidadDTO;
import com.example.demo1.models.entidades.Oportunidad;
import com.example.demo1.repositories.IOportunidadRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class OportunidadService {
    private final IOportunidadRepository oportunidadRepository;

    public OportunidadService(IOportunidadRepository oportunidadRepository) {
        this.oportunidadRepository = oportunidadRepository;
    }

    public Optional<Oportunidad> findById(Long id) {
        return oportunidadRepository.findById(id);
    }


    public Oportunidad actualizarDesdeDTO(Oportunidad original, CrearOportunidadDTO dto) {

        original.setTitulo(dto.getTitulo());
        original.setDescripcion(dto.getDescripcion());
        original.setCategoria(dto.getCategoria());
        original.setRequisitos(dto.getRequisitos());
        original.setEstado(dto.getEstado());
        original.setUbicacion(dto.getUbicacion());



        return oportunidadRepository.save(original);
    }
}
