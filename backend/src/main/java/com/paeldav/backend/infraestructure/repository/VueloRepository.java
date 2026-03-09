package com.paeldav.backend.infraestructure.repository;

import com.paeldav.backend.domain.entity.Vuelo;
import com.paeldav.backend.domain.enums.EstadoVuelo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositorio para la gestión de acceso a datos de la entidad Vuelo.
 * Maneja la programación, asignación de recursos (aeronaves/tripulación) y detección de conflictos de agenda.
 */
@Repository
public interface VueloRepository extends JpaRepository<Vuelo, Long> {

    // Filtra el listado de vuelos según su estado actual (ej. PROGRAMADO, EN_CURSO, COMPLETADO, CANCELADO)
    List<Vuelo> findByEstado(EstadoVuelo estado);

    // Recupera todos los vuelos creados o gestionados por un usuario del sistema en particular
    List<Vuelo> findByUsuarioId(Long usuarioId);

    // Obtiene el historial completo de vuelos (pasados y futuros) asignados a una aeronave específica
    List<Vuelo> findByAeronaveId(Long aeronaveId);

    // Busca los vuelos cuya hora de despegue planeada caiga exactamente dentro de un rango de fechas y horas
    @Query("SELECT v FROM Vuelo v WHERE v.fechaSalidaProgramada BETWEEN :inicio AND :fin")
    List<Vuelo> findByFechaSalidaBetween(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);

    // Busca vuelos que salgan desde o lleguen hacia un aeropuerto/ciudad en específico
    @Query("SELECT v FROM Vuelo v WHERE v.origen = :origen OR v.destino = :destino")
    List<Vuelo> findByOrigenOrDestino(@Param("origen") String origen, @Param("destino") String destino);

    // Hace un JOIN con la tabla de tripulación para encontrar todos los vuelos en los que participa un tripulante
    @Query("SELECT v FROM Vuelo v JOIN v.tripulacion t WHERE t.id = :tripulanteId")
    List<Vuelo> findByTripulanteId(@Param("tripulanteId") Long tripulanteId);

    // Permite filtrar vuelos usando múltiples estados a la vez (ej. traer los vuelos PROGRAMADOS y los EN_CURSO)
    List<Vuelo> findByEstadoIn(List<EstadoVuelo> estados);

    /**
     * Busca vuelos de una aeronave que se solapan con un rango de tiempo.
     * Detecta conflictos cuando: fechaSalida < finRango AND fechaLlegada > inicioRango
     */
    // Validación crítica para la asignación de flota: Evita que un avión tenga doble reserva en el mismo horario
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
    // Validación crítica de recursos humanos: Evita asignar a un piloto o azafata a dos vuelos simultáneos
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
    // Nota técnica: Esta consulta retorna los IDs de las aeronaves que SÍ están ocupadas (tienen colisión)
    // en ese rango. Usualmente esto se utiliza luego en el servicio con un "NOT IN" para obtener las disponibles.
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
    // Devuelve la lista de tripulantes ocupados en una franja de tiempo para filtrarlos de la lista de disponibles
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
    // Consulta nativa SQL (PostgreSQL): Calcula en base de datos las horas de vuelo reales sumando
    // la diferencia (EPOCH) entre la llegada y la salida real. Ideal para reportes de rendimiento de la flota.
    @Query(value = "SELECT " +
            "    a.id AS aeronaveId, " +
            "    a.matricula AS matricula, " +
            "    COUNT(v.id) AS totalVuelos, " +
            "    COALESCE(SUM(EXTRACT(EPOCH FROM (v.fecha_llegada_real - v.fecha_salida_real))/3600), 0) AS totalHoras " +
            "FROM vuelo v " +
            "JOIN aeronave a ON v.aeronave_id = a.id " +
            "WHERE v.fecha_salida_real BETWEEN :inicio AND :fin " +
            "AND v.aeronave_id IS NOT NULL " +
            "GROUP BY a.id, a.matricula",
            nativeQuery = true)
    List<Object[]> findEstadisticasPorAeronave(
            @Param("inicio") LocalDateTime inicio,
            @Param("fin") LocalDateTime fin);

    /**
     * Encuentra vuelos de una aeronave en un rango de fechas con fecha real.
     */
    // Utilizado para obtener el detalle de los vuelos ya ejecutados (históricos) de un avión en un periodo específico
    List<Vuelo> findByAeronaveIdAndFechaSalidaRealBetween(
            Long aeronaveId,
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin);
}