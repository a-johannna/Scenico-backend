/**
 * UserService.java
 * Proyecto: Scénico -Plataforma para artistas emergentes
 * Descripción: Servicio que gestiona la lógica de negocio relacionada con los usuarios.
 * Incluye operaciones de creación, actualización, eliminación, consulta, verificación
 * y subida de imagen de perfil.
 * Autor: Andrea Johanna Villavicencio Lema
 * Fecha: Mayo de 2025
 * Email: johannna.villavicencio@gmail.com
 */

package com.example.demo1.services;

import com.example.demo1.mappers.UserMapper;

import com.example.demo1.models.dtos.UserModel.CreateUserDTO;
import com.example.demo1.models.dtos.UserModel.UpdateUserDTO;
import com.example.demo1.models.dtos.UserModel.UserResponseDTO;
import com.example.demo1.models.entidades.UserModel;
import com.example.demo1.models.enums.RoleName;
import com.example.demo1.repositories.IUserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Servicio de UserModel que contiene la lógica para el manejo de entidades UserModel.
 * Este servicio interactúa con el repositorio y mapeadores para implementar la cpa de negocio.
 */
@Service
@Transactional
public class UserService {

    private final UserMapper userMapper;
    private final IUserRepository userRepository;
    private final RoleService roleService;

    /**
     * Constructor que inyecta las dependencias necesarias para el servicio.
     * @param userMapper        Mapper para transformar entidades y DTO
     * @param userRepository    Repositorio de usuario
     * @param roleService       Servicio de asignación de roles
     */
    public UserService(UserMapper userMapper, IUserRepository userRepository, RoleService roleService) {
        this.userMapper = userMapper;
        this.userRepository = userRepository;
        this.roleService = roleService;
    }

    /**
     * Crea un nuevo usuario con validaciones previas y asignación de rol inicial.
     * @param createUserDTO DTO con los datos del nuevo usuario
     * @return representación del usuario creado
     */
    public UserResponseDTO createUser(CreateUserDTO createUserDTO) {
        if (userRepository.existsByEmail(createUserDTO.getEmail())) {
            throw new IllegalStateException("Ya existe un usuario con este correo.");
        }
        if(userRepository.existsByUsername(createUserDTO.getUsername())) {
            throw new IllegalStateException("Ya existe un usuario con este nombre de usuario.");
        }

        createUserDTO.setTypeUser(RoleName.USER);
        UserModel userModel = userMapper.toEntity(createUserDTO);
        userModel.setUuid(UUID.randomUUID());

        roleService.asignarRolInicial(userModel);


        userModel = userRepository.save(userModel);
        return userMapper.toResponseDTO(userModel);
    }

    /**
     * Obtiene un usuario por UUID, lanza una excepción si no existe.
     * @param uuid identificador público del usuario
     * @return entidad UserModel
     */
    public UserModel getByUuid(UUID uuid) {
        return userRepository.findByUuid(uuid)
                .orElseThrow(() -> new IllegalStateException("Usuario no encontrado con UUID: " + uuid));
    }

    /**
     * Busca el usuario por el UUID y devuelve un DTO de respuesta.
     * @param uuid identificador del usuario
     * @return DTO con los datos públicos del usuario
     */
    public UserResponseDTO findByUuid(UUID uuid) {
        UserModel user = userRepository.findByUuid(uuid)
                .orElseThrow(() -> new IllegalStateException("No existe el usuario con el UUID: " + uuid));
        return userMapper.toResponseDTO(user);
    }

    /**
     * Busca el usuario por el username.
     * @param username nombre de usuario
     * @return DTO con la información del usuario
     */
    public UserResponseDTO findByUsername(String username) {
        UserModel user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("No existe el usuario con el nombre de usuario: " + username));
        return userMapper.toResponseDTO(user);
    }

    /**
     * Devuelve una lista con todos los usuarios registrados
     * @return lista de usuarios en formato DTO
     */
    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Actualiza un usuario existente a partir de un UUID y un DTO.
     * Comprueba que el email y el nombre no estén duplicados,
     * de modo que se actualice los datos del usuario correspondiente y evitar
     * actualizar dos veces con los mismos datos.
     * @param uuid              UUID del usuario a actualizar
     * @param updateUserDTO     Datos nuevos del usuario
     * @return                  DTO con los datos actualizados
     */
    public UserResponseDTO updateUser(UUID uuid, UpdateUserDTO updateUserDTO) {
        UserModel existingUser = userRepository.findByUuid(uuid)
                .orElseThrow(() -> new IllegalStateException("No existe el usuario."));

        // Validar email único
        if (updateUserDTO.getEmail() != null &&
                !updateUserDTO.getEmail().equals(existingUser.getEmail()) &&
                userRepository.existsByEmail(updateUserDTO.getEmail())) {
            throw new IllegalStateException("El email ya está en uso");
        }

        // Validar username único
        if (updateUserDTO.getUsername() != null &&
                !updateUserDTO.getUsername().equals(existingUser.getUsername()) &&
                userRepository.existsByUsername(updateUserDTO.getUsername())) {
            throw new IllegalStateException("Ya existe un usuario con este nombre de usuario.");
        }

        userMapper.updateUserFromDTO(updateUserDTO, existingUser);
        UserModel updatedUser = userRepository.save(existingUser);
        return userMapper.toResponseDTO(updatedUser);
    }

    /**
     * Elimina un usuario del sistema por el UUID.
     * @param uuid identificador único del usuario a eliminar
     */
    public void deleteUser(UUID uuid) {
        UserModel existingUser = userRepository.findByUuid(uuid)
                .orElseThrow(() -> new IllegalStateException("No existe el usuario con el id: " + uuid));
        userRepository.delete(existingUser);
    }

    /**
     * Verifica manualmente a un usuario marcándolo como verificado (aun sin usar)
     * @param uuid identificador del usuario a verificar
     * @return DTO con el estado de verificación actualizado
     */
    public UserResponseDTO verifyUser(UUID uuid) {
        UserModel user = userRepository.findByUuid(uuid)
                .orElseThrow(() -> new IllegalStateException("No existe el usuario con el id: " + uuid));
        user.setVerified(true);
        user.setUpdateAt(java.time.LocalDateTime.now());
        UserModel updatedUser = userRepository.save(user);
        return userMapper.toResponseDTO(updatedUser);
    }


    /**
     * Guarda la imagen de perfil del usuario y actualiza su ruta
     * en la base de datos.
     * @param uuid UUID del usuario
     * @param file archivo de imagen recibido
     * @return URL públicada de la imagen guardada
     */
    public String saveProfileImage(String uuid, MultipartFile file) {
        UserModel usuario = userRepository.findByUuid(UUID.fromString(uuid))
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path path = Paths.get("upload-photo/" + fileName);

        try {
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Error al guardar la imagen", e);
        }

        // Guardar la URL en el usuario
        String imageUrl = "http://tuservidor.com/upload-photo/" + fileName;
        usuario.setPhotoProfile(imageUrl);
        userRepository.save(usuario);

        return imageUrl;
    }

}