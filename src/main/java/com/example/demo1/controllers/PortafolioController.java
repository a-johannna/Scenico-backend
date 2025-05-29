/**
 * PortafolioController.java
 * Proyecto: Scénico -Plataforma para artistas emergentes
 * Descripción: Controlador REST que gestiona las operaciones CRUD relacionadas con los portafolios artísticos
 * de los usuarios. Permite crear, actualizar, eliminar, filtrar y listar portafolios públicos.
 * Autor: Andrea Johanna Villavicencio Lema
 * Fecha: Mayo de 2025
 * Email: johannna.villavicencio@gmail.com
 */
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
@RequestMapping("api/v1/users/portafolios")
public class PortafolioController {

    @Autowired
    private IPortafolioRepository portafolioRepository;

    @Autowired
    private IUserRepository userRepository;

    private final PortafolioService portafolioService;

    private final JwtTokenService jwtTokenService;

    private final com.example.demo1.services.UserService userService;


    /**
     * Constructor que inyecta los servicios necesarios.
     * @param portafolioService
     * @param jwtTokenService
     * @param userService
     */
    public PortafolioController(PortafolioService portafolioService, JwtTokenService jwtTokenService, com.example.demo1.services.UserService userService) {
        this.portafolioService = portafolioService;

        this.jwtTokenService = jwtTokenService;
        this.userService = userService;
    }

    /**
     * Crea un nuevo portafolio asociado al usuario autenticado.
     * @param dto   datos del portafolio
     * @return      respuesta con DTO público del portafolio creado
     */
    @PostMapping
    public ResponseEntity<?> crearPortafolio(@RequestBody @Valid PortafolioRequestDTO dto) {

        String token = jwtTokenService.resolveToken();


        UUID uuid = jwtTokenService.getUuidFromToken(token);


        UserModel user = userService.getByUuid(uuid);


        PortafolioPubliDTO nuevo = portafolioService.crearPortafolio(dto, user);


        return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
    }

    /**
     *  Obtiene los portafolios de un usuario por ID interno.
     * @param idUser    Identificador del usuario
     * @return          lista de portafolios en formato público
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
     * Obtiene portafolios por UUID de usuario.
     * @param uuid      uuid UUID del usuario
     * @return          lista de portafolios públicos
     */
    @GetMapping("/user/uuid/{uuid}")
    public ResponseEntity<List<PortafolioPubliDTO>> obtenerPortafolioPorUuid(@PathVariable UUID uuid) {
        UserModel user = userService.getByUuid(uuid);
        List<Portafolio> portafolios = portafolioRepository.findByUserModel(user);
        List<PortafolioPubliDTO> dtos = portafolios.stream()
                .map(PortafolioMapper::toPubliDTO)
                .toList();
        return ResponseEntity.ok(dtos);
    }


    /**
     * Obtiene portafolios por nombre de usuario.
     * @param username      nombre de usuario
     * @return              lista de portafolios públicos
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
     * Actualiza un portafolio, validando que el usuario autenticado sea el propietario.
     * @param idPortafolio      identificador del portafolio a actualizar
     * @param dto               datos nuevos del portafolio
     * @return                  portafolio actualizado
     */

    @PutMapping("idPortafolio/{idPortafolio}")
    public ResponseEntity<?> actualizarPortafolio(
            @PathVariable Long idPortafolio,
            @RequestBody @Valid PortafolioRequestDTO dto) {


        String token = jwtTokenService.resolveToken();
        UUID uuid = jwtTokenService.getUuidFromToken(token);
        UserModel user = userService.getByUuid(uuid);


        PortafolioPubliDTO actualizado = portafolioService.actualizarPortafolio(idPortafolio, dto, user);

        return ResponseEntity.ok(actualizado);
    }

    /**
     * Elimina un portafolio solo si pertenece al usuario autenticado.
     * @param idPortafolio      identificador del portafolio a eliminar
     * @return                  respuesta con estado 200 o error de permiso
     */
    @DeleteMapping("/username/{idPortafolio}")
    public ResponseEntity<?> eliminarPortafolioAuth(@PathVariable Long idPortafolio) {

        String token = jwtTokenService.resolveToken();
        UUID currentUserUuid = jwtTokenService.getUuidFromToken(token);

        Optional<Portafolio> optionalPortafolio = portafolioRepository.findById(idPortafolio);

        if (optionalPortafolio.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Portafolio portafolio = optionalPortafolio.get();

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
     * Filtra portafolios por tipo de archivo y/o etiqueta.
     * @param tipoArchivo  tipo de archivo
     * @param etiqueta     etiqueta parcial
     * @return             lista de portafolios que coinciden con los filtros
     */
    @GetMapping("/buscar")
    public ResponseEntity<List<PortafolioPubliDTO>> buscarPorTipoArchivoAndEtiquetas(@RequestParam(required = false)TipoArchivo tipoArchivo, @RequestParam(required = false) String etiqueta) {

        List<Portafolio> portafolios;
        if (tipoArchivo != null && etiqueta != null) {
            portafolios = portafolioRepository.findByTipoArchivoAndEtiquetasContainingIgnoreCase(tipoArchivo, etiqueta);
        } else if (tipoArchivo != null) {
            portafolios = portafolioRepository.findByTipoArchivo(tipoArchivo);
        } else if (etiqueta != null) {
            portafolios = portafolioRepository.findByEtiquetaYTipoArchivo(tipoArchivo, etiqueta);
        } else {
            portafolios = portafolioRepository.findAll();
        }
        List<PortafolioPubliDTO> dtos = portafolios.stream()
                .map(PortafolioMapper::toPubliDTO)
                .toList();
        return ResponseEntity.ok(dtos);
    }



    /**
     * Obtiene todos los portafolios disponibles, sin filtros ni autenticación.
     * Funciona como "modo explorador".
     * @return      lista completa de portafolios públicos
     */
    @GetMapping(value = "/all")
    public ResponseEntity<List<PortafolioPubliDTO>> findAllPortafoliosPublicos(){
        List<Portafolio> listaPortafolios = portafolioRepository.findAll();
        List<PortafolioPubliDTO> portlista = listaPortafolios.stream()
                .map(PortafolioMapper::toPubliDTO)
                .toList();
                return ResponseEntity.ok(portlista);
    }

    /**
     *  Manejador de excepciones para errores de validación.
     * @param ex excepción capturada
     * @return DTO de error con el mensaje y tipo
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        return ResponseEntity.badRequest()
                .body(new ErrorResponseDTO(message, "VALIDATION_ERROR"));
    }
}
