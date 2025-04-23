package com.example.demo1.controllers;

import com.example.demo1.models.dtos.Oportunidad.OportunidadPublicDTO;
import com.example.demo1.models.entidades.Oportunidad;
import com.example.demo1.models.entidades.UserModel;
import com.example.demo1.models.enums.EstadoOportunidad;
import com.example.demo1.models.enums.TypeUser;
import com.example.demo1.repositories.IOportunidadRepository;
import com.example.demo1.repositories.IUserRepository;
import com.example.demo1.mappers.OportunidadMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/oportunidades")
public class OportunidadController {
    @Autowired
    private IOportunidadRepository oportunidadRepository;
    @Autowired
    private IUserRepository userRepository;

    @PostMapping("/{idEmpresa}")
    public ResponseEntity<?> crearOportunidad(@PathVariable Long idEmpresa, @RequestBody Oportunidad nuevaOportunidad){
        Optional<UserModel> empOportunidad = userRepository.findById(idEmpresa);

        if(empOportunidad.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        UserModel empresaUser = empOportunidad.get();
            if (empresaUser.getTypeUser() == TypeUser.ENTERPRISE) {
                nuevaOportunidad.setUsuarioEmpresa(empresaUser);
                oportunidadRepository.save(nuevaOportunidad);
                return ResponseEntity.ok(nuevaOportunidad);
            } else {
                return ResponseEntity.status(403).body("El usuario no tiene permisos. Solo acceso para Empresas.");

            }

    }

    @GetMapping
    public List<Oportunidad> findAll(){
        return oportunidadRepository.findAll();
    }

    @GetMapping("/categoria/{categoria}")
    public List<Oportunidad> findByCategoriaIgnoreCase(@PathVariable String categoria){
        return oportunidadRepository.findByCategoriaIgnoreCase(categoria);
    }

    @GetMapping("/estadoOportunidad/{estadoOportunidad}")
    public List<Oportunidad> findByEstado(@PathVariable EstadoOportunidad estadoOportunidad){
        return oportunidadRepository.findByEstadoOportunidad(estadoOportunidad);
    }

    @GetMapping("/buscar")
    public List<Oportunidad> findByCategoriaIgnoreCaseAndEstado(@RequestParam String categoria, @RequestParam EstadoOportunidad estadoOportunidad){
        return oportunidadRepository.findByCategoriaIgnoreCaseAndEstado(categoria, estadoOportunidad);
    }


    @GetMapping("/publicas")
    public ResponseEntity<List<OportunidadPublicDTO>> listarOportunidadesPublico(){
        List<Oportunidad> oportunidades = oportunidadRepository.findAll();
        List<OportunidadPublicDTO> opotLista=oportunidades.stream()
                .map(OportunidadMapper::toOportunidadPublicDTO)
                .toList();
        return ResponseEntity.ok(opotLista);
    }


}
