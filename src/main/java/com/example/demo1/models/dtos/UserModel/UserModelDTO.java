package com.example.demo1.models.dtos.UserModel;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserModelDTO {

    @Size(min = 4, max = 50)
    private String firstName;
    @Size(min = 4, max = 50)
    private String lastName;
    @NotBlank(message = "El email es obligatorio.")
    @Email(message = "Formato de email inválido")
    private String email;
    private String location;
    @Pattern(regexp = "^(http|https)://.*$")
    private String photoProfile;
    private String description;

}
