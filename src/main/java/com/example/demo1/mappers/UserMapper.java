package com.example.demo1.mappers;

import com.example.demo1.models.dtos.UserModel.CreateUserDTO;
import com.example.demo1.models.dtos.UserModel.UpdateUserDTO;
import com.example.demo1.models.dtos.UserModel.UserResponseDTO;
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

    public UserResponseDTO toResponseDTO(UserModel userModel) {
        if (userModel == null) {
            return null;
        }

        UserResponseDTO dto = new UserResponseDTO();
        dto.setUuid(userModel.getUuid());
        dto.setUsername(userModel.getUsername());
        dto.setFirstName(userModel.getFirstName());
        dto.setLastName(userModel.getLastName());
        dto.setEmail(userModel.getEmail());
        dto.setLocation(userModel.getLocation());
        dto.setTypeUser(userModel.getTypeUser().name());
        dto.setPhotoProfile(userModel.getPhotoProfile());
        dto.setDescription(userModel.getDescription());
        dto.setVerified(Boolean.TRUE.equals(userModel.getVerified()));
        return dto;
    }



    public UserModel toEntity(CreateUserDTO dto) {
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

    public void updateUserFromDTO(UpdateUserDTO dto, UserModel userModel) {
        if (dto.getUsername() != null) {
            userModel.setUsername(dto.getUsername());
        }
        if (dto.getPassword() != null) {
            userModel.setPassword(passwordEncoderService.encodePassword(dto.getPassword()));
        }
        if (dto.getFirstName() != null) {
            userModel.setFirstName(dto.getFirstName());
        }
        if (dto.getLastName() != null) {
            userModel.setLastName(dto.getLastName());
        }
        if (dto.getEmail() != null) {
            userModel.setEmail(dto.getEmail());
        }
        if (dto.getLocation() != null) {
            userModel.setLocation(dto.getLocation());
        }
        if (dto.getPhotoProfile() != null) {
            userModel.setPhotoProfile(dto.getPhotoProfile());
        }
        if (dto.getDescription() != null) {
            userModel.setDescription(dto.getDescription());
        }
        if (dto.getTypeUser() != null) {
            userModel.setTypeUser(dto.getTypeUser());
        }
        userModel.setUpdateAt(LocalDateTime.now());
    }



}
