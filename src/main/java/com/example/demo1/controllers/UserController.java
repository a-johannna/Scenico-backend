/**
 * UserController.java
 * Proyecto: Scénico -Plataforma para artistas emergentes
 * Descripción: Controlador REST que gestiona las solicitudes HTTP relacionadas
 * con los usuarios. Expone endpoints públicos para el registro, consulta, actualización,
 * eliminación y verificación de usuarios.
 * En la mayoría de los casos si los errores comunes son (400 o 500)
 * en caso contrario (200 o 201) quiere decir que se ha realizado con éxito.
 * Autor: Andrea Johanna Villavicencio Lema
 * Fecha: Mayo de 2025
 * Email: johannna.villavicencio@gmail.com
 */
package com.example.demo1.controllers;

import com.example.demo1.models.dtos.UserModel.CreateUserDTO;
import com.example.demo1.models.dtos.ErrorResponseDTO;
import com.example.demo1.models.dtos.UserModel.UpdateUserDTO;
import com.example.demo1.models.dtos.UserModel.UserResponseDTO;
import com.example.demo1.services.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {


    private final UserService userService;

    /**
     * Constructor que inyecta el servicio de usuarios.
     * @param userService servicio de lógica de negocio relacionado con usuarios
     */
    public UserController(UserService userService) {
        this.userService = userService;
    }


    /**
     * Obtiene los datos del usuario según el UUID
     * @param uuid      identificador único del usuario
     * @return          DTO con la información del usuario
     */
    @GetMapping("/uuid/{uuid}")
    public ResponseEntity<UserResponseDTO> getUserByUuid(@PathVariable UUID uuid) {
        try {
            UserResponseDTO user = userService.findByUuid(uuid);
            return ResponseEntity.ok(user);
        } catch (IllegalStateException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Endpoint que busca por el nombre de usuario
     * @param username      nombre de usuario
     * @return              DTO con los datos del usuario
     */
    @GetMapping("/username/{username}")
    public ResponseEntity<UserResponseDTO> getUserByUsername(@PathVariable String username) {
      try {
          UserResponseDTO userResponseDTO = userService.findByUsername(username);
          return ResponseEntity.ok(userResponseDTO);

      } catch (Exception e) {
          return ResponseEntity.notFound().build();
      }
    }

    /**
     * Obtiene todos los usuarios registrados en la plataforma.
     * @return lista de usuarios en formato DTO (200)
     */
    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        List<UserResponseDTO> userResponseDTOS = userService.getAllUsers();
        return ResponseEntity.ok(userResponseDTOS);
    }

    /**
     * Endpoint que crea un nuevo usuario en la plataforma
     * @param dto       objeto CreateUserDTO que contiene los datos de un nuevo usuario
     * @param result    resultado del proceso de validación
     * @return          ResponseEntity con mapa de errores de validaciones(400)
     *                  o si crea correctamente el usuario da (201 o 200)
     */
    @PostMapping("/register")
    public ResponseEntity<?> createUser(@Valid @RequestBody CreateUserDTO dto, BindingResult result) {
        if (result.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            for (FieldError error : result.getFieldErrors()) {
                errors.put(error.getField(), error.getDefaultMessage());
            }
            return ResponseEntity.badRequest().body(errors);
        }

        UserResponseDTO createdUser = userService.createUser(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    /**
     * Actualiza los datos de un usuario por UUID
     * @param uuid          identificador del usuario a modificar
     * @param updateUser    objeto con los datos actualizar
     * @return              DTO actualizado del usuario (200 o 201)
     */
    @PutMapping("/uuid/{uuid}")
  public ResponseEntity<?> updateUser(@PathVariable UUID uuid, @Valid @RequestBody UpdateUserDTO updateUser) {

        try {
            UserResponseDTO userResponseDTO = userService.updateUser(uuid,updateUser);
            return ResponseEntity.ok(userResponseDTO);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponseDTO("Error al actualizar el usuario", "ERROR_UPDATING_USER"));
        }
  }


    /**
     * Elimina un usuario de la base de datos por UUID.
     * @param uuid  identificador dek usuario
     * @return      respuesta vacía con código 200 si es correcto
     */
    @DeleteMapping({"/uuid/{uuid}"})
    public ResponseEntity<Void> deleteUser(@PathVariable UUID uuid) {
      try {
          userService.deleteUser(uuid);
          return ResponseEntity.noContent().build();
      } catch (IllegalStateException e) {
          return ResponseEntity.badRequest().build();
      }
    }

    /**
     * Permite a un usuario subir una imagen de perfil
     * @param file  archivo con la imagen
     * @param uuid  UUID del usuario de la imagen de perfil
     * @return      URL pública de la imagen guardada
     */
    @PostMapping("/upload-photo")
    public ResponseEntity<String> uploadPhoto(@RequestParam("file") MultipartFile file,
                                              @RequestParam("uuid") String uuid) {
        String url = userService.saveProfileImage(uuid, file);
        return ResponseEntity.ok(url); // devuelves la URL para mostrar
    }

}


