package com.paeldav.backend.infraestructure.repository;

import com.paeldav.backend.domain.entity.Mantenimiento;
import com.paeldav.backend.domain.enums.TipoMantenimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository // Marca esta interfaz como repositorio de Spring
public interface MantenimientoRepository extends JpaRepository<Mantenimiento, Long> {

    // Obtiene todos los mantenimientos asociados a una aeronave
    List<Mantenimiento> findByAeronaveId(Long aeronaveId);

    // Obtiene mantenimientos según el tipo de mantenimiento
    List<Mantenimiento> findByTipo(TipoMantenimiento tipo);

    // Obtiene mantenimientos de una aeronave filtrados por tipo
    List<Mantenimiento> findByAeronaveIdAndTipo(Long aeronaveId, TipoMantenimiento tipo);

    // Consulta para obtener mantenimientos cuyo inicio esté dentro de un rango de fechas
    @Query("SELECT m FROM Mantenimiento m WHERE m.fechaInicio BETWEEN :inicio AND :fin")
    List<Mantenimiento> findByFechaInicioBetween(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);

    // Obtiene los últimos mantenimientos de una aeronave ordenados por fecha de inicio descendente
    @Query("SELECT m FROM Mantenimiento m WHERE m.aeronave.id = :aeronaveId ORDER BY m.fechaInicio DESC")
    List<Mantenimiento> findUltimosMantenimientos(@Param("aeronaveId") Long aeronaveId);

    // Obtiene mantenimientos asignados a un responsable específico
    List<Mantenimiento> findByResponsableId(Long responsableId);

    // Obtiene mantenimientos filtrando por si están completados o no
    List<Mantenimiento> findByCompletado(Boolean completado);

    // Obtiene mantenimientos vencidos de una aeronave (no completados y con fecha pasada)
    @Query("SELECT m FROM Mantenimiento m WHERE m.aeronave.id = :aeronaveId AND m.completado = false AND m.fechaInicio < CURRENT_TIMESTAMP ORDER BY m.fechaInicio ASC")
    List<Mantenimiento> findMantenimientosVencidosPorAeronave(@Param("aeronaveId") Long aeronaveId);
}