package com.example.demo1.controllers;

import com.example.demo1.models.dtos.UserModel.CreateUserDTO;
import com.example.demo1.models.dtos.UserModel.UpdateUserDTO;
import com.example.demo1.models.dtos.UserModel.UserResponseDTO;
import com.example.demo1.models.enums.RoleName;
import com.example.demo1.services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Pruebas unitarias para UserController
 */
@ExtendWith(MockitoExtension.class)
class UserControllerStandaloneTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        // Configuramos MockMvc en modo standalone, inyectando el controlador con su mock de UserService
        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                // Para que los errores de validación de @Valid en CreateUserDTO/UpdateUserDTO
                // se traduzcan en BindingResult y devuelvan 400 automáticamente, añadimos este advice:
                .setControllerAdvice(new MethodArgumentNotValidExceptionHandler())
                .build();
    }

    // ----------------------------------------------
    // 1) GET /api/v1/users/uuid/{uuid}
    // ----------------------------------------------
    @Nested
    @DisplayName("getUserByUuid(...)")
    class GetUserByUuidTests {

        @Test
        @DisplayName("Usuario existe → 200 y JSON con UserResponseDTO")
        void getUserByUuid_Exists_ReturnsOk() throws Exception {
            UUID uuid = UUID.randomUUID();
            UserResponseDTO dto = new UserResponseDTO();
            dto.setId_user(1L);
            dto.setUuid(uuid);
            dto.setUsername("user1");
            dto.setEmail("user1@ejemplo.com");

            when(userService.findByUuid(uuid)).thenReturn(dto);

            mockMvc.perform(get("/api/v1/users/uuid/{uuid}", uuid))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id_user").value(1))
                    .andExpect(jsonPath("$.uuid").value(uuid.toString()))
                    .andExpect(jsonPath("$.username").value("user1"))
                    .andExpect(jsonPath("$.email").value("user1@ejemplo.com"));

            verify(userService, times(1)).findByUuid(uuid);
        }

        @Test
        @DisplayName("Usuario no existe → 404 Not Found")
        void getUserByUuid_NotFound_Returns404() throws Exception {
            UUID uuid = UUID.randomUUID();
            when(userService.findByUuid(uuid)).thenThrow(new IllegalStateException("No existe"));

            mockMvc.perform(get("/api/v1/users/uuid/{uuid}", uuid))
                    .andExpect(status().isNotFound());

            verify(userService, times(1)).findByUuid(uuid);
        }
    }

    // ----------------------------------------------
    // 2) GET /api/v1/users/username/{username}
    // ----------------------------------------------
    @Nested
    @DisplayName("getUserByUsername(...)")
    class GetUserByUsernameTests {

        @Test
        @DisplayName("Usuario existe → 200 y JSON con UserResponseDTO")
        void getUserByUsername_Exists_ReturnsOk() throws Exception {
            String username = "user1";
            UserResponseDTO dto = new UserResponseDTO();
            dto.setId_user(2L);
            dto.setUuid(UUID.randomUUID());
            dto.setUsername(username);
            dto.setEmail("user1@ejemplo.com");

            when(userService.findByUsername(username)).thenReturn(dto);

            mockMvc.perform(get("/api/v1/users/username/{username}", username))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id_user").value(2))
                    .andExpect(jsonPath("$.username").value("user1"))
                    .andExpect(jsonPath("$.email").value("user1@ejemplo.com"));

            verify(userService, times(1)).findByUsername(username);
        }

        @Test
        @DisplayName("Usuario no existe → 404 Not Found")
        void getUserByUsername_NotFound_Returns404() throws Exception {
            String username = "noExiste";
            when(userService.findByUsername(username)).thenThrow(new IllegalStateException("No existe"));

            mockMvc.perform(get("/api/v1/users/username/{username}", username))
                    .andExpect(status().isNotFound());

            verify(userService, times(1)).findByUsername(username);
        }
    }

    // ----------------------------------------------
    // 3) GET /api/v1/users
    // ----------------------------------------------
    @Nested
    @DisplayName("getAllUsers()")
    class GetAllUsersTests {

        @Test
        @DisplayName("Lista vacía → 200 y JSON array vacío")
        void getAllUsers_EmptyList_ReturnsOkEmpty() throws Exception {
            when(userService.getAllUsers()).thenReturn(Collections.emptyList());

            mockMvc.perform(get("/api/v1/users"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.length()").value(0));

            verify(userService, times(1)).getAllUsers();
        }

        @Test
        @DisplayName("Varios usuarios → 200 y JSON con lista de DTOs")
        void getAllUsers_Multiple_ReturnsOkList() throws Exception {
            UserResponseDTO u1 = new UserResponseDTO();
            u1.setId_user(1L);
            u1.setUsername("a");
            u1.setEmail("a@ejemplo.com");
            UserResponseDTO u2 = new UserResponseDTO();
            u2.setId_user(2L);
            u2.setUsername("b");
            u2.setEmail("b@ejemplo.com");

            when(userService.getAllUsers()).thenReturn(Arrays.asList(u1, u2));

            mockMvc.perform(get("/api/v1/users"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.length()").value(2))
                    .andExpect(jsonPath("$[0].id_user").value(1))
                    .andExpect(jsonPath("$[1].id_user").value(2));

            verify(userService, times(1)).getAllUsers();
        }
    }

    // ----------------------------------------------
    // 4) POST /api/v1/users/register
    // ----------------------------------------------
    @Nested
    @DisplayName("createUser(...)")
    class CreateUserTests {

        @Test
        @DisplayName("DTO válido → 201 Created y JSON con UserResponseDTO")
        void createUser_ValidDTO_ReturnsCreated() throws Exception {
            CreateUserDTO dto = new CreateUserDTO();
            dto.setUsername("nuevoUser");
            dto.setFirstName("Anna");
            dto.setLastName("Gómez");
            dto.setEmail("nuevo@ejemplo.com");
            dto.setPassword("Pass1234");
            dto.setTypeUser(RoleName.USER);
            dto.setDescription("Soy artista de Bogotá");

            UserResponseDTO respuesta = new UserResponseDTO();
            respuesta.setId_user(10L);
            respuesta.setUuid(UUID.randomUUID());
            respuesta.setUsername("nuevoUser");
            respuesta.setEmail("nuevo@ejemplo.com");
            respuesta.setDescription("Soy artista de Bogotá");

            when(userService.createUser(any(CreateUserDTO.class))).thenReturn(respuesta);

            mockMvc.perform(post("/api/v1/users/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id_user").value(10))
                    .andExpect(jsonPath("$.username").value("nuevoUser"))
                    .andExpect(jsonPath("$.email").value("nuevo@ejemplo.com"))
                    .andExpect(jsonPath("$.description").value("Soy artista de Bogotá"));

            verify(userService, times(1)).createUser(any(CreateUserDTO.class));
        }

    }

    // ----------------------------------------------
    // 5) PUT /api/v1/users/uuid/{uuid}
    // ----------------------------------------------
    @Nested
    @DisplayName("updateUser(...)")
    class UpdateUserTests {

        @Test
        @DisplayName("DTO válido → 200 OK y JSON con UserResponseDTO")
        void updateUser_ValidDTO_ReturnsOk() throws Exception {
            UUID uuid = UUID.randomUUID();
            UpdateUserDTO dto = new UpdateUserDTO();
            dto.setEmail("actualizado@ejemplo.com");
            dto.setUsername("userActualizado");

            UserResponseDTO respuesta = new UserResponseDTO();
            respuesta.setId_user(5L);
            respuesta.setUsername("userActualizado");
            respuesta.setEmail("actualizado@ejemplo.com");

            when(userService.updateUser(eq(uuid), any(UpdateUserDTO.class)))
                    .thenReturn(respuesta);

            mockMvc.perform(put("/api/v1/users/uuid/{uuid}", uuid)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id_user").value(5))
                    .andExpect(jsonPath("$.username").value("userActualizado"))
                    .andExpect(jsonPath("$.email").value("actualizado@ejemplo.com"));

            verify(userService, times(1)).updateUser(eq(uuid), any(UpdateUserDTO.class));
        }

        @Test
        @DisplayName("Username duplicado → 400 Bad Request y ErrorResponseDTO")
        void updateUser_UsernameDuplicado_ReturnsBadRequest() throws Exception {
            UUID uuid = UUID.randomUUID();
            UpdateUserDTO dto = new UpdateUserDTO();
            dto.setEmail("orig@ejemplo.com");
            dto.setUsername("otroUser");

            when(userService.updateUser(eq(uuid), any(UpdateUserDTO.class)))
                    .thenThrow(new IllegalStateException("Ya existe un usuario con este nombre de usuario."));

            mockMvc.perform(put("/api/v1/users/uuid/{uuid}", uuid)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.message").value("Error al actualizar el usuario"))
                    .andExpect(jsonPath("$.code").value("ERROR_UPDATING_USER"));

            verify(userService, times(1)).updateUser(eq(uuid), any(UpdateUserDTO.class));
        }

        @Test
        @DisplayName("Error genérico del servicio → 400 Bad Request y ErrorResponseDTO")
        void updateUser_ServiceException_ReturnsBadRequest() throws Exception {
            UUID uuid = UUID.randomUUID();
            UpdateUserDTO dto = new UpdateUserDTO();
            dto.setEmail("email@ejemplo.com");
            dto.setUsername("usuarioX");

            when(userService.updateUser(eq(uuid), any(UpdateUserDTO.class)))
                    .thenThrow(new RuntimeException("Error inesperado"));

            mockMvc.perform(put("/api/v1/users/uuid/{uuid}", uuid)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.message").value("Error al actualizar el usuario"))
                    .andExpect(jsonPath("$.code").value("ERROR_UPDATING_USER"));

            verify(userService, times(1)).updateUser(eq(uuid), any(UpdateUserDTO.class));
        }
    }

    // ----------------------------------------------
    // 6) DELETE /api/v1/users/uuid/{uuid}
    // ----------------------------------------------
    @Nested
    @DisplayName("deleteUser(...)")
    class DeleteUserTests {

        @Test
        @DisplayName("Usuario existe → 204 No Content")
        void deleteUser_Exists_ReturnsNoContent() throws Exception {
            UUID uuid = UUID.randomUUID();
            doNothing().when(userService).deleteUser(uuid);

            mockMvc.perform(delete("/api/v1/users/uuid/{uuid}", uuid))
                    .andExpect(status().isNoContent());

            verify(userService, times(1)).deleteUser(uuid);
        }

        @Test
        @DisplayName("Usuario no existe → 400 Bad Request")
        void deleteUser_NotFound_ReturnsBadRequest() throws Exception {
            UUID uuid = UUID.randomUUID();
            doThrow(new IllegalStateException("No existe el usuario con el id: " + uuid))
                    .when(userService).deleteUser(uuid);

            mockMvc.perform(delete("/api/v1/users/uuid/{uuid}", uuid))
                    .andExpect(status().isBadRequest());

            verify(userService, times(1)).deleteUser(uuid);
        }
    }

    // ----------------------------------------------
    // 7) POST /api/v1/users/upload-photo
    // ----------------------------------------------
//    @Nested
//    @DisplayName("uploadPhoto(...)")
//    class UploadPhotoTests {
//
//        @Test
//        @DisplayName("Usuario existe y archivo válido → 200 OK y URL en cuerpo")
//        void uploadPhoto_ValidUser_ReturnsOkUrl() throws Exception {
//            UUID uuid = UUID.randomUUID();
//            String uuidStr = uuid.toString();
//
//            MockMultipartFile file = new MockMultipartFile(
//                    "file",
//                    "imagen.png",
//                    "image/png",
//                    "contenido".getBytes()
//            );
//
//            String urlEsperada = "http://tuservidor.com/upload-photo/archivo.png";
//            when(userService.saveProfileImage(eq(uuidStr), any())).thenReturn(urlEsperada);
//
//            mockMvc.perform(multipart("/api/v1/users/upload-photo")
//                            .file(file)
//                            .param("uuid", uuidStr))
//                    .andExpect(status().isOk())
//                    .andExpect(content().contentType(MediaType.TEXT_PLAIN_VALUE + ";charset=UTF-8"))
//                    .andExpect(content().string(urlEsperada));
//
//            ArgumentCaptor<String> uuidCaptor = ArgumentCaptor.forClass(String.class);
//            verify(userService, times(1)).saveProfileImage(uuidCaptor.capture(), any());
//            assert uuidCaptor.getValue().equals(uuidStr);
//        }
//
//        @Test
//        @DisplayName("Usuario no existe → 500 Internal Server Error")
//        void uploadPhoto_UserNotFound_ReturnsServerError() throws Exception {
//            UUID uuid = UUID.randomUUID();
//            String uuidStr = uuid.toString();
//
//            MockMultipartFile file = new MockMultipartFile(
//                    "file",
//                    "imagen.png",
//                    "image/png",
//                    "contenido".getBytes()
//            );
//
//            when(userService.saveProfileImage(eq(uuidStr), any()))
//                    .thenThrow(new RuntimeException("Usuario no encontrado"));
//
//            mockMvc.perform(multipart("/api/v1/users/upload-photo")
//                            .file(file)
//                            .param("uuid", uuidStr))
//                    .andExpect(status().isInternalServerError());
//
//            verify(userService, times(1)).saveProfileImage(eq(uuidStr), any());
//        }
//    }

    /**
     * Un pequeño ControllerAdvice para que los errores de validación (@Valid) devuelvan 400 Bad Request con JSON.
     */
    @ControllerAdvice
    static class MethodArgumentNotValidExceptionHandler {
        @ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class)
        public ResponseEntity<Map<String, String>> handleValidationErrors(
                org.springframework.web.bind.MethodArgumentNotValidException ex) {
            Map<String, String> errors = new HashMap<>();
            ex.getBindingResult().getFieldErrors().forEach(error ->
                    errors.put(error.getField(), error.getDefaultMessage()));
            return ResponseEntity.badRequest().body(errors);
        }
    }
}
