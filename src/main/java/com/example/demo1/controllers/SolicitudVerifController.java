package com.example.demo1.controllers;

import com.example.demo1.mappers.SolicitudVerificacionMapper;
import com.example.demo1.models.dtos.ErrorResponseDTO;
import com.example.demo1.models.dtos.SolicitudVerificacion.SolicitudVerificacionRequestDTO;
import com.example.demo1.models.dtos.SolicitudVerificacion.SolicitudVerificacionResponseDTO;
import com.example.demo1.models.enums.EstadoSolicitud;
import com.example.demo1.models.entidades.SolicitudVerificacion;
import com.example.demo1.models.entidades.UserModel;
import com.example.demo1.models.enums.TypeUser;
import com.example.demo1.repositories.ISolucitudVerifRepository;
import com.example.demo1.repositories.IUserRepository;
import com.example.demo1.services.SolicitudVerificacionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/solicitudes-verificacion")
public class SolicitudVerifController {


    private final SolicitudVerificacionService solicitudVerificacionService;
    

    private ISolucitudVerifRepository solicitudVerifRepository;


    private IUserRepository userRepository;

    public SolicitudVerifController(SolicitudVerificacionService solicitudVerificacionService) {
        this.solicitudVerificacionService = solicitudVerificacionService;
    }

    @PostMapping("/{idUser}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> crearSolicitudVerificacion(@PathVariable Long idUser,@Valid @RequestBody SolicitudVerificacionRequestDTO nuevaSolicitudDTO){
            Optional<UserModel> user = userRepository.findById(idUser);
            if(user.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

        if (nuevaSolicitudDTO.getArchivoDemoUrl() != null &&
                !nuevaSolicitudDTO.getArchivoDemoUrl().matches("^(http|https)://.*$")) {
            return ResponseEntity.badRequest().body("URL de archivo demo inválida");
        }

        //Verificar si ya tiene asignado el rol solicitado
        UserModel userModel = user.get();
        if(userModel.getTypeUser() == nuevaSolicitudDTO.getRolSolicitado()) {
            return ResponseEntity.badRequest().body("Ya tienes asignado el rol " + nuevaSolicitudDTO.getRolSolicitado());

        }

        //Verificar si ya está en proceso una solicitud de verificación
        List<SolicitudVerificacion> pendientes = solicitudVerifRepository.findByUserAndEstadoSolicitud(userModel, EstadoSolicitud.PENDIENTE);
        if(!pendientes.isEmpty()) {
            return ResponseEntity.badRequest().body("Ya tiene una solicitud pendiente de resolución.");
        }
        SolicitudVerificacion nuevaSolicitud = SolicitudVerificacionMapper.toEntity(nuevaSolicitudDTO);
        nuevaSolicitud.setUser(userModel);
        nuevaSolicitud.setRolSolicitado(nuevaSolicitudDTO.getRolSolicitado());

        SolicitudVerificacion savedSolicitud = solicitudVerifRepository.save(nuevaSolicitud);
        return ResponseEntity.ok(SolicitudVerificacionMapper.toResponseDTO(savedSolicitud));
    }

    @GetMapping("/userModel/{idUser}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<SolicitudVerificacionResponseDTO>> getSolicitudVerificacion(@PathVariable Long idUser){
        return userRepository.findById(idUser).map(userModel -> {
            List<SolicitudVerificacion> solicitudes = solicitudVerifRepository.findByUser(userModel);  //
            List<SolicitudVerificacionResponseDTO> dtos = solicitudes.stream()
                    .map(SolicitudVerificacionMapper::toResponseDTO)
                    .toList();
            return ResponseEntity.ok(dtos);
        }).orElse(ResponseEntity.notFound().build());
    }


    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<SolicitudVerificacionResponseDTO>> getSolicitudesPorEstado(@RequestParam(required = false) EstadoSolicitud estado){
        List<SolicitudVerificacion> solicitudes = estado != null ?
                solicitudVerifRepository.findByEstadoSolicitud(estado) :
                solicitudVerifRepository.findAll();

        List<SolicitudVerificacionResponseDTO> dtos = solicitudes.stream()
                .map(SolicitudVerificacionMapper::toResponseDTO)
                .toList();
        return ResponseEntity.ok(dtos);
    }


    @PutMapping("/{idSolicitud}/aprobar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SolicitudVerificacionResponseDTO> aprobarSolicitudVerificacion(@PathVariable Long idSolicitud){
        try {
            SolicitudVerificacionResponseDTO response =
                    solicitudVerificacionService.aprobarSolicitud(idSolicitud);
            return ResponseEntity.ok(response);
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
        

    }

    @PutMapping("/{idSolicitud}/rechazar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SolicitudVerificacionResponseDTO> rechazarSolicitud(@PathVariable Long idSolicitud, @RequestBody(required = false) String observacionAdmin){
        try {
            SolicitudVerificacionResponseDTO response =
                    solicitudVerificacionService.rechazarSolicitud(idSolicitud, observacionAdmin);
            return ResponseEntity.ok(response);
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }

    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        return ResponseEntity.badRequest()
                .body(new ErrorResponseDTO(message, "VALIDATION_ERROR"));
    }


}
