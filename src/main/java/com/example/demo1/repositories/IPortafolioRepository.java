package com.example.demo1.repositories;

import com.example.demo1.models.entidades.Portafolio;
import com.example.demo1.models.enums.TipoArchivo;
import com.example.demo1.models.entidades.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface IPortafolioRepository extends JpaRepository<Portafolio, Long> {

   // List<Portafolio> findByUsername(UserModel username);
//    List<Portafolio> findByTypeUser_Id(TypeUser idUser);
    List<Portafolio> findByUserModel(UserModel userModel);
    List<Portafolio> findByTipoArchivo(TipoArchivo tipoArchivo);
    @Query("SELECT p FROM Portafolio p JOIN p.etiquetas e " +
            "WHERE (:tipoArchivo IS NULL OR p.tipoArchivo = :tipoArchivo) " +
            "AND (:etiqueta IS NULL OR LOWER(e) LIKE LOWER(CONCAT('%', :etiqueta, '%')))")
    List<Portafolio> findByEtiquetaYTipoArchivo(@Param("tipoArchivo") TipoArchivo tipoArchivo,
                                            @Param("etiqueta") String etiqueta);

    List<Portafolio> findByTipoArchivoAndEtiquetasContainingIgnoreCase(TipoArchivo tipoArchivo, String etiquetas);


}
