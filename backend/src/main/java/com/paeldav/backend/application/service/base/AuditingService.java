package com.paeldav.backend.application.service.base;

import com.paeldav.backend.domain.entity.RegistroAuditoria;
import java.util.List;

/**
 * Servicio para registrar y gestionar la auditoría de operaciones críticas del sistema.
 * Proporciona trazabilidad completa de cambios realizados por usuarios.
 */
public interface AuditingService {

    /**
     * Registra una operación en la auditoría.
     *
     * @param usuarioId ID del usuario que realizó la operación
     * @param tipoOperacion tipo de operación (CREATE, UPDATE, DELETE, etc.)
     * @param tipoEntidad tipo de entidad afectada
     * @param entidadId ID de la entidad
     * @param descripcion descripción de la operación
     * @param cambiosAnteriores valores anteriores en JSON
     * @param cambiosNuevos nuevos valores en JSON
     */
    void registrarOperacion(Long usuarioId, String tipoOperacion, String tipoEntidad,
                           Long entidadId, String descripcion,
                           String cambiosAnteriores, String cambiosNuevos);

    /**
     * Registra una operación con información adicional de contexto.
     *
     * @param usuarioId ID del usuario
     * @param tipoOperacion tipo de operación
     * @param tipoEntidad tipo de entidad
     * @param entidadId ID de la entidad
     * @param descripcion descripción
     * @param cambiosAnteriores valores anteriores
     * @param cambiosNuevos nuevos valores
     * @param direccionIp dirección IP del cliente
     * @param userAgent agente de usuario
     * @param nivelSeveridad nivel de severidad (INFO, WARNING, ERROR, CRITICAL)
     */
    void registrarOperacionConContexto(Long usuarioId, String tipoOperacion, String tipoEntidad,
                                       Long entidadId, String descripcion,
                                       String cambiosAnteriores, String cambiosNuevos,
                                       String direccionIp, String userAgent, String nivelSeveridad);

    /**
     * Registra un error o excepción en la auditoría.
     *
     * @param usuarioId ID del usuario
     * @param tipoOperacion tipo de operación
     * @param tipoEntidad tipo de entidad
     * @param entidadId ID de la entidad
     * @param mensajeError mensaje de error
     * @param descripcion descripción adicional
     */
    void registrarError(Long usuarioId, String tipoOperacion, String tipoEntidad,
                       Long entidadId, String mensajeError, String descripcion);

    /**
     * Obtiene el historial de auditoría de una entidad.
     *
     * @param tipoEntidad tipo de entidad
     * @param entidadId ID de la entidad
     * @return lista de registros de auditoría
     */
    List<RegistroAuditoria> obtenerHistorialEntidad(String tipoEntidad, Long entidadId);

    /**
     * Obtiene el historial de auditoría de un usuario.
     *
     * @param usuarioId ID del usuario
     * @return lista de registros de auditoría
     */
    List<RegistroAuditoria> obtenerHistorialUsuario(Long usuarioId);

    /**
     * Obtiene los registros de auditoría filtrados por tipo de operación.
     *
     * @param tipoOperacion tipo de operación
     * @return lista de registros de auditoría
     */
    List<RegistroAuditoria> obtenerOperaciones(String tipoOperacion);

    /**
     * Obtiene los registros de auditoría de un período específico.
     *
     * @param fechaInicio fecha de inicio
     * @param fechaFin fecha de fin
     * @return lista de registros de auditoría
     */
    List<RegistroAuditoria> obtenerOperacionesPorPeriodo(
        java.time.LocalDateTime fechaInicio,
        java.time.LocalDateTime fechaFin
    );

    /**
     * Obtiene los registros de auditoría críticos de un período.
     *
     * @param fechaInicio fecha de inicio
     * @param fechaFin fecha de fin
     * @return lista de registros críticos
     */
    List<RegistroAuditoria> obtenerOperacionesCriticas(
        java.time.LocalDateTime fechaInicio,
        java.time.LocalDateTime fechaFin
    );
}
