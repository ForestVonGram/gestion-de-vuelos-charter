package com.paeldav.backend.presentation.controller;

import com.paeldav.backend.application.dto.incidencia.IncidenciaCreateDTO;
import com.paeldav.backend.application.dto.incidencia.IncidenciaDTO;
import com.paeldav.backend.application.service.base.IncidenciaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Controlador REST para la gestión de incidencias técnicas reportadas durante vuelos.
 * Proporciona endpoints para reportar, consultar y resolver incidencias.
 */
@RestController
@RequestMapping("/api/incidencias")
@RequiredArgsConstructor
public class IncidenciaController {

    private final IncidenciaService incidenciaService;

    /**
     * Reporta una nueva incidencia técnica durante un vuelo.
     *
     * @param incidenciaCreateDTO DTO con los datos de la incidencia a reportar
     * @return ResponseEntity con la incidencia reportada (201 Created)
     */
    @PostMapping
    public ResponseEntity<IncidenciaDTO> reportarIncidencia(
            @Valid @RequestBody IncidenciaCreateDTO incidenciaCreateDTO) {
        IncidenciaDTO incidenciaDTO = incidenciaService.reportarIncidencia(incidenciaCreateDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(incidenciaDTO);
    }

    /**
     * Obtiene una incidencia por su ID.
     *
     * @param id ID de la incidencia
     * @return ResponseEntity con los datos de la incidencia
     */
    @GetMapping("/{id}")
    public ResponseEntity<IncidenciaDTO> obtenerIncidenciaPorId(@PathVariable Long id) {
        IncidenciaDTO incidenciaDTO = incidenciaService.obtenerIncidenciaPorId(id);
        return ResponseEntity.ok(incidenciaDTO);
    }

    /**
     * Obtiene todas las incidencias registradas en el sistema.
     *
     * @return ResponseEntity con la lista de todas las incidencias
     */
    @GetMapping
    public ResponseEntity<List<IncidenciaDTO>> obtenerTodasIncidencias() {
        List<IncidenciaDTO> incidencias = incidenciaService.obtenerTodasIncidencias();
        return ResponseEntity.ok(incidencias);
    }

    /**
     * Obtiene todas las incidencias reportadas en un vuelo específico.
     *
     * @param vueloId ID del vuelo
     * @return ResponseEntity con la lista de incidencias del vuelo
     */
    @GetMapping("/vuelo/{vueloId}")
    public ResponseEntity<List<IncidenciaDTO>> obtenerIncidenciasPorVuelo(
            @PathVariable Long vueloId) {
        List<IncidenciaDTO> incidencias = incidenciaService.obtenerIncidenciasPorVuelo(vueloId);
        return ResponseEntity.ok(incidencias);
    }

    /**
     * Obtiene todas las incidencias pendientes (no resueltas) del sistema.
     *
     * @return ResponseEntity con la lista de incidencias pendientes
     */
    @GetMapping("/pendientes")
    public ResponseEntity<List<IncidenciaDTO>> obtenerIncidenciasPendientes() {
        List<IncidenciaDTO> incidencias = incidenciaService.obtenerIncidenciasPendientes();
        return ResponseEntity.ok(incidencias);
    }

    /**
     * Obtiene todas las incidencias no resueltas del sistema.
     *
     * @return ResponseEntity con la lista de incidencias no resueltas
     */
    @GetMapping("/no-resueltas")
    public ResponseEntity<List<IncidenciaDTO>> obtenerIncidenciasNoResueltas() {
        List<IncidenciaDTO> incidencias = incidenciaService.obtenerIncidenciasNoResueltas();
        return ResponseEntity.ok(incidencias);
    }

    /**
     * Obtiene todas las incidencias de un nivel de gravedad específico.
     *
     * @param gravedad nivel de gravedad (BAJA, MEDIA, ALTA, CRITICA)
     * @return ResponseEntity con la lista de incidencias del nivel especificado
     */
    @GetMapping("/gravedad/{gravedad}")
    public ResponseEntity<List<IncidenciaDTO>> obtenerIncidenciasPorGravedad(
            @PathVariable String gravedad) {
        List<IncidenciaDTO> incidencias = incidenciaService.obtenerIncidenciasPorGravedad(gravedad);
        return ResponseEntity.ok(incidencias);
    }

    /**
     * Obtiene todas las incidencias reportadas en un rango de fechas.
     *
     * @param inicio fecha de inicio del rango
     * @param fin fecha de fin del rango
     * @return ResponseEntity con la lista de incidencias en el rango
     */
    @GetMapping("/fecha")
    public ResponseEntity<List<IncidenciaDTO>> obtenerIncidenciasPorFecha(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {
        List<IncidenciaDTO> incidencias = incidenciaService.obtenerIncidenciasPorFecha(inicio, fin);
        return ResponseEntity.ok(incidencias);
    }

    /**
     * Marca una incidencia como resuelta y registra las acciones tomadas.
     *
     * @param id ID de la incidencia a resolver
     * @param accionesTomadas descripción de las acciones tomadas para resolver
     * @return ResponseEntity con la incidencia actualizada
     */
    @PatchMapping("/{id}/resolver")
    public ResponseEntity<IncidenciaDTO> resolverIncidencia(
            @PathVariable Long id,
            @RequestParam(required = false) String accionesTomadas) {
        IncidenciaDTO incidenciaDTO = incidenciaService.resolverIncidencia(id, accionesTomadas);
        return ResponseEntity.ok(incidenciaDTO);
    }
}
