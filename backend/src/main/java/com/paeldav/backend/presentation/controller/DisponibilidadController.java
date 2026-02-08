package com.paeldav.backend.presentation.controller;

import com.paeldav.backend.application.dto.aeronave.AeronaveDTO;
import com.paeldav.backend.application.dto.disponibilidad.DisponibilidadAeronaveDTO;
import com.paeldav.backend.application.dto.disponibilidad.DisponibilidadTripulanteDTO;
import com.paeldav.backend.application.dto.disponibilidad.ResultadoValidacionDTO;
import com.paeldav.backend.application.dto.tripulante.TripulanteDTO;
import com.paeldav.backend.application.service.base.DisponibilidadOperativaService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Controlador REST para consultar disponibilidad operativa.
 * Proporciona endpoints para verificar disponibilidad de aeronaves y tripulación,
 * y validar conflictos de agenda.
 */
@RestController
@RequestMapping("/api/disponibilidad")
@RequiredArgsConstructor
public class DisponibilidadController {

    private final DisponibilidadOperativaService disponibilidadService;

    /**
     * Consulta la disponibilidad de una aeronave en un rango de fechas.
     *
     * @param aeronaveId ID de la aeronave
     * @param fechaInicio inicio del rango (formato: yyyy-MM-ddTHH:mm:ss)
     * @param fechaFin fin del rango (formato: yyyy-MM-ddTHH:mm:ss)
     * @return disponibilidad de la aeronave con conflictos si existen
     */
    @GetMapping("/aeronave/{aeronaveId}")
    public ResponseEntity<DisponibilidadAeronaveDTO> consultarDisponibilidadAeronave(
            @PathVariable Long aeronaveId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin) {

        DisponibilidadAeronaveDTO resultado = disponibilidadService.consultarDisponibilidadAeronave(
                aeronaveId, fechaInicio, fechaFin);
        return ResponseEntity.ok(resultado);
    }

    /**
     * Consulta la disponibilidad de un tripulante en un rango de fechas.
     *
     * @param tripulanteId ID del tripulante
     * @param fechaInicio inicio del rango
     * @param fechaFin fin del rango
     * @return disponibilidad del tripulante con conflictos si existen
     */
    @GetMapping("/tripulante/{tripulanteId}")
    public ResponseEntity<DisponibilidadTripulanteDTO> consultarDisponibilidadTripulante(
            @PathVariable Long tripulanteId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin) {

        DisponibilidadTripulanteDTO resultado = disponibilidadService.consultarDisponibilidadTripulante(
                tripulanteId, fechaInicio, fechaFin);
        return ResponseEntity.ok(resultado);
    }

    /**
     * Obtiene todas las aeronaves disponibles para un rango de fechas.
     *
     * @param fechaInicio inicio del rango
     * @param fechaFin fin del rango
     * @param capacidadMinima capacidad mínima de pasajeros (opcional)
     * @return lista de aeronaves disponibles
     */
    @GetMapping("/aeronaves-disponibles")
    public ResponseEntity<List<AeronaveDTO>> consultarAeronavesDisponibles(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin,
            @RequestParam(required = false) Integer capacidadMinima) {

        List<AeronaveDTO> aeronaves = disponibilidadService.consultarAeronavesDisponibles(
                fechaInicio, fechaFin, capacidadMinima);
        return ResponseEntity.ok(aeronaves);
    }

    /**
     * Obtiene todos los tripulantes disponibles para un rango de fechas.
     *
     * @param fechaInicio inicio del rango
     * @param fechaFin fin del rango
     * @param soloPilotos si es true, solo retorna pilotos (opcional)
     * @return lista de tripulantes disponibles
     */
    @GetMapping("/tripulantes-disponibles")
    public ResponseEntity<List<TripulanteDTO>> consultarTripulantesDisponibles(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin,
            @RequestParam(required = false) Boolean soloPilotos) {

        List<TripulanteDTO> tripulantes = disponibilidadService.consultarTripulantesDisponibles(
                fechaInicio, fechaFin, soloPilotos);
        return ResponseEntity.ok(tripulantes);
    }

    /**
     * Valida si existe algún conflicto de agenda para una aeronave y tripulación.
     *
     * @param aeronaveId ID de la aeronave (opcional)
     * @param tripulantesIds lista de IDs de tripulantes (opcional)
     * @param fechaInicio inicio del rango
     * @param fechaFin fin del rango
     * @return resultado de la validación con conflictos si existen
     */
    @GetMapping("/validar-conflictos")
    public ResponseEntity<ResultadoValidacionDTO> validarConflictosAgenda(
            @RequestParam(required = false) Long aeronaveId,
            @RequestParam(required = false) List<Long> tripulantesIds,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin) {

        ResultadoValidacionDTO resultado = disponibilidadService.validarConflictosAgenda(
                aeronaveId, tripulantesIds, fechaInicio, fechaFin);
        return ResponseEntity.ok(resultado);
    }
}
