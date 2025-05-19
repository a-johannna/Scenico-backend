package com.example.demo1.controllers;

import com.example.demo1.mappers.PortafolioMapper;

import com.example.demo1.models.dtos.ErrorResponseDTO;
import com.example.demo1.models.dtos.Portafolio.PortafolioRequestDTO;
import com.example.demo1.models.entidades.Portafolio;
import com.example.demo1.models.dtos.Portafolio.PortafolioPubliDTO;
import com.example.demo1.models.entidades.UserModel;
import com.example.demo1.models.enums.TipoArchivo;
import com.example.demo1.repositories.IPortafolioRepository;
import com.example.demo1.repositories.IUserRepository;
import com.example.demo1.services.JwtTokenService;
import com.example.demo1.services.PortafolioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/portafolios")
public class PortafolioController {

    @Autowired
    private IPortafolioRepository portafolioRepository;

    @Autowired
    private IUserRepository userRepository;

    private final PortafolioService portafolioService;

    private final JwtTokenService jwtTokenService;

    private final com.example.demo1.services.UserService userService;



    public PortafolioController(PortafolioService portafolioService, JwtTokenService jwtTokenService, com.example.demo1.services.UserService userService) {
        this.portafolioService = portafolioService;

        this.jwtTokenService = jwtTokenService;
        this.userService = userService;
    }

//    @PostMapping("userModel/{idUser}")
//    public ResponseEntity<?> crearPortafolio(@PathVariable Long idUser, @RequestBody PortafolioRequestDTO requestDTO) {
//        return userRepository.findById(idUser).map(userModel -> {
//          Portafolio nuevoPortafolio = PortafolioMapper.toEntity(requestDTO);
//          nuevoPortafolio.setUserModel(userModel);
//
//            return ResponseEntity.ok(portafolioRepository.save(nuevoPortafolio));
//
//        }).orElse(ResponseEntity.notFound().build());
//    }

//    @PostMapping("userModel/{id_User}")
//    public ResponseEntity<?> crearPortafolio(@PathVariable Long id_User,
//                                             @Valid @RequestBody PortafolioRequestDTO requestDTO) {
//        return userRepository.findById(id_User).map(user -> {
//            try {
//                PortafolioPubliDTO creado = portafolioService.crearPortafolio(requestDTO, user);
//                return ResponseEntity.status(HttpStatus.CREATED).body(creado);
//            } catch (Exception e) {
//                return ResponseEntity.badRequest().body("Error al crear portafolio: " + e.getMessage());
//            }
//        }).orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado"));
//    }

    @PostMapping
    public ResponseEntity<?> crearPortafolio(@RequestBody @Valid PortafolioRequestDTO dto) {
        // Obtener el token desde el contexto de seguridad
        String token = jwtTokenService.resolveToken();  // Extrae el token del encabezado Authorization

        // Extraer el UUID del usuario desde el token
        UUID uuid = jwtTokenService.getUuidFromToken(token);

        // Obtener el usuario por UUID
        UserModel user = userService.getByUuid(uuid);

        // Crear el portafolio y obtener el DTO público directamente del servicio
        PortafolioPubliDTO nuevo = portafolioService.crearPortafolio(dto, user);

        // Devolver respuesta con DTO público
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
    }

    /**
     *
     * @param idUser
     * @return
     */

    @GetMapping("userModel/{idUser}")
    public ResponseEntity<List<PortafolioPubliDTO>> obtenerPortafolioPorUsuario(@PathVariable Long idUser) {
        return userRepository.findById(idUser).map(userModel -> {
            List<Portafolio> portafolios = portafolioRepository.findByUserModel(userModel);
            List<PortafolioPubliDTO> dtos = portafolios.stream()
                    .map(PortafolioMapper::toPubliDTO)
                    .toList();
            return ResponseEntity.ok(dtos);
        }).orElse(ResponseEntity.notFound().build());

    }

    /**
     *
     * @param username
     * @return
     */

    @GetMapping("user/username/{username}")
    public ResponseEntity<List<PortafolioPubliDTO>> obtenerPortafolioPorUsername(@PathVariable String username) {
        return userRepository.findByUsername(username).map(userModel -> {
            List<Portafolio> portafolios = portafolioRepository.findByUserModel(userModel);
            List<PortafolioPubliDTO> dtos = portafolios.stream()
                    .map(PortafolioMapper::toPubliDTO)
                    .toList();
            return ResponseEntity.ok(dtos);
        }).orElse(ResponseEntity.notFound().build());
    }

    /**
     *
     * @param idPortafolio
     * @param dto
     * @return
     */

    @PutMapping("/{idPortafolio}")
    public ResponseEntity<?> actualizarPortafolio(
            @PathVariable Long idPortafolio,
            @RequestBody @Valid PortafolioRequestDTO dto) {

        // 1. Obtener UUID del usuario autenticado
        String token = jwtTokenService.resolveToken();
        UUID uuid = jwtTokenService.getUuidFromToken(token);
        UserModel user = userService.getByUuid(uuid);

        // 2. Llamar al servicio para actualizar
        PortafolioPubliDTO actualizado = portafolioService.actualizarPortafolio(idPortafolio, dto, user);

        return ResponseEntity.ok(actualizado);
    }

    /**
     *
     * @param idPortafolio
     * @return
     */

    @DeleteMapping("/username/{idPortafolio}")
    public ResponseEntity<?> eliminarPortafolioAuth(@PathVariable Long idPortafolio) {
        // Obtener UUID del usuario autenticado desde el token
        String token = jwtTokenService.resolveToken();
        UUID currentUserUuid = jwtTokenService.getUuidFromToken(token);

        // Buscar el portafolio
        Optional<Portafolio> optionalPortafolio = portafolioRepository.findById(idPortafolio);

        if (optionalPortafolio.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Portafolio portafolio = optionalPortafolio.get();

        // Verificar que el portafolio pertenece al usuario autenticado
        if (!portafolio.getUserModel().getUuid().equals(currentUserUuid)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No tienes permiso para eliminar este portafolio.");
        }

        portafolioRepository.delete(portafolio);
        return ResponseEntity.ok().build();
    }

    /**
     *
     * @param idPortafolio
     * @return
     */
    @DeleteMapping("/{idPortafolio}")
    public ResponseEntity<?> eliminarPortafolio(@PathVariable Long idPortafolio) {
       if (portafolioRepository.findById(idPortafolio).isPresent()) {
           portafolioRepository.deleteById(idPortafolio);
           return ResponseEntity.ok().build();
       }else {
           return ResponseEntity.notFound().build();
       }

    }

    /**
     *
     * @param tipoArchivo
     * @param etiqueta
     * @return
     */

    @GetMapping("/buscar")
    public ResponseEntity<List<PortafolioPubliDTO>> buscarPorTipoArchivoAndEtiquetas(@RequestParam(required = false)TipoArchivo tipoArchivo, @RequestParam(required = false) String etiqueta) {

        List<Portafolio> portafolios;
        if (tipoArchivo != null && etiqueta != null) {
            portafolios = portafolioRepository.findByTipoArchivoAndEtiquetasContainingIgnoreCase(tipoArchivo, etiqueta);
        } else if (tipoArchivo != null) {
            portafolios = portafolioRepository.findByTipoArchivo(tipoArchivo);
        } else if (etiqueta != null) {
            portafolios = portafolioRepository.findByEtiquetasContainingIgnoreCase(etiqueta);
        } else {
            portafolios = portafolioRepository.findAll();
        }
        List<PortafolioPubliDTO> dtos = portafolios.stream()
                .map(PortafolioMapper::toPubliDTO)
                .toList();
        return ResponseEntity.ok(dtos);
    }



    /**
     *
     *  Método para obtener todas las portafolios publicas que estén en la base de datos, sin tener que pasar por el servicio.
     *  La idea es que funcione o modo de explorador donde se muestren todos los portafolios.
     * @return
     */
    @GetMapping
    public ResponseEntity<List<PortafolioPubliDTO>> obtenerOportunidadesPublico(){
        List<Portafolio> listaPortafolios = portafolioRepository.findAll();
        List<PortafolioPubliDTO> portlista = listaPortafolios.stream()
                .map(PortafolioMapper::toPubliDTO)
                .toList();
                return ResponseEntity.ok(portlista);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        return ResponseEntity.badRequest()
                .body(new ErrorResponseDTO(message, "VALIDATION_ERROR"));
    }
}
