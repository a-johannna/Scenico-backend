package com.example.demo1.controllers;

import com.example.demo1.models.dtos.UserModelDTO;
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
    public ResponseEntity<UserModel> getUserById(@PathVariable Long id) {
        return userRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/uuid/{uuid}")
    public ResponseEntity<UserModel> getUserByUuid(@PathVariable UUID uuid) {
        return userRepository.findByUuid(uuid)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());

    }

    @GetMapping("/username/{username}")
    public ResponseEntity<UserModel> getUserByUsername(@PathVariable String username) {
        return userRepository.findByUsername(username)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public List<UserModel> listAllUsers() {
        return userRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody UserModel userModel) {
        if (userRepository.existByEmail(userModel.getEmail())){
            return  ResponseEntity.badRequest()
                    .body("No hay un usuario registrado con este correo.");
        }
        UserModel usuarioExistente = userRepository.save(userModel);
        return ResponseEntity.ok(usuarioExistente);
    }

  /**  @PutMapping("/{id}")
    public ResponseEntity<UserModel> updateUser(@PathVariable Long id, @RequestBody UserModel newUserModel) {
        return userRepository.findById(id).map(userModel -> {
            userModel.setFirstName(newUserModel.getFirstName());
            userModel.setLastName(newUserModel.getLastName());
            userModel.setEmail(newUserModel.getEmail());
            userModel.setLocation(newUserModel.getLocation());
            userModel.setPhotoProfile(newUserModel.getPhotoProfile());
            userModel.setDescription(newUserModel.getDescription());
            return ResponseEntity.ok(userRepository.save(userModel));

        }).orElse(ResponseEntity.notFound().build());
    }

   **/


  @PutMapping("/{id}")
  public ResponseEntity<?> updateUser(@PathVariable Long id, @Valid @RequestBody UserModelDTO newUserDTO) {
      Optional<UserModel> userOpt = userRepository.findById(id);

      if (userOpt.isEmpty()) {
          return ResponseEntity.notFound().build();
      }

      UserModel userModel = userOpt.get();
      if (!userModel.getEmail().equals(newUserDTO.getEmail()) &&
      userRepository.existByEmail(newUserDTO.getEmail())) {
          return ResponseEntity.badRequest().body("Ya existe un usuario con este correo.");

      }

      userModel.setFirstName(newUserDTO.getFirstName());
      userModel.setLastName(newUserDTO.getLastName());
      userModel.setEmail(newUserDTO.getEmail());
      userModel.setLocation(newUserDTO.getLocation());
      userModel.setPhotoProfile(newUserDTO.getPhotoProfile());
      userModel.setDescription(newUserDTO.getDescription());
      return ResponseEntity.ok(userRepository.save(userModel));
  }



    @DeleteMapping({"/{id}"})
    public ResponseEntity<UserModel> deleteUser(@PathVariable Long id) {
        if (!userRepository.existsById(id)){
            return ResponseEntity.notFound().build();
        }
        userRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}


