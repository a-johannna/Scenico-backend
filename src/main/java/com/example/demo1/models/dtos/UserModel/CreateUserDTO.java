package com.example.demo1.models.dtos.UserModel;


import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Data
public class CreateUserDTO{
    @NotBlank(message = "El username es obligatorio")
    @Size(min = 4, max = 50)
    private String username;

    @NotBlank(message = "Este campo es obligatorio.")
    @Size(min = 4, max = 50)
    private String firstName;

    @Size(min = 4, max = 50)
    private String lastName;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Formato de email inválido")
    private String email;

    private String location;

    @NotBlank(message = "La contraseña es obligatoria")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$",
            message = "La contraseña debe tener al menos 8 caracteres, una letra y un número")
    private String password;

    //@NotNull(message = "El tipo de usuario es obligatorio")
    //private TypeUser typeUser = TypeUser.USER;   //Creo que deberismo quitarlo

    @Pattern(regexp = "^(http|https)://.*$")
    private String photoProfile;

    @Size(min = 8, max = 255)
    @NotBlank(message = "Inserte una pequeña descripción")
    private String description;
}