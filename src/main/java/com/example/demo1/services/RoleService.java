package com.example.demo1.services;

import com.example.demo1.models.entidades.UserModel;
import com.example.demo1.models.enums.RoleName;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
public class RoleService {

    public void actualizarRolesSegunTipo(UserModel user, RoleName nuevoTipo) {
        if (nuevoTipo == null) {
            throw new IllegalArgumentException("Tipo de usuario no puede ser null");
        }

        if (user.getTypeUser() != null && user.getTypeUser().equals(nuevoTipo)) {
            throw new IllegalStateException("El usuario ya tiene el rol solicitado: " + nuevoTipo);
        }

        // Asignar directamente el nuevo rol
        user.setTypeUser(nuevoTipo);
    }

    public void asignarRolInicial(UserModel user) {
        user.setTypeUser(RoleName.USER);
    }

    public boolean tienePermisosAdministrativos(UserModel user, RoleName nuevoTipo) {
        // Solo ADMIN puede asignar el rol ADMIN
        if (nuevoTipo == RoleName.ADMIN && user.getTypeUser() != RoleName.ADMIN) {
            throw new AccessDeniedException("No tienes permisos para asignar rol ADMIN.");
        }

        return true;
    }
}
