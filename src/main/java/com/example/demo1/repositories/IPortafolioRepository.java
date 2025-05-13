package com.example.demo1.repositories;

import com.example.demo1.models.entidades.Portafolio;
import com.example.demo1.models.enums.TipoArchivo;
import com.example.demo1.models.entidades.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IPortafolioRepository extends JpaRepository<Portafolio, Long> {

   // List<Portafolio> findByUsername(UserModel username);
//    List<Portafolio> findByTypeUser_Id(TypeUser idUser);
    List<Portafolio> findByUserModel(UserModel userModel);
    List<Portafolio> findByTipoArchivo(TipoArchivo tipoArchivo);
    List<Portafolio> findByEtiquetasContainingIgnoreCase(String etiquetas);
    List<Portafolio> findByTipoArchivoAndEtiquetasContainingIgnoreCase(TipoArchivo tipoArchivo, String etiquetas);


}
