package com.paeldav.backend.infraestructure.repository;

import com.paeldav.backend.domain.entity.Incidencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositorio JPA para la entidad Incidencia.
 * Proporciona métodos para acceder y manipular datos de incidencias en la base de datos.
 */
@Repository
public interface IncidenciaRepository extends JpaRepository<Incidencia, Long> {

    /**
     * Obtiene todas las incidencias asociadas a un vuelo específico.
     * @param vueloId ID del vuelo
     * @return lista de incidencias de ese vuelo
     */
    List<Incidencia> findByVueloId(Long vueloId);

    /**
     * Obtiene todas las incidencias según su estado de resolución.
     * @param resuelta true para incidencias resueltas, false para pendientes
     * @return lista de incidencias con ese estado
     */
    List<Incidencia> findByResuelta(Boolean resuelta);

    /**
     * Obtiene todas las incidencias reportadas en un rango de fechas.
     * @param inicio fecha y hora de inicio del rango
     * @param fin fecha y hora de fin del rango
     * @return lista de incidencias reportadas en ese período
     */
    @Query("SELECT i FROM Incidencia i WHERE i.fechaReporte BETWEEN :inicio AND :fin")
    List<Incidencia> findByFechaReporteBetween(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);

    /**
     * Obtiene todas las incidencias pendientes de resolver, ordenadas por fecha de reporte descendente.
     * @return lista de incidencias no resueltas, de más recientes a más antiguas
     */
    @Query("SELECT i FROM Incidencia i WHERE i.resuelta = false ORDER BY i.fechaReporte DESC")
    List<Incidencia> findPendientes();
}