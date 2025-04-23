package com.example.demo1.repositories;

import com.example.demo1.models.enums.EstadoPostulacion;
import com.example.demo1.models.entidades.Oportunidad;
import com.example.demo1.models.entidades.Postulacion;
import com.example.demo1.models.entidades.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IPostulacionRepository  extends JpaRepository<Postulacion, Long> {

    List<Postulacion> findByUsuarioArtista(UserModel artista);
    List<Postulacion> findByOportunidad(Oportunidad oportunidad);
    Optional<Postulacion> findByUsuarioArtistaYOportunidad(UserModel artista, Oportunidad oportunidad);
    List<Postulacion> findByEstado(EstadoPostulacion estadoPostulacion);
}
