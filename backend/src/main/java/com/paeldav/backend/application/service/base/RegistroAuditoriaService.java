package com.paeldav.backend.application.service.base;

import com.paeldav.backend.application.dto.registroauditoria.RegistroAuditoriaDTO;
import com.paeldav.backend.domain.enums.TipoEventoAuditoria;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Interfaz para la gestión de registros de auditoría de acceso.
 */
public interface RegistroAuditoriaService {

    /**
     * Registra un evento de acceso al sistema.
     *
     * @param usuarioId ID del usuario (puede ser null si es acceso fallido)
     * @param tipoEvento tipo de evento
     * @param directorIP dirección IP del cliente
     * @param navegador información del navegador/cliente
     * @param resultado true si fue exitoso, false si falló
     * @param detallesError detalles de error si aplica
     * @return DTO del registro creado
     */
    RegistroAuditoriaDTO registrarEvento(Long usuarioId, TipoEventoAuditoria tipoEvento,
                                        String directorIP, String navegador,
                                        Boolean resultado, String detallesError);

    /**
     * Obtiene todos los eventos de auditoría de un usuario.
     *
     * @param usuarioId ID del usuario
     * @return lista de DTOs de registros de auditoría
     */
    List<RegistroAuditoriaDTO> obtenerAuditoriaPorUsuario(Long usuarioId);

    /**
     * Obtiene todos los eventos de un tipo específico.
     *
     * @param tipoEvento tipo de evento
     * @return lista de DTOs de registros de auditoría
     */
    List<RegistroAuditoriaDTO> obtenerEventosPorTipo(TipoEventoAuditoria tipoEvento);

    /**
     * Obtiene eventos de auditoría en un rango de fechas.
     *
     * @param inicio fecha y hora de inicio
     * @param fin fecha y hora de fin
     * @return lista de DTOs de registros de auditoría
     */
    List<RegistroAuditoriaDTO> obtenerEventosPorFecha(LocalDateTime inicio, LocalDateTime fin);

    /**
     * Obtiene eventos de auditoría de un usuario en un rango de fechas.
     *
     * @param usuarioId ID del usuario
     * @param inicio fecha y hora de inicio
     * @param fin fecha y hora de fin
     * @return lista de DTOs de registros de auditoría
     */
    List<RegistroAuditoriaDTO> obtenerEventosPorUsuarioYFecha(Long usuarioId,
                                                               LocalDateTime inicio,
                                                               LocalDateTime fin);

    /**
     * Obtiene todos los intentos de acceso fallidos.
     *
     * @return lista de DTOs de registros de auditoría fallidos
     */
    List<RegistroAuditoriaDTO> obtenerIntentosAccesoDenegados();

    /**
     * Obtiene intentos de acceso fallidos por tipo de evento.
     *
     * @param tipoEvento tipo de evento fallido
     * @return lista de DTOs de registros de auditoría fallidos
     */
    List<RegistroAuditoriaDTO> obtenerIntentosAccesoDenegadosPorTipo(TipoEventoAuditoria tipoEvento);

    /**
     * Obtiene todos los eventos de auditoría.
     *
     * @return lista de DTOs de registros de auditoría
     */
    List<RegistroAuditoriaDTO> obtenerTodosLosEventos();
}
