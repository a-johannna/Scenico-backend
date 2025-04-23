package com.example.demo1.models.entidades;

import com.example.demo1.models.enums.TypeUser;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.UUID;

@Setter
@Getter
@Entity
@Table(name = "userModel")
public class UserModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idUser;
    @Column(unique = true, nullable = false, updatable = false)
    private UUID uuid = UUID.randomUUID();
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
    private TypeUser typeUser;

    private boolean verified = false;
    private String location;
    @Pattern(regexp = "^(http|https)://.*$")
    private String photoProfile;
    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime updateAt;
    private String description;
    
    
@PrePersist
protected void onCreate() {
    createdAt = LocalDateTime.now();
    updateAt = LocalDateTime.now();
}

@PreUpdate
protected void onUpdate() {
    updateAt = LocalDateTime.now();
}
}