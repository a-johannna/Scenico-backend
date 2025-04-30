package com.example.demo1.controllers;


import com.example.demo1.models.dtos.UserModel.CreateUserDTO;
import com.example.demo1.models.dtos.ErrorResponseDTO;
import com.example.demo1.models.dtos.UserModel.UpdateUserDTO;
import com.example.demo1.models.dtos.UserModel.UserResponseDTO;
import com.example.demo1.repositories.IUserRepository;
import com.example.demo1.services.RoleService;
import com.example.demo1.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @Autowired
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }


    @GetMapping("/{uuid}")
    public ResponseEntity<UserResponseDTO> getUserByUuid(@PathVariable UUID uuid) {
        try {
            UserResponseDTO user = userService.findByUuid(uuid);
            return ResponseEntity.ok(user);
        } catch (IllegalStateException e) {
            return ResponseEntity.notFound().build();
        }
    }


    @GetMapping("/{username}")
    public ResponseEntity<UserResponseDTO> getUserByUsername(@PathVariable String username) {
      try {
          UserResponseDTO userResponseDTO = userService.findByUsername(username);
          return ResponseEntity.ok(userResponseDTO);

      } catch (Exception e) {
          return ResponseEntity.notFound().build();
      }
    }

    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        List<UserResponseDTO> userResponseDTOS = userService.getAllUsers();
        return ResponseEntity.ok(userResponseDTOS);
    }

    @PostMapping
    public ResponseEntity<UserResponseDTO> createUser(@Valid @RequestBody CreateUserDTO createUserDTO) {
        try {
            UserResponseDTO createdUser = userService.createUser(createUserDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);

        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }



    @PutMapping("/{uuid}")
  public ResponseEntity<?> updateUser(@PathVariable UUID uuid, @Valid @RequestBody UpdateUserDTO updateUser) {

        try {
            UserResponseDTO userResponseDTO = userService.updateUser(uuid,updateUser);
            return ResponseEntity.ok(userResponseDTO);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponseDTO("Error al actualizar el usuario", "ERROR_UPDATING_USER"));
        }
  }



    @DeleteMapping({"/{id}"})
    public ResponseEntity<Void> deleteUser(@PathVariable UUID uuid) {
      try {
          userService.deleteUser(uuid);
          return ResponseEntity.noContent().build();
      } catch (IllegalStateException e) {
          return ResponseEntity.badRequest().build();
      }
    }
}


