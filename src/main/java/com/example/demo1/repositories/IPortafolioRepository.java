/**
 * IPortafolioRepository.java
 * Proyecto: Scénico -Plataforma para artistas emergentes
 * Descripción: Repositorio JPA para la entidad Portafolio.
 * Proporciona métodos personalizados para consultar portafolios
 * por usuario, tipos de archivos y etiquetas asociadas.
 * Autor: Andrea Johanna Villavicencio Lema
 * Fecha: Mayo de 2025
 * Email: johannna.villavicencio@gmail.com
 */
package com.example.demo1.repositories;

import com.example.demo1.models.entidades.Portafolio;
import com.example.demo1.models.enums.TipoArchivo;
import com.example.demo1.models.entidades.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Interfaz que define operaciones de acceso a datos para la entidad de Portafolio.
 * Extiende JpaRepository para heredar operaciones CRUD.
 */
public interface IPortafolioRepository extends JpaRepository<Portafolio, Long> {

    /**
     * Busca todos los portafolios asociados a un usuario específico.
     * @param userModel  entidad del usuario
     * @return           lista de portafolios creados por ese usuario
     */
    List<Portafolio> findByUserModel(UserModel userModel);

     /**
      * Devuelve los portafolios filtrados por tipo de archivo
      * @param tipoArchivo   tipo de archivo a filtrar
      * @return              lista de portafolios que contiene ese tipo de archivo
      */
     List<Portafolio> findByTipoArchivo(TipoArchivo tipoArchivo);

    /**
     * Busca portafolios filtrando por tipo de archivo y etiquetas parcial
     * @param tipoArchivo  tipo de archivo
     * @param etiqueta     texto parcial de la etiqueta
     * @return             lista de portafolios que coinciden con los filtros
     */
    @Query("SELECT p FROM Portafolio p JOIN p.etiquetas e " +
                  "WHERE (:tipoArchivo IS NULL OR p.tipoArchivo = :tipoArchivo) " +
                  "AND (:etiqueta IS NULL OR LOWER(e) LIKE LOWER(CONCAT('%', :etiqueta, '%')))")
    List<Portafolio> findByEtiquetaYTipoArchivo(@Param("tipoArchivo") TipoArchivo tipoArchivo,
                                            @Param("etiqueta") String etiqueta);

    /**
     * Busca portafolios que coinciden con exactamente con un tipo de archivo y contenga una etiqueta específica.
     * @param tipoArchivo   tipo de archivo
     * @param etiquetas     etiqueta que debe estar contenida en el portafolio
     * @return              lista de portafolios que cumplen ambos criterios
     */
    List<Portafolio> findByTipoArchivoAndEtiquetasContainingIgnoreCase(TipoArchivo tipoArchivo, String etiquetas);


}
