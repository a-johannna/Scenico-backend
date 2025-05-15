package com.example.demo1.models.dtos.UserModel;

import com.example.demo1.models.enums.RoleName;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;


@Data
public class UpdateUserDTO {
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$",
            message = "La contraseña debe tener al menos 8 caracteres, una letra y un número")
    private String password;
    @Pattern(regexp = "^(http|https)://.*$")
    private String photoProfile;

    @Size(min = 8, max = 255)
    private String description;

    private String location;

    @Email(message = "Formato de email inválido.")
    private String email;

    @Size(min = 4, max = 50)
    private String username;

    @Size(min = 4, max = 50)
    private String firstName;

    @Size(min = 4, max = 50)
    private String lastName;


    private RoleName typeUser;


}
