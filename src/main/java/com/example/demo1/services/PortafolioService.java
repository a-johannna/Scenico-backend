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

}
