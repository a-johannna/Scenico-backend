package com.example.demo1.models.entidades;


import com.example.demo1.models.enums.TipoArchivo;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@Table(name = "portafolios")
public class Portafolio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_portafolio")
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
    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> etiquetas;
    private LocalDateTime fechaCreacion = LocalDateTime.now();
    @ManyToOne
    @JoinColumn(name = "id_user")
    private UserModel userModel;

}
