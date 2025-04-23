package com.example.demo1.controllers;

import com.example.demo1.models.enums.EstadoSolicitud;
import com.example.demo1.models.entidades.SolicitudVerificacion;
import com.example.demo1.models.entidades.UserModel;
import com.example.demo1.repositories.ISolucitudVerifRepository;
import com.example.demo1.repositories.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<?> crearSolicitudVerificacion(@PathVariable Long idUser, @RequestBody SolicitudVerificacion nuevaSolicitud){
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
        nuevaSolicitud.setUser(userModel);
        return new ResponseEntity<>(solicitudVerifRepository.save(nuevaSolicitud), HttpStatus.OK);
    }

    @GetMapping("/userModel/{idUser}")
    public ResponseEntity<List<SolicitudVerificacion>> getSolicitudVerificacion(@PathVariable Long IdUser){
        return userRepository.findById(IdUser).map(userModel -> {
            List<SolicitudVerificacion> solicitudes = solicitudVerifRepository.findByUsername(userModel.getUsername());  //
            return new ResponseEntity<>(solicitudes, HttpStatus.OK);
        }).orElse(ResponseEntity.notFound().build());
    }


    @GetMapping
    public List<SolicitudVerificacion> getSolicitudesPorEstado(@RequestParam(required = false) EstadoSolicitud estado){
        if (estado != null) {
            return solicitudVerifRepository.findByEstado(estado);
        }

        return solicitudVerifRepository.findAll();
    }


    @PutMapping("/{idSolicitud}/aprobar")
    public ResponseEntity<?> aprobarSolicitudVerificacion(@PathVariable Long idSolicitud){
        return solicitudVerifRepository.findById(idSolicitud).map(solicitudVerificacion -> {
            solicitudVerificacion.setEstadoSolicitud(EstadoSolicitud.ACEPTADO);
            solicitudVerificacion.getUser().setVerified(true);
            solicitudVerifRepository.save(solicitudVerificacion);
            userRepository.save(solicitudVerificacion.getUser());
            return ResponseEntity.ok().build();
        }).orElse(ResponseEntity.notFound().build());

    }

    @PutMapping("/{idSolicitud}/rechazar")
    public ResponseEntity<?> rechazarSolicitud(@PathVariable Long idSolicitud, @RequestBody(required = false) String observacionAdmin){
        return solicitudVerifRepository.findById(idSolicitud).map(solicitudVerificacion -> {
            solicitudVerificacion.setEstadoSolicitud(EstadoSolicitud.RECHAZADO);
            if(observacionAdmin != null) {
                solicitudVerificacion.setObservacionesAdmin(observacionAdmin);
            }
            solicitudVerifRepository.save(solicitudVerificacion);
            return ResponseEntity.ok().build();
        }).orElse(ResponseEntity.notFound().build());
}



}
