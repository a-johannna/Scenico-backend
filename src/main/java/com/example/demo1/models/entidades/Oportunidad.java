/**
 * Oportunidad.java
 * Proyecto: Scénico -Plataforma para artistas emergentes
 * Descripción: Entidad que representa una oportunidad laboral o colaborativa publicada por una empresa.
 * Incluye información detallada como título, descripción, requisitos, ubicación, fecha de creación,
 * fecha de cierre y estado de la oportunidad.
 * Autor: Andrea Johanna Villavicencio Lema
 * Fecha: Mayo de 2025
 * Email: johannna.villavicencio@gmail.com
 */
package com.example.demo1.models.entidades;

import com.example.demo1.models.enums.EstadoOportunidad;
import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

/**
 * Entidad JPA que representa una oferta publicada por una empresa o productor.
 */
@Setter
@Getter
@Entity
@Table(name = "oportunidades")
public class Oportunidad {

    /**
     * Identificador único de la oportunidad (clave primaria).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Título breve del casting (oportunidad).
     */
    @NotBlank(message = "El título es obligatorio")
    @Size(max = 100)
    private String titulo;

    /**
     * Descripción detallada de la oportunidad.
     */
    @NotBlank(message = "La descrpción es obligatoria.")
    @Column(columnDefinition = "TEXT")
    @Size(max = 2000)
    private String descripcion;

    /**
     * Categoría artística o profesional de la oportunidad (por ejemplo, música, teatro).
     */
    @NotBlank
    private String categoria;

    /**
     * Requisitos mínimos que debe cumplir el artista para postularse.
     */
    @NotBlank
    @Size(max = 500)
    private String requisitos;

    /**
     * Ubicación geográfica donde se desarrolla la oportunidad.
     */
    @NotBlank
    private String ubicacion;

    /**
     * Fecha de publicación de la oportunidad. Se asigna automáticamente al crearla.
     */
    private LocalDateTime fecha = LocalDateTime.now();

    /**
     *
     */
    @Future(message = "La fecha de cierre debe de ser posterior a la fecha actual.")
    private LocalDateTime fechaCierre;

    /**
     * Estado actual de la oportunidad (ABIERTA, CERRADA)
     */
    @Enumerated(EnumType.STRING)
    private EstadoOportunidad estado = EstadoOportunidad.ABIERTO;

    /**
     * Relación muchos a uno con el usuario de tipo empresa que creó la oportunidad
     */
    @ManyToOne(optional = false)
    @JoinColumn(name = "id_user")
    private UserModel usuarioEmpresa;

}
