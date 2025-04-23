package com.example.demo1.repositories;

import com.example.demo1.models.enums.EstadoSolicitud;
import com.example.demo1.models.entidades.SolicitudVerificacion;
import com.example.demo1.models.entidades.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ISolucitudVerifRepository extends JpaRepository<SolicitudVerificacion, Long> {
        List<SolicitudVerificacion> findByUsername(String username);
        List<SolicitudVerificacion> findByEstado(EstadoSolicitud estado);
        List<SolicitudVerificacion> findByUsernameAndEstadoSolicitud(UserModel user, EstadoSolicitud estado);

        List<SolicitudVerificacion> findByUser(UserModel user);


        UserModel getUser();
}
