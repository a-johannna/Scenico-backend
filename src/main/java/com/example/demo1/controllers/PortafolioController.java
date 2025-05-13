package com.example.demo1.controllers;

import com.example.demo1.mappers.PortafolioMapper;

import com.example.demo1.models.dtos.ErrorResponseDTO;
import com.example.demo1.models.dtos.Portafolio.PortafolioRequestDTO;
import com.example.demo1.models.entidades.Portafolio;
import com.example.demo1.models.dtos.Portafolio.PortafolioPubliDTO;
import com.example.demo1.models.enums.TipoArchivo;
import com.example.demo1.repositories.IPortafolioRepository;
import com.example.demo1.repositories.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
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
    public ResponseEntity<?> crearPortafolio(@PathVariable Long idUser, @RequestBody PortafolioRequestDTO requestDTO) {
        return userRepository.findById(idUser).map(userModel -> {
          Portafolio nuevoPortafolio = PortafolioMapper.toEntity(requestDTO);
          nuevoPortafolio.setUserModel(userModel);

            return ResponseEntity.ok(portafolioRepository.save(nuevoPortafolio));

        }).orElse(ResponseEntity.notFound().build());
    }

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



   /*+
   Revisar, metodo redundante
    */
    @GetMapping("/publicos")
    public ResponseEntity<List<PortafolioPubliDTO>> obtenerOportunidadesPublico(){
        List<Portafolio> listaPortafolios = portafolioRepository.findAll();
        List<PortafolioPubliDTO> portlista = listaPortafolios.stream()
                .map(PortafolioMapper::toPubliDTO)
                .toList();
                return ResponseEntity.ok(portlista);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getAllErrors().getFirst().getDefaultMessage();
        return ResponseEntity.badRequest()
                .body(new ErrorResponseDTO(message, "VALIDATION_ERROR"));
    }
}
