package com.example.demo1.models.entidades;

import com.example.demo1.models.enums.EstadoPostulacion;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
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


}
