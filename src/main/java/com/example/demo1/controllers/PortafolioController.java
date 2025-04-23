package com.example.demo1.controllers;

import com.example.demo1.mappers.PortafolioMapper;

import com.example.demo1.models.entidades.Portafolio;
import com.example.demo1.models.dtos.PortafolioPubliDTO;
import com.example.demo1.models.enums.TipoArchivo;
import com.example.demo1.repositories.IPortafolioRepository;
import com.example.demo1.repositories.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/portafolios")
public class PortafolioController {

    @Autowired
    private IPortafolioRepository portafolioRepository;

    @Autowired
    private IUserRepository userRepository;

    @PostMapping("userModel/{idUser}")
    public ResponseEntity<?> crearPortafolio(@PathVariable Long idUser, @RequestBody Portafolio nuevoContenido) {
        return userRepository.findById(idUser).map(userModel -> {
            nuevoContenido.setUserModel(userModel);
            return ResponseEntity.ok(portafolioRepository.save(nuevoContenido));
        }).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("userModel/{idUser}")
    public ResponseEntity<List<Portafolio>> obtenerPortafolioPorUsuario(@RequestParam Long idUser) {
        return userRepository.findById(idUser).map(userModel -> {
            List<Portafolio> portafolios = portafolioRepository.findByUserModel_Id(userModel);
            return ResponseEntity.ok(portafolios);
        }).orElse(ResponseEntity.notFound().build());

    }

    @DeleteMapping("/{idPortafolio}")
    public ResponseEntity<?> eliminarPortafolio(@PathVariable Long idPortafolio) {
       if (portafolioRepository.findById(idPortafolio).isPresent()) {
           portafolioRepository.deleteById(idPortafolio);
           return ResponseEntity.ok().build();
       }else {
           return ResponseEntity.notFound().build();
       }

    }

    @GetMapping("/buscar")
    public ResponseEntity<List<Portafolio>> buscarPorTipoArchivoAndEtiquetas(@RequestParam(required = false)TipoArchivo tipoArchivo, @RequestParam(required = false) String etiqueta) {

        if (tipoArchivo != null && etiqueta != null) {
            return ResponseEntity.ok(portafolioRepository.findbyTipoArchivoAndEtiquetasContainingIgnoreCase(tipoArchivo, etiqueta));
        } else if (tipoArchivo != null) {
            return ResponseEntity.ok(portafolioRepository.findByTipoArchivo(tipoArchivo));
        } else if (etiqueta != null) {
            return ResponseEntity.ok(portafolioRepository.findByEtiquetasContainingIgnoreCase(etiqueta));
        } else {
            return ResponseEntity.ok(portafolioRepository.findAll());
        }
    }


   /** public ResponseEntity<List<PortafolioPubliDTO>> obtenerPublicos(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Portafolio> publicos = portafolioRepository.findAll(pageable);

        List<PortafolioPubliDTO> datos = publicos.stream().map(portafolio -> new PortafolioPubliDTO(
                portafolio.getTitulo(),
                portafolio.getIdPortafolio(),
                portafolio.getDescripcion(),
                portafolio.getTipoArchivo().name(),
                portafolio.getUrlArchivo(),
                portafolio.getEtiquetas(),
                portafolio.getUserModel().getUsername()

        )).toList();
        return ResponseEntity.ok(datos);
    }**/

    @GetMapping("/publicos")
    public ResponseEntity<List<PortafolioPubliDTO>> obtenerOportunidadesPublico(){
        List<Portafolio> listaPortafolios = portafolioRepository.findAll();
        List<PortafolioPubliDTO> portlista = listaPortafolios.stream()
                .map(PortafolioMapper::toPubliDTO)
                .toList();
                return ResponseEntity.ok(portlista);
    }

}
