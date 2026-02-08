package com.paeldav.backend.presentation.controller;

import com.paeldav.backend.application.dto.vuelo.*;
import com.paeldav.backend.application.service.base.VueloService;
import com.paeldav.backend.domain.enums.EstadoVuelo;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión de agendamiento de vuelos.
 * Proporciona endpoints para crear, consultar, actualizar y cancelar vuelos.
 */
@RestController
@RequestMapping("/api/vuelos")
@RequiredArgsConstructor
public class VueloController {

    private final VueloService vueloService;

    /**
     * Crea un nuevo vuelo.
     *
     * @param vueloCreateDTO DTO con los datos del nuevo vuelo
     * @return ResponseEntity con el vuelo creado (201 Created)
     */
    @PostMapping
    public ResponseEntity<VueloDTO> crearVuelo(
            @Valid @RequestBody VueloCreateDTO vueloCreateDTO) {
        VueloDTO vueloDTO = vueloService.crearVuelo(vueloCreateDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(vueloDTO);
    }

    /**
     * Obtiene un vuelo por su ID.
     *
     * @param id ID del vuelo
     * @return ResponseEntity con los datos del vuelo
     */
    @GetMapping("/{id}")
    public ResponseEntity<VueloDTO> obtenerVueloPorId(@PathVariable Long id) {
        VueloDTO vueloDTO = vueloService.obtenerVueloPorId(id);
        return ResponseEntity.ok(vueloDTO);
    }

    /**
     * Obtiene todos los vuelos del sistema.
     *
     * @return ResponseEntity con la lista de todos los vuelos
     */
    @GetMapping
    public ResponseEntity<List<VueloDTO>> obtenerTodosVuelos() {
        List<VueloDTO> vuelos = vueloService.obtenerTodosVuelos();
        return ResponseEntity.ok(vuelos);
    }

    /**
     * Actualiza los datos de un vuelo existente.
     *
     * @param id ID del vuelo a actualizar
     * @param vueloUpdateDTO DTO con los datos a actualizar
     * @return ResponseEntity con los datos del vuelo actualizado
     */
    @PutMapping("/{id}")
    public ResponseEntity<VueloDTO> actualizarVuelo(
            @PathVariable Long id,
            @Valid @RequestBody VueloUpdateDTO vueloUpdateDTO) {
        VueloDTO vueloDTO = vueloService.actualizarVuelo(id, vueloUpdateDTO);
        return ResponseEntity.ok(vueloDTO);
    }

    /**
     * Cancela un vuelo existente.
     *
     * @param id ID del vuelo a cancelar
     * @return ResponseEntity sin contenido (204 No Content)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelarVuelo(@PathVariable Long id) {
        vueloService.cancelarVuelo(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Cambia el estado de un vuelo.
     *
     * @param id ID del vuelo
     * @param nuevoEstado nuevo estado para el vuelo
     * @return ResponseEntity con los datos del vuelo con el estado actualizado
     */
    @PatchMapping("/{id}/estado")
    public ResponseEntity<VueloDTO> cambiarEstadoVuelo(
            @PathVariable Long id,
            @RequestParam EstadoVuelo nuevoEstado) {
        VueloDTO vueloDTO = vueloService.cambiarEstadoVuelo(id, nuevoEstado);
        return ResponseEntity.ok(vueloDTO);
    }

    /**
     * Obtiene todos los vuelos en un estado específico.
     *
     * @param estado estado de vuelo a filtrar
     * @return ResponseEntity con la lista de vuelos en el estado especificado
     */
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<VueloDTO>> obtenerVuelosPorEstado(
            @PathVariable EstadoVuelo estado) {
        List<VueloDTO> vuelos = vueloService.obtenerVuelosPorEstado(estado);
        return ResponseEntity.ok(vuelos);
    }

    // ==================== APROBACIÓN Y RECHAZO ====================

    /**
     * Aprueba una solicitud de vuelo.
     *
     * @param id ID del vuelo a aprobar
     * @param dto DTO con datos de aprobación (opcional)
     * @return ResponseEntity con el vuelo aprobado
     */
    @PostMapping("/{id}/aprobar")
    public ResponseEntity<VueloDTO> aprobarSolicitud(
            @PathVariable Long id,
            @RequestBody(required = false) SolicitudAprobacionDTO dto) {
        VueloDTO vueloDTO = vueloService.aprobarSolicitud(id, dto);
        return ResponseEntity.ok(vueloDTO);
    }

    /**
     * Rechaza una solicitud de vuelo.
     *
     * @param id ID del vuelo a rechazar
     * @param dto DTO con motivo de rechazo (obligatorio)
     * @return ResponseEntity con el vuelo rechazado
     */
    @PostMapping("/{id}/rechazar")
    public ResponseEntity<VueloDTO> rechazarSolicitud(
            @PathVariable Long id,
            @Valid @RequestBody SolicitudRechazoDTO dto) {
        VueloDTO vueloDTO = vueloService.rechazarSolicitud(id, dto);
        return ResponseEntity.ok(vueloDTO);
    }

    // ==================== ASIGNACIÓN DE RECURSOS ====================

    /**
     * Asigna una aeronave a un vuelo.
     *
     * @param id ID del vuelo
     * @param dto DTO con ID de aeronave a asignar
     * @return ResponseEntity con el vuelo actualizado
     */
    @PutMapping("/{id}/aeronave")
    public ResponseEntity<VueloDTO> asignarAeronave(
            @PathVariable Long id,
            @Valid @RequestBody AsignacionAeronaveDTO dto) {
        VueloDTO vueloDTO = vueloService.asignarAeronave(id, dto);
        return ResponseEntity.ok(vueloDTO);
    }

    /**
     * Asigna tripulación a un vuelo.
     *
     * @param id ID del vuelo
     * @param dto DTO con lista de IDs de tripulantes
     * @return ResponseEntity con el vuelo actualizado
     */
    @PutMapping("/{id}/tripulacion")
    public ResponseEntity<VueloDTO> asignarTripulacion(
            @PathVariable Long id,
            @Valid @RequestBody AsignacionTripulacionDTO dto) {
        VueloDTO vueloDTO = vueloService.asignarTripulacion(id, dto);
        return ResponseEntity.ok(vueloDTO);
    }

    // ==================== HISTORIAL Y CONSULTAS ====================

    /**
     * Obtiene el historial de cambios de un vuelo.
     *
     * @param id ID del vuelo
     * @return ResponseEntity con el historial del vuelo
     */
    @GetMapping("/{id}/historial")
    public ResponseEntity<List<HistorialVueloDTO>> obtenerHistorialVuelo(
            @PathVariable Long id) {
        List<HistorialVueloDTO> historial = vueloService.obtenerHistorialVuelo(id);
        return ResponseEntity.ok(historial);
    }

    /**
     * Obtiene todos los vuelos de un usuario específico.
     *
     * @param usuarioId ID del usuario
     * @return ResponseEntity con la lista de vuelos del usuario
     */
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<VueloDTO>> obtenerVuelosPorUsuario(
            @PathVariable Long usuarioId) {
        List<VueloDTO> vuelos = vueloService.obtenerVuelosPorUsuario(usuarioId);
        return ResponseEntity.ok(vuelos);
    }
}
