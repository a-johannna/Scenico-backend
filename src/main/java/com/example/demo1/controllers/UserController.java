package com.example.demo1.controllers;

import com.example.demo1.mappers.UserMapper;
import com.example.demo1.models.dtos.UserModel.CreateUserDTO;
import com.example.demo1.models.dtos.ErrorResponseDTO;
import com.example.demo1.models.dtos.UserModel.UserModelDTO;
import com.example.demo1.models.entidades.UserModel;
import com.example.demo1.repositories.IUserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/userModel")
public class UserController {

    @Autowired
    private IUserRepository userRepository;

    @GetMapping("/{id}")
    public ResponseEntity<UserModelDTO> getUserById(@PathVariable Long id) {
        return userRepository.findById(id)
                .map(UserMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/uuid/{uuid}")
    public ResponseEntity<UserModelDTO> getUserByUuid(@PathVariable UUID uuid) {
        return userRepository.findByUuid(uuid)
                .map(UserMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());

    }

    @GetMapping("/username/{username}")
    public ResponseEntity<UserModelDTO> getUserByUsername(@PathVariable String username) {
        return userRepository.findByUsername(username)
                .map(UserMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public List<UserModelDTO> listAllUsers() {
        return userRepository.findAll().stream().map(UserMapper::toDTO).toList();
    }

    @PostMapping
    public ResponseEntity<?> createUser(@Valid @RequestBody CreateUserDTO createUserDTO) {
        if (userRepository.existsByEmail(createUserDTO.getEmail())) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponseDTO("Ya existe un usuario con este correo.","EMAIL_EXISTS"));
        }
        if (userRepository.existsByUsername(createUserDTO.getUsername())) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponseDTO("El nombre de usuario ya está en uso.","USERNAME_EXISTS"));
        }

        UserModel userModel = UserMapper.toEntity(createUserDTO);
        UserModel savedUser = userRepository.save(userModel);
        return ResponseEntity.ok(UserMapper.toDTO(savedUser));
    }



    @PutMapping("/{id}")
  public ResponseEntity<?> updateUser(@PathVariable Long id, @Valid @RequestBody UserModelDTO newUserDTO) {
      Optional<UserModel> userOpt = userRepository.findById(id);

      if (userOpt.isEmpty()) {
          return ResponseEntity.notFound().build();
      }

      UserModel userModel = userOpt.get();
      if (!userModel.getEmail().equals(newUserDTO.getEmail()) &&
      userRepository.existsByEmail(newUserDTO.getEmail())) {
          return ResponseEntity.badRequest().body(new ErrorResponseDTO("Ya existe un usuario con este correo.", "EMAIL_EXISTS"));

      }

      userModel.setFirstName(newUserDTO.getFirstName());
      userModel.setLastName(newUserDTO.getLastName());
      userModel.setEmail(newUserDTO.getEmail());
      userModel.setLocation(newUserDTO.getLocation());
      userModel.setPhotoProfile(newUserDTO.getPhotoProfile());
      userModel.setDescription(newUserDTO.getDescription());

      UserModel savedUser = userRepository.save(userModel);
      return ResponseEntity.ok(UserMapper.toDTO(savedUser));
  }



    @DeleteMapping({"/{id}"})
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        if (!userRepository.existsById(id)){
            return ResponseEntity.notFound().build();
        }
        userRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}


