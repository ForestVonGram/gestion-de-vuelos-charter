package com.paeldav.backend.presentation.controller;

import com.paeldav.backend.application.dto.documentotecnico.DocumentoTecnicoCreateDTO;
import com.paeldav.backend.application.dto.documentotecnico.DocumentoTecnicoDTO;
import com.paeldav.backend.application.dto.documentotecnico.DocumentoTecnicoUpdateDTO;
import com.paeldav.backend.application.service.base.DocumentoTecnicoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Controlador REST para la gestión de documentos técnicos de aeronaves.
 * Proporciona endpoints para carga, consulta y administración de documentación técnica.
 */
@RestController
@RequestMapping("/api/documentos-tecnicos")
@RequiredArgsConstructor
public class DocumentoTecnicoController {

    private final DocumentoTecnicoService documentoTecnicoService;

    /**
     * Carga un nuevo documento técnico (archivo + metadatos).
     *
     * @param file archivo a cargar
     * @param aeronaveId ID de la aeronave
     * @param nombre nombre del documento
     * @param tipo tipo de documento
     * @param descripcion descripción del documento
     * @param fechaVencimiento fecha de vencimiento (opcional)
     * @param numeroDocumento número de documento (opcional)
     * @param cargadoPorId ID del personal que carga (opcional)
     * @param observaciones observaciones (opcional)
     * @return ResponseEntity con el documento cargado (201 Created)
     */
    @PostMapping("/cargar")
    public ResponseEntity<DocumentoTecnicoDTO> cargarDocumento(
            @RequestParam("file") MultipartFile file,
            @RequestParam("aeronaveId") Long aeronaveId,
            @RequestParam("nombre") String nombre,
            @RequestParam("tipo") String tipo,
            @RequestParam(value = "descripcion", required = false) String descripcion,
            @RequestParam(value = "fechaVencimiento", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaVencimiento,
            @RequestParam(value = "numeroDocumento", required = false) String numeroDocumento,
            @RequestParam(value = "cargadoPorId", required = false) Long cargadoPorId,
            @RequestParam(value = "observaciones", required = false) String observaciones) {

        DocumentoTecnicoCreateDTO documentoCreateDTO = DocumentoTecnicoCreateDTO.builder()
                .aeronaveId(aeronaveId)
                .nombre(nombre)
                .tipo(tipo)
                .descripcion(descripcion)
                .fechaVencimiento(fechaVencimiento)
                .numeroDocumento(numeroDocumento)
                .cargadoPorId(cargadoPorId)
                .observaciones(observaciones)
                .build();

        DocumentoTecnicoDTO documentoDTO = documentoTecnicoService.cargarDocumento(file, documentoCreateDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(documentoDTO);
    }

    /**
     * Obtiene un documento técnico por su ID.
     *
     * @param id ID del documento
     * @return ResponseEntity con los datos del documento
     */
    @GetMapping("/{id}")
    public ResponseEntity<DocumentoTecnicoDTO> obtenerDocumentoPorId(@PathVariable Long id) {
        DocumentoTecnicoDTO documentoDTO = documentoTecnicoService.obtenerDocumentoPorId(id);
        return ResponseEntity.ok(documentoDTO);
    }

    /**
     * Obtiene todos los documentos técnicos registrados.
     *
     * @return ResponseEntity con la lista de documentos
     */
    @GetMapping
    public ResponseEntity<List<DocumentoTecnicoDTO>> obtenerTodosDocumentos() {
        List<DocumentoTecnicoDTO> documentos = documentoTecnicoService.obtenerTodosDocumentos();
        return ResponseEntity.ok(documentos);
    }

    /**
     * Obtiene todos los documentos técnicos de una aeronave específica.
     *
     * @param aeronaveId ID de la aeronave
     * @return ResponseEntity con la lista de documentos
     */
    @GetMapping("/aeronave/{aeronaveId}")
    public ResponseEntity<List<DocumentoTecnicoDTO>> obtenerDocumentosPorAeronave(
            @PathVariable Long aeronaveId) {
        List<DocumentoTecnicoDTO> documentos = documentoTecnicoService.obtenerDocumentosPorAeronave(aeronaveId);
        return ResponseEntity.ok(documentos);
    }

    /**
     * Obtiene los documentos de un tipo específico.
     *
     * @param tipo tipo de documento
     * @return ResponseEntity con la lista de documentos
     */
    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<List<DocumentoTecnicoDTO>> obtenerDocumentosPorTipo(
            @PathVariable String tipo) {
        List<DocumentoTecnicoDTO> documentos = documentoTecnicoService.obtenerDocumentosPorTipo(tipo);
        return ResponseEntity.ok(documentos);
    }

    /**
     * Obtiene los documentos de una aeronave de un tipo específico.
     *
     * @param aeronaveId ID de la aeronave
     * @param tipo tipo de documento
     * @return ResponseEntity con la lista de documentos
     */
    @GetMapping("/aeronave/{aeronaveId}/tipo/{tipo}")
    public ResponseEntity<List<DocumentoTecnicoDTO>> obtenerDocumentosPorAeronaveYTipo(
            @PathVariable Long aeronaveId,
            @PathVariable String tipo) {
        List<DocumentoTecnicoDTO> documentos = documentoTecnicoService
                .obtenerDocumentosPorAeronaveYTipo(aeronaveId, tipo);
        return ResponseEntity.ok(documentos);
    }

    /**
     * Obtiene los documentos cargados por un personal específico.
     *
     * @param personalId ID del personal
     * @return ResponseEntity con la lista de documentos
     */
    @GetMapping("/personal/{personalId}")
    public ResponseEntity<List<DocumentoTecnicoDTO>> obtenerDocumentosPorPersonal(
            @PathVariable Long personalId) {
        List<DocumentoTecnicoDTO> documentos = documentoTecnicoService.obtenerDocumentosPorPersonal(personalId);
        return ResponseEntity.ok(documentos);
    }

    /**
     * Obtiene los últimos documentos de una aeronave.
     *
     * @param aeronaveId ID de la aeronave
     * @return ResponseEntity con la lista de últimos documentos
     */
    @GetMapping("/aeronave/{aeronaveId}/ultimos")
    public ResponseEntity<List<DocumentoTecnicoDTO>> obtenerUltimosDocumentos(
            @PathVariable Long aeronaveId) {
        List<DocumentoTecnicoDTO> documentos = documentoTecnicoService.obtenerUltimosDocumentos(aeronaveId);
        return ResponseEntity.ok(documentos);
    }

    /**
     * Obtiene los documentos vigentes de una aeronave.
     *
     * @param aeronaveId ID de la aeronave
     * @return ResponseEntity con la lista de documentos vigentes
     */
    @GetMapping("/aeronave/{aeronaveId}/vigentes")
    public ResponseEntity<List<DocumentoTecnicoDTO>> obtenerDocumentosVigentes(
            @PathVariable Long aeronaveId) {
        List<DocumentoTecnicoDTO> documentos = documentoTecnicoService.obtenerDocumentosVigentes(aeronaveId);
        return ResponseEntity.ok(documentos);
    }

    /**
     * Obtiene los documentos vencidos de una aeronave.
     *
     * @param aeronaveId ID de la aeronave
     * @return ResponseEntity con la lista de documentos vencidos
     */
    @GetMapping("/aeronave/{aeronaveId}/vencidos")
    public ResponseEntity<List<DocumentoTecnicoDTO>> obtenerDocumentosVencidos(
            @PathVariable Long aeronaveId) {
        List<DocumentoTecnicoDTO> documentos = documentoTecnicoService.obtenerDocumentosVencidos(aeronaveId);
        return ResponseEntity.ok(documentos);
    }

    /**
     * Obtiene los documentos próximos a vencer (en los próximos 30 días).
     *
     * @return ResponseEntity con la lista de documentos próximos a vencer
     */
    @GetMapping("/proximos-vencer")
    public ResponseEntity<List<DocumentoTecnicoDTO>> obtenerDocumentosProximosAVencer() {
        List<DocumentoTecnicoDTO> documentos = documentoTecnicoService.obtenerDocumentosProximosAVencer();
        return ResponseEntity.ok(documentos);
    }

    /**
     * Obtiene los documentos cargados en un rango de fechas.
     *
     * @param inicio fecha de inicio
     * @param fin fecha de fin
     * @return ResponseEntity con la lista de documentos
     */
    @GetMapping("/fecha")
    public ResponseEntity<List<DocumentoTecnicoDTO>> obtenerDocumentosPorFecha(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {
        List<DocumentoTecnicoDTO> documentos = documentoTecnicoService.obtenerDocumentosPorFecha(inicio, fin);
        return ResponseEntity.ok(documentos);
    }

    /**
     * Obtiene un documento por su número de documento.
     *
     * @param numeroDocumento número de documento
     * @return ResponseEntity con el documento encontrado
     */
    @GetMapping("/numero/{numeroDocumento}")
    public ResponseEntity<DocumentoTecnicoDTO> obtenerDocumentoPorNumero(
            @PathVariable String numeroDocumento) {
        DocumentoTecnicoDTO documentoDTO = documentoTecnicoService.obtenerDocumentoPorNumero(numeroDocumento);
        return ResponseEntity.ok(documentoDTO);
    }

    /**
     * Actualiza los metadatos de un documento.
     *
     * @param id ID del documento
     * @param documentoUpdateDTO datos a actualizar
     * @return ResponseEntity con el documento actualizado
     */
    @PutMapping("/{id}")
    public ResponseEntity<DocumentoTecnicoDTO> actualizarDocumento(
            @PathVariable Long id,
            @Valid @RequestBody DocumentoTecnicoUpdateDTO documentoUpdateDTO) {
        DocumentoTecnicoDTO documentoDTO = documentoTecnicoService.actualizarDocumento(id, documentoUpdateDTO);
        return ResponseEntity.ok(documentoDTO);
    }

    /**
     * Elimina un documento técnico.
     *
     * @param id ID del documento
     * @return ResponseEntity sin contenido (204 No Content)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarDocumento(@PathVariable Long id) {
        documentoTecnicoService.eliminarDocumento(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Marca un documento como no vigente sin eliminarlo.
     *
     * @param id ID del documento
     * @return ResponseEntity sin contenido (204 No Content)
     */
    @PatchMapping("/{id}/marcar-no-vigente")
    public ResponseEntity<Void> marcarNoVigente(@PathVariable Long id) {
        documentoTecnicoService.marcarNoVigente(id);
        return ResponseEntity.noContent().build();
    }
}
