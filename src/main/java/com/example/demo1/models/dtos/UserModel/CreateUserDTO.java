package com.example.demo1.models.dtos.UserModel;

import com.example.demo1.models.enums.TypeUser;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CreateUserDTO {
    @NotBlank(message = "El username es obligatorio")
    @Size(min = 4, max = 50)
    private String username;

    @Size(min = 4, max = 50)
    private String firstName;

    @Size(min = 4, max = 50)
    private String lastName;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Formato de email inválido")
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    private String password;

    private String location;

    @NotNull(message = "El tipo de usuario es obligatorio")
    private TypeUser typeUser;

    @Pattern(regexp = "^(http|https)://.*$")
    private String photoProfile;

    private String description;
}