package com.paeldav.backend.presentation.controller;

import com.paeldav.backend.application.dto.registroauditoria.RegistroAuditoriaDTO;
import com.paeldav.backend.application.service.base.RegistroAuditoriaService;
import com.paeldav.backend.domain.enums.TipoEventoAuditoria;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Controlador REST para la consulta de registros de auditoría de acceso.
 */
@RestController
@RequestMapping("/api/auditoria/accesos")
@RequiredArgsConstructor
public class RegistroAuditoriaController {

    private final RegistroAuditoriaService registroAuditoriaService;

    /**
     * Obtiene todos los eventos de auditoría.
     *
     * @return ResponseEntity con la lista de eventos
     */
    @GetMapping
    public ResponseEntity<List<RegistroAuditoriaDTO>> obtenerTodosLosEventos() {
        List<RegistroAuditoriaDTO> eventos = registroAuditoriaService.obtenerTodosLosEventos();
        return ResponseEntity.ok(eventos);
    }

    /**
     * Obtiene los eventos de auditoría de un usuario específico.
     *
     * @param usuarioId ID del usuario
     * @return ResponseEntity con la lista de eventos del usuario
     */
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<RegistroAuditoriaDTO>> obtenerAuditoriaPorUsuario(@PathVariable Long usuarioId) {
        List<RegistroAuditoriaDTO> eventos = registroAuditoriaService.obtenerAuditoriaPorUsuario(usuarioId);
        return ResponseEntity.ok(eventos);
    }

    /**
     * Obtiene los eventos de un tipo específico.
     *
     * @param tipoEvento tipo de evento
     * @return ResponseEntity con la lista de eventos filtrados
     */
    @GetMapping("/tipo/{tipoEvento}")
    public ResponseEntity<List<RegistroAuditoriaDTO>> obtenerEventosPorTipo(@PathVariable TipoEventoAuditoria tipoEvento) {
        List<RegistroAuditoriaDTO> eventos = registroAuditoriaService.obtenerEventosPorTipo(tipoEvento);
        return ResponseEntity.ok(eventos);
    }

    /**
     * Obtiene los eventos en un rango de fechas.
     *
     * @param inicio fecha y hora de inicio
     * @param fin fecha y hora de fin
     * @return ResponseEntity con la lista de eventos en el rango
     */
    @GetMapping("/fecha")
    public ResponseEntity<List<RegistroAuditoriaDTO>> obtenerEventosPorFecha(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {
        List<RegistroAuditoriaDTO> eventos = registroAuditoriaService.obtenerEventosPorFecha(inicio, fin);
        return ResponseEntity.ok(eventos);
    }

    /**
     * Obtiene los eventos de un usuario en un rango de fechas.
     *
     * @param usuarioId ID del usuario
     * @param inicio fecha y hora de inicio
     * @param fin fecha y hora de fin
     * @return ResponseEntity con la lista de eventos del usuario en el rango
     */
    @GetMapping("/usuario/{usuarioId}/fecha")
    public ResponseEntity<List<RegistroAuditoriaDTO>> obtenerEventosPorUsuarioYFecha(
            @PathVariable Long usuarioId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {
        List<RegistroAuditoriaDTO> eventos = registroAuditoriaService
                .obtenerEventosPorUsuarioYFecha(usuarioId, inicio, fin);
        return ResponseEntity.ok(eventos);
    }

    /**
     * Obtiene todos los intentos de acceso denegados.
     *
     * @return ResponseEntity con la lista de intentos fallidos
     */
    @GetMapping("/intentos-denegados")
    public ResponseEntity<List<RegistroAuditoriaDTO>> obtenerIntentosAccesoDenegados() {
        List<RegistroAuditoriaDTO> eventos = registroAuditoriaService.obtenerIntentosAccesoDenegados();
        return ResponseEntity.ok(eventos);
    }

    /**
     * Obtiene los intentos de acceso denegados por tipo de evento.
     *
     * @param tipoEvento tipo de evento fallido
     * @return ResponseEntity con la lista de intentos fallidos del tipo
     */
    @GetMapping("/intentos-denegados/tipo/{tipoEvento}")
    public ResponseEntity<List<RegistroAuditoriaDTO>> obtenerIntentosAccesoDenegadosPorTipo(
            @PathVariable TipoEventoAuditoria tipoEvento) {
        List<RegistroAuditoriaDTO> eventos = registroAuditoriaService.obtenerIntentosAccesoDenegadosPorTipo(tipoEvento);
        return ResponseEntity.ok(eventos);
    }
}
