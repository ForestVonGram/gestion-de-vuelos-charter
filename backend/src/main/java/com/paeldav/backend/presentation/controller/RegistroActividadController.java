package com.paeldav.backend.presentation.controller;

import com.paeldav.backend.application.dto.registroactividad.RegistroActividadDTO;
import com.paeldav.backend.application.service.base.RegistroActividadService;
import com.paeldav.backend.domain.enums.TipoActividad;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Controlador REST para la consulta de registros de actividad.
 */
@RestController
@RequestMapping("/api/auditoria/actividades")
@RequiredArgsConstructor
public class RegistroActividadController {

    private final RegistroActividadService registroActividadService;

    /**
     * Obtiene todas las actividades registradas.
     *
     * @return ResponseEntity con la lista de actividades
     */
    @GetMapping
    public ResponseEntity<List<RegistroActividadDTO>> obtenerTodasLasActividades() {
        List<RegistroActividadDTO> actividades = registroActividadService.obtenerTodasLasActividades();
        return ResponseEntity.ok(actividades);
    }

    /**
     * Obtiene las actividades de un usuario específico.
     *
     * @param usuarioId ID del usuario
     * @return ResponseEntity con la lista de actividades del usuario
     */
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<RegistroActividadDTO>> obtenerActividadesPorUsuario(@PathVariable Long usuarioId) {
        List<RegistroActividadDTO> actividades = registroActividadService.obtenerActividadesPorUsuario(usuarioId);
        return ResponseEntity.ok(actividades);
    }

    /**
     * Obtiene las actividades de un tipo específico.
     *
     * @param tipoActividad tipo de actividad
     * @return ResponseEntity con la lista de actividades filtradas
     */
    @GetMapping("/tipo/{tipoActividad}")
    public ResponseEntity<List<RegistroActividadDTO>> obtenerActividadesPorTipo(@PathVariable TipoActividad tipoActividad) {
        List<RegistroActividadDTO> actividades = registroActividadService.obtenerActividadesPorTipo(tipoActividad);
        return ResponseEntity.ok(actividades);
    }

    /**
     * Obtiene las actividades en un rango de fechas.
     *
     * @param inicio fecha y hora de inicio
     * @param fin fecha y hora de fin
     * @return ResponseEntity con la lista de actividades en el rango
     */
    @GetMapping("/fecha")
    public ResponseEntity<List<RegistroActividadDTO>> obtenerActividadesPorFecha(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {
        List<RegistroActividadDTO> actividades = registroActividadService.obtenerActividadesPorFecha(inicio, fin);
        return ResponseEntity.ok(actividades);
    }

    /**
     * Obtiene las actividades de un usuario en un rango de fechas.
     *
     * @param usuarioId ID del usuario
     * @param inicio fecha y hora de inicio
     * @param fin fecha y hora de fin
     * @return ResponseEntity con la lista de actividades del usuario en el rango
     */
    @GetMapping("/usuario/{usuarioId}/fecha")
    public ResponseEntity<List<RegistroActividadDTO>> obtenerActividadesPorUsuarioYFecha(
            @PathVariable Long usuarioId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {
        List<RegistroActividadDTO> actividades = registroActividadService
                .obtenerActividadesPorUsuarioYFecha(usuarioId, inicio, fin);
        return ResponseEntity.ok(actividades);
    }
}
