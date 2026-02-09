package com.paeldav.backend.application.service.base;

import com.paeldav.backend.application.dto.mantenimiento.MantenimientoCreateDTO;
import com.paeldav.backend.application.dto.mantenimiento.MantenimientoDTO;
import com.paeldav.backend.domain.enums.TipoMantenimiento;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Servicio para la gestión de mantenimiento de aeronaves.
 * Incluye operaciones para registro de mantenimientos preventivos, correctivos
 * e inspecciones técnicas.
 */
public interface MantenimientoService {

    /**
     * Registra un nuevo mantenimiento para una aeronave.
     *
     * @param mantenimientoCreateDTO datos del mantenimiento a crear
     * @return DTO del mantenimiento registrado
     * @throws AeronaveNoEncontradaException si la aeronave no existe
     */
    MantenimientoDTO registrarMantenimiento(MantenimientoCreateDTO mantenimientoCreateDTO);

    /**
     * Obtiene un mantenimiento por su ID.
     *
     * @param id identificador del mantenimiento
     * @return DTO del mantenimiento
     * @throws MantenimientoNoEncontradoException si el mantenimiento no existe
     */
    MantenimientoDTO obtenerMantenimientoPorId(Long id);

    /**
     * Obtiene todos los mantenimientos registrados.
     *
     * @return lista de DTOs de mantenimientos
     */
    List<MantenimientoDTO> obtenerTodosMantenimientos();

    /**
     * Obtiene los mantenimientos de una aeronave específica.
     *
     * @param aeronaveId identificador de la aeronave
     * @return lista de DTOs de mantenimientos de la aeronave
     */
    List<MantenimientoDTO> obtenerMantenimientosPorAeronave(Long aeronaveId);

    /**
     * Obtiene los mantenimientos pendientes (no completados) de una aeronave.
     *
     * @param aeronaveId identificador de la aeronave
     * @return lista de DTOs de mantenimientos pendientes
     */
    List<MantenimientoDTO> obtenerMantenimientosPendientesPorAeronave(Long aeronaveId);

    /**
     * Obtiene los mantenimientos de un tipo específico.
     *
     * @param tipo tipo de mantenimiento (PREVENTIVO, CORRECTIVO, etc.)
     * @return lista de DTOs de mantenimientos del tipo especificado
     */
    List<MantenimientoDTO> obtenerMantenimientosPorTipo(TipoMantenimiento tipo);

    /**
     * Obtiene los mantenimientos de una aeronave de un tipo específico.
     *
     * @param aeronaveId identificador de la aeronave
     * @param tipo tipo de mantenimiento
     * @return lista de DTOs de mantenimientos
     */
    List<MantenimientoDTO> obtenerMantenimientosPorAeronaveYTipo(Long aeronaveId, TipoMantenimiento tipo);

    /**
     * Obtiene los mantenimientos realizados en un rango de fechas.
     *
     * @param inicio fecha de inicio
     * @param fin fecha de fin
     * @return lista de DTOs de mantenimientos en el rango
     */
    List<MantenimientoDTO> obtenerMantenimientosPorFecha(LocalDateTime inicio, LocalDateTime fin);

    /**
     * Marca un mantenimiento como completado.
     *
     * @param id identificador del mantenimiento
     * @param fechaFin fecha de finalización del mantenimiento
     * @param observaciones observaciones finales del mantenimiento
     * @return DTO del mantenimiento actualizado
     * @throws MantenimientoNoEncontradoException si el mantenimiento no existe
     */
    MantenimientoDTO completarMantenimiento(Long id, LocalDateTime fechaFin, String observaciones);

    /**
     * Obtiene los últimos mantenimientos realizados a una aeronave.
     *
     * @param aeronaveId identificador de la aeronave
     * @return lista de DTOs de últimos mantenimientos
     */
    List<MantenimientoDTO> obtenerUltimosMantenimientos(Long aeronaveId);

    /**
     * Obtiene todos los mantenimientos pendientes (no completados).
     *
     * @return lista de DTOs de mantenimientos pendientes
     */
    List<MantenimientoDTO> obtenerMantenimientosPendientes();

    /**
     * Obtiene los mantenimientos asignados a un responsable específico.
     *
     * @param responsableId identificador del responsable (usuario)
     * @return lista de DTOs de mantenimientos asignados
     */
    List<MantenimientoDTO> obtenerMantenimientosPorResponsable(Long responsableId);

    /**
     * Verifica si una aeronave tiene mantenimiento vencido (no completado y fecha pasada).
     *
     * @param aeronaveId identificador de la aeronave
     * @return true si tiene mantenimiento vencido, false en caso contrario
     */
    boolean verificarMantenimientoVencido(Long aeronaveId);

    /**
     * Obtiene los mantenimientos vencidos de una aeronave.
     *
     * @param aeronaveId identificador de la aeronave
     * @return lista de DTOs de mantenimientos vencidos
     */
    List<MantenimientoDTO> obtenerMantenimientosVencidos(Long aeronaveId);

    /**
     * Verifica si una aeronave tiene mantenimiento pendiente.
     *
     * @param aeronaveId identificador de la aeronave
     * @return true si tiene mantenimiento pendiente, false en caso contrario
     */
    boolean verificarMantenimientoPendiente(Long aeronaveId);
}
