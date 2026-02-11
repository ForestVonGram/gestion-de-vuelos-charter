package com.paeldav.backend.infraestructure.repository;

import com.paeldav.backend.domain.entity.Vuelo;
import com.paeldav.backend.domain.enums.EstadoVuelo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VueloRepository extends JpaRepository<Vuelo, Long> {

    List<Vuelo> findByEstado(EstadoVuelo estado);

    List<Vuelo> findByUsuarioId(Long usuarioId);

    List<Vuelo> findByAeronaveId(Long aeronaveId);

    @Query("SELECT v FROM Vuelo v WHERE v.fechaSalidaProgramada BETWEEN :inicio AND :fin")
    List<Vuelo> findByFechaSalidaBetween(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);

    @Query("SELECT v FROM Vuelo v WHERE v.origen = :origen OR v.destino = :destino")
    List<Vuelo> findByOrigenOrDestino(@Param("origen") String origen, @Param("destino") String destino);

    @Query("SELECT v FROM Vuelo v JOIN v.tripulacion t WHERE t.id = :tripulanteId")
    List<Vuelo> findByTripulanteId(@Param("tripulanteId") Long tripulanteId);

    List<Vuelo> findByEstadoIn(List<EstadoVuelo> estados);

    /**
     * Busca vuelos de una aeronave que se solapan con un rango de tiempo.
     * Detecta conflictos cuando: fechaSalida < finRango AND fechaLlegada > inicioRango
     */
    @Query("SELECT v FROM Vuelo v WHERE v.aeronave.id = :aeronaveId " +
           "AND v.estado IN :estadosActivos " +
           "AND v.fechaSalidaProgramada < :fechaFin " +
           "AND v.fechaLlegadaProgramada > :fechaInicio")
    List<Vuelo> findVuelosEnRangoPorAeronave(
            @Param("aeronaveId") Long aeronaveId,
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin,
            @Param("estadosActivos") List<EstadoVuelo> estadosActivos);

    /**
     * Busca vuelos de un tripulante que se solapan con un rango de tiempo.
     */
    @Query("SELECT v FROM Vuelo v JOIN v.tripulacion t WHERE t.id = :tripulanteId " +
           "AND v.estado IN :estadosActivos " +
           "AND v.fechaSalidaProgramada < :fechaFin " +
           "AND v.fechaLlegadaProgramada > :fechaInicio")
    List<Vuelo> findVuelosEnRangoPorTripulante(
            @Param("tripulanteId") Long tripulanteId,
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin,
            @Param("estadosActivos") List<EstadoVuelo> estadosActivos);

    /**
     * Busca todas las aeronaves que NO tienen vuelos en el rango especificado.
     */
    @Query("SELECT DISTINCT v.aeronave.id FROM Vuelo v WHERE v.estado IN :estadosActivos " +
           "AND v.fechaSalidaProgramada < :fechaFin " +
           "AND v.fechaLlegadaProgramada > :fechaInicio")
    List<Long> findAeronaveIdsConVuelosEnRango(
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin,
            @Param("estadosActivos") List<EstadoVuelo> estadosActivos);

    /**
     * Busca IDs de tripulantes que tienen vuelos en el rango especificado.
     */
    @Query("SELECT DISTINCT t.id FROM Vuelo v JOIN v.tripulacion t WHERE v.estado IN :estadosActivos " +
           "AND v.fechaSalidaProgramada < :fechaFin " +
           "AND v.fechaLlegadaProgramada > :fechaInicio")
    List<Long> findTripulanteIdsConVuelosEnRango(
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin,
            @Param("estadosActivos") List<EstadoVuelo> estadosActivos);

    /**
     * Obtiene estadísticas de vuelos por aeronave en un rango de fechas.
     * Retorna: [aeronaveId, matricula, totalVuelos, horasVuelo]
     */
    @Query("SELECT v.aeronave.id, v.aeronave.matricula, COUNT(v), COALESCE(SUM(CAST(EXTRACT(EPOCH FROM (v.fechaLlegadaReal - v.fechaSalidaReal))/3600 AS DOUBLE)), 0) " +
           "FROM Vuelo v WHERE v.fechaSalidaReal BETWEEN :inicio AND :fin AND v.aeronave IS NOT NULL " +
           "GROUP BY v.aeronave.id, v.aeronave.matricula")
    List<Object[]> findEstadisticasPorAeronave(
            @Param("inicio") LocalDateTime inicio,
            @Param("fin") LocalDateTime fin);

    /**
     * Encuentra vuelos de una aeronave en un rango de fechas con fecha real.
     */
    List<Vuelo> findByAeronaveIdAndFechaSalidaRealBetween(
            Long aeronaveId,
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin);
}
