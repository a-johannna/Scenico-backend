package com.example.demo1.models.dtos.UserModel;


import lombok.Data;
import lombok.Getter;

import java.util.UUID;

@Getter
@Data
public class UserResponseDTO  {
    private UUID uuid;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String location;
    private String photoProfile;
    private String description;
    private String typeUser;
    private boolean verified;



}
