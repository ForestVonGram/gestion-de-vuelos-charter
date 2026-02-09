package com.paeldav.backend.presentation.controller;

import com.paeldav.backend.application.dto.repostaje.RepostajeCreateDTO;
import com.paeldav.backend.application.dto.repostaje.RepostajeDTO;
import com.paeldav.backend.application.service.base.RepostajeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Controlador REST para la gestión de repostajes de combustible de aeronaves.
 * Proporciona endpoints para registro, consulta y análisis de repostajes.
 */
@RestController
@RequestMapping("/api/repostajes")
@RequiredArgsConstructor
public class RepostajeController {

    private final RepostajeService repostajeService;

    /**
     * Registra un nuevo repostaje de combustible.
     *
     * @param repostajeCreateDTO DTO con los datos del repostaje a registrar
     * @return ResponseEntity con el repostaje registrado (201 Created)
     */
    @PostMapping
    public ResponseEntity<RepostajeDTO> registrarRepostaje(
            @Valid @RequestBody RepostajeCreateDTO repostajeCreateDTO) {
        RepostajeDTO repostajeDTO = repostajeService.registrarRepostaje(repostajeCreateDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(repostajeDTO);
    }

    /**
     * Obtiene un repostaje por su ID.
     *
     * @param id ID del repostaje
     * @return ResponseEntity con los datos del repostaje
     */
    @GetMapping("/{id}")
    public ResponseEntity<RepostajeDTO> obtenerRepostajePorId(@PathVariable Long id) {
        RepostajeDTO repostajeDTO = repostajeService.obtenerRepostajePorId(id);
        return ResponseEntity.ok(repostajeDTO);
    }

    /**
     * Obtiene todos los repostajes registrados en el sistema.
     *
     * @return ResponseEntity con la lista de todos los repostajes
     */
    @GetMapping
    public ResponseEntity<List<RepostajeDTO>> obtenerTodosRepostajes() {
        List<RepostajeDTO> repostajes = repostajeService.obtenerTodosRepostajes();
        return ResponseEntity.ok(repostajes);
    }

    /**
     * Obtiene todos los repostajes de una aeronave específica.
     *
     * @param aeronaveId ID de la aeronave
     * @return ResponseEntity con la lista de repostajes de la aeronave
     */
    @GetMapping("/aeronave/{aeronaveId}")
    public ResponseEntity<List<RepostajeDTO>> obtenerRepostajePorAeronave(
            @PathVariable Long aeronaveId) {
        List<RepostajeDTO> repostajes = repostajeService.obtenerRepostajePorAeronave(aeronaveId);
        return ResponseEntity.ok(repostajes);
    }

    /**
     * Obtiene todos los repostajes asociados a un vuelo específico.
     *
     * @param vueloId ID del vuelo
     * @return ResponseEntity con la lista de repostajes del vuelo
     */
    @GetMapping("/vuelo/{vueloId}")
    public ResponseEntity<List<RepostajeDTO>> obtenerRepostajePorVuelo(
            @PathVariable Long vueloId) {
        List<RepostajeDTO> repostajes = repostajeService.obtenerRepostajePorVuelo(vueloId);
        return ResponseEntity.ok(repostajes);
    }

    /**
     * Obtiene los repostajes realizados en un rango de fechas.
     *
     * @param inicio fecha de inicio del rango
     * @param fin fecha de fin del rango
     * @return ResponseEntity con la lista de repostajes en el rango
     */
    @GetMapping("/fecha")
    public ResponseEntity<List<RepostajeDTO>> obtenerRepostajePorFecha(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {
        List<RepostajeDTO> repostajes = repostajeService.obtenerRepostajePorFecha(inicio, fin);
        return ResponseEntity.ok(repostajes);
    }

    /**
     * Obtiene los repostajes realizados por un personal específico.
     *
     * @param personalId ID del personal
     * @return ResponseEntity con la lista de repostajes del personal
     */
    @GetMapping("/personal/{personalId}")
    public ResponseEntity<List<RepostajeDTO>> obtenerRepostajePorPersonal(
            @PathVariable Long personalId) {
        List<RepostajeDTO> repostajes = repostajeService.obtenerRepostajePorPersonal(personalId);
        return ResponseEntity.ok(repostajes);
    }

    /**
     * Obtiene los últimos repostajes realizados a una aeronave.
     *
     * @param aeronaveId ID de la aeronave
     * @return ResponseEntity con la lista de últimos repostajes
     */
    @GetMapping("/aeronave/{aeronaveId}/ultimos")
    public ResponseEntity<List<RepostajeDTO>> obtenerUltimosRepostajes(
            @PathVariable Long aeronaveId) {
        List<RepostajeDTO> repostajes = repostajeService.obtenerUltimosRepostajes(aeronaveId);
        return ResponseEntity.ok(repostajes);
    }

    /**
     * Obtiene los repostajes de un proveedor específico.
     *
     * @param proveedor nombre del proveedor
     * @return ResponseEntity con la lista de repostajes del proveedor
     */
    @GetMapping("/proveedor/{proveedor}")
    public ResponseEntity<List<RepostajeDTO>> obtenerRepostajePorProveedor(
            @PathVariable String proveedor) {
        List<RepostajeDTO> repostajes = repostajeService.obtenerRepostajePorProveedor(proveedor);
        return ResponseEntity.ok(repostajes);
    }

    /**
     * Calcula la cantidad total de combustible repostado en una aeronave.
     *
     * @param aeronaveId ID de la aeronave
     * @return ResponseEntity con el total de combustible en litros
     */
    @GetMapping("/aeronave/{aeronaveId}/total-combustible")
    public ResponseEntity<Double> calcularCombustibleTotalAeronave(
            @PathVariable Long aeronaveId) {
        Double total = repostajeService.calcularCombustibleTotalAeronave(aeronaveId);
        return ResponseEntity.ok(total);
    }

    /**
     * Calcula el costo total de repostajes en un período.
     *
     * @param inicio fecha de inicio del período
     * @param fin fecha de fin del período
     * @return ResponseEntity con el costo total
     */
    @GetMapping("/costo-total-periodo")
    public ResponseEntity<Double> calcularCostoTotalPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {
        Double total = repostajeService.calcularCostoTotalPeriodo(inicio, fin);
        return ResponseEntity.ok(total);
    }

    /**
     * Actualiza un repostaje existente.
     *
     * @param id ID del repostaje a actualizar
     * @param repostajeCreateDTO DTO con los nuevos datos
     * @return ResponseEntity con el repostaje actualizado
     */
    @PutMapping("/{id}")
    public ResponseEntity<RepostajeDTO> actualizarRepostaje(
            @PathVariable Long id,
            @Valid @RequestBody RepostajeCreateDTO repostajeCreateDTO) {
        RepostajeDTO repostajeDTO = repostajeService.actualizarRepostaje(id, repostajeCreateDTO);
        return ResponseEntity.ok(repostajeDTO);
    }

    /**
     * Elimina un repostaje.
     *
     * @param id ID del repostaje a eliminar
     * @return ResponseEntity sin contenido (204 No Content)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarRepostaje(@PathVariable Long id) {
        repostajeService.eliminarRepostaje(id);
        return ResponseEntity.noContent().build();
    }
}
