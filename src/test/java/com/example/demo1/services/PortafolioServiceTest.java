package com.example.demo1.services;

import com.example.demo1.mappers.PortafolioMapper;
import com.example.demo1.models.dtos.Portafolio.PortafolioPubliDTO;
import com.example.demo1.models.dtos.Portafolio.PortafolioRequestDTO;
import com.example.demo1.models.entidades.Portafolio;
import com.example.demo1.models.entidades.UserModel;
import com.example.demo1.models.enums.TipoArchivo;
import com.example.demo1.repositories.IPortafolioRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PortafolioServiceTest {

    @Mock
    private IPortafolioRepository portafolioRepository;

    @InjectMocks
    private PortafolioService portafolioService;

    // ---------------------------------------------------------
    // 1) Pruebas para crearPortafolio(...)
    // ---------------------------------------------------------
    @Nested
    @DisplayName("crearPortafolio(...)")
    class CrearPortafolioTests {

        @Test
        @DisplayName("Creación exitosa: mapea, asigna usuario y fecha, guarda y retorna DTO")
        void crearPortafolio_ValidDatos_CreaYDevuelveDTO() {
            // 1) Preparamos PortafolioRequestDTO con datos de prueba
            List<String> etiquetas = Arrays.asList("arte", "prueba");
            PortafolioRequestDTO dto = new PortafolioRequestDTO(
                    "Título de prueba",
                    "Descripción de prueba",
                    TipoArchivo.IMAGE,
                    "http://archivo.png",
                    "http://imagen.png",
                    "imagen.png",
                    "Descripción imagen",
                    etiquetas
            );

            // 2) Creamos UserModel ficticio
            UserModel user = new UserModel();
            user.setUuid(UUID.randomUUID());
            user.setUsername("usuarioX");

            // 3) Preparamos la entidad vacía que el mapper toEntity(dto) devolverá
            Portafolio entidadSinCampos = new Portafolio();

            // 4) Preparamos la entidad “guardada” por el repositorio
            Portafolio entidadGuardada = new Portafolio();
            entidadGuardada.setIdPortafolio(123L);
            entidadGuardada.setUserModel(user);
            entidadGuardada.setFechaCreacion(LocalDateTime.now());
            entidadGuardada.setTitulo("Título de prueba");
            entidadGuardada.setDescripcion("Descripción de prueba");
            entidadGuardada.setTipoArchivo(TipoArchivo.IMAGE);
            entidadGuardada.setUrlArchivo("http://archivo.png");
            entidadGuardada.setUrlImagen("http://imagen.png");
            entidadGuardada.setNombreImagen("imagen.png");
            entidadGuardada.setDescripcionImagen("Descripción imagen");
            entidadGuardada.setEtiquetas(etiquetas);

            // 5) Preparamos el DTO público que el mapper toPubliDTO(entidadGuardada) devolverá
            //    Observa que PortafolioPubliDTO no tiene constructor vacío,
            //    así que invocamos el constructor que recibe todos los campos:
            PortafolioPubliDTO publiDTO = new PortafolioPubliDTO(
                    "Título de prueba",
                    123L,
                    "Descripción de prueba",
                    TipoArchivo.IMAGE,
                    "http://archivo.png",
                    "http://imagen.png",
                    "Descripción imagen",
                    etiquetas,
                    "usuarioX"
            );

            // 6) Mockeamos métodos estáticos de PortafolioMapper
            try (MockedStatic<PortafolioMapper> mapperMock = Mockito.mockStatic(PortafolioMapper.class)) {
                mapperMock.when(() -> PortafolioMapper.toEntity(dto))
                        .thenReturn(entidadSinCampos);
                mapperMock.when(() -> PortafolioMapper.toPubliDTO(entidadGuardada))
                        .thenReturn(publiDTO);

                // 7) Configuramos el repositorio: save(entidadSinCampos) devuelve entidadGuardada
                when(portafolioRepository.save(entidadSinCampos))
                        .thenReturn(entidadGuardada);

                // 8) Ejecutamos el método bajo prueba
                PortafolioPubliDTO resultado = portafolioService.crearPortafolio(dto, user);

                // 9) Verificaciones de resultado
                assertThat(resultado).isNotNull();
                assertThat(resultado.getIdPortafolio()).isEqualTo(123L);
                assertThat(resultado.getTitulo()).isEqualTo("Título de prueba");
                assertThat(resultado.getUrlImagen()).isEqualTo("http://imagen.png");
                assertThat(resultado.getNombreUsuario()).isEqualTo("usuarioX");

                // Verificamos interacciones con el mapper y repositorio
                mapperMock.verify(() -> PortafolioMapper.toEntity(dto), times(1));
                verify(portafolioRepository, times(1)).save(entidadSinCampos);
                mapperMock.verify(() -> PortafolioMapper.toPubliDTO(entidadGuardada), times(1));

                // Verificamos que entidadSinCampos recibió usuario y fecha antes del save
                assertThat(entidadSinCampos.getUserModel()).isEqualTo(user);
                assertThat(entidadSinCampos.getFechaCreacion()).isNotNull();
            }
        }
    }

    // ---------------------------------------------------------
    // 2) Pruebas para actualizarPortafolio(...)
    // ---------------------------------------------------------
    @Nested
    @DisplayName("actualizarPortafolio(...)")
    class ActualizarPortafolioTests {

        @Test
        @DisplayName("Portafolio no encontrado → lanza IllegalArgumentException")
        void actualizarPortafolio_NoExiste_LanzaException() {
            Long id = 999L;
            PortafolioRequestDTO dto = new PortafolioRequestDTO(
                    "Cualquier título",
                    "Cualquier descripción",
                    TipoArchivo.AUDIO,
                    null,
                    null,
                    null,
                    null,
                    Collections.emptyList()
            );
            UserModel user = new UserModel();
            user.setUuid(UUID.randomUUID());

            when(portafolioRepository.findById(id)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> portafolioService.actualizarPortafolio(id, dto, user))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Portafolio no encontrado");

            verify(portafolioRepository, times(1)).findById(id);
            verify(portafolioRepository, never()).save(any());
        }

        @Test
        @DisplayName("Usuario no propietario → lanza SecurityException")
        void actualizarPortafolio_NotOwner_LanzaSecurityException() {
            Long id = 100L;
            PortafolioRequestDTO dto = new PortafolioRequestDTO(
                    "Título X",
                    "Desc X",
                    TipoArchivo.VIDEO,
                    "http://video.mp4",
                    "http://miniatura.png",
                    "miniatura.png",
                    "Pie de imagen",
                    List.of("etiqueta1")
            );

            // El portafolio existe con un propietario distinto
            Portafolio existente = new Portafolio();
            UserModel otroUsuario = new UserModel();
            otroUsuario.setUuid(UUID.randomUUID());
            existente.setUserModel(otroUsuario);

            when(portafolioRepository.findById(id)).thenReturn(Optional.of(existente));

            UserModel demandante = new UserModel();
            demandante.setUuid(UUID.randomUUID()); // distinto de existente.userModel.uuid

            assertThatThrownBy(() -> portafolioService.actualizarPortafolio(id, dto, demandante))
                    .isInstanceOf(SecurityException.class)
                    .hasMessage("No tienes permiso para modificar este portafolio");

            verify(portafolioRepository, times(1)).findById(id);
            verify(portafolioRepository, never()).save(any());
        }

        @Test
        @DisplayName("Actualización exitosa: modifica campos, guarda y retorna DTO")
        void actualizarPortafolio_ValidDatos_ActualizaYDevuelveDTO() {
            Long id = 200L;
            // 1) Portafolio existente con propietario
            Portafolio existente = new Portafolio();
            existente.setIdPortafolio(id);
            UserModel propietario = new UserModel();
            propietario.setUuid(UUID.randomUUID());
            existente.setUserModel(propietario);
            existente.setTitulo("Título Antiguo");
            existente.setDescripcion("Desc Antiguo");
            existente.setTipoArchivo(TipoArchivo.DOCUMENT);
            existente.setUrlArchivo("http://archivo_antiguo.txt");
            existente.setUrlImagen("http://img_antigua.png");
            existente.setNombreImagen("antigua.png");
            existente.setDescripcionImagen("Pie antigua");
            existente.setEtiquetas(Arrays.asList("antiguo1", "antiguo2"));

            // 2) DTO con nuevos datos
            List<String> nuevasEtiquetas = Arrays.asList("nuevo1", "nuevo2");
            PortafolioRequestDTO dto = new PortafolioRequestDTO(
                    "Título Nuevo",
                    "Desc Nueva",
                    TipoArchivo.VIDEO,
                    "http://video_nuevo.mp4",
                    "http://img_nueva.png",
                    "nueva.png",
                    "Pie nueva",
                    nuevasEtiquetas
            );

            when(portafolioRepository.findById(id)).thenReturn(Optional.of(existente));

            // 3) Simulamos repositorio: save(existente) devuelve entidad “actualizada”
            Portafolio actualizado = new Portafolio();
            actualizado.setIdPortafolio(id);
            actualizado.setUserModel(propietario);
            actualizado.setTitulo(dto.getTitulo());
            actualizado.setDescripcion(dto.getDescripcion());
            actualizado.setTipoArchivo(dto.getTipoArchivo());
            actualizado.setUrlArchivo(dto.getUrlArchivo());
            actualizado.setUrlImagen(dto.getUrlImagen());
            actualizado.setNombreImagen(dto.getNombreImagen());
            actualizado.setDescripcionImagen(dto.getDescripcionImagen());
            actualizado.setEtiquetas(nuevasEtiquetas);

            when(portafolioRepository.save(existente)).thenReturn(actualizado);

            // 4) DTO público que retornará el mapper
            PortafolioPubliDTO publiDTO = new PortafolioPubliDTO(
                    "Título Nuevo",
                    id,
                    "Desc Nueva",
                    TipoArchivo.VIDEO,
                    "http://video_nuevo.mp4",
                    "http://img_nueva.png",
                    "Pie nueva",
                    nuevasEtiquetas,
                    propietario.getUsername() // PuertoPublicoDTO espera nombreUsuario
            );

            try (MockedStatic<PortafolioMapper> mapperMock = Mockito.mockStatic(PortafolioMapper.class)) {
                mapperMock.when(() -> PortafolioMapper.toPubliDTO(actualizado))
                        .thenReturn(publiDTO);

                // 5) Llamamos a actualizarPortafolio
                PortafolioPubliDTO resultado = portafolioService.actualizarPortafolio(id, dto, propietario);

                // 6) Verificaciones del resultado
                assertThat(resultado).isNotNull();
                assertThat(resultado.getIdPortafolio()).isEqualTo(id);
                assertThat(resultado.getTitulo()).isEqualTo("Título Nuevo");
                assertThat(resultado.getUrlImagen()).isEqualTo("http://img_nueva.png");
                assertThat(resultado.getNombreUsuario()).isEqualTo(propietario.getUsername());

                // Verificamos que los campos de la entidad existente cambiaron antes del save
                assertThat(existente.getTitulo()).isEqualTo("Título Nuevo");
                assertThat(existente.getDescripcion()).isEqualTo("Desc Nueva");
                assertThat(existente.getTipoArchivo()).isEqualTo(TipoArchivo.VIDEO);
                assertThat(existente.getUrlArchivo()).isEqualTo("http://video_nuevo.mp4");
                assertThat(existente.getUrlImagen()).isEqualTo("http://img_nueva.png");
                assertThat(existente.getNombreImagen()).isEqualTo("nueva.png");
                assertThat(existente.getDescripcionImagen()).isEqualTo("Pie nueva");
                assertThat(existente.getEtiquetas()).containsExactlyElementsOf(nuevasEtiquetas);

                verify(portafolioRepository, times(1)).findById(id);
                verify(portafolioRepository, times(1)).save(existente);
                mapperMock.verify(() -> PortafolioMapper.toPubliDTO(actualizado), times(1));
            }
        }
    }
}
