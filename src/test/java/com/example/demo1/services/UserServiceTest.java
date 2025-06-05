package com.example.demo1.services;

import com.example.demo1.mappers.UserMapper;
import com.example.demo1.models.dtos.UserModel.CreateUserDTO;
import com.example.demo1.models.dtos.UserModel.UpdateUserDTO;
import com.example.demo1.models.dtos.UserModel.UserResponseDTO;
import com.example.demo1.models.entidades.UserModel;
import com.example.demo1.models.enums.RoleName;
import com.example.demo1.repositories.IUserRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private IUserRepository userRepository;

    @Mock
    private RoleService roleService;

    @InjectMocks
    private UserService userService;

    // Directorio temporal que usaremos para simular "upload-photo"
    @TempDir
    static Path tempDir;

    // Antes de cada prueba de saveProfileImage, redirigimos la carpeta "upload-photo" a nuestra carpeta temporal
    // Guardamos la ruta original para restaurar después.
    private static Path originalUploadPhotoDir;

    @BeforeAll
    static void beforeAll() throws IOException {
        // Hacemos que la carpeta "upload-photo" apunte al tempDir/upload-photo
        originalUploadPhotoDir = Paths.get("upload-photo");
        // Si existe alguna carpeta "upload-photo" en el proyecto real, la movemos temporalmente:
        if (Files.exists(originalUploadPhotoDir)) {
            Files.move(originalUploadPhotoDir, tempDir.resolve("old-upload-photo"), StandardCopyOption.REPLACE_EXISTING);
        }
        // Creamos la carpeta vacía que usará UserService (al invocar saveProfileImage)
        Files.createDirectories(tempDir.resolve("upload-photo"));
        // Hacemos que la ruta "upload-photo" sea en realidad tempDir/upload-photo
        System.setProperty("user.dir", tempDir.toAbsolutePath().toString());
    }

    @AfterAll
    static void afterAll() throws IOException {
        // Restauramos "upload-photo" (si existía antes)
        Path pruebaUpload = tempDir.resolve("upload-photo");
        if (Files.exists(pruebaUpload)) {
            // Lo borramos para limpiar
            deleteRecursively(pruebaUpload);
        }
        if (Files.exists(tempDir.resolve("old-upload-photo"))) {
            Files.move(tempDir.resolve("old-upload-photo"), originalUploadPhotoDir, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private static void deleteRecursively(Path path) throws IOException {
        if (Files.notExists(path)) return;
        if (Files.isDirectory(path)) {
            try (DirectoryStream<Path> ds = Files.newDirectoryStream(path)) {
                for (Path child : ds) {
                    deleteRecursively(child);
                }
            }
        }
        Files.delete(path);
    }

    // ---------------------------------------------------------
    // 1) Pruebas para createUser(...)
    // ---------------------------------------------------------
    @Nested
    @DisplayName("createUser(...)")
    class CreateUserTests {

        @Test
        @DisplayName("Cuando existe un usuario con el mismo email, lanza IllegalStateException")
        void createUser_EmailExistente_LanzaError() {
            CreateUserDTO dto = new CreateUserDTO();
            dto.setEmail("a@ejemplo.com");
            dto.setUsername("usuario1");

            when(userRepository.existsByEmail("a@ejemplo.com")).thenReturn(true);

            assertThatThrownBy(() -> userService.createUser(dto))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("Ya existe un usuario con este correo.");

            verify(userRepository, times(1)).existsByEmail("a@ejemplo.com");
            verify(userRepository, never()).existsByUsername(anyString());
            verify(userRepository, never()).save(any());
            verify(roleService, never()).asignarRolInicial(any());
        }

        @Test
        @DisplayName("Cuando existe un usuario con el mismo username, lanza IllegalStateException")
        void createUser_UsernameExistente_LanzaError() {
            CreateUserDTO dto = new CreateUserDTO();
            dto.setEmail("nuevo@ejemplo.com");
            dto.setUsername("userExistente");

            when(userRepository.existsByEmail("nuevo@ejemplo.com")).thenReturn(false);
            when(userRepository.existsByUsername("userExistente")).thenReturn(true);

            assertThatThrownBy(() -> userService.createUser(dto))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("Ya existe un usuario con este nombre de usuario.");

            verify(userRepository, times(1)).existsByEmail("nuevo@ejemplo.com");
            verify(userRepository, times(1)).existsByUsername("userExistente");
            verify(userRepository, never()).save(any());
            verify(roleService, never()).asignarRolInicial(any());
        }

        @Test
        @DisplayName("Creación satisfactoria de usuario: mapea, asigna rol y guarda")
        void createUser_DatosValidos_CreaUsuarioYRetornaDTO() {
            CreateUserDTO dto = new CreateUserDTO();
            dto.setEmail("ok@ejemplo.com");
            dto.setUsername("usuarioOK");
            // Antes de llamar, el método createUser impone dto.setTypeUser(RoleName.USER)
            // Así que podemos simplemente verificar que userMapper.toEntity recibe un CreateUserDTO cuyo typeUser sea USER.

            when(userRepository.existsByEmail("ok@ejemplo.com")).thenReturn(false);
            when(userRepository.existsByUsername("usuarioOK")).thenReturn(false);

            // Simulamos mapper → entidad
            UserModel entidadSinId = new UserModel();
            entidadSinId.setEmail("ok@ejemplo.com");
            entidadSinId.setUsername("usuarioOK");
            // uuid se asigna en createUser, no hace falta ponerlo aquí

            when(userMapper.toEntity(Mockito.argThat(arg -> {
                // Verificamos que el typeUser que se estableció en createUser sea RoleName.USER
                return arg.getTypeUser() == RoleName.USER
                        && "ok@ejemplo.com".equals(arg.getEmail())
                        && "usuarioOK".equals(arg.getUsername());
            }))).thenReturn(entidadSinId);

            // Cuando el servicio de roles asigne el rol inicial, no necesitamos que haga nada concreto
            doNothing().when(roleService).asignarRolInicial(entidadSinId);

            // Simulamos que el repositorio asigna el ID
            UserModel entidadGuardada = new UserModel();
            entidadGuardada.setId_user(42L);
            entidadGuardada.setEmail("ok@ejemplo.com");
            entidadGuardada.setUsername("usuarioOK");
            entidadGuardada.setUuid(entidadSinId.getUuid()); // mismo UUID
            entidadGuardada.setTypeUser(RoleName.USER);
            entidadGuardada.setCreatedAt(entidadSinId.getCreatedAt());
            entidadGuardada.setVerified(entidadSinId.isVerified());

            when(userRepository.save(entidadSinId)).thenReturn(entidadGuardada);

            // Simulamos mapper → DTO de respuesta
            UserResponseDTO respuestaDTO = new UserResponseDTO();
            respuestaDTO.setId_user(42L);
            respuestaDTO.setEmail("ok@ejemplo.com");
            respuestaDTO.setUsername("usuarioOK");
            when(userMapper.toResponseDTO(entidadGuardada)).thenReturn(respuestaDTO);

            // Ejecutamos
            UserResponseDTO resultado = userService.createUser(dto);

            // Verificaciones
            assertThat(resultado).isNotNull();
            assertThat(resultado.getId_user()).isEqualTo(42L);
            assertThat(resultado.getEmail()).isEqualTo("ok@ejemplo.com");
            assertThat(resultado.getUsername()).isEqualTo("usuarioOK");

            verify(userRepository, times(1)).existsByEmail("ok@ejemplo.com");
            verify(userRepository, times(1)).existsByUsername("usuarioOK");
            verify(userMapper, times(1)).toEntity(any(CreateUserDTO.class));
            verify(roleService, times(1)).asignarRolInicial(entidadSinId);
            verify(userRepository, times(1)).save(entidadSinId);
            verify(userMapper, times(1)).toResponseDTO(entidadGuardada);
        }
    }

    // ---------------------------------------------------------
    // 2) Pruebas para getByUuid(...) y findByUuid(...)
    // ---------------------------------------------------------
    @Nested
    @DisplayName("getByUuid(...) y findByUuid(...)")
    class GetAndFindByUuidTests {

        @Test
        @DisplayName("getByUuid: usuario no encontrado → IllegalStateException")
        void getByUuid_NoExiste_LanzaError() {
            UUID buscado = UUID.randomUUID();
            when(userRepository.findByUuid(buscado)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.getByUuid(buscado))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("Usuario no encontrado con UUID: " + buscado);

            verify(userRepository, times(1)).findByUuid(buscado);
        }

        @Test
        @DisplayName("getByUuid: usuario existe → retorna entidad")
        void getByUuid_Existe_RetornaEntidad() {
            UUID buscado = UUID.randomUUID();
            UserModel m = new UserModel();
            m.setUuid(buscado);
            m.setUsername("pepito");
            when(userRepository.findByUuid(buscado)).thenReturn(Optional.of(m));

            UserModel result = userService.getByUuid(buscado);

            assertThat(result).isEqualTo(m);
            verify(userRepository, times(1)).findByUuid(buscado);
        }

        @Test
        @DisplayName("findByUuid: usuario no encontrado → IllegalStateException")
        void findByUuid_NoExiste_LanzaError() {
            UUID buscado = UUID.randomUUID();
            when(userRepository.findByUuid(buscado)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.findByUuid(buscado))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("No existe el usuario con el UUID: " + buscado);

            verify(userRepository, times(1)).findByUuid(buscado);
        }

        @Test
        @DisplayName("findByUuid: usuario existe → mapea a DTO y retorna")
        void findByUuid_Existe_RetornaDTO() {
            UUID buscado = UUID.randomUUID();
            UserModel m = new UserModel();
            m.setUuid(buscado);
            m.setEmail("xyz@ejemplo.com");
            m.setUsername("pepito");
            when(userRepository.findByUuid(buscado)).thenReturn(Optional.of(m));

            UserResponseDTO dto = new UserResponseDTO();
            dto.setId_user(10L);
            dto.setEmail("xyz@ejemplo.com");
            dto.setUsername("pepito");
            when(userMapper.toResponseDTO(m)).thenReturn(dto);

            UserResponseDTO resultado = userService.findByUuid(buscado);

            assertThat(resultado).isEqualTo(dto);
            verify(userRepository, times(1)).findByUuid(buscado);
            verify(userMapper, times(1)).toResponseDTO(m);
        }
    }

    // ---------------------------------------------------------
    // 3) Pruebas para findByUsername(...)
    // ---------------------------------------------------------
    @Nested
    @DisplayName("findByUsername(...)")
    class FindByUsernameTests {

        @Test
        @DisplayName("Usuario no encontrado → IllegalStateException")
        void findByUsername_NoExiste_LanzaError() {
            when(userRepository.findByUsername("juan")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.findByUsername("juan"))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("No existe el usuario con el nombre de usuario: juan");

            verify(userRepository, times(1)).findByUsername("juan");
        }

        @Test
        @DisplayName("Usuario existe → mapea y retorna DTO")
        void findByUsername_Existe_RetornaDTO() {
            UserModel m = new UserModel();
            m.setId_user(7L);
            m.setUsername("juan");
            m.setEmail("juan@ejemplo.com");
            when(userRepository.findByUsername("juan")).thenReturn(Optional.of(m));

            UserResponseDTO dto = new UserResponseDTO();
            dto.setId_user(7L);
            dto.setUsername("juan");
            dto.setEmail("juan@ejemplo.com");
            when(userMapper.toResponseDTO(m)).thenReturn(dto);

            UserResponseDTO resultado = userService.findByUsername("juan");

            assertThat(resultado).isEqualTo(dto);
            verify(userRepository, times(1)).findByUsername("juan");
            verify(userMapper, times(1)).toResponseDTO(m);
        }
    }

    // ---------------------------------------------------------
    // 4) Pruebas para getAllUsers()
    // ---------------------------------------------------------
    @Nested
    @DisplayName("getAllUsers()")
    class GetAllUsersTests {

        @Test
        @DisplayName("Cuando la lista está vacía → retorna lista vacía")
        void getAllUsers_Vacia_RetornaListaVacia() {
            when(userRepository.findAll()).thenReturn(Collections.emptyList());

            List<UserResponseDTO> lista = userService.getAllUsers();
            assertThat(lista).isEmpty();

            verify(userRepository, times(1)).findAll();
        }

        @Test
        @DisplayName("Cuando hay varios usuarios → devuelve lista de DTOs")
        void getAllUsers_Varios_RetornaListaDTO() {
            UserModel u1 = new UserModel();
            u1.setId_user(1L);
            u1.setUsername("a");
            u1.setEmail("a@ejemplo.com");
            UserModel u2 = new UserModel();
            u2.setId_user(2L);
            u2.setUsername("b");
            u2.setEmail("b@ejemplo.com");

            when(userRepository.findAll()).thenReturn(Arrays.asList(u1, u2));

            UserResponseDTO dto1 = new UserResponseDTO();
            dto1.setId_user(1L);
            dto1.setUsername("a");
            dto1.setEmail("a@ejemplo.com");
            UserResponseDTO dto2 = new UserResponseDTO();
            dto2.setId_user(2L);
            dto2.setUsername("b");
            dto2.setEmail("b@ejemplo.com");

            when(userMapper.toResponseDTO(u1)).thenReturn(dto1);
            when(userMapper.toResponseDTO(u2)).thenReturn(dto2);

            List<UserResponseDTO> resultado = userService.getAllUsers();

            assertThat(resultado).hasSize(2)
                    .containsExactly(dto1, dto2);

            verify(userRepository, times(1)).findAll();
            verify(userMapper, times(1)).toResponseDTO(u1);
            verify(userMapper, times(1)).toResponseDTO(u2);
        }
    }

    // ---------------------------------------------------------
    // 5) Pruebas para updateUser(...)
    // ---------------------------------------------------------
    @Nested
    @DisplayName("updateUser(...)")
    class UpdateUserTests {

        @Test
        @DisplayName("Usuario no existe → IllegalStateException")
        void updateUser_NoExiste_LanzaError() {
            UUID buscado = UUID.randomUUID();
            UpdateUserDTO dto = new UpdateUserDTO();
            when(userRepository.findByUuid(buscado)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.updateUser(buscado, dto))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("No existe el usuario.");

            verify(userRepository, times(1)).findByUuid(buscado);
            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("El nuevo email pertenece a otro usuario → IllegalStateException")
        void updateUser_EmailDuplicado_LanzaError() {
            UUID buscado = UUID.randomUUID();
            UserModel original = new UserModel();
            original.setUuid(buscado);
            original.setEmail("orig@ejemplo.com");
            original.setUsername("origUser");

            UpdateUserDTO dto = new UpdateUserDTO();
            dto.setEmail("otro@ejemplo.com");
            dto.setUsername("origUser"); // no cambia el username

            when(userRepository.findByUuid(buscado)).thenReturn(Optional.of(original));
            when(userRepository.existsByEmail("otro@ejemplo.com")).thenReturn(true);

            assertThatThrownBy(() -> userService.updateUser(buscado, dto))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("El email ya está en uso");

            verify(userRepository, times(1)).findByUuid(buscado);
            verify(userRepository, times(1)).existsByEmail("otro@ejemplo.com");
            verify(userRepository, never()).existsByUsername(anyString());
            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("El nuevo username pertenece a otro usuario → IllegalStateException")
        void updateUser_UsernameDuplicado_LanzaError() {
            UUID buscado = UUID.randomUUID();
            UserModel original = new UserModel();
            original.setUuid(buscado);
            original.setEmail("orig@ejemplo.com");
            original.setUsername("origUser");

            UpdateUserDTO dto = new UpdateUserDTO();
            dto.setEmail("orig@ejemplo.com"); // el email no cambia
            dto.setUsername("otroUser");       // intentamos cambiar solo el username

            when(userRepository.findByUuid(buscado)).thenReturn(Optional.of(original));
            when(userRepository.existsByUsername("otroUser")).thenReturn(true);

            assertThatThrownBy(() -> userService.updateUser(buscado, dto))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("Ya existe un usuario con este nombre de usuario.");

            verify(userRepository, times(1)).findByUuid(buscado);
            verify(userRepository, times(1)).existsByUsername("otroUser");
            verify(userRepository, never()).existsByEmail(anyString());
            verify(userRepository, never()).save(any());
        }


        @Test
        @DisplayName("Actualización exitosa: mapea campos y guarda")
        void updateUser_DatosValidos_ActualizaYRetornaDTO() {
            UUID buscado = UUID.randomUUID();
            UserModel original = new UserModel();
            original.setUuid(buscado);
            original.setEmail("orig@ejemplo.com");
            original.setUsername("origUser");
            original.setFirstName("Juan");
            original.setLastName("Pérez");

            UpdateUserDTO dto = new UpdateUserDTO();
            dto.setEmail("nuevo@ejemplo.com");
            dto.setUsername("nuevoUser");
            dto.setFirstName("Carlos"); // por ejemplo, cambiamos nombre
            dto.setLastName("González"); // cambiamos apellido

            when(userRepository.findByUuid(buscado)).thenReturn(Optional.of(original));
            when(userRepository.existsByEmail("nuevo@ejemplo.com")).thenReturn(false);
            when(userRepository.existsByUsername("nuevoUser")).thenReturn(false);

            // El mapper debe copiar campos de dto en original
            doAnswer(invocation -> {
                // Simulamos que updateUserFromDTO copia email, username, firstName, lastName
                UpdateUserDTO passedDto = invocation.getArgument(0);
                UserModel passedUser = invocation.getArgument(1);
                passedUser.setEmail(passedDto.getEmail());
                passedUser.setUsername(passedDto.getUsername());
                passedUser.setFirstName(passedDto.getFirstName());
                passedUser.setLastName(passedDto.getLastName());
                return null;
            }).when(userMapper).updateUserFromDTO(eq(dto), eq(original));

            UserModel guardado = new UserModel();
            guardado.setUuid(buscado);
            guardado.setEmail("nuevo@ejemplo.com");
            guardado.setUsername("nuevoUser");
            guardado.setFirstName("Carlos");
            guardado.setLastName("González");
            when(userRepository.save(original)).thenReturn(guardado);

            UserResponseDTO respuestaDTO = new UserResponseDTO();
            respuestaDTO.setId_user(5L);
            respuestaDTO.setEmail("nuevo@ejemplo.com");
            respuestaDTO.setUsername("nuevoUser");
            respuestaDTO.setFirstName("Carlos");
            respuestaDTO.setLastName("González");
            when(userMapper.toResponseDTO(guardado)).thenReturn(respuestaDTO);

            UserResponseDTO resultado = userService.updateUser(buscado, dto);

            assertThat(resultado.getEmail()).isEqualTo("nuevo@ejemplo.com");
            assertThat(resultado.getUsername()).isEqualTo("nuevoUser");
            assertThat(resultado.getFirstName()).isEqualTo("Carlos");
            assertThat(resultado.getLastName()).isEqualTo("González");

            verify(userRepository, times(1)).findByUuid(buscado);
            verify(userRepository, times(1)).existsByEmail("nuevo@ejemplo.com");
            verify(userRepository, times(1)).existsByUsername("nuevoUser");
            verify(userMapper, times(1)).updateUserFromDTO(any(UpdateUserDTO.class), any(UserModel.class));
            verify(userRepository, times(1)).save(original);
            verify(userMapper, times(1)).toResponseDTO(guardado);
        }
    }

    // ---------------------------------------------------------
    // 6) Pruebas para deleteUser(...)
    // ---------------------------------------------------------
    @Nested
    @DisplayName("deleteUser(...)")
    class DeleteUserTests {

        @Test
        @DisplayName("Usuario no existe → IllegalStateException")
        void deleteUser_NoExiste_LanzaError() {
            UUID buscado = UUID.randomUUID();
            when(userRepository.findByUuid(buscado)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.deleteUser(buscado))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("No existe el usuario con el id: " + buscado);

            verify(userRepository, times(1)).findByUuid(buscado);
            verify(userRepository, never()).delete(any());
        }

        @Test
        @DisplayName("Elimina usuario existente sin devolver nada")
        void deleteUser_Existe_EliminaLlamandoRepositorio() {
            UUID buscado = UUID.randomUUID();
            UserModel m = new UserModel();
            m.setUuid(buscado);
            when(userRepository.findByUuid(buscado)).thenReturn(Optional.of(m));

            // No esperamos excepción
            userService.deleteUser(buscado);

            verify(userRepository, times(1)).findByUuid(buscado);
            verify(userRepository, times(1)).delete(m);
        }
    }

    // ---------------------------------------------------------
    // 7) Pruebas para verifyUser(...)
    // ---------------------------------------------------------
    @Nested
    @DisplayName("verifyUser(...)")
    class VerifyUserTests {

        @Test
        @DisplayName("Usuario no existe → IllegalStateException")
        void verifyUser_NoExiste_LanzaError() {
            UUID buscado = UUID.randomUUID();
            when(userRepository.findByUuid(buscado)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.verifyUser(buscado))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("No existe el usuario con el id: " + buscado);

            verify(userRepository, times(1)).findByUuid(buscado);
            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("Verifica usuario existente: setVerified y save")
        void verifyUser_Existe_MarcaVerificadoYRetornaDTO() {
            UUID buscado = UUID.randomUUID();
            UserModel m = new UserModel();
            m.setUuid(buscado);
            m.setVerified(false);
            m.setUpdateAt(LocalDateTime.now().minusDays(1));

            when(userRepository.findByUuid(buscado)).thenReturn(Optional.of(m));

            // Cuando guarde, devolvemos el mismo objeto con verified=true
            UserModel guardado = new UserModel();
            guardado.setUuid(buscado);
            guardado.setVerified(true);
            guardado.setUpdateAt(m.getUpdateAt()); // el campo updateAt se actualiza internamente antes del save
            when(userRepository.save(m)).thenReturn(guardado);

            UserResponseDTO dto = new UserResponseDTO();
            dto.setId_user(99L);
            dto.setVerified(true);
            dto.setEmail("test@ejemplo.com");
            when(userMapper.toResponseDTO(guardado)).thenReturn(dto);

            UserResponseDTO resultado = userService.verifyUser(buscado);

            assertThat(resultado.isVerified()).isTrue();
            verify(userRepository, times(1)).findByUuid(buscado);
            verify(userRepository, times(1)).save(m);
            verify(userMapper, times(1)).toResponseDTO(guardado);
        }
    }

    // ---------------------------------------------------------
    // 8) Pruebas para saveProfileImage(...)
    // ---------------------------------------------------------
//    @Nested
//    @DisplayName("saveProfileImage(...)")
//    class SaveProfileImageTests {
//
//        @Test
//        @DisplayName("Cuando el usuario no existe, lanza RuntimeException")
//        void saveProfileImage_NoExisteUsuario_LanzaError() {
//            String uuidStr = UUID.randomUUID().toString();
//            when(userRepository.findByUuid(UUID.fromString(uuidStr))).thenReturn(Optional.empty());
//
//            MockMultipartFile file = new MockMultipartFile(
//                    "file",
//                    "imagen.jpg",
//                    "image/jpeg",
//                    "contenidoImagen".getBytes()
//            );
//
//            assertThatThrownBy(() -> userService.saveProfileImage(uuidStr, file))
//                    .isInstanceOf(RuntimeException.class)
//                    .hasMessage("Usuario no encontrado");
//
//            verify(userRepository, times(1)).findByUuid(UUID.fromString(uuidStr));
//            verify(userRepository, never()).save(any());
//        }
//
//        @Test
//        @DisplayName("Guarda exitosamente la imagen y retorna la URL esperada")
//        void saveProfileImage_UsuarioExiste_GuardaYRetornaURL() throws IOException {
//            // 1) Preparamos usuario en repo
//            UUID uuidUsuario = UUID.randomUUID();
//            UserModel m = new UserModel();
//            m.setUuid(uuidUsuario);
//            m.setEmail("u@ejemplo.com");
//            m.setUsername("u");
//            // El campo photoProfile se llenará después
//            when(userRepository.findByUuid(uuidUsuario)).thenReturn(Optional.of(m));
//
//            // 2) Creamos un MockMultipartFile
//            byte[] contenido = "datos-de-prueba".getBytes();
//            MockMultipartFile file = new MockMultipartFile(
//                    "file",
//                    "foto.png",
//                    "image/png",
//                    contenido
//            );
//
//            // 3) Llamamos al método
//            String resultadoUrl = userService.saveProfileImage(uuidUsuario.toString(), file);
//
//            // 4) Verificaciones
//            // 4.1 El archivo debe haberse copiado en tempDir/upload-photo/
//            Path carpetaUpload = tempDir.resolve("upload-photo");
//            try (DirectoryStream<Path> ds = Files.newDirectoryStream(carpetaUpload)) {
//                // Debe haber exactamente un archivo cuyo nombre contenga el original "foto.png"
//                List<Path> encontrados = new ArrayList<>();
//                for (Path p : ds) {
//                    encontrados.add(p);
//                }
//                assertThat(encontrados).hasSize(1);
//                String nombreGuardado = encontrados.get(0).getFileName().toString();
//                assertThat(nombreGuardado)
//                        .endsWith("_foto.png");
//                // Verificamos que el contenido del archivo sea el mismo que enviamos
//                byte[] leido = Files.readAllBytes(carpetaUpload.resolve(nombreGuardado));
//                assertThat(leido).isEqualTo(contenido);
//
//                // 4.2 La URL devuelta debe tener la forma "http://tuservidor.com/upload-photo/<uuid>_foto.png"
//                assertThat(resultadoUrl).contains("http://tuservidor.com/upload-photo/");
//                assertThat(resultadoUrl).endsWith("/" + nombreGuardado);
//
//                // 4.3 El usuario debe haber sido guardado con el campo photoProfile actualizado
//                //    Dado que el servicio llama a userRepository.save(m), verificamos esa llamada:
//                ArgumentCaptor<UserModel> captor = ArgumentCaptor.forClass(UserModel.class);
//                verify(userRepository, times(1)).save(captor.capture());
//                UserModel guardadoUsuario = captor.getValue();
//                assertThat(guardadoUsuario.getPhotoProfile()).isEqualTo(resultadoUrl);
//            }
//        }
//    }
}
