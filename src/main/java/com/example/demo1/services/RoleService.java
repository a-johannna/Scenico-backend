package com.example.demo1.services;

import com.example.demo1.models.entidades.Rols.Role;
import com.example.demo1.models.entidades.UserModel;
import com.example.demo1.models.enums.RoleName;
import com.example.demo1.repositories.IRoleRepository;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

import static com.example.demo1.models.enums.RoleName.EMPRESA;

@Service
public class RoleService {

    private final IRoleRepository roleRepository;

    public RoleService(IRoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public void actualizarRolesSegunTipo(UserModel user, RoleName nuevoTipo) {
        // Mantener el rol básico de usuario
        Set<Role> nuevosRoles = new HashSet<>();
        Role roleUser = roleRepository.findByName(RoleName.USER)
                .orElseThrow(() -> new IllegalStateException("Rol básico no encontrado"));
        nuevosRoles.add(roleUser);

        // Asignar roles adicionales según el tipo
        switch (nuevoTipo) {
            case ARTIST:
                Role roleArtist = roleRepository.findByName(RoleName.ARTIST)
                        .orElseThrow(() -> new IllegalStateException("Rol de artista no encontrado"));
                nuevosRoles.add(roleArtist);
                break;

            case EMPRESA:
                Role roleEmpresa = roleRepository.findByName(EMPRESA)
                        .orElseThrow(() -> new IllegalStateException("Rol de empresa no encontrado"));
                nuevosRoles.add(roleEmpresa);
                break;

            case ADMIN:
                Role roleAdmin = roleRepository.findByName(RoleName.ADMIN)
                        .orElseThrow(() -> new IllegalStateException("Rol de administrador no encontrado"));
                // Para admin, mantenemos los roles previos y añadimos el de admin
                nuevosRoles.addAll(user.getRoles());
                nuevosRoles.add(roleAdmin);
                break;

            case USER:
                // Para VIEWER solo mantiene el rol básico
                break;

            default:
                throw new IllegalStateException("Tipo de usuario no válido");
        }

        // Actualizar los roles del usuario
        user.getRoles().clear();
        user.getRoles().addAll(nuevosRoles);
    }

    public void asignarRolInicial(UserModel user) {
        user.setTypeUser(RoleName.USER);

        Role role = roleRepository.findByName(RoleName.USER)
                .orElseGet(() -> {
                    Role nuevo = new Role();
                    nuevo.setName(RoleName.USER);
                    return roleRepository.save(nuevo);
                });

        user.getRoles().clear(); // si estás seguro de que quieres limpiar otros roles
        user.addRole(role);
    }


    public boolean tienePermisosAdministrativos(UserModel user) {
      return user.hasRole(RoleName.ADMIN);


    }



}
