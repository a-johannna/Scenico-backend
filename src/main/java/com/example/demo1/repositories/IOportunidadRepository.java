package com.example.demo1.repositories;

import com.example.demo1.models.enums.EstadoOportunidad;
import com.example.demo1.models.entidades.Oportunidad;
import com.example.demo1.models.entidades.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IOportunidadRepository extends JpaRepository<Oportunidad, Long> {
    List<Oportunidad> findByUsuarioEmpresa(UserModel empresa);
    List<Oportunidad> findByCategoriaIgnoreCase(String categoria);
    List<Oportunidad> findByEstadoOportunidad(EstadoOportunidad estado);
    List<Oportunidad> findByCategoriaIgnoreCaseAndEstado(String categoria, EstadoOportunidad estado);

}
