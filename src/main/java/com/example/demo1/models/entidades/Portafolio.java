package com.example.demo1.models.entidades;


import com.example.demo1.models.enums.TipoArchivo;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@Table(name = "portafolios")
public class Portafolio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPortafolio;
    private String titulo;
    @Column(columnDefinition = "TEXT")
    private String descripcion;
    @Enumerated(EnumType.STRING)
    private TipoArchivo tipoArchivo;
    private String urlArchivo;
    private String urlImagen;
    private String nombreImagen;
    private String descripcionImagen;
    private String etiquetas;
    private LocalDateTime fechaCreacion = LocalDateTime.now();
    @ManyToOne(optional = false)
    @JoinColumn(name = "id_user")
    private UserModel userModel;

}
