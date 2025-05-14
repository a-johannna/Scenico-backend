package com.example.demo1.controllers;

import com.example.demo1.models.dtos.ErrorResponseDTO;
import com.example.demo1.models.dtos.Oportunidad.CrearOportunidadDTO;
import com.example.demo1.models.dtos.Oportunidad.OportunidadPublicDTO;
import com.example.demo1.models.dtos.Oportunidad.OportunidadResponseDTO;
import com.example.demo1.models.entidades.Oportunidad;
import com.example.demo1.models.enums.EstadoOportunidad;
import com.example.demo1.models.enums.TypeUser;
import com.example.demo1.repositories.IOportunidadRepository;
import com.example.demo1.repositories.IUserRepository;
import com.example.demo1.mappers.OportunidadMapper;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.method.MethodValidationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("api/oportunidades")
public class OportunidadController {
    @Autowired
    private IOportunidadRepository oportunidadRepository;
    @Autowired
    private IUserRepository userRepository;

    @PostMapping("/{idEmpresa}")
    public ResponseEntity<?> crearOportunidad(@PathVariable Long idEmpresa, @Valid @RequestBody CrearOportunidadDTO dto){

        return  userRepository.findById(idEmpresa)
                .map(empresaUser -> {
                    if (empresaUser.getTypeUser() != TypeUser.ENTERPRISE) {
                        return  ResponseEntity.status(403)
                                .body(new ErrorResponseDTO("Solo acceso para Empresas", "FORBIDDEN"));
                    }
                    Oportunidad nuevaOportunidad = OportunidadMapper.toEntity(dto);
                    nuevaOportunidad.setUsuarioEmpresa(empresaUser);
                    Oportunidad savedOportunidad = oportunidadRepository.save(nuevaOportunidad);
                    return ResponseEntity.ok(OportunidadMapper.toResponseDTO(savedOportunidad));
                })
                .orElse(ResponseEntity.notFound().build());

    }

    @GetMapping("/api/oportunidades/{id}")
    public ResponseEntity<OportunidadResponseDTO> obtenerOportunidadById(@PathVariable Long id) {
        return oportunidadRepository.findById(id)
                .map(oportunidad -> ResponseEntity.ok(OportunidadMapper.toResponseDTO(oportunidad)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/api/oportunidades")
    public ResponseEntity<List<OportunidadResponseDTO>> findAll() {
        List<OportunidadResponseDTO> oportunidades = oportunidadRepository.findAll().stream()
                .map(OportunidadMapper:: toResponseDTO)
                .toList();

        return  ResponseEntity.ok(oportunidades);
    }

    @GetMapping("/categoria/{categoria}")
   /** public List<Oportunidad> findByCategoriaIgnoreCase(@PathVariable String categoria){
        return oportunidadRepository.findByCategoriaIgnoreCase(categoria);
    }
**/
   public ResponseEntity<List<OportunidadResponseDTO>> findByCategoriaIgnoreCase(@PathVariable String categoria) {
       List<OportunidadResponseDTO> oportunidades = oportunidadRepository
               .findByCategoriaIgnoreCase(categoria).stream()
               .map(OportunidadMapper::toResponseDTO)
               .toList();
       return  ResponseEntity.ok(oportunidades);
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
    /**
     *   public List<Oportunidad> findByCategoriaIgnoreCaseAndEstado(@RequestParam String categoria, @RequestParam EstadoOportunidad estadoOportunidad){
     *         return oportunidadRepository.findByCategoriaIgnoreCaseAndEstado(categoria, estadoOportunidad);
     *     }
     */
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
    public ResponseEntity<List<OportunidadPublicDTO>> listarOportunidadesPublico(){
        List<OportunidadPublicDTO> oportunidades = oportunidadRepository.findAll().stream()
                .map(OportunidadMapper::toOportunidadPublicDTO)
                .toList();
        return ResponseEntity.ok(oportunidades);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarOportunidad(
            @PathVariable Long id,
            @Valid @RequestBody CrearOportunidadDTO dto) {
      return  oportunidadRepository.findById(id)
              .map(oportunidad -> {
                  OportunidadMapper.updateFromDTO(oportunidad,dto);
                  Oportunidad updated = oportunidadRepository.save(oportunidad);
                  return ResponseEntity.ok(OportunidadMapper.toResponseDTO(updated));
              }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarOportunidad(@PathVariable Long id) {
      return oportunidadRepository.findById(id)
              .map(oportunidad ->  {
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


}
