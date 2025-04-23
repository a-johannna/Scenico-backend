package com.example.demo1.models.entidades;

import com.example.demo1.models.enums.EstadoSolicitud;
import jakarta.persistence.*;

import java.time.LocalDateTime;

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


    public Long getIdSolicitud() {
        return idSolicitud;
    }

    public void setIdSolicitud(Long id) {
        this.idSolicitud = id;
    }

    public UserModel getUser() {
        return user;
    }

    public void setUser(UserModel user) {
        this.user = user;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getArchivoDemoUrl() {
        return archivoDemoUrl;
    }

    public void setArchivoDemoUrl(String archivoDemoUrl) {
        this.archivoDemoUrl = archivoDemoUrl;
    }

    public LocalDateTime getDateSolicitud() {
        return dateSolicitud;
    }

    public void setDateSolicitud(LocalDateTime dateSolicitud) {
        this.dateSolicitud = dateSolicitud;
    }

    public EstadoSolicitud getEstadoSolicitud() {
        return estadoSolicitud;
    }

    public void setEstadoSolicitud(EstadoSolicitud estadoSolicitud) {
        this.estadoSolicitud = estadoSolicitud;
    }

    public String getObservacionesAdmin() {
        return observacionesAdmin;
    }

    public void setObservacionesAdmin(String observacionesAdmin) {
        this.observacionesAdmin = observacionesAdmin;
    }
}
