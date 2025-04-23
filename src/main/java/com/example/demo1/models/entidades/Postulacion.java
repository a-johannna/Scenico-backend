package com.example.demo1.models.entidades;

import com.example.demo1.models.enums.EstadoPostulacion;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "postulacion")
public class Postulacion {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false)
    @JoinColumn(name = "id_artista")
    private UserModel usuarioArtista;
    @ManyToOne(optional = false)
    @JoinColumn(name = "id_oportunidad")
    private Oportunidad oportunidad;
    @Column(columnDefinition = "TEXT")
    private String mensaje;
    private LocalDateTime fecha = LocalDateTime.now();
    @Enumerated(EnumType.STRING)
    private EstadoPostulacion estadoPostulacion = EstadoPostulacion.PENDENTE;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserModel getUsuarioArtista() {
        return usuarioArtista;
    }

    public void setUsuarioArtista(UserModel usuarioArtista) {
        this.usuarioArtista = usuarioArtista;
    }

    public Oportunidad getOportunidad() {
        return oportunidad;
    }

    public void setOportunidad(Oportunidad oportunidad) {
        this.oportunidad = oportunidad;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public EstadoPostulacion getEstadoPostulacion() {
        return estadoPostulacion;
    }

    public void setEstadoPostulacion(EstadoPostulacion estadoPostulacion) {
        this.estadoPostulacion = estadoPostulacion;
    }
}
