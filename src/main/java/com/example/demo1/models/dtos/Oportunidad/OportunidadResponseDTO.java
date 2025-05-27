package com.example.demo1.models.dtos.Oportunidad;

import com.example.demo1.models.enums.EstadoOportunidad;
import com.example.demo1.models.enums.EstadoSolicitud;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class OportunidadResponseDTO {
    private Long id;
    private String titulo;
    private String descripcion;
    private String categoria;
    private String requisitos;
    private String ubicacion;
    private LocalDateTime fecha;
    private LocalDateTime fechaCierre;
    private EstadoOportunidad estadoOportunidad;
    private UserInfoDTO usuarioEmpresa;

    @Getter
    @Setter
    public static class UserInfoDTO{
        private String username;
        private String email;
        private boolean verified;

        private String firstName;
        private String lastName;
        private String location;
        private String photoProfile;
    }
}