package com.paeldav.backend.presentation.controller;

import com.paeldav.backend.application.dto.vuelo.VueloCreateDTO;
import com.paeldav.backend.application.dto.vuelo.VueloDTO;
import com.paeldav.backend.application.dto.vuelo.VueloUpdateDTO;
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
}
