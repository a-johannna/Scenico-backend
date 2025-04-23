package com.example.demo1.models.entidades;

import com.example.demo1.models.enums.EstadoOportunidad;
import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

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
    private EstadoOportunidad estadoOportunidad = EstadoOportunidad.ABIERTO;

    @ManyToOne(optional = false)
    @JoinColumn(name = "idEmpresa")
    private UserModel usuarioEmpresa;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getRequisitos() {
        return requisitos;
    }

    public void setRequisitos(String requisitos) {
        this.requisitos = requisitos;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public LocalDateTime getFechaCierre() {
        return fechaCierre;
    }

    public void setFechaCierre(LocalDateTime fechaCierre) {
        this.fechaCierre = fechaCierre;
    }

    public EstadoOportunidad getEstadoOportunidad() {
        return estadoOportunidad;
    }

    public void setEstadoOportunidad(EstadoOportunidad estadoOportunidad) {
        this.estadoOportunidad = estadoOportunidad;
    }

    public UserModel getUsuarioEmpresa() {
        return usuarioEmpresa;
    }

    public void setUsuarioEmpresa(UserModel usuarioEmpresa) {
        this.usuarioEmpresa = usuarioEmpresa;
    }
}
