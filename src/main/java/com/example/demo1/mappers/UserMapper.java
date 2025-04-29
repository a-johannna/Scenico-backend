package com.example.demo1.mappers;

import com.example.demo1.models.dtos.UserModel.CreateUserDTO;
import com.example.demo1.models.dtos.UserModel.UserModelDTO;
import com.example.demo1.models.entidades.UserModel;
import com.example.demo1.services.PasswordEncoderService;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class UserMapper {

    private final PasswordEncoderService passwordEncoderService;

    public UserMapper(PasswordEncoderService passwordEncoderService) {
        this.passwordEncoderService = passwordEncoderService;
    }

    public static UserModelDTO toDTO(UserModel userModel)
    {
        UserModelDTO dto = new UserModelDTO();
        dto.setFirstName(userModel.getFirstName());
        dto.setLastName(userModel.getLastName());
        dto.setEmail(userModel.getEmail());
        dto.setLocation(userModel.getLocation());
        dto.setPhotoProfile(userModel.getPhotoProfile());
        dto.setDescription(userModel.getDescription());
        return dto;
    }

    /**public static UserModel toEntity(UserModelDTO dto) {
        UserModel userModel = new UserModel();
        userModel.setFirstName(dto.getFirstName());
        userModel.setLastName(dto.getLastName());
        userModel.setEmail(dto.getEmail());
        userModel.setLocation(dto.getLocation());
        userModel.setPhotoProfile(dto.getPhotoProfile());
        userModel.setDescription(dto.getDescription());

        return userModel;

    } **/

    public static UserModel toEntity(CreateUserDTO dto) {
        UserModel userModel = new UserModel();
        userModel.setUsername(dto.getUsername());
        userModel.setPassword(passwordEncoderService.encodePassword(dto.getPassword()));
        userModel.setFirstName(dto.getFirstName());
        userModel.setLastName(dto.getLastName());
        userModel.setEmail(dto.getEmail());
        userModel.setLocation(dto.getLocation());
        userModel.setPhotoProfile(dto.getPhotoProfile());
        userModel.setDescription(dto.getDescription());
        userModel.setTypeUser(dto.getTypeUser());
        userModel.setVerified(false);
        userModel.setCreatedAt(LocalDateTime.now());
        userModel.setUpdateAt(LocalDateTime.now());
        return userModel;

    }


}
