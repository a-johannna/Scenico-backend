package com.example.demo1.models.dtos.UserModel;

import com.example.demo1.models.enums.RoleName;
import jakarta.validation.constraints.*;
import lombok.Data;


import java.util.UUID;


@Data
public class UserModelPublicDTO {
    private UUID uuid;

    @NotBlank(message = "El username es obligatorio")
    @Size(min = 4, max = 50)
    private String username;

    @NotBlank(message = "Este campo es obligatorio.")
    private String firstName;

    @Size(min = 4, max = 50)
    private String lastName;

    @NotBlank(message = "El email es obligatorio.")
    @Email(message = "Formato de email inválido")
    private String email;

    private String location;

    @NotNull(message = "El tipo de usuario es obligatorio")
    private RoleName typeUser;

    @Pattern(regexp = "^(http|https)://.*$")
    private String photoProfile;

    @Size(min = 8, max = 255)
    @NotBlank(message = "Inserte una pequeña descripción")
    private String description;

    private boolean verified;



}