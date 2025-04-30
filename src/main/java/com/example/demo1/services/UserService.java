package com.example.demo1.services;

import com.example.demo1.mappers.UserMapper;

import com.example.demo1.models.dtos.UserModel.CreateUserDTO;
import com.example.demo1.models.dtos.UserModel.UpdateUserDTO;
import com.example.demo1.models.dtos.UserModel.UserResponseDTO;
import com.example.demo1.models.entidades.UserModel;
import com.example.demo1.models.enums.TypeUser;
import com.example.demo1.repositories.IUserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserService {

    private final UserMapper userMapper;
    private final IUserRepository userRepository;
    private final RoleService roleService;

    public UserService(UserMapper userMapper, IUserRepository userRepository, RoleService roleService) {
        this.userMapper = userMapper;
        this.userRepository = userRepository;
        this.roleService = roleService;
    }

    public UserResponseDTO createUser(CreateUserDTO createUserDTO) {
        if (userRepository.existsByEmail(createUserDTO.getEmail())) {
            throw new IllegalStateException("Ya existe un usuario con este correo.");
        }
        if(userRepository.existsByUsername(createUserDTO.getUsername())) {
            throw new IllegalStateException("Ya existe un usuario con este nombre de usuario.");
        }

        createUserDTO.setTypeUser(TypeUser.USER);
        UserModel userModel = userMapper.toEntity(createUserDTO);
        userModel.setUuid(UUID.randomUUID());

        roleService.asignarRolInicial(userModel);


        userModel = userRepository.save(userModel);
        return userMapper.toResponseDTO(userModel);
    }

    public UserResponseDTO getUserById(Long id) {
        UserModel userModel = userRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("No existe el usuario con el id: " + id));
        return userMapper.toResponseDTO(userModel);
    }

    public UserResponseDTO findByUuid(UUID uuid) {
        UserModel user = userRepository.findByUuid(uuid)
                .orElseThrow(() -> new IllegalStateException("No existe el usuario con el UUID: " + uuid));
        return userMapper.toResponseDTO(user);
    }

    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

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

    public void deleteUser(UUID uuid) {
        UserModel existingUser = userRepository.findByUuid(uuid)
                .orElseThrow(() -> new IllegalStateException("No existe el usuario con el id: " + uuid));
        userRepository.delete(existingUser);
    }

    public UserResponseDTO verifyUser(UUID uuid) {
        UserModel user = userRepository.findByUuid(uuid)
                .orElseThrow(() -> new IllegalStateException("No existe el usuario con el id: " + uuid));
        user.setVerified(true);
        user.setUpdateAt(java.time.LocalDateTime.now());
        UserModel updatedUser = userRepository.save(user);
        return userMapper.toResponseDTO(updatedUser);
    }

    public UserResponseDTO findByUsername(String username) {
        UserModel user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("No existe el usuario con el nombre de usuario: " + username));
        return userMapper.toResponseDTO(user);
    }

    public UserResponseDTO findByUsernameIgnoreCase(String email) {
        UserModel user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("No existe el usuario con el correo: " + email));
        return userMapper.toResponseDTO(user);
    }
}