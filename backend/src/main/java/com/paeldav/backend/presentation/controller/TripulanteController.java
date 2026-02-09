package com.paeldav.backend.presentation.controller;

import com.paeldav.backend.application.dto.tripulante.TripulanteCreateDTO;
import com.paeldav.backend.application.dto.tripulante.TripulanteDTO;
import com.paeldav.backend.application.dto.tripulante.TripulanteUpdateDTO;
import com.paeldav.backend.application.service.base.TripulanteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión de tripulantes.
 */
@RestController
@RequestMapping("/api/tripulantes")
@RequiredArgsConstructor
public class TripulanteController {

    private final TripulanteService tripulanteService;

    /**
     * Registra un nuevo tripulante.
     */
    @PostMapping("/registrar")
    public ResponseEntity<TripulanteDTO> registrarTripulante(
            @Valid @RequestBody TripulanteCreateDTO tripulanteCreateDTO) {
        TripulanteDTO tripulante = tripulanteService.registrarTripulante(tripulanteCreateDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(tripulante);
    }

    /**
     * Obtiene un tripulante por su ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<TripulanteDTO> obtenerTripulante(@PathVariable Long id) {
        TripulanteDTO tripulante = tripulanteService.obtenerTripulantePorId(id);
        return ResponseEntity.ok(tripulante);
    }

    /**
     * Obtiene un tripulante por número de licencia.
     */
    @GetMapping("/licencia/{numeroLicencia}")
    public ResponseEntity<TripulanteDTO> obtenerTripulantePorLicencia(
            @PathVariable String numeroLicencia) {
        TripulanteDTO tripulante = tripulanteService.obtenerTripulantePorNumeroLicencia(numeroLicencia);
        return ResponseEntity.ok(tripulante);
    }

    /**
     * Obtiene todos los tripulantes.
     */
    @GetMapping
    public ResponseEntity<List<TripulanteDTO>> obtenerTodosTripulantes() {
        List<TripulanteDTO> tripulantes = tripulanteService.obtenerTodosTripulantes();
        return ResponseEntity.ok(tripulantes);
    }

    /**
     * Obtiene todos los pilotos.
     */
    @GetMapping("/pilotos")
    public ResponseEntity<List<TripulanteDTO>> obtenerPilotos() {
        List<TripulanteDTO> pilotos = tripulanteService.obtenerPilotos();
        return ResponseEntity.ok(pilotos);
    }

    /**
     * Obtiene todos los auxiliares.
     */
    @GetMapping("/auxiliares")
    public ResponseEntity<List<TripulanteDTO>> obtenerAuxiliares() {
        List<TripulanteDTO> auxiliares = tripulanteService.obtenerAuxiliares();
        return ResponseEntity.ok(auxiliares);
    }

    /**
     * Obtiene tripulantes disponibles.
     */
    @GetMapping("/disponibles")
    public ResponseEntity<List<TripulanteDTO>> obtenerDisponibles() {
        List<TripulanteDTO> disponibles = tripulanteService.obtenerTripulantesDisponibles();
        return ResponseEntity.ok(disponibles);
    }

    /**
     * Edita un tripulante.
     */
    @PutMapping("/{id}")
    public ResponseEntity<TripulanteDTO> editarTripulante(
            @PathVariable Long id,
            @Valid @RequestBody TripulanteUpdateDTO tripulanteUpdateDTO) {
        TripulanteDTO tripulante = tripulanteService.editarTripulante(id, tripulanteUpdateDTO);
        return ResponseEntity.ok(tripulante);
    }

    /**
     * Elimina un tripulante.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarTripulante(@PathVariable Long id) {
        tripulanteService.eliminarTripulante(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Valida un tripulante (certifi caciones y requisitos técnicos).
     */
    @PostMapping("/{id}/validar")
    public ResponseEntity<String> validarTripulante(@PathVariable Long id) {
        tripulanteService.validarTripulante(id);
        return ResponseEntity.ok("Tripulante validado exitosamente");
    }
}
