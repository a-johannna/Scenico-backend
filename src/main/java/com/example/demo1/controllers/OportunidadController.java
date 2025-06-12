/**
 * OportunidadController.java
 * Proyecto: Scénico - Plataforma para artistas emergentes
 * Descripción: Controlador encargado de gestionar las operaciones relacionadas con la entidad Oportunidad.
 * Incluye funcionalidades para la creación, edición, eliminación y consulta de oportunidades
 * por parte de usuarios con rol de empresa.
 * Autor: Andrea Johanna Villavicencio Lema
 * Fecha: Mayo de 2025
 * Email: johannna.villavicencio@gmail.com
 */
package com.example.demo1.controllers;

import com.example.demo1.models.dtos.ErrorResponseDTO;
import com.example.demo1.models.dtos.Oportunidad.CrearOportunidadDTO;
import com.example.demo1.models.dtos.Oportunidad.OportunidadPublicDTO;
import com.example.demo1.models.dtos.Oportunidad.OportunidadResponseDTO;
import com.example.demo1.models.entidades.Oportunidad;
import com.example.demo1.models.entidades.UserModel;
import com.example.demo1.models.enums.EstadoOportunidad;
import com.example.demo1.models.enums.RoleName;
import com.example.demo1.repositories.IOportunidadRepository;
import com.example.demo1.repositories.IUserRepository;
import com.example.demo1.mappers.OportunidadMapper;
import com.example.demo1.services.JwtTokenService;
import com.example.demo1.services.OportunidadService;
import com.example.demo1.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.method.MethodValidationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/users/empresas/oportunidades")
public class OportunidadController {
    @Autowired
    private IOportunidadRepository oportunidadRepository;
    @Autowired
    private IUserRepository userRepository;

    private final JwtTokenService jwtTokenService;

    private final OportunidadService oportunidadService;

    private final UserService userService;

    public OportunidadController(JwtTokenService jwtTokenService, OportunidadService oportunidadService, UserService userService) {
        this.jwtTokenService = jwtTokenService;
        this.oportunidadService = oportunidadService;
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<?> crearOportunidad(@Valid @RequestBody CrearOportunidadDTO dto) {
        String token = jwtTokenService.resolveToken();
        if (token == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponseDTO("Token no proporcionado", "UNAUTHORIZED"));
        }
        UUID uuid = jwtTokenService.getUuidFromToken(token);

        return userRepository.findByUuid(uuid)
                .map(empresaUser -> {
                    if (empresaUser.getTypeUser() != RoleName.ENTERPRISE) {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                .body(new ErrorResponseDTO("Solo acceso para Empresas", "FORBIDDEN"));
                    }
                    Oportunidad nuevaOportunidad = OportunidadMapper.toEntity(dto);
                    nuevaOportunidad.setUsuarioEmpresa(empresaUser);
                    nuevaOportunidad.setFechaCierre(LocalDateTime.now().plusDays(30));

                    Oportunidad savedOportunidad = oportunidadRepository.save(nuevaOportunidad);
                    return ResponseEntity
                            .status(HttpStatus.CREATED)
                            .body(OportunidadMapper.toResponseDTO(savedOportunidad));
                })
                .orElse(ResponseEntity.notFound().build());

    }

    @GetMapping("/api/oportunidades/{id}")
    public ResponseEntity<OportunidadResponseDTO> obtenerOportunidadById(@PathVariable Long id) {
        return oportunidadRepository.findById(id)
                .map(oportunidad -> ResponseEntity.ok(OportunidadMapper.toResponseDTO(oportunidad)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/todas/publicas")
    public ResponseEntity<List<OportunidadResponseDTO>> findAll() {
        List<OportunidadResponseDTO> oportunidades = oportunidadRepository.findAll().stream()
                .map(OportunidadMapper::toResponseDTO)
                .toList();

        return ResponseEntity.ok(oportunidades);
    }

    @GetMapping("/categoria/{categoria}")

    public ResponseEntity<List<OportunidadResponseDTO>> findByCategoriaIgnoreCase(@PathVariable String categoria) {
        List<OportunidadResponseDTO> oportunidades = oportunidadRepository
                .findByCategoriaIgnoreCase(categoria).stream()
                .map(OportunidadMapper::toResponseDTO)
                .toList();
        return ResponseEntity.ok(oportunidades);
    }

    @GetMapping("/estadoOportunidad/{estadoOportunidad}")
    /* public List<Oportunidad> findByEstado(@PathVariable EstadoOportunidad estadoOportunidad){return oportunidadRepository.findByEstadoOportunidad(estadoOportunidad); }*/
    public ResponseEntity<List<OportunidadResponseDTO>> findByEstado(@PathVariable EstadoOportunidad estadoOportunidad) {
        List<OportunidadResponseDTO> oportunidades = oportunidadRepository
                .findByEstado(estadoOportunidad).stream()
                .map(OportunidadMapper::toResponseDTO)
                .toList();
        return ResponseEntity.ok(oportunidades);
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<OportunidadResponseDTO>> findByCategoriaAndEstado(
            @RequestParam String categoria, @RequestParam EstadoOportunidad estadoOportunidad
    ) {
        List<OportunidadResponseDTO> oportunidades = oportunidadRepository
                .findByCategoriaIgnoreCaseAndEstado(categoria, estadoOportunidad).stream()
                .map(OportunidadMapper::toResponseDTO)
                .toList();
        return ResponseEntity.ok(oportunidades);
    }


    @GetMapping("/publicas")
    public ResponseEntity<List<OportunidadPublicDTO>> listarOportunidadesPublico() {
        List<OportunidadPublicDTO> oportunidades = oportunidadRepository.findAll().stream()
                .map(OportunidadMapper::toOportunidadPublicDTO)
                .toList();
        return ResponseEntity.ok(oportunidades);
    }

    @PutMapping("idOportunidad/{id}")
    public ResponseEntity<?> actualizarOportunidad(
            @PathVariable Long id,
            @RequestBody @Valid CrearOportunidadDTO dto) {

        // 1. Extraer el token (por ejemplo del header "Authorization")
        String token = jwtTokenService.resolveToken();
        if (token == null) {
            // No viene token o es inválido
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponseDTO("Token no proporcionado o inválido", "UNAUTHORIZED"));
        }

        // 2. Obtener el UUID del usuario logueado
        UUID uuidUsuario;
        try {
            uuidUsuario = jwtTokenService.getUuidFromToken(token);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponseDTO("Token inválido o expirado", "UNAUTHORIZED"));
        }

        // 3. Buscar el UserModel correspondiente a ese UUID
        UserModel currentUser = userService.getByUuid(uuidUsuario);
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponseDTO("Usuario no encontrado para el token proporcionado", "UNAUTHORIZED"));
        }

        // 4. Obtener la oportunidad a editar
        Optional<Oportunidad> maybeOport = oportunidadService.findById(id);
        if (maybeOport.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        Oportunidad oportunidad = maybeOport.get();

        // 5. Comprobar que el usuario es de tipo ENTERPRISE y que coincide con quien creó la oportunidad
//        //    (se podría omitir la comprobación de ENTERPRISE si basta con validar que el UUID coincide)
//        if (currentUser.getTypeUser() != RoleName.ENTERPRISE ||
//                !oportunidad.getUsuarioEmpresa().getUuid().equals(currentUser.getUuid())) {
//            return ResponseEntity. status(HttpStatus.FORBIDDEN)
//                    .body(new ErrorResponseDTO("No autorizado para actualizar esta oportunidad", "FORBIDDEN"));
//        }

        // 6. Llamamos al servicio para que haga el mapeo y guarde los cambios
        //    El servicio puede encargarse de copiar solo los campos editables
        Oportunidad updated = oportunidadService.actualizarDesdeDTO(oportunidad, dto);

        // 7. Convertir la entidad actualizada a un DTO de respuesta
        OportunidadResponseDTO respuesta = OportunidadMapper.toResponseDTO(updated);

        return ResponseEntity.ok(respuesta);
    }



    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarOportunidad(@PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        UserModel currentUser = userRepository.findByEmail(email).orElse(null);

        return oportunidadRepository.findById(id)
                .map(oportunidad -> {
                    if (currentUser == null || currentUser.getTypeUser() != RoleName.ENTERPRISE ||
                            !oportunidad.getUsuarioEmpresa().getId_user().equals(currentUser.getId_user())) {
                        return ResponseEntity.status(403)
                                .body(new ErrorResponseDTO("No autorizado para eliminar esta oportunidad", "FORBIDDEN"));
                    }
                    oportunidadRepository.delete(oportunidad);
                    return ResponseEntity.ok().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
    @ExceptionHandler(MethodValidationException.class)
    public ResponseEntity<ErrorResponseDTO> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        String message = ex. getBindingResult().getAllErrors().get(0).getDefaultMessage();
        return  ResponseEntity.badRequest()
                .body(new ErrorResponseDTO(message, "VALIDATION_ERROR"));
    }
    @GetMapping("/uuid/{uuid}")
    public ResponseEntity<List<OportunidadResponseDTO>> getOportunidadesByEmpresa(@PathVariable UUID uuid) {
        var empresaOptional = userRepository.findByUuid(uuid);

        if (empresaOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        UserModel usuarioEmpresa = empresaOptional.get();

        if (usuarioEmpresa.getTypeUser() != RoleName.ENTERPRISE) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(List.of());
        }

        List<OportunidadResponseDTO> oportunidades = oportunidadRepository
                .findByUsuarioEmpresaUuid(uuid).stream()
                .map(OportunidadMapper::toResponseDTO)
                .toList();

        return ResponseEntity.ok(oportunidades);
    }



}