package com.paeldav.backend.infraestructure.repository;

import com.paeldav.backend.domain.entity.RegistroActividad;
import com.paeldav.backend.domain.enums.TipoActividad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository // Indica que esta interfaz es un repositorio gestionado por Spring
public interface RegistroActividadRepository extends JpaRepository<RegistroActividad, Long> {

    // Obtiene todas las actividades registradas de un usuario
    List<RegistroActividad> findByUsuarioId(Long usuarioId);

    // Obtiene actividades filtradas por tipo de actividad
    List<RegistroActividad> findByTipoActividad(TipoActividad tipoActividad);

    // Obtiene actividades de un usuario filtradas por tipo de actividad
    List<RegistroActividad> findByUsuarioIdAndTipoActividad(Long usuarioId, TipoActividad tipoActividad);

    // Obtiene actividades registradas dentro de un rango de tiempo
    List<RegistroActividad> findByTimestampBetween(LocalDateTime inicio, LocalDateTime fin);

    // Obtiene actividades de un usuario dentro de un rango de tiempo
    List<RegistroActividad> findByUsuarioIdAndTimestampBetween(Long usuarioId, LocalDateTime inicio, LocalDateTime fin);

    // Obtiene actividades de un tipo específico dentro de un rango de tiempo
    List<RegistroActividad> findByTipoActividadAndTimestampBetween(TipoActividad tipoActividad, LocalDateTime inicio, LocalDateTime fin);
}