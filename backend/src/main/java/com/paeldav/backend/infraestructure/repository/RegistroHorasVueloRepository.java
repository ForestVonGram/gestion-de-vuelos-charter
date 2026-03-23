package com.paeldav.backend.infraestructure.repository;

import com.paeldav.backend.domain.entity.RegistroHorasVuelo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositorio JPA para la entidad RegistroHorasVuelo.
 * Proporciona métodos para acceder y manipular registros de horas de vuelo en la base de datos.
 */
@Repository
public interface RegistroHorasVueloRepository extends JpaRepository<RegistroHorasVuelo, Long> {

    /**
     * Obtiene todos los registros de horas para un tripulante específico.
     * @param tripulanteId ID del tripulante
     * @return lista de registros de horas de ese tripulante
     */
    List<RegistroHorasVuelo> findByTripulanteId(Long tripulanteId);

    /**
     * Obtiene todos los registros de horas para un vuelo específico.
     * @param vueloId ID del vuelo
     * @return lista de registros de horas de ese vuelo
     */
    List<RegistroHorasVuelo> findByVueloId(Long vueloId);

    /**
     * Obtiene todos los registros de horas según su estado de aprobación.
     * @param aprobado true para registros aprobados, false para pendientes
     * @return lista de registros con ese estado
     */
    List<RegistroHorasVuelo> findByAprobado(Boolean aprobado);

    /**
     * Calcula el total de horas voladas por un tripulante.
     * @param tripulanteId ID del tripulante
     * @return suma total de horas voladas
     */
    @Query("SELECT SUM(r.horasVoladas) FROM RegistroHorasVuelo r WHERE r.tripulante.id = :tripulanteId")
    Double sumHorasByTripulanteId(@Param("tripulanteId") Long tripulanteId);

    /**
     * Calcula el total de horas voladas por un tripulante en un período específico.
     * @param tripulanteId ID del tripulante
     * @param inicio fecha y hora de inicio del período
     * @param fin fecha y hora de fin del período
     * @return suma total de horas voladas en ese período
     */
    @Query("SELECT SUM(r.horasVoladas) FROM RegistroHorasVuelo r WHERE r.tripulante.id = :tripulanteId " +
            "AND r.fechaRegistro BETWEEN :inicio AND :fin")
    Double sumHorasByTripulanteIdAndFechaBetween(
            @Param("tripulanteId") Long tripulanteId,
            @Param("inicio") LocalDateTime inicio,
            @Param("fin") LocalDateTime fin);

    /**
     * Obtiene los registros de horas de un tripulante filtrados por estado de aprobación.
     * @param tripulanteId ID del tripulante
     * @param aprobado estado de aprobación
     * @return lista de registros que cumplen ambos criterios
     */
    List<RegistroHorasVuelo> findByTripulanteIdAndAprobado(Long tripulanteId, Boolean aprobado);

    /**
     * Encuentra registros de horas en un rango de fechas.
     * @param inicio fecha y hora de inicio del rango
     * @param fin fecha y hora de fin del rango
     * @return lista de registros en ese período
     */
    @Query("SELECT r FROM RegistroHorasVuelo r WHERE r.fechaRegistro BETWEEN :inicio AND :fin")
    List<RegistroHorasVuelo> findByFechaRegistroBetween(
            @Param("inicio") LocalDateTime inicio,
            @Param("fin") LocalDateTime fin);

    /**
     * Encuentra registros no aprobados.
     * @return lista de registros pendientes de aprobación
     */
    List<RegistroHorasVuelo> findByAprobadoFalse();
}