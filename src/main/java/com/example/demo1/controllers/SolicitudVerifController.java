package com.example.demo1.controllers;

import com.example.demo1.mappers.SolicitudVerificacionMapper;
import com.example.demo1.models.dtos.ErrorResponseDTO;
import com.example.demo1.models.dtos.SolicitudVerificacion.SolicitudVerificacionDTO;
import com.example.demo1.models.dtos.SolicitudVerificacion.SolicitudVerificacionResponseDTO;
import com.example.demo1.models.enums.EstadoSolicitud;
import com.example.demo1.models.entidades.SolicitudVerificacion;
import com.example.demo1.models.entidades.UserModel;
import com.example.demo1.repositories.ISolucitudVerifRepository;
import com.example.demo1.repositories.IUserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/solicitudes-verificacion")
public class SolicitudVerifController {

    @Autowired
    private ISolucitudVerifRepository solicitudVerifRepository;

    @Autowired
    private IUserRepository userRepository;

    @PostMapping("/{idUser}")
    public ResponseEntity<?> crearSolicitudVerificacion(@PathVariable Long idUser,@Valid @RequestBody SolicitudVerificacionDTO nuevaSolicituddDTO){
            Optional<UserModel> user = userRepository.findById(idUser);
            if(user.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

        UserModel userModel = user.get();
        if(userModel.isVerified()) {
            return ResponseEntity.badRequest().body("El usuario ya has sido verificado.");
        }

        List<SolicitudVerificacion> pendientes = solicitudVerifRepository.findByUsernameAndEstadoSolicitud(userModel, EstadoSolicitud.PENDIENTE);
        if(!pendientes.isEmpty()) {
            return ResponseEntity.badRequest().body("Ya se ha realizado una solicitud.");
        }
        SolicitudVerificacion nuevaSolicitud = SolicitudVerificacionMapper.toEntity(nuevaSolicituddDTO);
        nuevaSolicitud.setUser(userModel);
        SolicitudVerificacion savedSolicitud = solicitudVerifRepository.save(nuevaSolicitud);
        return ResponseEntity.ok(SolicitudVerificacionMapper.toResponseDTO(savedSolicitud));
    }

    @GetMapping("/userModel/{idUser}")
    public ResponseEntity<List<SolicitudVerificacionResponseDTO>> getSolicitudVerificacion(@PathVariable Long idUser){
        return userRepository.findById(idUser).map(userModel -> {
            List<SolicitudVerificacion> solicitudes = solicitudVerifRepository.findByUsername(userModel.getUsername());  //
            List<SolicitudVerificacionResponseDTO> dtos = solicitudes.stream()
                    .map(SolicitudVerificacionMapper::toResponseDTO)
                    .toList();
            return ResponseEntity.ok(dtos);
        }).orElse(ResponseEntity.notFound().build());
    }


    @GetMapping
    public ResponseEntity<List<SolicitudVerificacionResponseDTO>> getSolicitudesPorEstado(@RequestParam(required = false) EstadoSolicitud estado){
        List<SolicitudVerificacion> solicitudes = estado != null ?
                solicitudVerifRepository.findByEstado(estado) :
                solicitudVerifRepository.findAll();

        List<SolicitudVerificacionResponseDTO> dtos = solicitudes.stream()
                .map(SolicitudVerificacionMapper::toResponseDTO)
                .toList();
        return ResponseEntity.ok(dtos);
    }


    @PutMapping("/{idSolicitud}/aprobar")
    public ResponseEntity<SolicitudVerificacionResponseDTO> aprobarSolicitudVerificacion(@PathVariable Long idSolicitud){
        return solicitudVerifRepository.findById(idSolicitud).map(solicitudVerificacion -> {
            solicitudVerificacion.setEstadoSolicitud(EstadoSolicitud.ACEPTADO);
            solicitudVerificacion.getUser().setVerified(true);
            SolicitudVerificacion savedSolicitud = solicitudVerifRepository.save(solicitudVerificacion);
            userRepository.save(solicitudVerificacion.getUser());
            return ResponseEntity.ok(SolicitudVerificacionMapper.toResponseDTO(savedSolicitud));
        }).orElse(ResponseEntity.notFound().build());

    }

    @PutMapping("/{idSolicitud}/rechazar")
    public ResponseEntity<SolicitudVerificacionResponseDTO> rechazarSolicitud(@PathVariable Long idSolicitud, @RequestBody(required = false) String observacionAdmin){
        return solicitudVerifRepository.findById(idSolicitud).map(solicitudVerificacion -> {
            solicitudVerificacion.setEstadoSolicitud(EstadoSolicitud.RECHAZADO);
            if(observacionAdmin != null) {
                solicitudVerificacion.setObservacionesAdmin(observacionAdmin);
            }
            SolicitudVerificacion savedSolicitud = solicitudVerifRepository.save(solicitudVerificacion);
            return ResponseEntity.ok(SolicitudVerificacionMapper.toResponseDTO(savedSolicitud));
        }).orElse(ResponseEntity.notFound().build());
}


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        return ResponseEntity.badRequest()
                .body(new ErrorResponseDTO(message, "VALIDATION_ERROR"));
    }


}
