/**
 * IOportunidadRepository.java
 * Proyecto: Scénico - Plataforma para artistas emergentes
 * Descripción: Repositorio JPA para acceder y gestionar entidades de tipo Oportunidad.
 * Incluye métodos personalizados para filtrar oportunidades por empresa, categoría y estado.
 * Autor: Andrea Johanna Villavicencio Lema
 * Fecha: Mayo de 2025
 * Email: johannna.villavicencio@gmail.com
 */
package com.example.demo1.repositories;

import com.example.demo1.models.enums.EstadoOportunidad;
import com.example.demo1.models.entidades.Oportunidad;
import com.example.demo1.models.entidades.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * Interfaz que extiende JpaRepository para realizar operaciones CRUD y consultas personalizadas sobre Oportunidad.
 */
public interface IOportunidadRepository extends JpaRepository<Oportunidad, Long> {

    /**
     * Busca todas las oportunidades creadas por el UUID del usuario.
     * @param uuid   identificación único público del usuario
     * @return          lista de oportunidades publicadas por esa empresa
     */
    List<Oportunidad> findByUsuarioEmpresaUuid(UUID uuid);

    /**
     * Busca oportunidades por categoría, ignorando mayúsculas y minúsculas.
     * @param categoria     nombre de la categoría
     * @return              lista de oportunidades que coinciden con la categoría
     */
    List<Oportunidad> findByCategoriaIgnoreCase(String categoria);

    /**
     *  Devuelve todas las oportunidades que se encuentren en un estado específico (ABIERTO, CERRADO).
     * @param estado    estado de la oportunidad
     * @return          lista de oportunidades con ese estado
     */
    List<Oportunidad> findByEstado(EstadoOportunidad estado);

    /**
     * Busca oportunidades por categoría y estado al mismo tiempo.
     * @param categoria     categoría de la oportunidad
     * @param estado        estado actual de la oportunidad
     * @return              lista de oportunidades que coinciden con ambos criterios
     */
    List<Oportunidad> findByCategoriaIgnoreCaseAndEstado(String categoria, EstadoOportunidad estado);

}