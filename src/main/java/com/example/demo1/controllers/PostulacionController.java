package com.example.demo1.controllers;

import com.example.demo1.models.entidades.Oportunidad;
import com.example.demo1.models.entidades.Postulacion;
import com.example.demo1.models.entidades.UserModel;
import com.example.demo1.models.enums.EstadoPostulacion;
import com.example.demo1.models.enums.TypeUser;
import com.example.demo1.repositories.IOportunidadRepository;
import com.example.demo1.repositories.IPostulacionRepository;
import com.example.demo1.repositories.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.Optional;

@RestController
@RequestMapping("/api/postulaciones")
public class PostulacionController {
    @Autowired
    private IPostulacionRepository postulacionRepository;
    @Autowired
    private IUserRepository userRepository;
    @Autowired
    private IOportunidadRepository oportunidadRepository;

    @PostMapping("/{idArtista}/{idOportunidad}")
    public ResponseEntity<?> crearPostulacion(@PathVariable Long idArtista, @PathVariable Long idOportunidad,
    @RequestBody(required = false) String mensaje)
    {
        Optional<UserModel> artistaOptional = userRepository.findById(idArtista);
        Optional<Oportunidad> oportunidadOptional = oportunidadRepository.findById(idOportunidad);

        if (artistaOptional.isEmpty() || oportunidadOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        UserModel artista = artistaOptional.get();
        Oportunidad oportunidad = oportunidadOptional.get();

        if(artista.getTypeUser() != TypeUser.ARTIST) {
            return  ResponseEntity.status(403).body("Solo pueden artistas.");
        }
        if(postulacionRepository.findByUsuarioArtistaAndOportunidad(artista, oportunidad).isPresent()) {
            return ResponseEntity.badRequest().body("Han sido ocupadas todas las vacantes.");
        }

        Postulacion postulacion = new Postulacion();
        postulacion.setUsuarioArtista(artista);
        postulacion.setOportunidad(oportunidad);
        postulacion.setMensaje(mensaje);
        postulacion.setEstadoPostulacion(EstadoPostulacion.PENDENTE);
        postulacion.setFecha(java.time.LocalDateTime.now());

        return  ResponseEntity.ok(postulacionRepository.save(postulacion));

    }

    @GetMapping("/artista/{idArtista}")
    public ResponseEntity<?> obtenerPostulacionesPorArtista(@PathVariable Long idArtista) {
        Optional<UserModel> artista = userRepository.findById(idArtista);

        if (artista.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(postulacionRepository.findByUsuarioArtista(artista.get()));
    }

    @GetMapping("/oportunidad/{idOportunidad}")
    public ResponseEntity<?> obtenerPostulacionesPorOportunidad(@PathVariable Long idOportunidad){
        Optional<Oportunidad> oportunidad = oportunidadRepository.findById(idOportunidad);
        if(oportunidad.isEmpty()){
            return ResponseEntity.notFound().build();

        }
        return ResponseEntity.ok(postulacionRepository.findByOportunidad(oportunidad.get()));
    }

    @GetMapping("/{idPostulacion}/estado")
    public ResponseEntity<?> actualizarEstado(@PathVariable Long idPostulacion, @RequestParam EstadoPostulacion nuevoEstado){
        Optional<Postulacion> postulacion = postulacionRepository.findById(idPostulacion);
        if(postulacion.isEmpty()){
            return ResponseEntity.notFound().build();

        }

        Postulacion postulacion1 = postulacion.get();
        postulacion1.setEstadoPostulacion(nuevoEstado);
        return ResponseEntity.ok(postulacionRepository.save(postulacion1));
    }


}
