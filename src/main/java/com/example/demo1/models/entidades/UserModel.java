package com.example.demo1.models.entidades;
import com.example.demo1.models.enums.RoleName;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.lang.Nullable;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Getter
@Setter
@Entity
@Table(name = "user")

public class UserModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_user")
    private Long id_user;   // ID interno de la base de datos.
    @Column(name ="uuid", unique = true, nullable = false, updatable = false)
    private UUID uuid;// externo de la base de datos.(Público)
    @PrePersist
    protected void onCreate() {
        if (uuid == null) {
            uuid = UUID.randomUUID();

        }
        createdAt = LocalDateTime.now();
        updateAt = LocalDateTime.now();
    }
    @Column(unique = true, nullable = false)
    @Size(min = 4, max = 50)
    private String username;
    @Size(min = 4, max = 50)
    private String firstName;
    @Size(min = 4, max = 50)
    private String lastName;
    @NotBlank(message = "Es necesario rellenar este campo.")
    @Email
    @Column(unique = true, nullable = false)
    private String email;
    @Size(min = 8)
    @JsonProperty(access =  JsonProperty.Access.WRITE_ONLY)
    private String password;

    @Enumerated(EnumType.STRING)
    private RoleName typeUser;

    private Boolean verified = false;
    private String location;
    //@Pattern(regexp = "^(http|https)://.*$")
    @Nullable
    private String photoProfile;
    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime updateAt;
    private String description;


    @PreUpdate
    protected void onUpdate() {
        updateAt = LocalDateTime.now();
    }


    public Boolean isVerified() {

        return Boolean.TRUE.equals(this.verified);


    }
}