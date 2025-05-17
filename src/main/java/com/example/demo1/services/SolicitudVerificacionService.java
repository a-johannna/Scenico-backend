package com.example.demo1.services;

import com.example.demo1.mappers.SolicitudVerificacionMapper;
import com.example.demo1.models.dtos.SolicitudVerificacion.SolicitudVerificacionResponseDTO;
import com.example.demo1.models.entidades.SolicitudVerificacion;
import com.example.demo1.models.entidades.UserModel;
import com.example.demo1.models.enums.EstadoSolicitud;
import com.example.demo1.models.enums.RoleName;
import com.example.demo1.repositories.ISolucitudVerifRepository;
import com.example.demo1.repositories.IUserRepository;
import org.springframework.stereotype.Service;

import javax.management.relation.Role;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class SolicitudVerificacionService {

    private final ISolucitudVerifRepository solicitudVerifRepository;
    private final IUserRepository userRepository;
    private final RoleService roleService;

    public SolicitudVerificacionService(
            ISolucitudVerifRepository solicitudVerifRepository,
            IUserRepository userRepository,
            RoleService roleService) {
        this.solicitudVerifRepository = solicitudVerifRepository;
        this.userRepository = userRepository;
        this.roleService = roleService;
    }


    private void validarProgresionRoles(RoleName rolActual, RoleName rolSolicitado) {
        if (rolActual == rolSolicitado) {
            throw new IllegalStateException("Ya tienes este rol asignado");
        }

        // Un admin no puede cambiar su rol
        if (rolActual == RoleName.ADMIN) {
            throw new IllegalStateException("Un administrador no puede cambiar su rol");
        }

        switch (rolSolicitado) {
            case USER -> throw new IllegalStateException("No se puede solicitar el rol básico");
            case ADMIN -> {
            } // Cualquiera puede solicitar ser admin (requiere verificación)
            case ARTIST -> {
                if (rolActual != RoleName.USER) {
                    throw new IllegalStateException("Solo usuarios normales pueden solicitar ser artistas");
                }
            }
            case ENTERPRISE -> {
                if (rolActual != RoleName.USER && rolActual != RoleName.ARTIST) {
                    throw new IllegalStateException("Solo usuarios normales o artistas pueden ser empresa");
                }
            }
            default -> throw new IllegalStateException("Rol solicitado no válido");
        }
    }

    public SolicitudVerificacionResponseDTO aprobarSolicitud(Long idSolicitud) {
        // 1. Validar existencia de la solicitud
        SolicitudVerificacion solicitud = solicitudVerifRepository.findById(idSolicitud)
                .orElseThrow(() -> new IllegalStateException("Solicitud no encontrada"));

        // 2. Validaciones básicas
        if (solicitud.getEstadoSolicitud() != EstadoSolicitud.PENDIENTE) {
            throw new IllegalStateException("La solicitud no está en estado pendiente");
        }

        UserModel user = solicitud.getUser();
        if (user == null) {
            throw new IllegalStateException("Usuario no encontrado");
        }

        RoleName rolSolicitado = solicitud.getRolSolicitado();

        // 3. Validar rol y solicitudes pendientes
        if (user.getTypeUser() == rolSolicitado) {
            throw new IllegalStateException("El usuario ya tiene el rol solicitado");
        }

        List<SolicitudVerificacion> solicitudesPendientes =
                solicitudVerifRepository.findByUserAndEstadoSolicitud(user, EstadoSolicitud.PENDIENTE);
        if (!solicitudesPendientes.isEmpty() &&
                solicitudesPendientes.get(0).getIdSolicitud() != solicitud.getIdSolicitud()) {
            throw new IllegalStateException("El usuario tiene otras solicitudes pendientes");
        }

        // 4. Validar progresión de roles
        validarProgresionRoles(user.getTypeUser(), rolSolicitado);

        try {
            // 5. Actualizar usuario y roles
            actualizarUsuario(user, rolSolicitado);

            // 6. Actualizar solicitud
            solicitud.setEstadoSolicitud(EstadoSolicitud.ACEPTADO);
            solicitud.setFechaResolucion(LocalDateTime.now());

            return SolicitudVerificacionMapper.toResponseDTO(
                    solicitudVerifRepository.save(solicitud));
        } catch (Exception e) {
            throw new IllegalStateException("Error al procesar la solicitud: " + e.getMessage());
        }
    }


    private void actualizarUsuario(UserModel user, RoleName nuevoRol) {
        user.setTypeUser(nuevoRol);
        user.setVerified(true);
        user.setUpdateAt(LocalDateTime.now());
        roleService.actualizarRolesSegunTipo(user, nuevoRol);
        userRepository.save(user);

    }

    public SolicitudVerificacionResponseDTO rechazarSolicitud(Long idSolicitud, String observacionAdmin) {
        // 1. Validar existencia de la solicitud
        SolicitudVerificacion solicitud = solicitudVerifRepository.findById(idSolicitud)
                .orElseThrow(() -> new IllegalStateException("Solicitud no encontrada"));

        // 2. Validar estado de la solicitud
        if (solicitud.getEstadoSolicitud() != EstadoSolicitud.PENDIENTE) {
            throw new IllegalStateException("La solicitud no está en estado pendiente");
        }

        // 3. Validar que se proporcionaron observaciones
        if (observacionAdmin == null || observacionAdmin.trim().isEmpty()) {
            throw new IllegalStateException("Debe proporcionar observaciones para el rechazo");
        }

        try {
            // 4. Actualizar la solicitud
            solicitud.setEstadoSolicitud(EstadoSolicitud.RECHAZADO);
            solicitud.setFechaResolucion(LocalDateTime.now());
            solicitud.setObservacionesAdmin(observacionAdmin.trim());

            // 5. El usuario mantiene su rol actual y estado de verificación
            UserModel user = solicitud.getUser();
            user.setUpdateAt(LocalDateTime.now());
            userRepository.save(user);

            // 6. Guardar y retornar la solicitud actualizada
            return SolicitudVerificacionMapper.toResponseDTO(
                    solicitudVerifRepository.save(solicitud));

        } catch (Exception e) {
            throw new IllegalStateException("Error al rechazar la solicitud: " + e.getMessage());
        }
    }
}