package com.paeldav.backend.application.service.base;

import com.paeldav.backend.application.dto.aeronave.AeronaveDTO;
import com.paeldav.backend.application.dto.disponibilidad.DisponibilidadAeronaveDTO;
import com.paeldav.backend.application.dto.disponibilidad.DisponibilidadTripulanteDTO;
import com.paeldav.backend.application.dto.disponibilidad.ResultadoValidacionDTO;
import com.paeldav.backend.application.dto.tripulante.TripulanteDTO;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Servicio para gestionar la disponibilidad operativa de aeronaves y tripulación.
 * Permite consultar disponibilidad y validar conflictos de agenda.
 */
public interface DisponibilidadOperativaService {

    /**
     * Consulta la disponibilidad de una aeronave en un rango de fechas.
     *
     * @param aeronaveId ID de la aeronave
     * @param fechaInicio inicio del rango a consultar
     * @param fechaFin fin del rango a consultar
     * @return DTO con el estado de disponibilidad y conflictos si existen
     */
    DisponibilidadAeronaveDTO consultarDisponibilidadAeronave(
            Long aeronaveId,
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin);

    /**
     * Consulta la disponibilidad de un tripulante en un rango de fechas.
     *
     * @param tripulanteId ID del tripulante
     * @param fechaInicio inicio del rango a consultar
     * @param fechaFin fin del rango a consultar
     * @return DTO con el estado de disponibilidad y conflictos si existen
     */
    DisponibilidadTripulanteDTO consultarDisponibilidadTripulante(
            Long tripulanteId,
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin);

    /**
     * Obtiene todas las aeronaves disponibles para un rango de fechas.
     *
     * @param fechaInicio inicio del rango
     * @param fechaFin fin del rango
     * @param capacidadMinima capacidad mínima de pasajeros requerida (null para ignorar)
     * @return lista de aeronaves disponibles
     */
    List<AeronaveDTO> consultarAeronavesDisponibles(
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin,
            Integer capacidadMinima);

    /**
     * Obtiene todos los tripulantes disponibles para un rango de fechas.
     *
     * @param fechaInicio inicio del rango
     * @param fechaFin fin del rango
     * @param soloPilotos si es true, solo retorna pilotos
     * @return lista de tripulantes disponibles
     */
    List<TripulanteDTO> consultarTripulantesDisponibles(
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin,
            Boolean soloPilotos);

    /**
     * Valida si existe algún conflicto de agenda para una aeronave y tripulación
     * en un rango de fechas específico.
     *
     * @param aeronaveId ID de la aeronave (puede ser null si no se asigna aeronave)
     * @param tripulantesIds lista de IDs de tripulantes a validar
     * @param fechaInicio inicio del rango
     * @param fechaFin fin del rango
     * @return resultado de la validación con lista de conflictos si existen
     */
    ResultadoValidacionDTO validarConflictosAgenda(
            Long aeronaveId,
            List<Long> tripulantesIds,
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin);

    /**
     * Valida conflictos de agenda y lanza excepción si hay conflictos.
     * Útil para validar antes de asignar recursos a un vuelo.
     *
     * @param aeronaveId ID de la aeronave
     * @param tripulantesIds lista de IDs de tripulantes
     * @param fechaInicio inicio del rango
     * @param fechaFin fin del rango
     * @throws com.paeldav.backend.exception.ConflictoDisponibilidadException si hay conflictos
     */
    void validarYLanzarSiHayConflictos(
            Long aeronaveId,
            List<Long> tripulantesIds,
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin);
}
