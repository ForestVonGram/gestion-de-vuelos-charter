package com.paeldav.backend.application.service.base;

import com.paeldav.backend.application.dto.alerta.AlertaCreateDTO;
import com.paeldav.backend.application.dto.alerta.AlertaDTO;
import com.paeldav.backend.domain.enums.TipoAlerta;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Servicio para la gestión de alertas de mantenimiento de aeronaves.
 * Incluye operaciones para crear, obtener y resolver alertas.
 */
public interface AlertaService {

    /**
     * Crea una nueva alerta.
     *
     * @param alertaCreateDTO datos de la alerta a crear
     * @return DTO de la alerta creada
     */
    AlertaDTO crearAlerta(AlertaCreateDTO alertaCreateDTO);

    /**
     * Obtiene una alerta por su ID.
     *
     * @param id identificador de la alerta
     * @return DTO de la alerta
     */
    AlertaDTO obtenerAlertaPorId(Long id);

    /**
     * Obtiene todas las alertas registradas.
     *
     * @return lista de DTOs de alertas
     */
    List<AlertaDTO> obtenerTodasAlertas();

    /**
     * Obtiene las alertas de una aeronave específica.
     *
     * @param aeronaveId identificador de la aeronave
     * @return lista de DTOs de alertas de la aeronave
     */
    List<AlertaDTO> obtenerAlertasPorAeronave(Long aeronaveId);

    /**
     * Obtiene las alertas activas de una aeronave.
     *
     * @param aeronaveId identificador de la aeronave
     * @return lista de DTOs de alertas activas
     */
    List<AlertaDTO> obtenerAlertasActivasPorAeronave(Long aeronaveId);

    /**
     * Obtiene las alertas de un tipo específico.
     *
     * @param tipo tipo de alerta
     * @return lista de DTOs de alertas del tipo especificado
     */
    List<AlertaDTO> obtenerAlertasPorTipo(TipoAlerta tipo);

    /**
     * Obtiene las alertas activas de un tipo específico.
     *
     * @param tipo tipo de alerta
     * @return lista de DTOs de alertas activas del tipo especificado
     */
    List<AlertaDTO> obtenerAlertasActivasPorTipo(TipoAlerta tipo);

    /**
     * Obtiene las alertas activas de una aeronave de un tipo específico.
     *
     * @param aeronaveId identificador de la aeronave
     * @param tipo tipo de alerta
     * @return lista de DTOs de alertas activas
     */
    List<AlertaDTO> obtenerAlertasActivasPorAeronaveYTipo(Long aeronaveId, TipoAlerta tipo);

    /**
     * Obtiene todas las alertas activas del sistema.
     *
     * @return lista de DTOs de alertas activas
     */
    List<AlertaDTO> obtenerAlertasActivas();

    /**
     * Marca una alerta como resuelta.
     *
     * @param id identificador de la alerta
     * @param observaciones observaciones de la resolución
     * @return DTO de la alerta actualizada
     */
    AlertaDTO resolverAlerta(Long id, String observaciones);

    /**
     * Obtiene las alertas de una aeronave en un rango de fechas.
     *
     * @param aeronaveId identificador de la aeronave
     * @param inicio fecha de inicio
     * @param fin fecha de fin
     * @return lista de DTOs de alertas en el rango
     */
    List<AlertaDTO> obtenerAlertasPorAeronaveYFecha(Long aeronaveId, LocalDateTime inicio, LocalDateTime fin);

    /**
     * Genera una alerta de mantenimiento vencido para una aeronave.
     *
     * @param aeronaveId identificador de la aeronave
     * @return DTO de la alerta generada, o null si no hay mantenimientos vencidos
     */
    AlertaDTO generarAlertaMantenimientoVencido(Long aeronaveId);

    /**
     * Genera una alerta de mantenimiento próximo a vencer para una aeronave.
     *
     * @param aeronaveId identificador de la aeronave
     * @param diasAnticipacion días de anticipación para generar la alerta
     * @return DTO de la alerta generada, o null si no hay mantenimientos próximos
     */
    AlertaDTO generarAlertaMantenimientoProximo(Long aeronaveId, int diasAnticipacion);

    /**
     * Obtiene las alertas relacionadas a un mantenimiento específico.
     *
     * @param mantenimientoId identificador del mantenimiento
     * @return lista de DTOs de alertas relacionadas
     */
    List<AlertaDTO> obtenerAlertasPorMantenimiento(Long mantenimientoId);
}
