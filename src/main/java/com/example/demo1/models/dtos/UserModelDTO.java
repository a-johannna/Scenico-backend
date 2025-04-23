package com.example.demo1.models.dtos;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserModelDTO {

    private String firstName;
    private String lastName;
    private String email;
    private String location;
    private String photoProfile;
    private String description;

}
