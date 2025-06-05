package com.example.demo1.controllers;

import com.example.demo1.mappers.PortafolioMapper;
import com.example.demo1.models.dtos.ErrorResponseDTO;
import com.example.demo1.models.dtos.Portafolio.PortafolioPubliDTO;
import com.example.demo1.models.dtos.Portafolio.PortafolioRequestDTO;
import com.example.demo1.models.entidades.Portafolio;
import com.example.demo1.models.entidades.UserModel;
import com.example.demo1.models.enums.TipoArchivo;
import com.example.demo1.repositories.IPortafolioRepository;
import com.example.demo1.repositories.IUserRepository;
import com.example.demo1.services.JwtTokenService;
import com.example.demo1.services.PortafolioService;
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
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Pruebas unitarias para PortafolioController
 */
@ExtendWith(MockitoExtension.class)
class PortafolioControllerTest {

    @Mock
    private PortafolioService portafolioService;

    @Mock
    private JwtTokenService jwtTokenService;

    @Mock
    private UserService userService;

    @Mock
    private IPortafolioRepository portafolioRepository;

    @Mock
    private IUserRepository userRepository;

    @InjectMocks
    private PortafolioController portafolioController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        // Asignamos los repositorios con ReflectionTestUtils, pues en el controlador son @Autowired
        ReflectionTestUtils.setField(portafolioController, "portafolioRepository", portafolioRepository);
        ReflectionTestUtils.setField(portafolioController, "userRepository", userRepository);

        // Configuramos el Validator para que funcione @Valid en PortafolioRequestDTO
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders.standaloneSetup(portafolioController)
                .setControllerAdvice(new ValidationExceptionHandler())
                .setValidator(validator)
                .setMessageConverters(new org.springframework.http.converter.json.MappingJackson2HttpMessageConverter())
                .build();
    }

    /**
     * Manejador que captura MethodArgumentNotValidException y devuelve ErrorResponseDTO.
     */
    @ControllerAdvice
    static class ValidationExceptionHandler {
        @ExceptionHandler(MethodArgumentNotValidException.class)
        public org.springframework.http.ResponseEntity<ErrorResponseDTO> handleValidationExceptions(MethodArgumentNotValidException ex) {
            String message = ex.getBindingResult().getAllErrors().get(0).getDefaultMessage();
            return org.springframework.http.ResponseEntity.badRequest()
                    .body(new ErrorResponseDTO(message, "VALIDATION_ERROR"));
        }
    }

    // ----------------------------------------------------
    // 1) POST /api/v1/users/portafolios  : crearPortafolio
    // ----------------------------------------------------
    @Nested
    @DisplayName("crearPortafolio(...)")
    class CrearPortafolioTests {
        @Test
        @DisplayName("DTO válido y usuario autenticado → 201 Created con DTO público")
        void crearPortafolio_Valid_ReturnsCreated() throws Exception {
            // 1) Construimos un PortafolioRequestDTO “de ejemplo” solo para armar el JSON:
            List<String> etiquetas = Arrays.asList("arte", "prueba");
            PortafolioRequestDTO dtoParaJson = new PortafolioRequestDTO(
                    "Título Test",
                    "Descripción Test",
                    TipoArchivo.IMAGE,
                    "http://archivo.png",
                    "http://imagen.png",
                    "imagen.png",
                    "Pie de imagen",
                    etiquetas
            );

            // 2) Simulamos token y extracción de UUID:
            String fakeToken = "Bearer x.y.z";
            UUID userUuid = UUID.randomUUID();
            when(jwtTokenService.resolveToken()).thenReturn(fakeToken);
            when(jwtTokenService.getUuidFromToken(fakeToken)).thenReturn(userUuid);

            // 3) Simulamos userService.getByUuid(...)
            UserModel user = new UserModel();
            user.setUuid(userUuid);
            user.setUsername("usuarioTest");
            when(userService.getByUuid(userUuid)).thenReturn(user);

            // 4) Preparamos el PortafolioPubliDTO de respuesta:
            PortafolioPubliDTO publiDTO = new PortafolioPubliDTO(
                    "Título Test",
                    42L,
                    "Descripción Test",
                    TipoArchivo.IMAGE,
                    "http://archivo.png",
                    "http://imagen.png",
                    "Pie de imagen",
                    etiquetas,
                    "usuarioTest"
            );

            // 5) IMPORTANTE: aquí usamos argument matchers para que coincida
            //    con “cualquier” PortafolioRequestDTO, no el dtoParaJson exacto.
            when(portafolioService.crearPortafolio(
                    any(PortafolioRequestDTO.class),
                    eq(user)
            ))
                    .thenReturn(publiDTO);

            // 6) Ahora ejecutamos la petición con MockMvc, convirtiendo dtoParaJson a JSON:
            mockMvc.perform(post("/api/v1/users/portafolios")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dtoParaJson)))
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.idPortafolio").value(42))
                    .andExpect(jsonPath("$.titulo").value("Título Test"))
                    .andExpect(jsonPath("$.nombreUsuario").value("usuarioTest"));

            // 7) Verificamos que se haya llamado a los métodos esperados
            verify(jwtTokenService, times(1)).resolveToken();
            verify(jwtTokenService, times(1)).getUuidFromToken(fakeToken);
            verify(userService, times(1)).getByUuid(userUuid);
            verify(portafolioService, times(1))
                    .crearPortafolio(any(PortafolioRequestDTO.class), eq(user));
        }


        @Test
        @DisplayName("DTO inválido (falta título) → 400 Bad Request con ErrorResponseDTO")
        void crearPortafolio_InvalidDTO_Returns400() throws Exception {
            // Construimos un DTO con título vacío ("") para activar @NotBlank
            PortafolioRequestDTO dto = new PortafolioRequestDTO(
                    "",                 // Inválido: @NotBlank
                    "Descripción Test",
                    TipoArchivo.IMAGE,
                    "http://archivo.png",
                    null,
                    null,
                    null,
                    Collections.emptyList()
            );

            mockMvc.perform(post("/api/v1/users/portafolios")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.message").value("Este campo es obligatorio"))
                    .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));

            verify(portafolioService, never()).crearPortafolio(any(), any());
        }
    }

    // ----------------------------------------------------
    // 2) GET /api/v1/users/portafolios/userModel/{idUser}
    // ----------------------------------------------------
    @Nested
    @DisplayName("obtenerPortafolioPorUsuario(...)")
    class ObtenerPorUsuarioTests {

        @Test
        @DisplayName("Usuario existe → 200 OK con lista de DTOs")
        void obtenerPortafolioPorUsuario_Exists_ReturnsOk() throws Exception {
            Long idUser = 7L;
            UserModel user = new UserModel();
            user.setId_user(idUser);
            user.setUuid(UUID.randomUUID());
            user.setUsername("juan");

            Portafolio p1 = new Portafolio();
            p1.setIdPortafolio(1L);
            p1.setUserModel(user);
            p1.setTitulo("A1");
            p1.setDescripcion("D1");
            p1.setTipoArchivo(TipoArchivo.VIDEO);
            p1.setUrlArchivo("http://v1.mp4");
            p1.setUrlImagen("http://img1.png");
            p1.setDescripcionImagen("img1");
            p1.setEtiquetas(Arrays.asList("x"));

            Portafolio p2 = new Portafolio();
            p2.setIdPortafolio(2L);
            p2.setUserModel(user);
            p2.setTitulo("A2");
            p2.setDescripcion("D2");
            p2.setTipoArchivo(TipoArchivo.AUDIO);
            p2.setUrlArchivo("http://a2.mp3");
            p2.setUrlImagen("http://img2.png");
            p2.setDescripcionImagen("img2");
            p2.setEtiquetas(List.of("y"));

            when(userRepository.findById(idUser)).thenReturn(Optional.of(user));
            when(portafolioRepository.findByUserModel(user)).thenReturn(Arrays.asList(p1, p2));

            // Interceptamos las llamadas estáticas a PortafolioMapper.toPubliDTO(...)
            try (MockedStatic<PortafolioMapper> mapperMock = Mockito.mockStatic(PortafolioMapper.class)) {
                PortafolioPubliDTO dto1 = new PortafolioPubliDTO(
                        "A1", 1L, "D1", TipoArchivo.VIDEO,
                        "http://v1.mp4", "http://img1.png", "img1",
                        Arrays.asList("x"), "juan"
                );
                PortafolioPubliDTO dto2 = new PortafolioPubliDTO(
                        "A2", 2L, "D2", TipoArchivo.AUDIO,
                        "http://a2.mp3", "http://img2.png", "img2",
                        Arrays.asList("y"), "juan"
                );
                mapperMock.when(() -> PortafolioMapper.toPubliDTO(p1)).thenReturn(dto1);
                mapperMock.when(() -> PortafolioMapper.toPubliDTO(p2)).thenReturn(dto2);

                mockMvc.perform(get("/api/v1/users/portafolios/userModel/{idUser}", idUser))
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.length()").value(2))
                        .andExpect(jsonPath("$[0].idPortafolio").value(1))
                        .andExpect(jsonPath("$[1].idPortafolio").value(2));

                verify(userRepository, times(1)).findById(idUser);
                verify(portafolioRepository, times(1)).findByUserModel(user);
                mapperMock.verify(() -> PortafolioMapper.toPubliDTO(p1), times(1));
                mapperMock.verify(() -> PortafolioMapper.toPubliDTO(p2), times(1));
            }
        }

        @Test
        @DisplayName("Usuario no existe → 404 Not Found")
        void obtenerPortafolioPorUsuario_NotFound_Returns404() throws Exception {
            Long idUser = 999L;
            when(userRepository.findById(idUser)).thenReturn(Optional.empty());

            mockMvc.perform(get("/api/v1/users/portafolios/userModel/{idUser}", idUser))
                    .andExpect(status().isNotFound());

            verify(userRepository, times(1)).findById(idUser);
            verify(portafolioRepository, never()).findByUserModel(any());
        }
    }

    // ----------------------------------------------------
    // 3) GET /api/v1/users/portafolios/user/uuid/{uuid}
    // ----------------------------------------------------
    @Nested
    @DisplayName("obtenerPortafolioPorUuid(...)")
    class ObtenerPorUuidTests {

        @Test
        @DisplayName("Usuario existe → 200 OK con lista de DTOs")
        void obtenerPortafolioPorUuid_Exists_ReturnsOk() throws Exception {
            UUID uuid = UUID.randomUUID();
            UserModel user = new UserModel();
            user.setUuid(uuid);
            user.setUsername("maria");

            Portafolio p = new Portafolio();
            p.setIdPortafolio(5L);
            p.setUserModel(user);
            p.setTitulo("Obra M");
            p.setDescripcion("Desc M");
            p.setTipoArchivo(TipoArchivo.DOCUMENT);
            p.setUrlArchivo("http://doc.pdf");
            p.setUrlImagen("http://imgM.png");
            p.setDescripcionImagen("pie M");
            p.setEtiquetas(Arrays.asList("doc"));

            when(userService.getByUuid(uuid)).thenReturn(user);
            when(portafolioRepository.findByUserModel(user)).thenReturn(Collections.singletonList(p));

            try (MockedStatic<PortafolioMapper> mapperMock = Mockito.mockStatic(PortafolioMapper.class)) {
                PortafolioPubliDTO dto = new PortafolioPubliDTO(
                        "Obra M", 5L, "Desc M", TipoArchivo.DOCUMENT,
                        "http://doc.pdf", "http://imgM.png", "pie M",
                        Arrays.asList("doc"), "maria"
                );
                mapperMock.when(() -> PortafolioMapper.toPubliDTO(p)).thenReturn(dto);

                mockMvc.perform(get("/api/v1/users/portafolios/user/uuid/{uuid}", uuid))
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.length()").value(1))
                        .andExpect(jsonPath("$[0].idPortafolio").value(5));

                verify(userService, times(1)).getByUuid(uuid);
                verify(portafolioRepository, times(1)).findByUserModel(user);
                mapperMock.verify(() -> PortafolioMapper.toPubliDTO(p), times(1));
            }
        }
    }

    // ----------------------------------------------------
    // 4) GET /api/v1/users/portafolios/user/username/{username}
    // ----------------------------------------------------
    @Nested
    @DisplayName("obtenerPortafolioPorUsername(...)")
    class ObtenerPorUsernameTests {

        @Test
        @DisplayName("Usuario existe → 200 OK con lista de DTOs")
        void obtenerPortafolioPorUsername_Exists_ReturnsOk() throws Exception {
            String username = "carlos";
            UserModel user = new UserModel();
            user.setUsername(username);
            user.setUuid(UUID.randomUUID());

            Portafolio p = new Portafolio();
            p.setIdPortafolio(10L);
            p.setUserModel(user);
            p.setTitulo("Obra C");
            p.setDescripcion("Desc C");
            p.setTipoArchivo(TipoArchivo.AUDIO);
            p.setUrlArchivo("http://audio.mp3");
            p.setUrlImagen("http://imgC.png");
            p.setDescripcionImagen("pie C");
            p.setEtiquetas(List.of("musica"));

            when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
            when(portafolioRepository.findByUserModel(user)).thenReturn(Collections.singletonList(p));

            try (MockedStatic<PortafolioMapper> mapperMock = Mockito.mockStatic(PortafolioMapper.class)) {
                PortafolioPubliDTO dto = new PortafolioPubliDTO(
                        "Obra C", 10L, "Desc C", TipoArchivo.AUDIO,
                        "http://audio.mp3", "http://imgC.png", "pie C",
                        Arrays.asList("musica"), "carlos"
                );
                mapperMock.when(() -> PortafolioMapper.toPubliDTO(p)).thenReturn(dto);

                mockMvc.perform(get("/api/v1/users/portafolios/user/username/{username}", username))
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.length()").value(1))
                        .andExpect(jsonPath("$[0].idPortafolio").value(10));

                verify(userRepository, times(1)).findByUsername(username);
                verify(portafolioRepository, times(1)).findByUserModel(user);
                mapperMock.verify(() -> PortafolioMapper.toPubliDTO(p), times(1));
            }
        }

        @Test
        @DisplayName("Usuario no existe → 404 Not Found")
        void obtenerPortafolioPorUsername_NotFound_Returns404() throws Exception {
            String username = "noexiste";
            when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

            mockMvc.perform(get("/api/v1/users/portafolios/user/username/{username}", username))
                    .andExpect(status().isNotFound());

            verify(userRepository, times(1)).findByUsername(username);
            verify(portafolioRepository, never()).findByUserModel(any());
        }
    }

    // ----------------------------------------------------
    // 5) PUT /api/v1/users/portafolios/idPortafolio/{idPortafolio}
    // ----------------------------------------------------
    @Nested
    @DisplayName("actualizarPortafolio(...)")
    class ActualizarPortafolioTests {

        @Test
        @DisplayName("DTO válido y usuario autenticado → 200 OK con DTO actualizado")
        void actualizarPortafolio_Valid_ReturnsOk() throws Exception {
            Long idPortafolio = 55L;
            List<String> etiquetas = List.of("u", "v");
            PortafolioRequestDTO dto = new PortafolioRequestDTO(
                    "Nuevo Título",
                    "Nueva Desc",
                    TipoArchivo.VIDEO,
                    "http://nuevo.mp4",
                    "http://nuevo.png",
                    "nuevo.png",
                    "Pie nuevo",
                    etiquetas
            );

            String fakeToken = "Bearer abc";
            UUID uuid = UUID.randomUUID();
            when(jwtTokenService.resolveToken()).thenReturn(fakeToken);
            when(jwtTokenService.getUuidFromToken(fakeToken)).thenReturn(uuid);

            UserModel user = new UserModel();
            user.setUuid(uuid);
            user.setUsername("pepe");
            when(userService.getByUuid(uuid)).thenReturn(user);

            // Preparamos el PortafolioPubliDTO resultado
            PortafolioPubliDTO updatedDTO = new PortafolioPubliDTO(
                    "Nuevo Título",
                    55L,
                    "Nueva Desc",
                    TipoArchivo.VIDEO,
                    "http://nuevo.mp4",
                    "http://nuevo.png",
                    "Pie nuevo",
                    etiquetas,
                    "pepe"
            );
            // Usamos argument matcher para el DTO, ya que Jackson crea otra instancia al deserializar
            when(portafolioService.actualizarPortafolio(
                    eq(idPortafolio),
                    any(PortafolioRequestDTO.class),
                    eq(user)
            )).thenReturn(updatedDTO);

            mockMvc.perform(put("/api/v1/users/portafolios/idPortafolio/{idPortafolio}", idPortafolio)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.idPortafolio").value(55))
                    .andExpect(jsonPath("$.titulo").value("Nuevo Título"))
                    .andExpect(jsonPath("$.nombreUsuario").value("pepe"));

            verify(jwtTokenService, times(1)).resolveToken();
            verify(jwtTokenService, times(1)).getUuidFromToken(fakeToken);
            verify(userService, times(1)).getByUuid(uuid);
            verify(portafolioService, times(1))
                    .actualizarPortafolio(eq(idPortafolio), any(PortafolioRequestDTO.class), eq(user));
        }



    @Test
        @DisplayName("DTO inválido (sin título) → 400 Bad Request con ErrorResponseDTO")
        void actualizarPortafolio_InvalidDTO_Returns400() throws Exception {
            // Sin título causa @NotBlank
            PortafolioRequestDTO dto = new PortafolioRequestDTO(
                    "",
                    "Desc",
                    TipoArchivo.AUDIO,
                    null,
                    null,
                    null,
                    null,
                    Collections.emptyList()
            );
            mockMvc.perform(put("/api/v1/users/portafolios/idPortafolio/{idPortafolio}", 5L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Este campo es obligatorio"))
                    .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));

            verify(portafolioService, never()).actualizarPortafolio(anyLong(), any(), any());
        }
    }

    // ----------------------------------------------------
    // 6) DELETE /api/v1/users/portafolios/username/{idPortafolio}  : eliminarPortafolioAuth
    // ----------------------------------------------------
    @Nested
    @DisplayName("eliminarPortafolioAuth(...)")
    class EliminarPortafolioAuthTests {

        @Test
        @DisplayName("Portafolio no existe → 404 Not Found")
        void eliminarPortafolioAuth_NotFound_Returns404() throws Exception {
            Long idPortafolio = 99L;
            String fakeToken = "Bearer t";
            UUID uuid = UUID.randomUUID();
            when(jwtTokenService.resolveToken()).thenReturn(fakeToken);
            when(jwtTokenService.getUuidFromToken(fakeToken)).thenReturn(uuid);

            when(portafolioRepository.findById(idPortafolio)).thenReturn(Optional.empty());

            mockMvc.perform(delete("/api/v1/users/portafolios/username/{idPortafolio}", idPortafolio))
                    .andExpect(status().isNotFound());

            verify(portafolioRepository, times(1)).findById(idPortafolio);
        }

        @Test
        @DisplayName("Usuario no propietario → 403 Forbidden")
        void eliminarPortafolioAuth_NotOwner_Returns403() throws Exception {
            Long idPortafolio = 11L;
            String fakeToken = "Bearer tk";
            UUID uuid = UUID.randomUUID();
            when(jwtTokenService.resolveToken()).thenReturn(fakeToken);
            when(jwtTokenService.getUuidFromToken(fakeToken)).thenReturn(uuid);

            UserModel propietario = new UserModel();
            propietario.setUuid(UUID.randomUUID()); // distinto del token
            Portafolio existente = new Portafolio();
            existente.setIdPortafolio(idPortafolio);
            existente.setUserModel(propietario);

            when(portafolioRepository.findById(idPortafolio)).thenReturn(Optional.of(existente));

            mockMvc.perform(delete("/api/v1/users/portafolios/username/{idPortafolio}", idPortafolio))
                    .andExpect(status().isForbidden())
                    .andExpect(content().string("\"No tienes permiso para eliminar este portafolio.\""));

            verify(portafolioRepository, times(1)).findById(idPortafolio);
            verify(portafolioRepository, never()).delete(any());
        }

        @Test
        @DisplayName("Usuario propietario → 200 OK y llamada a delete(...)")
        void eliminarPortafolioAuth_Owner_ReturnsOk() throws Exception {
            Long idPortafolio = 22L;
            String fakeToken = "Bearer tz";
            UUID uuid = UUID.randomUUID();
            when(jwtTokenService.resolveToken()).thenReturn(fakeToken);
            when(jwtTokenService.getUuidFromToken(fakeToken)).thenReturn(uuid);

            UserModel propietario = new UserModel();
            propietario.setUuid(uuid);
            Portafolio existente = new Portafolio();
            existente.setIdPortafolio(idPortafolio);
            existente.setUserModel(propietario);

            when(portafolioRepository.findById(idPortafolio)).thenReturn(Optional.of(existente));

            mockMvc.perform(delete("/api/v1/users/portafolios/username/{idPortafolio}", idPortafolio))
                    .andExpect(status().isOk());

            verify(portafolioRepository, times(1)).findById(idPortafolio);
            verify(portafolioRepository, times(1)).delete(existente);
        }
    }

    // ----------------------------------------------------
    // 7) DELETE /api/v1/users/portafolios/{idPortafolio}  : eliminarPortafolio
    // ----------------------------------------------------
    @Nested
    @DisplayName("eliminarPortafolio(...)")
    class EliminarPortafolioTests {

        @Test
        @DisplayName("Portafolio existe → 200 OK y deleteById(...)")
        void eliminarPortafolio_Exists_ReturnsOk() throws Exception {
            Long idPortafolio = 33L;
            when(portafolioRepository.findById(idPortafolio)).thenReturn(Optional.of(new Portafolio()));

            mockMvc.perform(delete("/api/v1/users/portafolios/{idPortafolio}", idPortafolio))
                    .andExpect(status().isOk());

            verify(portafolioRepository, times(1)).findById(idPortafolio);
            verify(portafolioRepository, times(1)).deleteById(idPortafolio);
        }

        @Test
        @DisplayName("Portafolio no existe → 404 Not Found")
        void eliminarPortafolio_NotFound_Returns404() throws Exception {
            Long idPortafolio = 44L;
            when(portafolioRepository.findById(idPortafolio)).thenReturn(Optional.empty());

            mockMvc.perform(delete("/api/v1/users/portafolios/{idPortafolio}", idPortafolio))
                    .andExpect(status().isNotFound());

            verify(portafolioRepository, times(1)).findById(idPortafolio);
            verify(portafolioRepository, never()).deleteById(anyLong());
        }
    }

    // ----------------------------------------------------
    // 8) GET /api/v1/users/portafolios/buscar  : buscarPorTipoArchivoAndEtiquetas
    // ----------------------------------------------------
    @Nested
    @DisplayName("buscarPorTipoArchivoAndEtiquetas(...)")
    class BuscarTests {

        @Test
        @DisplayName("Ambos filtros presentes → findByTipoArchivoAndEtiquetasContainingIgnoreCase(...)")
        void buscar_BothParams_ReturnsFiltered() throws Exception {
            TipoArchivo tipo = TipoArchivo.AUDIO;
            String etiqueta = "rock";

            Portafolio p = new Portafolio();
            p.setIdPortafolio(77L);
            p.setTipoArchivo(tipo);
            p.setDescripcion("D");
            p.setUrlArchivo("http://x.mp3");
            p.setUrlImagen("http://img.png");
            p.setDescripcionImagen("pie");
            p.setEtiquetas(Arrays.asList("rock", "jazz"));
            p.setTitulo("T");
            p.setUserModel(new UserModel(){{
                setUuid(UUID.randomUUID());
                setUsername("u");
            }});

            when(portafolioRepository.findByTipoArchivoAndEtiquetasContainingIgnoreCase(tipo, etiqueta))
                    .thenReturn(Collections.singletonList(p));

            try (MockedStatic<PortafolioMapper> mapperMock = Mockito.mockStatic(PortafolioMapper.class)) {
                PortafolioPubliDTO dto = new PortafolioPubliDTO(
                        "T", 77L, "D", tipo,
                        "http://x.mp3", "http://img.png", "pie",
                        Arrays.asList("rock", "jazz"), "u"
                );
                mapperMock.when(() -> PortafolioMapper.toPubliDTO(p)).thenReturn(dto);

                mockMvc.perform(get("/api/v1/users/portafolios/buscar")
                                .param("tipoArchivo", tipo.name())
                                .param("etiqueta", etiqueta))
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.length()").value(1))
                        .andExpect(jsonPath("$[0].idPortafolio").value(77));

                verify(portafolioRepository, times(1))
                        .findByTipoArchivoAndEtiquetasContainingIgnoreCase(tipo, etiqueta);
                mapperMock.verify(() -> PortafolioMapper.toPubliDTO(p), times(1));
            }
        }

        @Test
        @DisplayName("Solo tipoArchivo presente → findByTipoArchivo(...)")
        void buscar_OnlyTipo_ReturnsFiltered() throws Exception {
            TipoArchivo tipo = TipoArchivo.VIDEO;
            Portafolio p = new Portafolio();
            p.setIdPortafolio(88L);
            p.setTipoArchivo(tipo);
            p.setTitulo("V");
            p.setDescripcion("DX");
            p.setUrlArchivo("http://v.mp4");
            p.setUrlImagen("http://imgv.png");
            p.setDescripcionImagen("piev");
            p.setEtiquetas(List.of("cine"));
            p.setUserModel(new UserModel(){{
                setUuid(UUID.randomUUID());
                setUsername("v");
            }});

            when(portafolioRepository.findByTipoArchivo(tipo))
                    .thenReturn(Collections.singletonList(p));

            try (MockedStatic<PortafolioMapper> mapperMock = Mockito.mockStatic(PortafolioMapper.class)) {
                PortafolioPubliDTO dto = new PortafolioPubliDTO(
                        "V", 88L, "DX", tipo,
                        "http://v.mp4", "http://imgv.png", "piev",
                        Arrays.asList("cine"), "v"
                );
                mapperMock.when(() -> PortafolioMapper.toPubliDTO(p)).thenReturn(dto);

                mockMvc.perform(get("/api/v1/users/portafolios/buscar")
                                .param("tipoArchivo", tipo.name()))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.length()").value(1))
                        .andExpect(jsonPath("$[0].idPortafolio").value(88));

                verify(portafolioRepository, times(1)).findByTipoArchivo(tipo);
                mapperMock.verify(() -> PortafolioMapper.toPubliDTO(p), times(1));
            }
        }

        @Test
        @DisplayName("Solo etiqueta presente : findByEtiquetaYTipoArchivo(null, etiqueta)")
        void buscar_OnlyEtiqueta_ReturnsFiltered() throws Exception {
            String etiqueta = "rock";
            Portafolio p = new Portafolio();
            p.setIdPortafolio(99L);
            p.setTipoArchivo(null);
            p.setTitulo("E");
            p.setDescripcion("DE");
            p.setUrlArchivo("http://e.mp3");
            p.setUrlImagen("http://imge.png");
            p.setDescripcionImagen("piee");
            p.setEtiquetas(List.of("rock"));
            p.setUserModel(new UserModel(){{
                setUuid(UUID.randomUUID());
                setUsername("e");
            }});

            when(portafolioRepository.findByEtiquetaYTipoArchivo(null, etiqueta))
                    .thenReturn(Collections.singletonList(p));

            try (MockedStatic<PortafolioMapper> mapperMock = Mockito.mockStatic(PortafolioMapper.class)) {
                PortafolioPubliDTO dto = new PortafolioPubliDTO(
                        "E", 99L, "DE", null,
                        "http://e.mp3", "http://imge.png", "piee",
                        Arrays.asList("rock"), "e"
                );
                mapperMock.when(() -> PortafolioMapper.toPubliDTO(p)).thenReturn(dto);

                mockMvc.perform(get("/api/v1/users/portafolios/buscar")
                                .param("etiqueta", etiqueta))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.length()").value(1))
                        .andExpect(jsonPath("$[0].idPortafolio").value(99));

                verify(portafolioRepository, times(1)).findByEtiquetaYTipoArchivo(null, etiqueta);
                mapperMock.verify(() -> PortafolioMapper.toPubliDTO(p), times(1));
            }
        }

        @Test
        @DisplayName("Sin parámetros → findAll()")
        void buscar_NoParams_ReturnsAll() throws Exception {
            Portafolio p = new Portafolio();
            p.setIdPortafolio(111L);
            p.setTipoArchivo(TipoArchivo.DOCUMENT);
            p.setTitulo("ALL");
            p.setDescripcion("DA");
            p.setUrlArchivo("http://all.pdf");
            p.setUrlImagen("http://imgall.png");
            p.setDescripcionImagen("piall");
            p.setEtiquetas(List.of("all"));
            p.setUserModel(new UserModel(){{
                setUuid(UUID.randomUUID());
                setUsername("all");
            }});

            when(portafolioRepository.findAll()).thenReturn(Collections.singletonList(p));

            try (MockedStatic<PortafolioMapper> mapperMock = Mockito.mockStatic(PortafolioMapper.class)) {
                PortafolioPubliDTO dto = new PortafolioPubliDTO(
                        "ALL", 111L, "DA", TipoArchivo.DOCUMENT,
                        "http://all.pdf", "http://imgall.png", "piall",
                        List.of("all"), "all"
                );
                mapperMock.when(() -> PortafolioMapper.toPubliDTO(p)).thenReturn(dto);

                mockMvc.perform(get("/api/v1/users/portafolios/buscar"))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.length()").value(1))
                        .andExpect(jsonPath("$[0].idPortafolio").value(111));

                verify(portafolioRepository, times(1)).findAll();
                mapperMock.verify(() -> PortafolioMapper.toPubliDTO(p), times(1));
            }
        }
    }

    // ----------------------------------------------------
    // 9) GET /api/v1/users/portafolios/all  : findAllPortafoliosPublicos
    // ----------------------------------------------------
    @Nested
    @DisplayName("findAllPortafoliosPublicos(...)")
    class FindAllTests {

        @Test
        @DisplayName("Lista no vacía → 200 OK con todos los DTOs")
        void findAllPortafoliosPublicos_ReturnsOk() throws Exception {
            Portafolio p1 = new Portafolio();
            p1.setIdPortafolio(201L);
            p1.setTitulo("X1");
            p1.setDescripcion("D1");
            p1.setTipoArchivo(TipoArchivo.AUDIO);
            p1.setUrlArchivo("http://f1.mp3");
            p1.setUrlImagen("http://i1.png");
            p1.setDescripcionImagen("pi1");
            p1.setEtiquetas(List.of("a"));
            p1.setUserModel(new UserModel(){{
                setUuid(UUID.randomUUID());
                setUsername("u1");
            }});

            Portafolio p2 = new Portafolio();
            p2.setIdPortafolio(202L);
            p2.setTitulo("X2");
            p2.setDescripcion("D2");
            p2.setTipoArchivo(TipoArchivo.IMAGE);
            p2.setUrlArchivo("http://f2.png");
            p2.setUrlImagen("http://i2.png");
            p2.setDescripcionImagen("pi2");
            p2.setEtiquetas(Arrays.asList("b"));
            p2.setUserModel(new UserModel(){{
                setUuid(UUID.randomUUID());
                setUsername("u2");
            }});

            when(portafolioRepository.findAll()).thenReturn(Arrays.asList(p1, p2));

            try (MockedStatic<PortafolioMapper> mapperMock = Mockito.mockStatic(PortafolioMapper.class)) {
                PortafolioPubliDTO dto1 = new PortafolioPubliDTO(
                        "X1", 201L, "D1", TipoArchivo.AUDIO,
                        "http://f1.mp3", "http://i1.png", "pi1",
                        List.of("a"), "u1"
                );
                PortafolioPubliDTO dto2 = new PortafolioPubliDTO(
                        "X2", 202L, "D2", TipoArchivo.IMAGE,
                        "http://f2.png", "http://i2.png", "pi2",
                        List.of("b"), "u2"
                );
                mapperMock.when(() -> PortafolioMapper.toPubliDTO(p1)).thenReturn(dto1);
                mapperMock.when(() -> PortafolioMapper.toPubliDTO(p2)).thenReturn(dto2);

                mockMvc.perform(get("/api/v1/users/portafolios/all"))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.length()").value(2))
                        .andExpect(jsonPath("$[0].idPortafolio").value(201))
                        .andExpect(jsonPath("$[1].idPortafolio").value(202));

                verify(portafolioRepository, times(1)).findAll();
                mapperMock.verify(() -> PortafolioMapper.toPubliDTO(p1), times(1));
                mapperMock.verify(() -> PortafolioMapper.toPubliDTO(p2), times(1));
            }
        }

        @Test
        @DisplayName("Lista vacía → 200 OK y array vacío")
        void findAllPortafoliosPublicos_Empty_ReturnsOkEmpty() throws Exception {
            when(portafolioRepository.findAll()).thenReturn(Collections.emptyList());

            mockMvc.perform(get("/api/v1/users/portafolios/all"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(0));

            verify(portafolioRepository, times(1)).findAll();
        }
    }
}
