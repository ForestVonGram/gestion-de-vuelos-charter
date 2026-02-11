package com.paeldav.backend.infraestructure.repository;

import com.paeldav.backend.domain.entity.Reporte;
import com.paeldav.backend.domain.enums.TipoReporte;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad Reporte.
 * Proporciona métodos de acceso a datos para reportes operativos.
 */
@Repository
public interface ReporteRepository extends JpaRepository<Reporte, Long> {

    /**
     * Obtiene reportes filtrados por tipo.
     */
    List<Reporte> findByTipo(TipoReporte tipo);

    /**
     * Obtiene reportes filtrados por tipo con paginación.
     */
    Page<Reporte> findByTipo(TipoReporte tipo, Pageable pageable);

    /**
     * Obtiene reportes generados por un usuario específico.
     */
    List<Reporte> findByGeneradoPorId(Long usuarioId);

    /**
     * Obtiene reportes generados en un rango de fechas.
     */
    @Query("SELECT r FROM Reporte r WHERE r.fechaGeneracion BETWEEN :inicio AND :fin")
    List<Reporte> findByFechaGeneracionBetween(
            @Param("inicio") LocalDateTime inicio,
            @Param("fin") LocalDateTime fin);

    /**
     * Obtiene reportes por tipo y rango de fechas.
     */
    @Query("SELECT r FROM Reporte r WHERE r.tipo = :tipo AND r.fechaGeneracion BETWEEN :inicio AND :fin")
    List<Reporte> findByTipoAndFechaGeneracionBetween(
            @Param("tipo") TipoReporte tipo,
            @Param("inicio") LocalDateTime inicio,
            @Param("fin") LocalDateTime fin);

    /**
     * Obtiene reportes por tipo, usuario y rango de fechas.
     */
    @Query("SELECT r FROM Reporte r WHERE r.tipo = :tipo AND r.generadoPor.id = :usuarioId AND r.fechaGeneracion BETWEEN :inicio AND :fin")
    List<Reporte> findByTipoAndGeneradoPorAndFechaBetween(
            @Param("tipo") TipoReporte tipo,
            @Param("usuarioId") Long usuarioId,
            @Param("inicio") LocalDateTime inicio,
            @Param("fin") LocalDateTime fin);

    /**
     * Obtiene el último reporte de un tipo específico.
     */
    Optional<Reporte> findFirstByTipoOrderByFechaGeneracionDesc(TipoReporte tipo);

    /**
     * Obtiene reportes con paginación y ordenados por fecha descendente.
     */
    Page<Reporte> findAllByOrderByFechaGeneracionDesc(Pageable pageable);

    /**
     * Cuenta reportes por tipo.
     */
    Long countByTipo(TipoReporte tipo);
}
