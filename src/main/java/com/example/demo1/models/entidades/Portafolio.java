/**
 * Portafolio.java
 * Proyecto: Scénico -Plataforma para artistas emergentes
 * Descripción: Entidad que representa la obra o el contenido de obras publicadas por un usuario.
 * Incluye título, descripción, información necesaria de la entidad.
 * Autor: Andrea Johanna Villavicencio Lema
 * Fecha: Mayo de 2025
 * Email: johannna.villavicencio@gmail.com
 */
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

    /**
     * Identificador único de la entidad
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_portafolio")
    private Long idPortafolio;

    /**
     * Título de la obra
     */
    private String titulo;

    /**
     * Descripción detallada del contenido artístico
     */
    @Column(columnDefinition = "TEXT")
    private String descripcion;

    /**
     * Tipo de archivo asociado (image,video,audio,documento)
     */
    @Enumerated(EnumType.STRING)
    private TipoArchivo tipoArchivo;

    /**
     * URL del archivo principal subido por el artista
     */
    private String urlArchivo;

    /**
     *  URL de la miniatura o imagen destacada asociada a la obra
     */
    private String urlImagen;

    /**
     * Nombre original de imagen
     */
    private String nombreImagen;

    /**
     * Descripción alternativa o pie de imagen
     */
    private String descripcionImagen;

    /**
     *
     */
    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> etiquetas;

    /**
     * Fecha y hora de creación del portafolio. Se asigna al momento de instanciar la entidad
     */
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    /**
     * Relación de muchos a uno con el usuario del portafolio
     */
    @ManyToOne
    @JoinColumn(name = "id_user")
    private UserModel userModel;

}
