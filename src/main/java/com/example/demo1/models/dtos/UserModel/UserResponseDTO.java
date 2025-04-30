package com.example.demo1.models.dtos.UserModel;


import com.example.demo1.models.enums.TypeUser;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
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
   // private boolean active;
    private boolean verified;



}
