package com.example.demo1.models.entidades;

import com.example.demo1.models.enums.EstadoOportunidad;
import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@Table(name = "oportunidades")
public class Oportunidad {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank(message = "El título es obligatorio")
    @Size(max = 100)
    private String titulo;
    @NotBlank(message = "La descrpción es obligatoria.")
    @Column(columnDefinition = "TEXT")
    @Size(max = 2000)
    private String descripcion;
    @NotBlank
    private String categoria;
    @NotBlank
    @Size(max = 500)
    private String requisitos;
    @NotBlank
    private String ubicacion;
    private LocalDateTime fecha = LocalDateTime.now();
    @Future(message = "La fecha de cierre debe de ser posterior a la fecha actual.")
    private LocalDateTime fechaCierre;


    @Enumerated(EnumType.STRING)
    private EstadoOportunidad estado = EstadoOportunidad.ABIERTO;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_Empresa")
    private UserModel usuarioEmpresa;

}
