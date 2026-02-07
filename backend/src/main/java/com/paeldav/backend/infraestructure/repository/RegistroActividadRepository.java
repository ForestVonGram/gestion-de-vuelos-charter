package com.paeldav.backend.infraestructure.repository;

import com.paeldav.backend.domain.entity.RegistroActividad;
import com.paeldav.backend.domain.enums.TipoActividad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RegistroActividadRepository extends JpaRepository<RegistroActividad, Long> {

    List<RegistroActividad> findByUsuarioId(Long usuarioId);

    List<RegistroActividad> findByTipoActividad(TipoActividad tipoActividad);

    List<RegistroActividad> findByUsuarioIdAndTipoActividad(Long usuarioId, TipoActividad tipoActividad);

    List<RegistroActividad> findByTimestampBetween(LocalDateTime inicio, LocalDateTime fin);

    List<RegistroActividad> findByUsuarioIdAndTimestampBetween(Long usuarioId, LocalDateTime inicio, LocalDateTime fin);

    List<RegistroActividad> findByTipoActividadAndTimestampBetween(TipoActividad tipoActividad, LocalDateTime inicio, LocalDateTime fin);
}
