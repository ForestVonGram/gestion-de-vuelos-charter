package com.paeldav.backend.presentation.controller;

import com.paeldav.backend.application.dto.nomina.NominaCreateDTO;
import com.paeldav.backend.application.dto.nomina.NominaDTO;
import com.paeldav.backend.application.dto.nomina.NominaFiltroDTO;
import com.paeldav.backend.application.dto.nomina.NominaUpdateDTO;
import com.paeldav.backend.application.service.base.NominaService;
import com.paeldav.backend.domain.enums.EstadoNomina;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Controlador REST para la gestión de nóminas.
 * Proporciona endpoints CRUD para crear, actualizar, consultar y eliminar nóminas del personal.
 * Solo accessible por usuarios con rol ADMINISTRADOR.
 */
@RestController
@RequestMapping("/api/nominas")
@RequiredArgsConstructor
public class NominaController {

    private final NominaService nominaService;

    /**
     * Genera una nueva nómina para un personal.
     *
     * @param nominaCreateDTO DTO con los datos de la nómina
     * @return ResponseEntity con la nómina generada (201 Created)
     */
    @PostMapping
    public ResponseEntity<NominaDTO> generarNomina(
            @Valid @RequestBody NominaCreateDTO nominaCreateDTO) {
        NominaDTO nominaDTO = nominaService.generarNomina(nominaCreateDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(nominaDTO);
    }

    /**
     * Obtiene una nómina por su ID.
     *
     * @param id ID de la nómina
     * @return ResponseEntity con los datos de la nómina
     */
    @GetMapping("/{id}")
    public ResponseEntity<NominaDTO> obtenerNominaPorId(@PathVariable Long id) {
        NominaDTO nominaDTO = nominaService.obtenerNominaPorId(id);
        return ResponseEntity.ok(nominaDTO);
    }

    /**
     * Obtiene todas las nóminas de un personal específico.
     *
     * @param personalId ID del personal
     * @return ResponseEntity con la lista de nóminas del personal
     */
    @GetMapping("/personal/{personalId}")
    public ResponseEntity<List<NominaDTO>> obtenerNominasPorPersonal(
            @PathVariable Long personalId) {
        List<NominaDTO> nominas = nominaService.obtenerNominasPorPersonal(personalId);
        return ResponseEntity.ok(nominas);
    }

    /**
     * Obtiene todas las nóminas de un período específico.
     *
     * @param mes mes de las nóminas
     * @param ano año de las nóminas
     * @return ResponseEntity con la lista de nóminas del período
     */
    @GetMapping("/periodo")
    public ResponseEntity<List<NominaDTO>> obtenerNominasPorPeriodo(
            @RequestParam Integer mes,
            @RequestParam Integer ano) {
        List<NominaDTO> nominas = nominaService.obtenerNominasPorPeriodo(mes, ano);
        return ResponseEntity.ok(nominas);
    }

    /**
     * Obtiene una nómina específica de un personal en un período.
     *
     * @param personalId ID del personal
     * @param mes mes de la nómina
     * @param ano año de la nómina
     * @return ResponseEntity con la nómina si existe
     */
    @GetMapping("/personal/{personalId}/periodo")
    public ResponseEntity<NominaDTO> obtenerNominaPorPersonalYPeriodo(
            @PathVariable Long personalId,
            @RequestParam Integer mes,
            @RequestParam Integer ano) {
        NominaDTO nominaDTO = nominaService.obtenerNominaPorPersonalYPeriodo(personalId, mes, ano);
        return ResponseEntity.ok(nominaDTO);
    }

    /**
     * Obtiene todas las nóminas en un estado específico.
     *
     * @param estado estado de la nómina
     * @return ResponseEntity con la lista de nóminas en ese estado
     */
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<NominaDTO>> obtenerNominasPorEstado(
            @PathVariable EstadoNomina estado) {
        List<NominaDTO> nominas = nominaService.obtenerNominasPorEstado(estado);
        return ResponseEntity.ok(nominas);
    }

    /**
     * Obtiene todas las nóminas dentro de un rango de fechas.
     *
     * @param fechaInicio fecha de inicio
     * @param fechaFin fecha de fin
     * @return ResponseEntity con la lista de nóminas en el rango
     */
    @GetMapping("/fecha")
    public ResponseEntity<List<NominaDTO>> obtenerNominasPorFecha(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin) {
        List<NominaDTO> nominas = nominaService.obtenerNominasPorFecha(fechaInicio, fechaFin);
        return ResponseEntity.ok(nominas);
    }

    /**
     * Obtiene todas las nóminas con aplicación de filtros.
     *
     * @param filtro DTO con criterios de filtrado
     * @return ResponseEntity con la lista de nóminas filtradas
     */
    @PostMapping("/filtro")
    public ResponseEntity<List<NominaDTO>> listarNominasConFiltro(
            @RequestBody NominaFiltroDTO filtro) {
        List<NominaDTO> nominas = nominaService.listarNominasConFiltro(filtro);
        return ResponseEntity.ok(nominas);
    }

    /**
     * Obtiene el historial completo de nóminas de un personal.
     *
     * @param personalId ID del personal
     * @return ResponseEntity con el historial de nóminas
     */
    @GetMapping("/personal/{personalId}/historial")
    public ResponseEntity<List<NominaDTO>> obtenerHistorialNominas(
            @PathVariable Long personalId) {
        List<NominaDTO> nominas = nominaService.obtenerHistorialNominas(personalId);
        return ResponseEntity.ok(nominas);
    }

    /**
     * Actualiza una nómina existente.
     *
     * @param id ID de la nómina
     * @param nominaUpdateDTO DTO con los datos a actualizar
     * @return ResponseEntity con la nómina actualizada
     */
    @PatchMapping("/{id}")
    public ResponseEntity<NominaDTO> actualizarNomina(
            @PathVariable Long id,
            @Valid @RequestBody NominaUpdateDTO nominaUpdateDTO) {
        NominaDTO nominaDTO = nominaService.actualizarNomina(id, nominaUpdateDTO);
        return ResponseEntity.ok(nominaDTO);
    }

    /**
     * Marca una nómina como pagada.
     *
     * @param id ID de la nómina
     * @return ResponseEntity con la nómina actualizada
     */
    @PatchMapping("/{id}/pagar")
    public ResponseEntity<NominaDTO> marcarComoPagada(@PathVariable Long id) {
        NominaDTO nominaDTO = nominaService.marcarComoPagada(id);
        return ResponseEntity.ok(nominaDTO);
    }

    /**
     * Marca una nómina como retenida.
     *
     * @param id ID de la nómina
     * @param motivo motivo de la retención
     * @return ResponseEntity con la nómina actualizada
     */
    @PatchMapping("/{id}/retener")
    public ResponseEntity<NominaDTO> marcarComoRetenida(
            @PathVariable Long id,
            @RequestParam(required = false) String motivo) {
        NominaDTO nominaDTO = nominaService.marcarComoRetenida(id, motivo);
        return ResponseEntity.ok(nominaDTO);
    }

    /**
     * Elimina una nómina.
     *
     * @param id ID de la nómina a eliminar
     * @return ResponseEntity sin contenido (204 No Content)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarNomina(@PathVariable Long id) {
        nominaService.eliminarNomina(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Calcula el total de nóminas pagadas para un personal.
     *
     * @param personalId ID del personal
     * @return ResponseEntity con el total de nóminas pagadas
     */
    @GetMapping("/personal/{personalId}/total-pagado")
    public ResponseEntity<Double> calcularTotalNominasPagadas(
            @PathVariable Long personalId) {
        Double total = nominaService.calcularTotalNominasPagadas(personalId);
        return ResponseEntity.ok(total);
    }

    @GetMapping("")
    public ResponseEntity<Page<NominaDTO>> obtenerNominas(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(required = false) EstadoNomina estadoNomina,
            @RequestParam (required = false) Integer mes,
            @RequestParam (required = false) Integer anio,
            @RequestParam (required = false) Integer personaId) {
        Page<NominaDTO> resultado = nominaService.obtenerNominas(page, estadoNomina, mes, anio, personaId);
        return ResponseEntity.ok(resultado);
    }

    /**
     * Procesa el pago de todas las nóminas pendientes.
     * Endpoint administrativo que ejecuta el procesamiento batch.
     *
     * @return ResponseEntity con el número de nóminas procesadas
     */
    @PostMapping("/procesar-pendientes")
    public ResponseEntity<Integer> procesarPagosNominaPendientes() {
        Integer procesadas = nominaService.procesarPagosNominaPendientes();
        return ResponseEntity.ok(procesadas);
    }
}
