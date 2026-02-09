package com.paeldav.backend.presentation.controller;

import com.paeldav.backend.application.dto.mantenimiento.MantenimientoCreateDTO;
import com.paeldav.backend.application.dto.mantenimiento.MantenimientoDTO;
import com.paeldav.backend.application.service.base.MantenimientoService;
import com.paeldav.backend.domain.enums.TipoMantenimiento;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Controlador REST para la gestión de mantenimiento preventivo y correctivo de aeronaves.
 * Proporciona endpoints para registro, consulta y finalización de mantenimientos.
 */
@RestController
@RequestMapping("/api/mantenimientos")
@RequiredArgsConstructor
public class MantenimientoController {

    private final MantenimientoService mantenimientoService;

    /**
     * Registra un nuevo mantenimiento (preventivo o correctivo) para una aeronave.
     *
     * @param mantenimientoCreateDTO DTO con los datos del mantenimiento a registrar
     * @return ResponseEntity con el mantenimiento registrado (201 Created)
     */
    @PostMapping
    public ResponseEntity<MantenimientoDTO> registrarMantenimiento(
            @Valid @RequestBody MantenimientoCreateDTO mantenimientoCreateDTO) {
        MantenimientoDTO mantenimientoDTO = mantenimientoService
                .registrarMantenimiento(mantenimientoCreateDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(mantenimientoDTO);
    }

    /**
     * Obtiene un mantenimiento por su ID.
     *
     * @param id ID del mantenimiento
     * @return ResponseEntity con los datos del mantenimiento
     */
    @GetMapping("/{id}")
    public ResponseEntity<MantenimientoDTO> obtenerMantenimientoPorId(@PathVariable Long id) {
        MantenimientoDTO mantenimientoDTO = mantenimientoService.obtenerMantenimientoPorId(id);
        return ResponseEntity.ok(mantenimientoDTO);
    }

    /**
     * Obtiene todos los mantenimientos registrados en el sistema.
     *
     * @return ResponseEntity con la lista de todos los mantenimientos
     */
    @GetMapping
    public ResponseEntity<List<MantenimientoDTO>> obtenerTodosMantenimientos() {
        List<MantenimientoDTO> mantenimientos = mantenimientoService.obtenerTodosMantenimientos();
        return ResponseEntity.ok(mantenimientos);
    }

    /**
     * Obtiene todos los mantenimientos de una aeronave específica.
     *
     * @param aeronaveId ID de la aeronave
     * @return ResponseEntity con la lista de mantenimientos de la aeronave
     */
    @GetMapping("/aeronave/{aeronaveId}")
    public ResponseEntity<List<MantenimientoDTO>> obtenerMantenimientosPorAeronave(
            @PathVariable Long aeronaveId) {
        List<MantenimientoDTO> mantenimientos = mantenimientoService
                .obtenerMantenimientosPorAeronave(aeronaveId);
        return ResponseEntity.ok(mantenimientos);
    }

    /**
     * Obtiene los mantenimientos pendientes (no completados) de una aeronave.
     *
     * @param aeronaveId ID de la aeronave
     * @return ResponseEntity con la lista de mantenimientos pendientes
     */
    @GetMapping("/aeronave/{aeronaveId}/pendientes")
    public ResponseEntity<List<MantenimientoDTO>> obtenerMantenimientosPendientesPorAeronave(
            @PathVariable Long aeronaveId) {
        List<MantenimientoDTO> mantenimientos = mantenimientoService
                .obtenerMantenimientosPendientesPorAeronave(aeronaveId);
        return ResponseEntity.ok(mantenimientos);
    }

    /**
     * Obtiene todos los mantenimientos de un tipo específico (PREVENTIVO, CORRECTIVO, etc.).
     *
     * @param tipo tipo de mantenimiento
     * @return ResponseEntity con la lista de mantenimientos del tipo especificado
     */
    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<List<MantenimientoDTO>> obtenerMantenimientosPorTipo(
            @PathVariable TipoMantenimiento tipo) {
        List<MantenimientoDTO> mantenimientos = mantenimientoService
                .obtenerMantenimientosPorTipo(tipo);
        return ResponseEntity.ok(mantenimientos);
    }

    /**
     * Obtiene los mantenimientos de una aeronave de un tipo específico.
     *
     * @param aeronaveId ID de la aeronave
     * @param tipo tipo de mantenimiento
     * @return ResponseEntity con la lista de mantenimientos
     */
    @GetMapping("/aeronave/{aeronaveId}/tipo/{tipo}")
    public ResponseEntity<List<MantenimientoDTO>> obtenerMantenimientosPorAeronaveYTipo(
            @PathVariable Long aeronaveId,
            @PathVariable TipoMantenimiento tipo) {
        List<MantenimientoDTO> mantenimientos = mantenimientoService
                .obtenerMantenimientosPorAeronaveYTipo(aeronaveId, tipo);
        return ResponseEntity.ok(mantenimientos);
    }

    /**
     * Obtiene los mantenimientos realizados en un rango de fechas.
     *
     * @param inicio fecha de inicio del rango
     * @param fin fecha de fin del rango
     * @return ResponseEntity con la lista de mantenimientos en el rango
     */
    @GetMapping("/fecha")
    public ResponseEntity<List<MantenimientoDTO>> obtenerMantenimientosPorFecha(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {
        List<MantenimientoDTO> mantenimientos = mantenimientoService
                .obtenerMantenimientosPorFecha(inicio, fin);
        return ResponseEntity.ok(mantenimientos);
    }

    /**
     * Marca un mantenimiento como completado.
     *
     * @param id ID del mantenimiento a completar
     * @param fechaFin fecha de finalización del mantenimiento
     * @param observaciones observaciones finales del mantenimiento
     * @return ResponseEntity con el mantenimiento actualizado
     */
    @PatchMapping("/{id}/completar")
    public ResponseEntity<MantenimientoDTO> completarMantenimiento(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin,
            @RequestParam(required = false) String observaciones) {
        MantenimientoDTO mantenimientoDTO = mantenimientoService
                .completarMantenimiento(id, fechaFin, observaciones);
        return ResponseEntity.ok(mantenimientoDTO);
    }

    /**
     * Obtiene los últimos mantenimientos realizados a una aeronave.
     *
     * @param aeronaveId ID de la aeronave
     * @return ResponseEntity con la lista de últimos mantenimientos
     */
    @GetMapping("/aeronave/{aeronaveId}/ultimos")
    public ResponseEntity<List<MantenimientoDTO>> obtenerUltimosMantenimientos(
            @PathVariable Long aeronaveId) {
        List<MantenimientoDTO> mantenimientos = mantenimientoService
                .obtenerUltimosMantenimientos(aeronaveId);
        return ResponseEntity.ok(mantenimientos);
    }

    /**
     * Obtiene todos los mantenimientos pendientes (no completados) del sistema.
     *
     * @return ResponseEntity con la lista de mantenimientos pendientes
     */
    @GetMapping("/pendientes")
    public ResponseEntity<List<MantenimientoDTO>> obtenerMantenimientosPendientes() {
        List<MantenimientoDTO> mantenimientos = mantenimientoService
                .obtenerMantenimientosPendientes();
        return ResponseEntity.ok(mantenimientos);
    }

    /**
     * Obtiene todos los mantenimientos asignados a un responsable específico.
     *
     * @param responsableId ID del responsable (usuario)
     * @return ResponseEntity con la lista de mantenimientos asignados
     */
    @GetMapping("/responsable/{responsableId}")
    public ResponseEntity<List<MantenimientoDTO>> obtenerMantenimientosPorResponsable(
            @PathVariable Long responsableId) {
        List<MantenimientoDTO> mantenimientos = mantenimientoService
                .obtenerMantenimientosPorResponsable(responsableId);
        return ResponseEntity.ok(mantenimientos);
    }
}
