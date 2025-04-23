package com.example.demo1.models.entidades;

import com.example.demo1.models.enums.EstadoSolicitud;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@Table(name = "solicitudes_verificaciones")
public class SolicitudVerificacion
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idSolicitud;
    @ManyToOne(optional = false)
    @JoinColumn(name = "idUser")
    private UserModel user;
    @Column(nullable = false, columnDefinition = "TEXT")
    private String descripcion;
    private String archivoDemoUrl;
    private LocalDateTime  dateSolicitud = LocalDateTime.now();
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoSolicitud estadoSolicitud = EstadoSolicitud.PENDIENTE;
    private String observacionesAdmin;


}
