/**
 * UserModel.java
 * Proyecto: Scénico -Plataforma para artistas emergentes
 * Descripción: Entidad que representa a un usuario dentro de la plataforma.
 * Puede ser user, artist, enterprise o admin. Esta clase incluye la información básica.
 * Autor: Andrea Johanna Villavicencio Lema
 * Fecha: Mayo de 2025
 * Email: johannna.villavicencio@gmail.com
 */

package com.example.demo1.models.entidades;
import com.example.demo1.models.enums.RoleName;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.lang.Nullable;

import java.time.LocalDateTime;
import java.util.UUID;


@Data
@Entity
@Table(name = "user")

public class UserModel {
    /**
     * Identificador interno único del usuario, clave primaria de la tabla.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_user")
    private Long id_user;
    /**
     * Identificador único del usuario, expuesto al público de manera segura.
     */
    @Column(name ="uuid", unique = true, nullable = false, updatable = false)
    private UUID uuid;

    /**
     * Método que se ejecuta justo antes que la entidad sea persistida por primera vez.
     * Genera un UUID si aún no se ha asignado al usuario,
     * y establece la fecha actual para los campos createdAt y updateAt.
     */
    @PrePersist
    protected void onCreate() {
        if (uuid == null) {
            uuid = UUID.randomUUID();

        }
        createdAt = LocalDateTime.now();
        updateAt = LocalDateTime.now();
    }

    /**
     * Nombre de usuario único en el sistema.
     */
    @Column(unique = true, nullable = false)
    @Size(min = 4, max = 50)
    private String username;

    /**
     * Nombre del usuario
     */
    @Size(min = 4, max = 50)
    private String firstName;

    /**
     * Apellido del usuario
     */
    @Size(min = 4, max = 50)
    private String lastName;

    /**
     * Correo electrónico único del usuario
     */
    @NotBlank(message = "Es necesario rellenar este campo.")
    @Email
    @Column(unique = true, nullable = false)
    private String email;

    /**
     * Contraseña cifrada del usuario
     */
    @Size(min = 8)
    @JsonProperty(access =  JsonProperty.Access.WRITE_ONLY)
    private String password;

    /**
     * Rol del usuario dentro de la plataforma (USER, ARTIST, ENTERPRISE O ADMIN)
     */
    @Enumerated(EnumType.STRING)
    private RoleName typeUser;

    /**
     * Indica si el usuario ha sido verificado manualmente.
     */
    private Boolean verified = false;

    /**
     * Ubicación del usuario (opcional)
     */
    private String location;

    /**
     * Ruta o nombre del archivo de imagen de perfil
     */
    //@Pattern(regexp = "^(http|https)://.*$")
    @Nullable
    private String photoProfile;

    /**
     * Fecha de creación automática.
     * Se asigna al momento de persistir por primera vez.
     */
    @CreatedDate
    private LocalDateTime createdAt;

    /**
     * Fecha de la última modificación de datos.
     * Se actualiza automáticamente al modificar la entidad.
     */
    @LastModifiedDate
    private LocalDateTime updateAt;

    /**
     * Descripción adicional del perfil
     */
    private String description;

    /**
     * Método que se ejecuta justo antes de actualizar la entidad.
     */
    @PreUpdate
    protected void onUpdate() {
        updateAt = LocalDateTime.now();
    }

    /**
     * Comprueba si el usuario ha sido verificado.
     * @return devuelve true si el campo verified es TRUE, false en caso contrario.
     */
    public Boolean isVerified() {

        return Boolean.TRUE.equals(this.verified);


    }
}